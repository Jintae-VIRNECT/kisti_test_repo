<template>
  <button
    class="mobile-capture-btn"
    :disabled="disabled"
    @click="capture"
  ></button>
</template>

<script>
export default {
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    capture() {
      if (this.disabled) return
      this.$eventBus.$emit('capture')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-capture-btn {
  position: relative;
  width: 6rem;
  height: 6rem;
  margin: 0 1rem;
  background-color: $new_color_bg_button_sub2;
  border-radius: 50%;

  &::after {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 2.4rem;
    height: 2.4rem;
    background: url(~assets/image/call/mdpi_icon_capture_new.svg) center/2.4rem
      no-repeat;
    transform: translate(-50%, -50%);
    content: '';
  }

  &:disabled::after {
    opacity: 0.6;
  }
}
</style>
