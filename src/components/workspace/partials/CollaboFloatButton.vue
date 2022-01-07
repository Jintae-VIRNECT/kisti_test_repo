<template>
  <float-button
    class="collabo-float-btn"
    :img="require('assets/image/ic_float_collabo.svg')"
    @onClick="onClick"
  ></float-button>
</template>

<script>
import FloatButton from 'FloatButton'

export default {
  components: {
    FloatButton,
  },
  methods: {
    onClick() {
      this.$eventBus.$emit('open:modal:create')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.collabo-float-btn {
  display: none;
}
@include responsive-mobile {
  .collabo-float-btn {
    position: fixed;
    right: 10px;
    bottom: 30px;
    display: flex;
    background-color: $new_color_bg_button_primary;
  }
}
</style>
