import { mapGetters } from 'vuex'
import { getInputDevice } from 'utils/deviceCheck'
import { ALLOW_NO_AUDIO, ALLOW_NO_DEVICE } from 'configs/env.config'
export default {
  data() {
    return {
      clicked: false,
    }
  },
  computed: {
    ...mapGetters(['settingInfo']),
  },
  methods: {
    async getDeviceId() {
      const { videoSource, audioSource } = await getInputDevice(
        {
          video: true,
          audio: true,
        },
        {
          camera: this.settingInfo.video,
          mic: this.settingInfo.mic,
        },
      )
      if (videoSource === false && audioSource === false) {
        if (!ALLOW_NO_DEVICE) {
          throw 'nodevice'
        } else {
          return false
        }
      }
      if (!ALLOW_NO_AUDIO && audioSource === false) {
        throw 'nodevice'
      }
      return { videoSource, audioSource }
    },
  },
}
