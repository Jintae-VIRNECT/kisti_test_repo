<template>
  <section class="setting-section">
    <div class="setting__title">{{ $t('workspace.setting_inout_device') }}</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_input_device') }}</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setMic"
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
          @changeValue="setSpeaker"
          :options="speakerDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="speakerId"
        >
        </r-select>
      </figure>
    </div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">{{ $t('workspace.setting_camera') }}</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setVideo"
          :options="videoDevices"
          :value="'deviceId'"
          :text="'label'"
          :defaultValue="videoId"
        ></r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">
          {{ '영상 품질' }}
        </p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setQuality"
          :options="resolutions"
          value="value"
          text="text"
          :defaultValue="videoQuality"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
import { resolution } from 'utils/settingOptions'
export default {
  components: {
    RSelect,
  },
  data() {
    return {
      resolutions: resolution,
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
    videoDevices: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    ...mapGetters(['mic', 'speaker', 'video']),
    micId() {
      return this.mic['deviceId']
    },
    speakerId() {
      return this.speaker['deviceId']
    },
    videoId() {
      return this.video['deviceId']
    },
    videoQuality() {
      return this.video['quality']
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
    setVideo(newDevice) {
      this.setDevices({
        video: { deviceId: newDevice.deviceId },
      })
      this.$localStorage.setDevice('video', 'deviceId', newDevice.deviceId)
    },
    setQuality(quality) {
      this.setDevices({
        video: { quality: quality.value },
      })
      this.$localStorage.setDevice('video', 'quality', quality.value)
    },
  },
}
</script>
