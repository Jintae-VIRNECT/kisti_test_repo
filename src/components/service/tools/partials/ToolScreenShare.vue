<template>
  <tool-button
    :text="$t('service.tool_screen_share')"
    :active="share"
    :isActive="share"
    :disabled="false"
    :src="require('assets/image/call/ic_sharing_on.svg')"
    :activeSrc="require('assets/image/call/ic_sharing_off.svg')"
    @click="toggleShare"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { ACTION } from 'configs/view.config'
import { CAMERA as CAMERA_STATUS } from 'configs/device.config'
import { mapGetters } from 'vuex'
export default {
  name: 'ToolScreenShare',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      STREAM_POINTING: ACTION.STREAM_POINTING,
      share: false,
    }
  },
  computed: {
    ...mapGetters([
      'allowPointing',
      'viewForce',
      'myInfo',
      'myTempStream',
      'settingInfo',
      'mainView',
    ]),
  },
  methods: {
    async toggleShare() {
      try {
        if (this.share) {
          this.stopScreenSharing()
        } else {
          await this.startScreenShare()
        }
      } catch (e) {
        console.error(e)
        this.share = false
      }
    },
    async startScreenShare() {
      const displayStream = await this.getDisplayStream()

      if (displayStream && displayStream.getVideoTracks().length > 0) {
        if (this.myInfo.cameraStatus !== CAMERA_STATUS.CAMERA_NONE) {
          this.$call.replaceTrack(
            displayStream.getVideoTracks()[0],
            this.mainView.stream,
          )
        } else {
          this.$call.rePublish(displayStream.getVideoTracks()[0])
          this.$eventBus.$emit('streamctl:hide', true)
          //모바일에서 대응할 수 있는 시간을 주기위한 딜레이
          await new Promise(r => setTimeout(r, 500))
        }

        this.$call.sendScreenSharing(true)
        this.share = true
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
      //종료 체크
      if (this.myTempStream) {
        this.$call.restoreMyStream(this.myInfo.video)
      } else {
        this.$call.sendCamera(CAMERA_STATUS.CAMERA_NONE)
        this.$call.rePublish()
      }

      this.$call.sendScreenSharing(false)
      this.$eventBus.$emit('streamctl:hide', false)
      this.share = false
    },
  },
}
</script>
