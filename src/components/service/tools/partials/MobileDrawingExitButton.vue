<template>
  <button class="mobile-drawing-exit-btn" @click="upload"></button>
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

.mobile-drawing-exit-btn {
  @include mobile-circle-btn(#e4434a);
  @include mobile-circle-btn-icon('~assets/image/ic_close_24.svg');
}
</style>
