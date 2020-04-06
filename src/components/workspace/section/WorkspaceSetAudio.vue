<template>
  <div>
    <div>음성 설정</div>
    <div>
      <span>입력 장치</span>
      <select v-model="selectAudio" @change="handleInputAudioStream">
        <option
          v-for="device in audioDevices"
          :key="device.deviceId"
          :value="device.deviceId"
          >{{ device.label }}</option
        >
      </select>
    </div>
    <div>
      <span>출력 장치</span>
      <select v-model="selectOutput" @change="setAudioOutput">
        <option
          v-for="device in outputDevices"
          :key="device.deviceId"
          :value="device.deviceId"
          >{{ device.label }}</option
        >
      </select>
      <audio
        ref="audioComponent"
        :srcObject.prop="audioStream"
        autoplay
      ></audio>
    </div>

    <div>
      <div>
        마이크 문제가 있나요? 테스트를 시작하고 아무 말이나 해보세요. 다시
        들려드리겠습니다.
      </div>
      <el-button @click="toggleMicTestMode">{{ micTestWord }}</el-button>
      <span>마이크 아이콘</span>
      <meter ref="audioMeter" min="0" max="100" :value="soundWidth"></meter>
    </div>
  </div>
</template>
<script>
import SoundMeter from 'plugins/remote/soundmeter'
export default {
  data: function() {
    return {
      videoDevices: [],
      audioDevices: [],

      audioContext: null,
      audioSoundMeter: null,
      audioStream: null,
      audioSoundVolume: 0,

      inputAudioVolume: 1,
      outputAudioVolume: 1,

      selectOutput: null,
      selectVideo: null,
      selectAudio: null,

      outputDevices: [],

      micTestMode: false,
      micTestWord: '마이크 테스트',
    }
  },
  mounted() {
    this.init()
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
  },
  created() {},
  computed: {
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  methods: {
    init() {
      navigator.mediaDevices
        .enumerateDevices()
        .then(devices => {
          console.log(devices)
          devices.forEach(device => {
            if (device.kind === 'videoinput') {
              this.videoDevices.push(device)
            } else if (device.kind === 'audioinput') {
              this.audioDevices.push(device)
            } else if (device.kind === 'audiooutput') {
              this.outputDevices.push(device)
            }
          })
        })
        .catch(err => {
          console.log(err)
        })
    },

    handleInputAudioStream() {
      console.log(this.selectAudio)

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

      navigator.mediaDevices
        .getUserMedia(constraints)
        .then(stream => connectSoundMeter(stream))
    },
    changeInputAudioVolume() {
      //this.$refs['audioComponent'].volume = this.inputAudioVolume
    },
    changeOutputAudioVolume() {
      this.$refs['audioComponent'].volume = this.outputAudioVolume
    },
    setAudioOutput() {
      this.$nextTick(() => {
        this.$refs['audioComponent'].setSinkId(this.selectAudio).then(() => {})
      })
    },
    toggleMicTestMode() {
      this.micTestMode = !this.micTestMode
      this.micTestWord = this.micTestMode
        ? '마이크 테스트중...'
        : '마이크 테스트'
    },
  },
}
</script>
<style lang="scss"></style>
