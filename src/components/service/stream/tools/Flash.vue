<template>
  <tool-button
    :text="$t('service.tool_flash')"
    :disable="disable"
    :isActive="status"
    :src="require('assets/image/ic_flash_off.svg')"
    :activeSrc="require('assets/image/ic_flash_on.svg')"
    placement="top"
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
    ...mapGetters(['mainView']),
    flashStatus() {
      if (this.mainView && this.mainView.id) {
        return this.mainView.flash
      }
      return -1
    },
    status() {
      return this.flashStatus === FLASH.FLASH_ON
    },
    disable() {
      return (
        this.flashStatus === FLASH.FLASH_NONE ||
        this.flashStatus === FLASH.NO_PERMISSION ||
        this.flashStatus === 'default'
      )
    },
  },
  watch: {
    flashStatus(status, bStatus) {
      if (status !== -1) {
        this.flashListener(status, bStatus)
      }
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
      this.$call.sendFlash(toStatus, [this.mainView.connectionId])
    },
    flashListener(status, bStatus) {
      // 응답
      if (parseInt(status) === FLASH.CAMERA_ZOOMING) {
        this.toastNotice(this.$t('service.flash_controlling'))
        return
      }
      if (parseInt(status) === FLASH.NO_PERMISSION) {
        this.toastDefault(this.$t('service.flash_no_permission'))
      }
      // if (parseInt(status) === FLASH.FLASH_NONE) {
      //   this.toastDefault(this.$t('service.flash_none'))
      // }
      if (
        parseInt(status) === FLASH.FLASH_ON &&
        parseInt(bStatus) === FLASH.FLASH_OFF
      ) {
        this.toastDefault(this.$t('service.flash_on'))
      }
      if (
        parseInt(status) === FLASH.FLASH_OFF &&
        parseInt(bStatus) === FLASH.FLASH_ON
      ) {
        this.toastDefault(this.$t('service.flash_off'))
      }
      if (parseInt(status) === FLASH.APP_IS_BACKGROUND) {
        this.toastDefault(this.$t('service.flash_app_disable'))
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
