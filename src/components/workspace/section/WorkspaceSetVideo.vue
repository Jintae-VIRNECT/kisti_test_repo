<template>
  <section class="setting-section">
    <div class="setting-section__title">{{ `영상 장치 선택` }}</div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_camera') }}</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setVideo"
          :options="videoDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="videoId"
        ></r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">
          {{ '입력 해상도' }}
        </p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setQuality"
          :options="resolutions"
          value="value"
          text="text"
          :defaultValue="videoQuality"
        >
        </r-select>
      </figure>
    </div>
    <div class="setting-section__body">
      <figure class="setting__figure">
        <p class="setting__label">{{ '영상 미리보기' }}</p>
        <div class="setting-video">
          <video
            v-if="stream"
            :srcObject.prop="stream"
            autoplay
            playsinline
            loop
          ></video>
          <div v-else class="setting-video__empty">
            <img src="~assets/image/ic_nocamera.svg" />
          </div>
        </div>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
import { resolution } from 'utils/settingOptions'
import { getUserMedia } from 'utils/deviceCheck'

export default {
  components: {
    RSelect,
  },
  data() {
    return {
      resolutions: resolution,
      stream: null,
      currentVideo: null,
    }
  },
  props: {
    videoDevices: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    ...mapGetters(['video']),
    videoId() {
      return this.video['deviceId']
    },
    videoQuality() {
      return this.video['quality']
    },
    currentQuality() {
      const current = resolution.find(
        resol => resol.value === this.videoQuality,
      ).resolution
      if (current === undefined) {
        return {
          width: 1280,
          height: 720,
        }
      }
      const size = current.split('X')
      return {
        width: parseInt(size[0]),
        height: parseInt(size[1]),
      }
    },
  },
  watch: {
    'videoDevices.length': 'initStream',
    videoQuality: 'initStream',
    videoId: 'initStream',
  },
  methods: {
    ...mapActions(['setDevices']),
    setVideo(newDevice) {
      this.setDevices({
        video: { deviceId: newDevice.deviceId },
      })
      this.currentVideo = newDevice.deviceId
      this.$localStorage.setDevice('video', 'deviceId', newDevice.deviceId)
    },
    setQuality(quality) {
      this.setDevices({
        video: { quality: quality.value },
      })
      this.$localStorage.setDevice('video', 'quality', quality.value)
    },
    async initStream() {
      if (this.videoDevices.length === 0) return
      const mediaRes = await getUserMedia(false, {
        deviceId: {
          ideal: this.videoId,
        },
        width: {
          ideal: this.currentQuality.width,
        },
        height: {
          ideal: this.currentQuality.height,
        },
      })
      if (typeof mediaRes === 'string') {
        if (mediaRes.toLowerCase().trim() === 'overconstrainederror') {
          const idx = resolution.findIndex(
            resol => resol.value === this.videoQuality,
          )
          if (idx > 0) {
            this.setQuality(resolution[idx - 1])
          }
        }
        return
      }
      this.stream = mediaRes
      const track = this.stream.getVideoTracks()[0]
      const settings = track.getSettings()
      const capability = track.getCapabilities()
      this.logger('call', `resolution::${settings.width}X${settings.height}`)
      this.debug('call::setting::', settings)
      this.debug('call::capability::', capability)
    },
  },
  mounted() {
    this.initStream()
  },
}
</script>
