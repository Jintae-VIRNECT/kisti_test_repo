import { startServerRecord, stopServerRecord } from 'api/workspace/record'
import { mapGetters } from 'vuex'
import { logger } from 'utils/logger'

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
        logger('SERVER RECORD', 'start')

        let today = this.$dayjs().format('YYYY-MM-DD_HH-mm-ss')

        const options = {
          iceServers: this.roomInfo.coturn,
          role: 'PUSLISHER',
          wsUri: this.roomInfo.wss,
        }

        const token = `${
          this.roomInfo.token
        }&recorder=true&options=${JSON.stringify(options)}`

        const result = await startServerRecord({
          framerate: 20,
          recordingFilename: today,
          recordingTimeLimit: 5,
          resolution: '720p',
          sessionId: this.roomInfo.sessionId,
          token: token,
          userData: {},
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
      logger('SERVER RECORD', 'stop')
      if (this.recordingId) {
        await stopServerRecord({ id: this.recordingId })
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
