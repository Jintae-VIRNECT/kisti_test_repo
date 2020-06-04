<template>
  <section class="setting-section">
    <div class="setting__title">입출력 장치</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">입력 장치</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setMic"
          :options="micDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="micDevice"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">출력 장치</p>
        <r-select
          ref="settingOutput"
          class="setting__r-selecter"
          v-on:changeValue="setSpeaker"
          :options="speakerDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="speakerDevice"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
export default {
  props: {
    micDevices: {
      type: Array,
      default: () => [],
    },
    speakerDevices: {
      type: Array,
      default: () => [],
    },
  },
  components: {
    RSelect,
  },
  computed: {
    ...mapGetters(['micDevice', 'speakerDevice']),
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  methods: {
    ...mapActions(['setMicDevice', 'setSpeakerDevice']),
    setMic(newMic) {
      this.setMicDevice(newMic.deviceId)
    },
    setSpeaker(newSpeaker) {
      this.setSpeakerDevice(newSpeaker.deviceId)
    },
  },
  created() {
    const micDefault = localStorage.getItem('micDevice')
    if (micDefault) {
      this.setMic(micDefault)
    }
    const speakerDefault = localStorage.getItem('speakerDevice')
    if (speakerDefault) {
      this.setSpeaker(speakerDefault)
    }
  },
}
</script>
