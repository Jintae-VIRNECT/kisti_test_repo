import {
  startServerRecord,
  stopServerRecord,
  getServerRecordList,
} from 'api/http/record'
import { mapGetters, mapActions } from 'vuex'
import { RECORD_INFO } from 'configs/env.config'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'

export default {
  data() {
    return {
      recordingId: null,
      recordTimeout: null,

      serverRecordRetryTimeout: null,
      serverRecordRetryCount: 0,
    }
  },
  computed: {
    ...mapGetters([
      'roomInfo',
      'serverRecord',
      'serverRecordStatus',
      'useRecording',
      'participants',
      'autoServerRecord',
    ]),
  },
  methods: {
    ...mapActions(['setServerRecordStatus']),
    async startServerRecord(reason) {
      try {
        this.logger('SERVER RECORD', 'start')

        const today = this.$dayjs().format('YYYY-MM-DD_HH-mm-ss')

        const options = {
          iceServers: RECORD_INFO['coturn'],
          role: 'PUBLISHER',
          wsUri: RECORD_INFO['wss'],
        }

        const metaData = {
          clientData: this.account.uuid,
          roleType: ROLE.EXPERT,
          deviceType: DEVICE.WEB,
          device: 0,
        }

        const optionString = JSON.stringify(options)
        const metaDataString = JSON.stringify(metaData)
        const tokenInfo = RECORD_INFO['token']

        const token = `${tokenInfo}&recorder=true&options=${optionString}&metaData=${metaDataString}`

        const fileName = `${today}_${this.roomInfo.sessionId}`

        const result = await startServerRecord({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          framerate: 20,
          recordingFilename: fileName,
          recordingTimeLimit: Number.parseInt(this.serverRecord.time, 10),
          resolution: this.serverRecord.resolution,
          sessionId: this.roomInfo.sessionId,
          token: token,
          metaData: {},
        })
        this.recordingId = result.recordingId

        this.setServerRecordStatus('START')
        this.$eventBus.$emit('showServerTimer')

        const timeout = Number.parseInt(this.serverRecord.time, 10) * 60 * 1000

        this.recordTimeout = setTimeout(() => {
          this.stopServerRecord()
        }, timeout)

        //자동 시작시
        if (reason === 'autoStart') {
          this.toastDefault(this.$t('서버 녹화를 자동으로 시작합니다.​'))
        } else {
          this.toastDefault(this.$t('service.record_server_start_message'))
        }
      } catch (e) {
        console.error('SERVER RECORD::', 'start failed')
        if (e.code === 1001) {
          this.toastError(this.$t('service.record_server_over_max_count'))
        } else if (e.code === 1002) {
          this.toastError(this.$t('service.record_server_no_storage'))
        }

        this.stopServerRecord()
      }
    },

    async stopServerRecord() {
      this.setServerRecordStatus('STOP')
      if (this.recordingId) {
        if (this.recordTimeout) {
          this.clearServerRecordTimer()
        }

        this.logger('SERVER RECORD', 'stop')
        await stopServerRecord({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          id: this.recordingId,
        })
        this.recordingId = null

        this.toastDefault(this.$t('service.record_server_end_message'))
      }
    },
    toggleServerRecord(status, reason) {
      if (status === 'STOP') {
        this.stopServerRecord()
      } else if (status === 'WAIT') {
        this.setServerRecordStatus('WAIT')
        this.startServerRecord(reason)
      }
    },
    async checkServerRecordings() {
      const result = await getServerRecordList({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        sessionId: this.roomInfo.sessionId,
      })

      const failedInPreparing =
        this.serverRecordStatus === 'PREPARE' && result.infos.length === 0

      if (failedInPreparing) {
        this.logger('SERVER RECORD', 'failed in preparing')
        this.processPreparingFailed()
        return
      }

      if (this.isLeader && result.infos.length > 0) {
        const recordInfo = result.infos[0]
        const status = recordInfo.status

        if (this.serverRecordRetryCount >= 11) {
          this.processPreparingFailed()
          return
        }

        if (status === 'preparing') {
          this.serverRecordRetryCount++
          this.setServerRecordStatus('PREPARE')
          const retryInterval = 1000

          this.serverRecordRetryTimeout = setTimeout(() => {
            this.logger('SERVER RECORD', 'check preparing')
            this.checkServerRecordings()
          }, retryInterval)
          return
        }

        this.recordingId = recordInfo.recordingId
        const elapsedTime = recordInfo.duration
        const timeout = recordInfo.timeLimit * 60 * 1000

        this.recordTimeout = setTimeout(() => {
          this.toggleServerRecord('STOP')
        }, timeout - elapsedTime * 1000)

        this.$eventBus.$emit('showServerTimer', elapsedTime)
        this.setServerRecordStatus('START')
      }
    },
    clearServerRecordTimer() {
      clearTimeout(this.recordTimeout)
      this.recordTimeout = null
    },
    processPreparingFailed() {
      this.toastDefault(this.$t('service.record_server_start_failed'))
      this.serverRecordRetryCount = 0
      this.setServerRecordStatus('STOP')
    },
  },

  mounted() {
    if (!this.useRecording) return
    this.setServerRecordStatus('STOP')
    if (!this.account.roleType === ROLE.LEADER) return

    this.$eventBus.$on('serverRecord', this.toggleServerRecord)
    this.checkServerRecordings()

    if (this.autoServerRecord && this.serverRecordStatus === 'STOP') {
      //서버 레코딩 시작
      this.toggleServerRecord('WAIT', 'autoStart')
    }
  },
  beforeDestroy() {
    if (!this.useRecording) return
    clearTimeout(this.serverRecordRetryTimeout)
    this.serverRecordRetryTimeout = null

    this.$eventBus.$off('serverRecord', this.toggleServerRecord)
  },
}
