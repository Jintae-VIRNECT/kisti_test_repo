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
        this.toastDefault('플래시가 없는 기기입니다.')
        return
      }

      if (this.flashStatus === FLASH.NO_PERMISSION) {
        this.toastDefault('상대방이 플래시 제어 허가 요청을 거절하였습니다.')
        return
      }
      const toStatus = !this.status
      this.$call.flash({ enable: toStatus })
    },
    flashListener(status) {
      // 응답
      if (parseInt(status) === FLASH.CAMERA_ZOOMING) {
        this.toastNotice('상대방이 영상을 확대/축소하고 있습니다.')
        return
      }
      if (parseInt(status) === FLASH.NO_PERMISSION) {
        this.toastDefault('상대방이 플래시 제어 허가 요청을 거절하였습니다.')
      }
      if (parseInt(status) === FLASH.FLASH_NONE) {
        this.toastDefault('플래시가 없는 기기입니다.')
      }
      if (parseInt(status) === FLASH.FLASH_ON) {
        this.toastDefault('상대방 디바이스의 플래시 기능이 켜졌습니다.')
      }
      if (parseInt(status) === FLASH.FLASH_OFF) {
        this.toastDefault('상대방 디바이스의 플래시 기능이 꺼졌습니다.')
      }
      if (parseInt(status) === FLASH.APP_IS_BACKGROUND) {
        this.toastDefault(
          '상대방 앱이 비활성화 상태입니다. 해당 작업을 실행할 수 없습니다.',
        )
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
