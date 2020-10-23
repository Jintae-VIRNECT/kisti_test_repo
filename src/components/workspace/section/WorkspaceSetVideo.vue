<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ $t('workspace.setting_video_choice') }}
    </div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_camera') }}</p>
        <r-select
          class="setting__r-selecter"
          :options="videoDevices"
          value="deviceId"
          text="label"
          :selectedValue.sync="videoId"
        ></r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_video_resolution') }}
        </p>
        <r-select
          class="setting__r-selecter"
          :options="resolutions"
          value="value"
          text="text"
          :selectedValue.sync="videoQuality"
        >
        </r-select>
      </figure>
    </div>
    <div class="setting-section__body">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ this.$t('workspace.setting_video_preview') }}
        </p>
        <div class="setting-video">
          <video
            v-if="stream"
            :srcObject.prop="stream"
            autoplay
            playsinline
            loop
          ></video>
          <div v-else class="setting-video__empty">
            <img src="~assets/image/workspace/ic_nocamera.svg" />
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
      videoId: '',
      videoQuality: '',
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
    // videoQuality() {
    //   return this.video['quality']
    // },
    currentQuality() {
      const idx = resolution.findIndex(
        resol => resol.value === this.videoQuality,
      )
      if (idx < 0) {
        return {
          width: 1280,
          height: 720,
        }
      } else {
        const size = resolution[idx].resolution.split('X')
        return {
          width: parseInt(size[0]),
          height: parseInt(size[1]),
        }
      }
    },
  },
  watch: {
    'videoDevices.length': 'initStream',
    videoQuality(quality) {
      this.setQuality(quality)
      this.initStream()
    },
    videoId(id) {
      this.setVideo(id)
      this.initStream()
    },
  },
  methods: {
    ...mapActions(['setDevices']),
    setVideo(deviceId) {
      this.setDevices({
        video: { deviceId: deviceId },
      })
      this.currentVideo = deviceId
      this.$localStorage.setDevice('video', 'deviceId', deviceId)
    },
    setQuality(quality) {
      this.setDevices({
        video: { quality: quality },
      })
      this.$localStorage.setDevice('video', 'quality', quality)
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
        if (mediaRes.toLowerCase().trim() === 'notreadableerror') {
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
  created() {
    this.initStream()
    this.videoId = this.video['deviceId']
    this.videoQuality = this.video['quality']
  },
}
</script>
