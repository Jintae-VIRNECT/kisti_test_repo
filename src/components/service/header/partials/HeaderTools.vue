<template>
<div class="header-tools">
  <toggle-button
    description="영상 on/off"
    :size="34"
    :active="onVideo"
    :activeSrc="require('assets/image/call/gnb_ic_video_on.png')"
    :inactiveSrc="require('assets/image/call/gnb_ic_video_off.png')"
    @action="videoOnOff"
  ></toggle-button>
  
  <toggle-button
    description="마이크 on/off"
    :size="34"
    :active="onMic"
    :activeSrc="require('assets/image/call/gnb_ic_voice_on.png')"
    :inactiveSrc="require('assets/image/call/gnb_ic_voice_off.png')"
    @action="micOnOff"
  ></toggle-button>
  
  <toggle-button
    description="스피커 on/off"
    :size="34"
    :active="!mute"
    :activeSrc="require('assets/image/call/gnb_ic_volum_on.png')"
    :inactiveSrc="require('assets/image/call/gnb_ic_volum_off.png')"
    @action="muteOnOff"
  ></toggle-button>
  
  <toggle-button
    description="알림"
    :size="34"
    :toggle="false"
    :activeSrc="require('assets/image/call/gnb_ic_notifi_nor.png')"
    @action="notice"
  ></toggle-button>

  <div class="header-tools__time">00:00</div>
  <button class="header-tools__leave" @click="leave">나가기</button>
</div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import ToggleButton from 'ToggleButton'
export default {
	name: "HeaderTools",
	components: {
    ToggleButton
  },
	data() {
		return {
      onVideo: true,
      onMic: true,
      onSpeaker: true
    }
	},
	computed: {
    ...mapGetters(['mainSession', 'mute'])
  },
	watch: {
    mainSession: {
      deep: true,
      handler: function(val) {
        if(val.stream) {
          let state = this.$openvidu.getState()
          this.onVideo = state.video
          this.onMic = state.audio
        }
      }
    }
  },
	methods: {
    ...mapActions(['muteOnOff']),
    videoOnOff() {
      this.onVideo = !this.onVideo
      this.$openvidu.videoOnOff()
    },
    micOnOff() {
      this.onMic = !this.onMic
      this.$openvidu.micOnOff()
    },
    notice() {
      console.log('notice')
    },
    // mute() {
      // this.$openvidu.mute()
    // },
    leave() {
      this.$openvidu.leave()
    }
  },

	/* Lifecycles */
	mounted() {}
}
</script>