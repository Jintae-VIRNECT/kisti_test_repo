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
          size="2.429rem"
          :active="micTestMode"
          :activeSrc="require('assets/image/setting/ic_mic.svg')"
          :inactiveSrc="require('assets/image/setting/ic_mic_mute.svg')"
          @action="toggleMicTestMode"
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
      <!-- for camera permission -->
      <!-- <video autoplay style="width:0px; height:0px"></video> -->
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

      //for camera permission.
      videoStream: null,
      audioSoundVolume: 0,

      micTestMode: false,
      micTestWord: '마이크 테스트',
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
    ...mapGetters(['mic']),
    mic() {
      return this.mic['deviceId']
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
    mic: {
      handler(newMic) {
        this.handleInputAudioStream(newMic)
      },
      immediate: true,
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
          .then(stream => {
            connectSoundMeter(stream)
            //for camera permission.
            this.videoStream = stream
          })
          .catch(err => console.error(err))
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
  margin-bottom: 2.857rem;
}
.mic-radius {
  border-radius: 50%;
}

.mic-item {
  flex: 0;
  margin: 0 0.714rem;
  &:first-child {
    margin-left: 0;
  }
  &.progress {
    flex: 1;
  }
}
</style>
