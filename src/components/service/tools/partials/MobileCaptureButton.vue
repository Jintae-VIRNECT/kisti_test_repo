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
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_capture_new.svg'
  );

  &:disabled::after {
    opacity: 0.6;
  }
}
</style>
