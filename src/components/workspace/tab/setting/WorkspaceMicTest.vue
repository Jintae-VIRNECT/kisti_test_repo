<template>
  <section class="setting-section">
    <p class="setting-section__title">
      {{ $t('workspace.setting_mic_test') }}
    </p>
    <p class="setting__label label-margin">
      {{ $t('workspace.setting_mic_test_description') }}
    </p>
    <div class="setting-section__body horizon">
      <div class="mic-item">
        <button class="btn" @click="toggleMicTestMode">
          {{ $t('workspace.setting_mic_test') }}
        </button>
      </div>
      <div class="mic-item__progress">
        <toggle-button
          class="mic-radius"
          description=""
          size="2.429rem"
          :active="micTestMode"
          :activeSrc="require('assets/image/setting/ic_mic.svg')"
          :inactiveSrc="require('assets/image/setting/ic_mic_mute.svg')"
          @action="toggleMicTestMode"
        ></toggle-button>
        <progress-bar :value="soundWidth" :max="progress.max"></progress-bar>
      </div>
      <audio
        ref="audioComponent"
        :srcObject.prop="audioStream"
        autoplay
        playsinline
      ></audio>
    </div>
  </section>
</template>
<script>
import SoundMeter from 'plugins/remote/soundmeter'
import ToggleButton from 'ToggleButton'
import ProgressBar from 'ProgressBar'
import { mapGetters } from 'vuex'
export default {
  data: function() {
    return {
      audioStream: null,

      audioSoundVolume: 0,

      micTestMode: false,
      progress: {
        max: 100,
        value: 0,
      },
    }
  },
  props: {},
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    this.$refs['audioComponent'].muted = true
  },
  components: {
    ToggleButton,
    ProgressBar,
  },
  computed: {
    ...mapGetters(['mic', 'speaker']),
    micId() {
      return this.mic['deviceId']
    },
    speakerId() {
      return this.speaker['deviceId']
    },
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  watch: {
    micId: {
      handler(newMic) {
        this.handleInputAudioStream(newMic)
      },
      immediate: true,
    },
    speakerId: {
      handler(newSpeaker) {
        this.setOutputDevice(newSpeaker)
      },
    },
  },
  methods: {
    handleInputAudioStream(selectedDevice) {
      const constraints = {
        video: true,
        audio: {
          deviceId: selectedDevice,
        },
      }

      const connectSoundMeter = stream => {
        this.audioStream = stream
        const soundMeter = new SoundMeter(this.audioContext)
        soundMeter.connectToSource(stream, e => {
          if (e) {
            console.error(e)
            return
          }
          setInterval(() => {
            this.audioSoundVolume = soundMeter.instant.toFixed(2)
          }, 200)
        })
      }
      if (this.audioContext !== null) {
        navigator.mediaDevices
          .getUserMedia(constraints)
          .then(stream => {
            connectSoundMeter(stream)
          })
          .catch(err => console.error(err))
      }
    },
    toggleMicTestMode() {
      this.micTestMode = !this.micTestMode
      this.$refs['audioComponent'].muted = !this.micTestMode
    },
    setOutputDevice(newSpeaker) {
      this.$refs['audioComponent'].setSinkId(newSpeaker)
    },
  },
  beforeDestroy() {
    if (this.audioStream) {
      this.audioStream.getTracks().forEach(track => {
        track.stop()
      })
      this.audioStream = null
    }
  },
}
</script>
<style lang="scss">
.label-margin {
  margin: -0.7143rem 0 2.857rem 0;
}

.mic-item {
  flex: 0;
  margin-right: 0.714rem;
  margin-left: 0;
}
.mic-item__progress {
  display: flex;
  flex: 1;
  align-items: center;
  margin-right: 2.286rem;
}
.mic-radius {
  flex: 0 1 auto;
  margin-right: 15px;
  border-radius: 50%;
}
</style>
