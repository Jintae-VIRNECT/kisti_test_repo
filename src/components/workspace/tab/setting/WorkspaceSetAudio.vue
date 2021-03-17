<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{
        isSafari
          ? $t('workspace.setting_input_device')
          : $t('workspace.setting_inout_device')
      }}
    </div>
    <div class="setting-section__body horizon">
      <figure class="setting__figure">
        <p class="setting__label" v-if="!isSafari">
          {{ $t('workspace.setting_input_device') }}
        </p>
        <r-select
          class="setting__r-selecter"
          :options="micDevices"
          value="deviceId"
          text="label"
          :selectedValue.sync="micId"
        >
        </r-select>
      </figure>

      <figure class="setting__figure" v-if="!isSafari">
        <p class="setting__label">
          {{ $t('workspace.setting_output_device') }}
        </p>
        <r-select
          ref="settingOutput"
          class="setting__r-selecter"
          :options="speakerDevices"
          value="deviceId"
          text="label"
          :selectedValue.sync="speakerId"
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
  components: {
    RSelect,
  },
  data() {
    return {
      speakerId: '',
      micId: '',
    }
  },
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
  computed: {
    ...mapGetters(['mic', 'speaker']),
    soundWidth() {
      if (this.micTestMode) {
        return parseInt(this.audioSoundVolume * 100)
      } else {
        return 0
      }
    },
  },
  watch: {
    micId(id) {
      this.setMic(id)
    },
    speakerId(id) {
      this.setMic(id)
    },
  },
  methods: {
    ...mapActions(['setDevices']),
    setMic(deviceId) {
      this.setDevices({
        mic: { deviceId: deviceId },
      })
      window.myStorage.setDevice('mic', 'deviceId', deviceId)
    },
    setSpeaker(deviceId) {
      this.setDevices({
        speaker: { deviceId: deviceId },
      })
      window.myStorage.setDevice('speaker', 'deviceId', deviceId)
    },
  },
  created() {
    if (this.mic['deviceId']) {
      this.micId = this.mic['deviceId']
    }
    if (this.speaker['deviceId']) {
      this.speakerId = this.speaker['deviceId']
    }
  },
}
</script>
