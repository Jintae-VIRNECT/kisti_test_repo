<template>
  <menu-button
    text="서버 녹화"
    :active="isRecording"
    :disabled="disabled"
    :src="require('assets/image/ic_record_off.svg')"
    :icActive="isRecording"
    :activeSrc="require('assets/image/ic_record_ing.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import { mapGetters } from 'vuex'
import toolMixin from './toolMixin'
import { startServerRecord, stopServerRecord } from 'api/workspace/record'
export default {
  name: 'ServerRecordMenu',
  mixins: [toolMixin],
  data() {
    return {
      isRecording: false,
      recordId: -1,
    }
  },
  computed: {
    ...mapGetters(['roomInfo']),
  },
  watch: {},
  methods: {
    recording() {
      if (this.disabled) return
      // if (this.checkBeta()) {
      //   return false
      // }
      if (!this.isRecording) {
        this.record()
        this.$eventBus.$emit('serverRecord', true)
      } else {
        this.stop()
        this.$eventBus.$emit('serverRecord', false)
      }
    },
    async record() {
      this.isRecording = true
      let today = this.$dayjs().format('YYYY-MM-DD_HH-mm-ss')

      let options = {
        iceServers: this.roomInfo.coturn,
        role: 'PUSLISHER',
        wsUri: this.roomInfo.wss,
      }
      let token = `?&token=${
        this.roomInfo.token
      }&recorder=true&options=${JSON.stringify(options)}`

      console.log(
        await startServerRecord({
          framerate: 20,
          recordingFilename: today + '.mp4',
          recordingTimeLimit: 5,
          resolution: '720p',
          sessionId: this.roomInfo.sessionId,
          token: token,
          userData: {},
        }),
      )
    },
    async stop() {
      this.isRecording = false
      await stopServerRecord(this.recordId)
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
