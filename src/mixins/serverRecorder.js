import {
  startServerRecord,
  stopServerRecord,
  getServerRecordList,
} from 'api/http/record'
import { mapGetters, mapActions } from 'vuex'
import { RECORD_INFO } from 'configs/env.config'
import { ROLE } from 'configs/remote.config'

export default {
  data() {
    return {
      recordingId: null,
      recordTimeout: null,
    }
  },
  computed: {
    ...mapGetters(['roomInfo', 'serverRecord', 'serverRecordStatus']),
  },
  methods: {
    ...mapActions(['setServerRecordStatus']),
    async startServerRecord() {
      try {
        this.logger('SERVER RECORD', 'start')

        const today = this.$dayjs().format('YYYY-MM-DD_HH-mm-ss')

        const options = {
          iceServers: RECORD_INFO['coturn'],
          role: 'PUBLISHER',
          wsUri: RECORD_INFO['wss'],
        }

        const token = `${
          RECORD_INFO['token']
        }&recorder=true&options=${JSON.stringify(options)}`

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

        this.toastDefault(this.$t('service.record_server_start_message'))
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
    toggleServerRecord(status) {
      if (status === 'STOP') {
        this.stopServerRecord()
      } else if (status === 'WAIT') {
        this.setServerRecordStatus('WAIT')
        this.startServerRecord()
      }
    },
    async checkServerRecordings() {
      const result = await getServerRecordList({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        sessionId: this.roomInfo.sessionId,
      })

      if (this.isLeader && result.infos.length > 0) {
        const elapsedTime = result.infos[0].duration
        this.recordingId = result.infos[0].recordingId
        const timeout = result.infos[0].timeLimit * 60 * 1000

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
  },

  mounted() {
    if (!this.account.roleType === ROLE.LEADER) return
    this.$eventBus.$on('serverRecord', this.toggleServerRecord)
    this.checkServerRecordings()
  },
  beforeDestroy() {
    this.$eventBus.$off('serverRecord', this.toggleServerRecord)
  },
}
