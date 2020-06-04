<template>
  <tool-button
    :text="'플래시 켜기/끄기'"
    :disable="disable"
    :active="status"
    :src="require('assets/image/ic_flash_on.svg')"
    :activeSrc="require('assets/image/ic_flash_off.svg')"
    @click="clickHandler()"
  ></tool-button>
</template>

<script>
import { mapGetters } from 'vuex'
import toastMixin from 'mixins/toast'
import ToolButton from 'ToolButton'

import * as device from 'utils/deviceinfo'

export default {
  name: 'ToolFlash',
  components: {
    ToolButton,
  },
  mixins: [toastMixin],
  data() {
    return {
      flashStatus: 'default', // 'default': 초기값 / 0: 플래시 꺼짐 / 1: 플래시 켜짐 / 2: 플래시 없음 / 3: 권한없음
      status: false,
    }
  },
  computed: {
    ...mapGetters({
      // flashStatus: 'flashStatus',
    }),
    // status() {
    //   if (this.flashStatus === device.FLASH_ON) {
    //     return true
    //   } else {
    //     return false
    //   }
    // },
    disable() {
      const state = this.flashStatus
      if (state === device.FLASH_NONE || state === device.NO_PERMISSION) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    // ...mapMutations(['deviceUpdate']),
    clickHandler() {
      this.status = !this.status
      // this.$eventBus.$emit('control:record',this.status);

      // if (this.flashStatus === device.FLASH_NONE) {
      //   this.toastDefault(this.$t('service.call_device_control_no_flash'))
      //   return
      // }

      // if (this.flashStatus === device.NO_PERMISSION) {
      //   this.toastDefault(this.$t('service.call_device_control_flash_deny'))
      //   return
      // }

      // this.$remoteSDK.message('flashControl', { enable: toStatus })
    },
    flashListener(message) {
      if ('status' in message) {
        // 응답
        if (parseInt(message.status) === device.CAMERA_ZOOMING) {
          this.toastNotice(this.$t('service.call_device_control_camera'))
          return
        }
        if (parseInt(message.status) === device.NO_PERMISSION) {
          this.toastDefault(this.$t('service.call_device_control_flash_deny'))
        }
        if (parseInt(message.status) === device.FLASH_NONE) {
          this.toastDefault(this.$t('service.call_device_control_no_flash'))
        }
        if (parseInt(message.status) === device.FLASH_ON) {
          this.toastDefault(this.$t('service.call_device_control_flash_on'))
        }
        if (parseInt(message.status) === device.FLASH_OFF) {
          this.toastDefault(this.$t('service.call_device_control_flash_off'))
        }
        if (parseInt(message.status) === device.APP_IS_BACKGROUND) {
          this.toastDefault(
            this.$t('service.call_ar_background_inactive_device'),
          )
        }
        // this.flashStatus = parseInt(message.status)
        this.deviceUpdate({
          flashStatus: parseInt(message.status),
        })
      } else {
        // 웹-웹 테스트용
        // this.flashStatus = message.enable ? device.FLASH_ON : device.FLASH_OFF
        this.deviceUpdate({
          flashStatus: message.enable ? device.FLASH_ON : device.FLASH_OFF,
        })
        this.$nextTick(() => {
          // this.$remoteSDK.message('flashControl', { status: device.NO_PERMISSION })  // 승인 거절거절
          this.$remoteSDK.message('flashControl', {
            status: this.flashStatus ? device.FLASH_ON : device.FLASH_OFF,
          })
          // this.$remoteSDK.message('flashControl', { status: device.CAMERA_ZOOMING }) // 카메라 zooming
        })
      }
    },
    flashInfoListener(message) {
      if ('status' in message) {
        if (
          message.status === device.FLASH_OFF ||
          message.status === device.FLASH_ON ||
          message.status === device.FLASH_NONE
        ) {
          // this.flashStatus = parseInt(message.status)
          this.deviceUpdate({
            flashStatus: parseInt(message.status),
          })
        }
      } else {
        // 웹-웹 테스트용
        // this.flashStatus = message.enable ? device.FLASH_ON : device.FLASH_OFF
        this.deviceUpdate({
          flashStatus: message.enable ? device.FLASH_ON : device.FLASH_OFF,
        })
        this.$nextTick(() => {
          // this.$remoteSDK.message('flashControl', { status: device.NO_PERMISSION })  // 승인 거절거절
          this.$remoteSDK.message('flashInfo', {
            status: this.flashStatus ? device.FLASH_ON : device.FLASH_OFF,
          })
          // this.$remoteSDK.message('flashControl', { status: device.CAMERA_ZOOMING }) // 카메라 zooming
        })
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
