<template>
  <section>
    <div class="workspace-setting-title">입출력 장치</div>
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
    }
  },
  props: {
    audioInputDevices: null,
    audioOutputDevices: null,
    selectAudioInput: null,
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
.mic-radius {
  border-radius: 50%;
}
</style>
