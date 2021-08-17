<template>
  <float-button
    class="openroom-float-btn"
    :img="require('assets/image/ic_float_openroom.svg')"
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
      this.$eventBus.$emit('open:modal:createOpen')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.openroom-float-btn {
  display: none;
}
@include responsive-mobile {
  .openroom-float-btn {
    position: fixed;
    right: 10px;
    bottom: 88px;
    display: flex;
    background-color: $new_color_bg_button_subprimary;
  }
}
</style>
