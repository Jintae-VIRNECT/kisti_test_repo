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
          :defaultValue="defaultMic"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">출력 장치</p>
        <r-select
          ref="settingOutput"
          class="setting__r-selecter"
          v-on:changeValue="setSpeaker"
          :options="speakers"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="defaultSpeaker"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapState } from 'vuex'
export default {
  props: {
    mics: null,
    speakers: null,
    defaultMic: null,
    defaultSpeaker: null,
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
    ...mapState({
      mic: state => state.settings.mic,
      speaker: state => state.settings.speaker,
    }),
  },
  methods: {
    setMic(newMic) {
      this.$store.dispatch('setMic', newMic.deviceId)
    },
    setSpeaker(newSpeaker) {
      this.$store.dispatch('setSpeaker', newSpeaker.deviceId)
    },
  },
}
</script>
