<template>
  <tool-button
    text="녹화"
    :active="isRecording"
    :src="require('assets/image/call/ic_record_off.svg')"
    :onActive="isRecording"
    :activeSrc="require('assets/image/call/ic_record_ing.svg')"
    @action="recording"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'RecordTool',
  mixins: [toolMixin],
  data() {
    return {
      isRecording: false,
    }
  },
  computed: {},
  watch: {},
  methods: {
    recording() {
      console.log('recording!!!')
      // this.active = 'recording'
      if (!this.isRecording) {
        this.setTool('record')
        this.record()
      } else {
        this.setTool('pointing')
        this.stop()
      }
    },
    record() {
      this.$openvidu
        .record()
        .then(() => {
          this.isRecording = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    stop() {
      this.$openvidu
        .stop()
        .then(() => {
          this.isRecording = false
        })
        .catch(err => {
          console.log(err)
        })
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
