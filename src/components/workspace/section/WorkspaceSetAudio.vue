<template>
  <section class="setting-section">
    <div class="setting__title">{{ $t('workspace.setting_inout_device') }}</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_input_device') }}</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setMic"
          :options="micDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="micId"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">
          {{ $t('workspace.setting_output_device') }}
        </p>
        <r-select
          ref="settingOutput"
          class="setting__r-selecter"
          v-on:changeValue="setSpeaker"
          :options="speakerDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="speakerId"
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
    ...mapGetters(['mic', 'speaker']),
    micId() {
      return this.mic['deviceId']
    },
    speakerId() {
      return this.speaker['deviceId']
    },
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  methods: {
    ...mapActions(['setDevices']),
    setMic(mic) {
      this.setDevices({
        mic: { deviceId: mic.deviceId },
      })
      this.$localStorage.setDevice('mic', 'deviceId', mic.deviceId)
    },
    setSpeaker(speaker) {
      this.setDevices({
        speaker: { deviceId: speaker.deviceId },
      })
      this.$localStorage.setDevice('speaker', 'deviceId', speaker.deviceId)
    },
  },
}
</script>
