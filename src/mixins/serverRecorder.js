import { startServerRecord, stopServerRecord } from 'api/http/record'
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      recordingId: null,
    }
  },
  computed: {
    ...mapGetters(['roomInfo']),
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
          recordingTimeLimit: 5,
          resolution: '720p',
          sessionId: this.roomInfo.sessionId,
          token: token,
          metaData: {},
        })
        this.recordingId = result.recordingId

        setTimeout(() => {
          this.$eventBus.$emit('serverRecord', false)
        }, 5 * 60 * 1000)
      } catch (e) {
        console.error('SERVER RECORD::', 'start failed')
        this.$eventBus.$emit('serverRecord', false)
      }
    },
    async stopServerRecord() {
      if (this.recordingId) {
        this.logger('SERVER RECORD', 'stop')
        await stopServerRecord({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          id: this.recordingId,
        })
        this.recordingId = null
      }
    },
    async toggleServerRecord(isStart) {
      if (isStart) {
        await this.startServerRecord()
      } else {
        await this.stopServerRecord()
      }
    },
  },

  mounted() {
    this.$eventBus.$on('serverRecord', this.toggleServerRecord)
  },
  beforeDestroy() {
    this.$eventBus.$off('serverRecord')
  },
}
