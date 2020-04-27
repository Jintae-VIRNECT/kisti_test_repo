<template>
  <section>
    <div class="setting__title">입출력 장치</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">입력 장치</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setAudioInputDevice"
          :options="audioInputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">출력 장치</p>
        <r-select
          ref="settingOutput"
          class="setting__r-selecter"
          v-on:changeValue="setAudioOutputDevice"
          :options="audioOutputDevices"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
export default {
  data() {
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
    defaultInputAudio: null,
    defaultOuputAudio: null,
  },
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
  mounted() {
    this.audioContext = new (window.AudioContext || window.webkitAudioContext)()
  },
}
</script>
