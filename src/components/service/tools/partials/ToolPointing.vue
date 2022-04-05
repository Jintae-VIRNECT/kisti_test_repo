<template>
  <tool-button
    :text="$t('service.tool_pointing')"
    :active="viewAction === STREAM_POINTING"
    :disabled="!canPointing"
    :src="require('assets/image/call/ic_pointing.svg')"
    @click="pointing"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { CAMERA, DEVICE } from 'configs/device.config'
import { ACTION } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'
import { mapGetters } from 'vuex'
export default {
  name: 'PointingTool',
  mixins: [toolMixin, toastMixin],
  data() {
    return {
      STREAM_POINTING: ACTION.STREAM_POINTING,
    }
  },
  computed: {
    ...mapGetters(['allowPointing', 'viewForce', 'mainView']),
    canPointing() {
      if (this.disabled) {
        return false
      }
      if (!this.viewForce) {
        return false
      }
      const isMobileWithScreenShareAndBackground =
        this.mainView.screenShare &&
        this.mainView.deviceType === DEVICE.MOBILE &&
        this.mainView.cameraStatus === CAMERA.APP_IS_BACKGROUND
      if (isMobileWithScreenShareAndBackground) return false

      if (this.account.roleType === ROLE.LEADER) {
        return true
      }

      return this.allowPointing
    },
  },
  watch: {
    canPointing(current) {
      const isMobileWithScreenShareAndBackground =
        this.mainView.screenShare &&
        this.mainView.deviceType === DEVICE.MOBILE &&
        this.mainView.cameraStatus === CAMERA.APP_IS_BACKGROUND

      if (!current && isMobileWithScreenShareAndBackground) {
        this.setAction('default')
      }
    },
  },
  methods: {
    pointing() {
      if (!this.viewForce) {
        this.toastDefault(this.$t('service.toast_no_sharing'))
        return
      }
      if (!this.canPointing) {
        this.toastDefault(this.$t('service.tool_pointing_block'))
        return
      }
      if (this.viewAction !== ACTION.STREAM_POINTING) {
        this.setAction('pointing')
      } else {
        this.setAction('default')
      }
    },
  },
}
</script>
