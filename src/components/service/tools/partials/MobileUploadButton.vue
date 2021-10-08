<template>
  <button class="mobile-upload-btn" @click="upload"></button>
</template>

<script>
export default {
  methods: {
    upload() {},
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-upload-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_upload_new.svg'
  );
}
</style>
