<template>
  <section>
    <div class="workspace-setting-title">영상 설정</div>
    <div class="workspace-setting-vertical-wrapper">
      <div class="workspace-setting-label">카메라</div>
      <r-select
        class="workspace-setting-r-selecter"
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

<style scoped lang="scss">
.video-wrapper {
  width: 300px;
  height: 226px;
  margin-top: 50px;
  border: 1px solid;
  border-color: #363638;
  position: relative;
  overflow: hidden;

  .preivew-text {
    width: 100%;
    color: rgb(236, 236, 236);
    font-family: 'NotoSansCJKkr-Medium';
    top: 90%;
    text-align: center;
    background-color: #000000;
    opacity: 50%;
    position: absolute;
  }
}

.video-preview {
  width: 100%;
  height: 100%;
}
</style>
