<template>
  <section class="setting-section">
    <div class="setting__title">영상 설정</div>
    <div>
      <p class="setting__label">카메라</p>
      <r-select
        class="setting__r-selecter"
        v-on:changeValue="setVideo"
        :options="videos"
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
      selectVideo: null,
    }
  },
  props: {
    videos: null,
  },
  components: {
    RSelect,
  },
  methods: {
    setVideo: function(newDevice) {
      this.selectVideo = newDevice.deviceId
      this.$emit('setVideo', newDevice)
    },
  },
  created() {},
}
</script>
