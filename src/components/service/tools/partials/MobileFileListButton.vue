<template>
  <button
    class="mobile-filelist-btn"
    :class="{
      notice,
    }"
    :disabled="disabled"
    @click="openFileListModal"
  ></button>
</template>

<script>
export default {
  props: {
    disabled: { type: Boolean },
    notice: { type: Boolean },
  },
  methods: {
    openFileListModal() {
      this.$emit('openFileListModal')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-filelist-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_filelist_new.svg'
  );
  @include mobile-circle-btn-disabled(
    $new_color_bg_button_disabled,
    $new_color_bg_button_sub,
    '~assets/image/call/mdpi_icon_filelist_new.svg'
  );
  &.notice::before {
    position: absolute;
    top: 15px;
    right: 15px;
    width: 0.6rem;
    height: 0.6rem;
    background-color: #d9333a;
    border-radius: 50%;
    content: '';
  }
}
</style>
