<template>
<div class="header-tools">
  <button
    class="header-tools__icon video" 
    :class="{ 'inactive': !onVideo }"
    @click="onVideo = !onVideo">영상 on/off</button>
  <button 
    class="header-tools__icon mic" 
    :class="{ 'inactive': !onMic }"
    @click="onMic = !onMic">마이크 on/off</button>
  <button 
    class="header-tools__icon speaker"
    :class="{ 'inactive': !onSpeaker }"
    @click="onSpeaker = !onSpeaker">스피커 on/off</button>
  <button 
    class="header-tools__icon notice"
    >알림</button>
  <div class="header-tools__time">00:00</div>
  <button class="header-tools__leave" @click="leave">나가기</button>
</div>
</template>

<script>
export default {
	name: "HeaderTools",
	components: {},
	data() {
		return {
      onVideo: true,
      onMic: true,
      onSpeaker: true,
      isRecording: false
    }
	},
	computed: {
  },
	watch: {},
	methods: {
    list() {
      this.$openvidu.active()
        .then(res => {
          console.log(res)
        })
    },
    record() {
      this.$openvidu.record()
        .then(() => {
          this.isRecording = true
        })
    },
    stop() {
      this.$openvidu.stop()
        .then(() => {
          this.isRecording = false
        })
    },
    mute() {
      this.$openvidu.mute()
    },
    leave() {
      this.$openvidu.leave()
    }
  },

	/* Lifecycles */
	mounted() {}
}
</script>