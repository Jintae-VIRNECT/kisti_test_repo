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
    /**
     * 사용할 장치 id를 반환
     *
     * @returns {Object}
     */
    async getDeviceId() {
      //장치 접근 및 id 획득
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
          return {
            videoSource: false,
            audioSource: false,
          }
        }
      }

      //오디오 장비가 없는경우
      if (!ALLOW_NO_AUDIO && audioSource === false) {
        throw 'nodevice'
      }
      return { videoSource, audioSource }
    },
  },
}
