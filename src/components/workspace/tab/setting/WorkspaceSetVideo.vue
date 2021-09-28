<template>
  <section class="setting-section">
    <div class="setting-section__title main">
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
    <div class="setting-section__body horizon">
      <figure v-if="!isMobileSize" class="setting__figure video">
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
      <figure class="setting__figure slidecontainer">
        <p class="setting__label">
          FPS
        </p>
        <range-slider
          v-if="videoFPS"
          :value.sync="videoFPS"
          :min="1"
          :max="30"
          :initValue="videoFPS"
        ></range-slider>

        <p v-if="isMobileSize" class="fps-value-mobile">
          {{ videoFPS }}
        </p>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import RangeSlider from 'RangeSlider'
import { mapGetters, mapActions } from 'vuex'
import { resolution } from 'utils/settingOptions'
import { getUserMedia } from 'utils/deviceCheck'
import _ from 'lodash'

export default {
  components: {
    RSelect,
    RangeSlider,
  },
  data() {
    return {
      resolutions: resolution,
      stream: null,
      videoId: '',
      videoQuality: '',
      invalid: false,
      checking: false,
      videoFPS: null,
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
          width: parseInt(size[0], 10),
          height: parseInt(size[1], 10),
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
    videoFPS: [
      function(fps) {
        this.setFPS(Number.parseInt(fps, 10))
      },
      _.debounce(function(fps) {
        if (this.stream) {
          const videoTrack = this.stream.getVideoTracks()[0]
          videoTrack.applyConstraints({
            frameRate: {
              max: Number.parseInt(fps, 10),
            },
          })
        }
      }, 200),
    ],
  },
  methods: {
    ...mapActions(['setDevices']),
    setVideo(deviceId) {
      this.setDevices({
        video: { deviceId: deviceId },
      })
      window.myStorage.setDevice('video', 'deviceId', deviceId)
    },
    setQuality(quality) {
      this.setDevices({
        video: { quality: quality },
      })
      window.myStorage.setDevice('video', 'quality', quality)
    },
    setFPS(fps) {
      this.setDevices({
        video: { fps: fps },
      })
      window.myStorage.setDevice('video', 'fps', fps)
    },
    async initStream() {
      if (this.checking) return
      if (this.videoDevices.length === 0) return

      this.checking = true
      this.resetStream()
      this.invalid = false

      this.$nextTick(async () => {
        try {
          await this.getStream()
          this.logStreamInfo()
          this.checking = false
        } catch (err) {
          this.initStreamErrorHandler(err)
        }
      })
    },
    resetStream() {
      if (this.stream) {
        this.stream.getTracks().forEach(track => {
          track.stop()
        })
        this.stream = null
      }
    },
    async getStream() {
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
        frameRate: {
          max: this.videoFPS,
        },
      }
      this.stream = await getUserMedia({
        audio: false,
        video: videoConstraint,
      })
    },
    logStreamInfo() {
      const track = this.stream.getVideoTracks()[0]
      const settings = track.getSettings()
      const capability = track.getCapabilities()
      this.logger('call', `resolution::${settings.width}X${settings.height}`)
      this.debug('call::setting::', settings)
      this.debug('call::capability::', capability)
    },
    initStreamErrorHandler(err) {
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
            this.setQuality(resolution[idx - 1].value)
          }
        }
        if (err.name === 'NotReadableError') {
        }
        return err.name
      } else {
        this.invalid = true
      }
    },
  },
  mounted() {
    this.videoId = this.video['deviceId']
    this.videoQuality = this.video['quality']

    if (this.video['fps']) {
      this.videoFPS = Number.parseInt(this.video['fps'], 10)
    } else {
      this.videoFPS = 30
    }

    this.initStream()
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
