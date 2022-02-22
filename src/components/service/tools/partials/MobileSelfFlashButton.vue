<template>
  <button
    class="self-flash-button"
    :class="{ on: isMyFlashOn }"
    @click="toggle"
  ></button>
</template>

<script>
import { FLASH as FLASH_STATUS } from 'configs/device.config'
import flashMixin from 'mixins/flash'

export default {
  name: 'MobileSelfFlashButton',
  mixins: [flashMixin],
  data() {
    return {
      on: false,
    }
  },
  computed: {
    isMyFlashDisable() {
      return (
        this.myInfo.flash === FLASH_STATUS.FLASH_NONE ||
        this.myInfo.flash === FLASH_STATUS.NO_PERMISSION ||
        this.myInfo.flash === 'default'
      )
    },
    isMyFlashOn() {
      return this.myInfo.flash === FLASH_STATUS.FLASH_ON
    },
  },
  methods: {
    toggle() {
      //내 기기의 플래시만 제어한다.
      if (!this.isMyFlashDisable) {
        const toStatus = !this.isMyFlashOn
        this.$call.sendFlash(toStatus, [this.myInfo.connectionId]) // 내 자신의 시그널을 받아 처리한다. (flashMixin의 flashControlListener에서)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.self-flash-button {
  width: 3.6rem;
  height: 3.6rem;
  background: url(~assets/image/call/mdpi_icon_flash_off_new.svg) center
    no-repeat;

  &.on {
    background: url(~assets/image/call/mdpi_icon_flash_on_new.svg) center
      no-repeat;
  }
}
</style>
