<template>
  <tooltip :content="`${$t('common.video')} on/off`">
    <toggle-button
      slot="body"
      customClass="toggle-header stream"
      :description="`${$t('common.video')} on/off`"
      :size="size"
      :disabled="disable"
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
import { ROLE } from 'configs/remote.config'
export default {
  name: 'Stream',
  components: {
    Tooltip,
    ToggleButton,
  },
  props: {
    size: {
      type: String,
      default: '2.429rem',
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['video', 'roomInfo', 'allowCameraControl', 'myInfo']),
    disable() {
      if (this.$route.name !== 'service') return false
      if (this.myInfo.screenShare === true) return true
      if (!this.roomInfo.videoRestrictedMode) return false
      if (this.account.roleType === ROLE.LEADER) return false
      return !this.allowCameraControl
    },
  },
  methods: {
    ...mapActions(['setDevices']),
    streamOnOff() {
      if (this.disable) {
        return
      }
      let video = !this.video.isOn
      this.setDevices({
        video: {
          isOn: video,
        },
      })
      this.$call.sendCamera(
        video ? CAMERA_STATUS.CAMERA_ON : CAMERA_STATUS.CAMERA_OFF,
      )
      window.myStorage.setDevice('video', 'isOn', video)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
