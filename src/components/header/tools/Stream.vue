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
import { CAMERA as CAMERA_STATUS } from 'configs/device.config'
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
      this.$call.sendCamera(
        video ? CAMERA_STATUS.CAMERA_ON : CAMERA_STATUS.CAMERA_OFF,
      )
      this.$localStorage.setDevice('video', 'isOn', video)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
