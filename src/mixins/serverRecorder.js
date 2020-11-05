import {
  startServerRecord,
  stopServerRecord,
  getServerRecordList,
} from 'api/http/record'
import { mapGetters } from 'vuex'
import { RECORD_INFO } from 'configs/env.config'

export default {
  data() {
    return {
      recordingId: null,
      recordTimeout: null,

      elapsedTime: 0,

      isContinue: false,
    }
  },
  computed: {
    ...mapGetters(['roomInfo', 'serverRecord']),
  },
  methods: {
    async startServerRecord() {
      try {
        this.logger('SERVER RECORD', 'start')

        let today = this.$dayjs().format('YYYY-MM-DD_HH-mm-ss')

        const options = {
          iceServers: RECORD_INFO['coturn'],
          role: 'PUBLISHER',
          wsUri: RECORD_INFO['wss'],
        }

        const token = `${
          RECORD_INFO['token']
        }&recorder=true&options=${JSON.stringify(options)}`

        const result = await startServerRecord({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          framerate: 20,
          recordingFilename: today,
          recordingTimeLimit: Number.parseInt(this.serverRecord.time, 10),
          resolution: this.serverRecord.resolution,
          sessionId: this.roomInfo.sessionId,
          token: token,
          metaData: {},
        })
        this.recordingId = result.recordingId

        const timeout = Number.parseInt(this.serverRecord.time, 10) * 60 * 1000
        this.recordTimeout = setTimeout(() => {
          this.$eventBus.$emit('serverRecord', {
            isStart: false,
          })
        }, timeout)
      } catch (e) {
        console.error('SERVER RECORD::', 'start failed')
        console.error('SERVER RECORD::', e)
        if (e.code === 1001) {
          this.toastError(
            this.$t(
              '현재 실행 가능한 서버녹화 개수가 초과 되었습니다. 잠시후에 다시 시도해 주세요.',
            ),
          )
        } else if (e.code === 1002) {
          this.toastError(
            this.$t(
              '서버에 녹화 파일을 저장 할 수 있는 공간이 부족합니다. 관리자에게 문의 해주세요',
            ),
          )
        }

        this.$eventBus.$emit('serverRecord', {
          isStart: false,
        })
      }
    },

    async stopServerRecord() {
      this.elapsedTime = 0
      this.isContinue = false

      if (this.recordingId) {
        if (this.recordTimeout) {
          clearTimeout(this.recordTimeout)
          this.recordTimeout = null
        }

        this.logger('SERVER RECORD', 'stop')
        await stopServerRecord({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          id: this.recordingId,
        })
        this.recordingId = null
      }
    },
    async toggleServerRecord(payload) {
      if (this.isContinue) return

      if (payload.isStart) {
        await this.startServerRecord()
      } else {
        await this.stopServerRecord()
      }
    },
    async checkServerRecordings() {
      const result = await getServerRecordList({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        sessionId: this.roomInfo.sessionId,
      })

      if (this.isLeader && result.infos.length > 0) {
        this.isContinue = true
        const elapsedTime = result.infos[0].duration
        this.recordingId = result.infos[0].recordingId
        this.elapsedTime = elapsedTime
        const timeout = result.infos[0].timeLimit * 1000
        this.continueServerRecord(timeout)
      }
    },

    async continueServerRecord(timeout) {
      this.recordTimeout = setTimeout(() => {
        this.$eventBus.$emit('serverRecord', {
          isStart: false,
        })
      }, timeout - this.elapsedTime * 1000)

      this.$eventBus.$emit('serverRecord', {
        isStart: true,
        elapsedTime: this.elapsedTime,
        isContinue: this.isContinue,
      })
      this.isContinue = false
    },
  },

  mounted() {
    this.$eventBus.$on('serverRecord', this.toggleServerRecord)
    this.checkServerRecordings()
  },
  beforeDestroy() {
    this.$eventBus.$off('serverRecord')
  },
}
