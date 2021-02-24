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
// import { VIEW } from 'configs/view.config'
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
    ]),
  },
  methods: {
    async toggleShare() {
      if (this.share) {
        this.stopScreenSharing()
      } else {
        await this.getScreenStream()
      }
      this.share = !this.share
    },
    async getScreenStream() {
      //pc자체에서 나오는 소리도 오디오 트랙에 넣어서..? 믹싱해서 보내야할듯?

      if (
        !navigator.mediaDevices ||
        !navigator.mediaDevices['getDisplayMedia']
      ) {
        throw 'NotSupportDisplayError'
      } else {
        const size = this.settingInfo.quality.split('X')
        const video = {
          width: parseInt(size[0]),
          height: parseInt(size[1]),
        }
        //@TODO: 스트림 못가지고오면 빠꾸
        const displayStream = await navigator.mediaDevices.getDisplayMedia({
          audio: true,
          video: video,
        })

        displayStream.getVideoTracks()[0].onended = () => {
          this.stopScreenSharing()
          this.share = false
        }

        //카메라가 꺼진 상태에서 스트림을 체크해봐야겠음
        if (this.myInfo.cameraStatus !== CAMERA_STATUS.CAMERA_NONE) {
          console.log('replace track 호출')
          this.$call.replaceTrack(
            displayStream.getVideoTracks()[0],
            this.mainView.stream,
          )
        } else {
          console.log('rePublish 호출')
          this.$call.rePublish(displayStream.getVideoTracks()[0])
          this.$call.sendCamera(CAMERA_STATUS.CAMERA_ON)
        }

        this.$call.sendScreenSharing(true)
        this.$eventBus.$emit('toggleScreenShare', true)
      }
    },
    stopScreenSharing() {
      //보존된 스트림이 없으면 리스토어 말고 그냥 리퍼블리시
      if (this.myTempStream) {
        this.$call.restoreMyStream(this.myInfo.video)
      } else {
        this.$call.sendCamera(CAMERA_STATUS.CAMERA_NONE)
        this.$call.rePublish()
      }

      this.$call.sendScreenSharing(false)
      this.$eventBus.$emit('video:share', false)
    },
  },
}
</script>
