<template>
  <tooltip :content="`${$t('common.video')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header"
      :description="`${$t('common.video')} on/off`"
      size="2.429rem"
      :active="video.isOn"
      :activeSrc="require('assets/image/ic_video_on.svg')"
      :inactiveSrc="require('assets/image/ic_video_off.svg')"
      @action="streamOnOff"
    ></toggle-button>
  </tooltip>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

import Tooltip from 'Tooltip'
import ToggleButton from 'ToggleButton'
export default {
  name: 'Stream',
  components: {
    Tooltip,
    ToggleButton,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['video']),
  },
  methods: {
    ...mapActions(['setDevices']),
    streamOnOff() {
      let video = !this.video.isOn

      this.setDevices({
        video: {
          isOn: video,
        },
      })
      this.$call.video(video)
      this.$localStorage.setDevice('video', 'isOn', video)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
