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
      if (!ALLOW_NO_DEVICE && videoSource === false && audioSource === false) {
        throw 'nodevice'
      }
      if (!ALLOW_NO_AUDIO && audioSource === false) {
        throw 'nodevice'
      }
      return { videoSource, audioSource }
    },
  },
}
