<template>
  <section class="setting-section">
    <p class="setting__title">
      마이크 테스트
    </p>
    <p class="setting__label label-margin-bottom">
      마이크 문제가 있나요? 테스트를 시작하고 아무 말이나 해보세요. 다시
      들려드리겠습니다.
    </p>
    <div class="setting-horizon-wrapper align-center">
      <div class="mic-item">
        <button class="btn" @click="toggleMicTestMode">
          {{ micTestWord }}
        </button>
      </div>
      <div class="mic-item">
        <toggle-button
          class="mic-radius"
          :description="''"
          :size="24"
          :active="micTestMode"
          :activeSrc="require('assets/image/setting/ic_mic.svg')"
          :inactiveSrc="require('assets/image/setting/ic_mic_mute.svg')"
          @action="micTestMode = !micTestMode"
        ></toggle-button>
      </div>

      <div class="mic-item progress">
        <progress-bar :value="soundWidth" :max="progress.max"></progress-bar>
      </div>
      <audio
        ref="audioComponent"
        :srcObject.prop="audioStream"
        autoplay
      ></audio>
    </div>
  </section>
</template>
<script>
import SoundMeter from 'plugins/remote/soundmeter'
import ToggleButton from 'ToggleButton'
import ProgressBar from 'ProgressBar'
export default {
  data: function() {
    return {
      audioStream: null,
      audioSoundVolume: 0,

      micTestMode: false,
      micTestWord: '마이크 테스트',
      progress: {
        max: 100,
        value: 0,
      },
    }
  },
  props: {
    selectAudioInput: null,
  },
  created() {},
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
    this.$refs['audioComponent'].muted = true
  },
  components: {
    ToggleButton,
    ProgressBar,
  },
  computed: {
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  watch: {
    selectAudioInput: function(newDevice) {
      this.handleInputAudioStream(newDevice)
    },
  },
  methods: {
    handleInputAudioStream(selectedDevice) {
      this.selectAudio = selectedDevice.deviceId
      const constraints = {
        video: false,
        audio: {
          deviceId: this.selectAudio,
        },
      }

      const connectSoundMeter = stream => {
        this.audioStream = stream
        const soundMeter = new SoundMeter(this.audioContext)
        soundMeter.connectToSource(stream, e => {
          if (e) {
            alert(e)
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
          .then(stream => connectSoundMeter(stream))
      }
    },
    toggleMicTestMode() {
      this.micTestMode = !this.micTestMode
      this.$refs['audioComponent'].muted = !this.micTestMode
    },
  },
}
</script>
<style lang="scss" scoped>
.align-center {
  align-items: center;
  justify-content: start;
}

.label-margin-bottom {
  margin-bottom: 40px;
}
.mic-radius {
  border-radius: 50%;
}

.mic-item {
  flex: 0;
  margin: 0 10px;
  &:first-child {
    margin-left: 0;
  }
  &.progress {
    flex: 1;
  }
}
</style>
