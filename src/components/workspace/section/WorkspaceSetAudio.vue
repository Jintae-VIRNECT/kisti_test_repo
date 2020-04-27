<template>
  <section>
    <div class="setting__title">입출력 장치</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">입력 장치</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setMic"
          :options="mics"
          :value="'deviceId'"
          :text="'label'"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">출력 장치</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setSpeaker"
          :options="speakers"
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
  data: function() {
    return {}
  },
  props: {
    mics: null,
    speakers: null,
    defaultInputAudio: null,
    defaultOuputAudio: null,
  },
  mounted() {},
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
    setMic(newInputAudioDevice) {
      this.$emit('selectedAudioInputDevice', newInputAudioDevice)
    },

    setSpeaker(newOutputAudioDevice) {
      this.$emit('selectedOutputAudioDevice', newOutputAudioDevice)
    },
  },
}
</script>
