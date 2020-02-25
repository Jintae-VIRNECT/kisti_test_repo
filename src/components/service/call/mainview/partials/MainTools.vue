<template>
<div class="main-tools">
  <tool-button
    text="포인팅"
    :active="active === 'pointing'"
    :src="require('assets/image/call/ic_pointing.png')"
    @action="pointing"></tool-button>
  <tool-button
    text="캡쳐 후 공유"
    :active="active === 'capture'"
    :src="require('assets/image/call/icn_capture.png')"
    @action="capture"></tool-button>
  <tool-button
    text="녹화"
    :active="isRecording"
    :src="require('assets/image/call/ic_record_off.svg')"
    :onActive="isRecording"
    :activeSrc="require('assets/image/call/ic_record_ing.svg')"
    @action="recording"></tool-button>
</div>
</template>

<script>
import ToolButton from './ToolButton'
export default {
	name: "MainTools",
	components: {
    ToolButton
  },
	data() {
		return {
      active: 'pointing',
      isRecording: false
    }
	},
	computed: {
  },
	watch: {},
	methods: {
    pointing() {
      console.log('pointing!!!!')
      this.active = 'pointing'
    },
    capture() {
      console.log('capture!!!!')
      // this.active = 'capture'

      this.$eventBus.$emit('capture')
    },
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
      this.$openvidu.record()
        .then(() => {
          this.isRecording = true
        })
        .catch(err => {
          console.log(err)
        })
    },
    stop() {
      this.$openvidu.stop()
        .then(() => {
          this.isRecording = false
        })
        .catch(err => {
          console.log(err)
        })
    },
  },

  /* Lifecycles */
  beforeDestroy() {
  },
	mounted() {
  }
}
</script>
