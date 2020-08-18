<template>
  <section class="setting-section">
    <div class="setting__title">{{ $t('workspace.setting_video') }}</div>
    <div>
      <p class="setting__label">{{ $t('workspace.setting_camera') }}</p>
      <r-select
        class="setting__r-selecter"
        v-on:changeValue="setVideo"
        :options="videos"
        :value="'deviceId'"
        :text="'label'"
        :defaultValue="videoId"
      ></r-select>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
export default {
  data: function() {
    return {
      selectVideo: null,
    }
  },
  props: {
    videos: null,
  },
  computed: {
    ...mapGetters(['video']),
    videoId() {
      return this.video['deviceId']
    },
  },
  components: {
    RSelect,
  },
  methods: {
    ...mapActions(['setDevices']),
    setVideo(newDevice) {
      this.setDevices({
        video: { deviceId: newDevice.deviceId },
      })
      this.$localStorage.setDevice('video', 'deviceId', newDevice.deviceId)
    },
  },
  created() {},
}
</script>
