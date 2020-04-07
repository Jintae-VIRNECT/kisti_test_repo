<template>
  <section class="workspace-setting-section">
    <div class="workspace-setting-title">영상 설정</div>

    <div class="workspace-setting-horizon-wrapper">
      <div class="workspace-setting-wrapper">
        <div class="workspace-setting-label">카메라</div>
        <div class="workspace-setting-label">품질</div>
      </div>
      <div class="workspace-setting-wrapper">
        <r-select
          v-on:changeValue="setVideoDevice"
          :options="videoDevices"
          :value="'deviceId'"
          :text="'label'"
        ></r-select>
        <r-select
          v-on:changeValue="handleVideoQuality"
          :options="videoQualityOpts"
          :value="'value'"
          :text="'text'"
        ></r-select>
        <div class="video-wrapper">
          <div class="preivew-text">미리보기</div>
          <video
            class="video-preview"
            :srcObject.prop="videoStream"
            autoplay
          ></video>
        </div>
      </div>
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
    handleVideoQuality: function(newQuality) {
      this.videoQuality = newQuality
    },
    setVideoDevice: function(newDevice) {
      console.log(newDevice)
      this.selectVideo = newDevice.deviceId
      this.getVideoStream()
    },
    getVideoStream() {
      console.log(this.selectVideo)
      const constraints = {
        video: {
          deviceId: this.selectVideo,
        },
        audio: false,
      }
      navigator.mediaDevices.getUserMedia(constraints).then(stream => {
        console.log(stream)
        this.videoStream = stream
      })
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
    // top: 0;
    // left: 50;
    position: absolute;
  }
}

.video-preview {
  width: 100%;
  height: 100%;
}
</style>
