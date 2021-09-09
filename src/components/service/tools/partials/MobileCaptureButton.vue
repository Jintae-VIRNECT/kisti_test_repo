<template>
  <button class="mobile-capture-btn"></button>
</template>

<script>
export default {}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-capture-btn {
  width: 6rem;
  height: 6rem;
  margin: 0 1rem;
  background: url(~assets/image/call/mdpi_icon_capture_new.svg) center/2.4rem
    no-repeat;
  background-color: $new_color_bg_button_sub2;
  border-radius: 50%;
}
</style>
