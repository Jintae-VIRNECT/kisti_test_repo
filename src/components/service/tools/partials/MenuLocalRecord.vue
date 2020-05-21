<template>
  <menu-button
    text="녹화"
    :active="isRecording"
    :src="require('assets/image/ic_local_record.svg')"
    :onActive="isRecording"
    :activeSrc="require('assets/image/ic_local_record_on.svg')"
    @click="recording"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'LocalRecordMenu',
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
        this.record()
      } else {
        this.stop()
      }
    },
    record() {
      this.isRecording = true
      // this.$openvidu
      //   .record()
      //   .then(() => {
      //     this.isRecording = true
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },
    stop() {
      this.isRecording = false
      // this.$openvidu
      //   .stop()
      //   .then(() => {
      //     this.isRecording = false
      //   })
      //   .catch(err => {
      //     console.log(err)
      //   })
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
