<template>
  <section>
    <div class="workspace-setting-title">비디오 및 오디오 설정</div>
    <div class="workspace-setting-horizon-wrapper">
      <div class="workspace-setting-vertical-wrapper">
        <span class="workspace-setting-label">입력 장치</span>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="setAudioInputDevice"
          :options="audioInputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </div>

      <div class="workspace-setting-vertical-wrapper">
        <span class="workspace-setting-label">출력 장치</span>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="setAudioOutputDevice"
          :options="audioOutputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </div>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'

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
    setAudioInputDevice(newInputAudioDevice) {
      this.$emit('selectedAudioInputDevice', newInputAudioDevice)
    },

    setAudioOutputDevice(newOutputAudioDevice) {
      this.$emit('selectedOutputAudioDevice', newOutputAudioDevice)
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
  justify-content: start;
}

.align-item {
  margin-right: 10px;
}

.align-item:nth-child(2n) {
  margin-right: 20px;
}

.button {
  width: 120px;
  height: 38px;
  background: linear-gradient(90deg, rgb(0, 84, 247) 0%, rgb(20, 95, 198) 100%);
  border-radius: 2px;
  color: rgb(255, 255, 255);
  font-family: NotoSansCJKkr-Medium;
  font-size: 14px;
  font-weight: 500;
}
</style>
