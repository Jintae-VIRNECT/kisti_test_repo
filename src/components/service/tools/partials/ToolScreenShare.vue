<template>
  <tool-button
    :text="$t('service.tool_screen_share')"
    :active="isScreenSharing"
    :isActive="isScreenSharing"
    :disabled="false"
    :src="require('assets/image/call/ic_sharing_on.svg')"
    :activeSrc="require('assets/image/call/ic_sharing_off.svg')"
    @click="toggleShare"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { CAMERA as CAMERA_STATUS } from 'configs/device.config'
import { mapActions, mapGetters } from 'vuex'
export default {
  name: 'ToolScreenShare',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      isScreenSharing: false,
    }
  },
  computed: {
    ...mapGetters(['myInfo', 'myTempStream', 'settingInfo', 'video']),
  },
  methods: {
    ...mapActions(['setMyTempStream']),
    async toggleShare() {
      try {
        if (this.isScreenSharing) {
          this.stopScreenSharing()
        } else {
          await this.startScreenShare()
        }
      } catch (e) {
        console.error(e)
        this.isScreenSharing = false
      }
    },
    async startScreenShare() {
      const displayStream = await this.getDisplayStream()

      if (displayStream && displayStream.getVideoTracks().length > 0) {
        if (this.myInfo.cameraStatus !== CAMERA_STATUS.CAMERA_NONE) {
          this.setMyTempStream(this.myInfo.stream.clone())
          this.$call.replaceTrack(displayStream.getVideoTracks()[0])
          this.$call.sendCamera(CAMERA_STATUS.CAMERA_ON)
        } else {
          this.$call.rePublish({
            videoSource: displayStream.getVideoTracks()[0],
            audioSource: this.myInfo.stream.getAudioTracks()[0].clone(),
          })
          this.$eventBus.$emit('streamctl:hide', true)

          //모바일에서 대응할 수 있는 시간을 주기위한 딜레이
          await new Promise(r => setTimeout(r, 500))
        }

        this.$call.sendScreenSharing(true)
        this.isScreenSharing = true
      }
    },

    async getDisplayStream() {
      if (
        !navigator.mediaDevices ||
        !navigator.mediaDevices['getDisplayMedia']
      ) {
        throw 'NotSupportDisplayError'
      } else {
        const size = this.settingInfo.quality.split('X')
        const video = {
          width: parseInt(size[0], 10),
          height: parseInt(size[1], 10),
        }

        const displayStream = await navigator.mediaDevices.getDisplayMedia({
          audio: true,
          video: video,
        })

        displayStream.getVideoTracks()[0].onended = () => {
          this.stopScreenSharing()
        }
        return displayStream
      }
    },
    stopScreenSharing() {
      if (this.myTempStream) {
        this.$call.replaceTrack(this.myTempStream.getVideoTracks()[0])
        this.$call.sendCamera(
          this.video.isOn ? CAMERA_STATUS.CAMERA_ON : CAMERA_STATUS.CAMERA_OFF,
        )
        this.setMyTempStream(null)
      } else {
        this.$call.rePublish({
          audioSource: this.myInfo.stream.getAudioTracks()[0].clone(),
        })
        this.$call.sendCamera(CAMERA_STATUS.CAMERA_NONE)
      }

      this.$call.sendScreenSharing(false)
      this.$eventBus.$emit('streamctl:hide', false)
      this.isScreenSharing = false
    },
  },
}
</script>
