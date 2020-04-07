<template>
  <section>
    <div class="workspace-setting-horizon-wrapper">
      <div>
        <span class="workspace-setting-label">입력 장치</span>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="handleInputAudioStream"
          :options="audioInputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </div>

      <span>
        <span class="workspace-setting-label">출력 장치</span>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="setAudioOutput"
          :options="audioOutputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
        <audio
          ref="audioComponent"
          :srcObject.prop="audioStream"
          autoplay
        ></audio>
      </span>
    </div>
    <div class="workspace-setting-title">마이크 테스트</div>
    <div class="workspace-setting-label">
      마이크 문제가 있나요? 테스트를 시작하고 아무 말이나 해보세요. 다시
      들려드리겠습니다.
    </div>
    <div class="workspace-setting-horizon-wrapper align-center">
      <el-button @click="toggleMicTestMode">{{ micTestWord }}</el-button>
      <span>
        <toggle-button
          :description="''"
          :size="24"
          :active="micTestMode"
          :activeSrc="require('assets/image/setting/mdpi_icn_mic.svg')"
          :inactiveSrc="require('assets/image/setting/icon_mic_mute.svg')"
          @action="micTestMode = !micTestMode"
        ></toggle-button>
      </span>

      <div style="width:300px">
        <progress-bar :value="soundWidth" :max="progress.max"></progress-bar>
      </div>
    </div>
  </section>
</template>
<script>
import SoundMeter from 'plugins/remote/soundmeter'
import RSelect from 'RemoteSelect'
import ToggleButton from 'ToggleButton'
import ProgressBar from 'ProgressBar'
export default {
  data: function() {
    return {
      videoDevices: [],
      audioDevices: [],

      audioContext: null,
      audioSoundMeter: null,
      audioStream: null,
      audioSoundVolume: 0,

      selectOutput: null,
      selectVideo: null,
      selectAudio: null,

      micTestMode: false,
      micTestWord: '마이크 테스트',
      progress: {
        max: 100,
        value: 0,
      },
    }
  },
  props: {
    audioInputDevices: null,
    audioOutputDevices: null,
  },
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
  },
  created() {},
  components: {
    RSelect,
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
  methods: {
    handleInputAudioStream(newValue) {
      this.selectAudio = newValue.deviceId
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
    setAudioOutput(newValue) {
      //local 스토리지에 저장필요함
      this.selectOutput = newValue.deviceId
      console.log(this.selectAudio)
      this.$nextTick(() => {
        this.$refs['audioComponent'].setSinkId(this.selectAudio).then(() => {})
      })
    },
    toggleMicTestMode() {
      this.micTestMode = !this.micTestMode
    },
  },
}
</script>
<style lang="scss" scoped>
.sub-title {
  width: 135px;
  height: 22px;
  color: rgb(250, 250, 250);
  font-size: 15px;
  font-family: NotoSansCJKkr-Bold;
  font-weight: bold;
  letter-spacing: 0px;
}

.align-center {
  align-items: center;
}
</style>
