<template>
  <button
    class="mobile-download-btn"
    :disabled="disabled"
    @click="upload"
  ></button>
</template>

<script>
export default {
  props: {
    disabled: { type: Boolean },
  },
  methods: {
    upload() {},
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-download-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_download_new.svg'
  );
  @include mobile-circle-btn-disabled(
    $new_color_bg_button_disabled,
    $new_color_bg_button_sub,
    '~assets/image/call/mdpi_icon_download_new.svg'
  );
}
</style>
