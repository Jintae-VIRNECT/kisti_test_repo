<template>
  <button
    class="mobile-flash-btn"
    :class="{ on: isFlashOn }"
    :disabled="isFlashDisable"
    @click="clickHandler"
  ></button>
</template>

<script>
import flashMixin from 'mixins/flash'

export default {
  name: 'MobileFlashButton',
  mixins: [flashMixin],
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-flash-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);

  &::after {
    position: absolute;
    top: 0;
    left: 0;
    width: 6rem;
    height: 6rem;
    background: url(~assets/image/call/ic_flash_off_new.svg) center/2.4rem
      no-repeat;
    opacity: 1;
    content: '';
  }
  &.on::after {
    background: url(~assets/image/call/ic_flash_on_new.svg) center/2.4rem
      no-repeat;
  }
  &:disabled::after {
    opacity: 0.6;
  }
}
</style>
