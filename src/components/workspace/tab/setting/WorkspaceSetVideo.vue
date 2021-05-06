<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ $t('workspace.setting_video_choice') }}
    </div>
    <div class="setting-section__body horizon">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_camera') }}</p>
        <r-select
          class="setting__r-selecter"
          :class="{ checking: checking }"
          :disabled="checking"
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
          :class="{ checking: checking }"
          :disabled="checking"
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
        <p class="setting__label" :class="{ warning: !!invalid }">
          {{ sumnailTitle }}
        </p>
        <div class="setting-video">
          <video
            v-if="stream"
            :srcObject.prop="stream"
            autoplay
            muted
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
      invalid: false,
      checking: false,
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
    sumnailTitle() {
      if (this.invalid === false) {
        return this.$t('workspace.setting_video_preview')
      } else if (this.invalid === 'OverconstrainedError') {
        return this.$t('workspace.setting_video_preview_invalid1')
      } else {
        return this.$t('workspace.setting_video_preview_invalid2')
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
      window.myStorage.setDevice('video', 'deviceId', deviceId)
    },
    setQuality(quality) {
      this.setDevices({
        video: { quality: quality },
      })
      window.myStorage.setDevice('video', 'quality', quality)
    },
    async initStream() {
      if (this.checking) return
      if (this.videoDevices.length === 0) return
      this.checking = true
      if (this.stream) {
        this.stream.getTracks().forEach(track => {
          track.stop()
        })
        this.stream = null
      }
      this.invalid = false
      this.$nextTick(async () => {
        try {
          const videoConstraint = {
            width: {
              exact: this.currentQuality.width,
            },
            height: {
              exact: this.currentQuality.height,
            },
            deviceId: {
              exact: this.videoId,
            },
          }
          this.stream = await getUserMedia({
            audio: false,
            video: videoConstraint,
          })
          const track = this.stream.getVideoTracks()[0]
          const settings = track.getSettings()
          const capability = track.getCapabilities()
          this.logger(
            'call',
            `resolution::${settings.width}X${settings.height}`,
          )
          this.debug('call::setting::', settings)
          this.debug('call::capability::', capability)
          this.checking = false
        } catch (err) {
          console.error(err)
          this.stream = null
          this.checking = false
          if (typeof err === 'object' && err.name) {
            this.invalid = err.name
            if (err.name === 'OverconstrainedError') {
              const idx = resolution.findIndex(
                resol => resol.value === this.videoQuality,
              )
              if (idx > 0) {
                // this.videoQuality = resolution[idx - 1].value
                this.setQuality(resolution[idx - 1].value)
              }
              // if (err.constraint === 'deviceId') {
              //   this.invalid = true
              // }
            }
            if (err.name === 'NotReadableError') {
            }
            return err.name
          } else {
            this.invalid = true
          }
        }
      })
    },
  },
  mounted() {
    this.initStream()
    this.videoId = this.video['deviceId']
    this.videoQuality = this.video['quality']
  },
  beforeDestroy() {
    if (this.stream) {
      this.stream.getTracks().forEach(track => {
        track.stop()
      })
      this.stream = null
    }
  },
}
</script>
