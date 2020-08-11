<template>
  <tool-button
    :text="$t('service.tool_flash')"
    :disable="disable"
    :icActive="status"
    :src="require('assets/image/ic_flash_off.svg')"
    :activeSrc="require('assets/image/ic_flash_on.svg')"
    @click="clickHandler()"
  ></tool-button>
</template>

<script>
import { mapGetters } from 'vuex'
import toastMixin from 'mixins/toast'
import ToolButton from 'ToolButton'

import { FLASH } from 'configs/device.config'

export default {
  name: 'ToolFlash',
  components: {
    ToolButton,
  },
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['mainView', 'deviceInfo']),
    flashStatus() {
      if (this.mainView && this.mainView.id) {
        return this.deviceInfo.flash
      }
      return -1
    },
    status() {
      if (this.flashStatus === FLASH.FLASH_ON) {
        return true
      } else {
        return false
      }
    },
    disable() {
      const state = this.flashStatus
      if (state === FLASH.FLASH_NONE || state === FLASH.NO_PERMISSION) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    flashStatus(status) {
      this.flashListener(status)
    },
  },
  methods: {
    // ...mapMutations(['deviceUpdate']),
    clickHandler() {
      if (this.flashStatus === FLASH.FLASH_NONE) {
        this.toastDefault(this.$t('service.flash_none'))
        return
      }

      if (this.flashStatus === FLASH.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
        return
      }
      const toStatus = !this.status
      this.$call.flash(toStatus)
    },
    flashListener(status) {
      // 응답
      if (parseInt(status) === FLASH.CAMERA_ZOOMING) {
        this.toastNotice(this.$t('service.flash_controlling'))
        return
      }
      if (parseInt(status) === FLASH.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
      }
      if (parseInt(status) === FLASH.FLASH_NONE) {
        this.toastDefault(this.$t('service.flash_none'))
      }
      if (parseInt(status) === FLASH.FLASH_ON) {
        this.toastDefault(this.$t('service.flash_on'))
      }
      if (parseInt(status) === FLASH.FLASH_OFF) {
        this.toastDefault(this.$t('service.flash_off'))
      }
      if (parseInt(status) === FLASH.APP_IS_BACKGROUND) {
        this.toastDefault(this.$t('service.flash_app_disable'))
      }
    },
    flashInfoListener(message) {
      if ('status' in message) {
        if (
          message.status === FLASH.FLASH_OFF ||
          message.status === FLASH.FLASH_ON ||
          message.status === FLASH.FLASH_NONE
        ) {
          // this.flashStatus = parseInt(message.status)
          this.deviceUpdate({
            flashStatus: parseInt(message.status),
          })
        }
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
