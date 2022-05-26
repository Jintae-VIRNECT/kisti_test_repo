<template>
  <button class="mobile-3d-control-btn" @click="onClick"></button>
</template>

<script>
export default {
  name: 'Mobile3dControlButton',
  methods: {
    onClick() {
      this.$emit('onClick')
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.mobile-3d-control-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon('~assets/image/call/icon_3d_control_new.svg');
}
</style>
