<template>
  <section>
    <div class="setting__title">영상 설정</div>
    <div class="setting-vertical-wrapper">
      <div class="setting__label">카메라</div>
      <r-select
        class="setting__r-selecter"
        v-on:changeValue="setVideoDevice"
        :options="videoDevices"
        :value="'deviceId'"
        :text="'label'"
      ></r-select>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
export default {
  data: function() {
    return {
      videoStream: null,
      selectVideo: null,
      videoQuality: '',
      videoQualityOpts: [
        {
          value: 720,
          text: '720p',
        },
        {
          value: 1080,
          text: '1080p',
        },
        {
          value: 2080,
          text: '2K',
        },
      ],
    }
  },
  props: {
    videoDevices: null,
  },
  components: {
    RSelect,
  },
  methods: {
    setVideoDevice: function(newDevice) {
      this.selectVideo = newDevice.deviceId
      this.$emit('selectedVideoDevice', newDevice)
    },
  },
}
</script>

<style scoped lang="scss"></style>
