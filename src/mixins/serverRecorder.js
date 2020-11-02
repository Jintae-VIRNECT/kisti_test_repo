import {
  startServerRecord,
  stopServerRecord,
  getServerRecordList,
} from 'api/http/record'
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      recordingId: null,
      recordTimeout: null,

      timeout: 5 * 60 * 1000,
      compensate: 2 * 1000,
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
          iceServers: window.urls['coturn'],
          role: 'PUBLISHER',
          wsUri: window.urls['wss'],
        }

        const token = `${
          window.urls['token']
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

        this.recordTimeout = setTimeout(() => {
          this.$eventBus.$emit('serverRecord', {
            isStart: false,
          })
        }, this.timeout + this.compensate)
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
        this.continueServerRecord()
      }
    },

    async continueServerRecord() {
      this.recordTimeout = setTimeout(() => {
        this.$eventBus.$emit('serverRecord', {
          isStart: false,
        })
      }, this.timeout + this.compensate - this.elapsedTime)

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
