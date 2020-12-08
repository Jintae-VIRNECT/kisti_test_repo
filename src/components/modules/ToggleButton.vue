<template>
  <button
    class="toggle-button"
    :class="[{ active: active }, customClass]"
    :style="{
      backgroundImage: 'url(' + src + ')',
      width: size,
      height: size,
    }"
    @click="$emit('action', $event)"
  >
    {{ description }}
  </button>
</template>

<script>
export default {
  name: 'ToggleButton',
  props: {
    customClass: {
      type: String,
      default: '',
    },
    description: String,
    toggle: {
      type: Boolean,
      default: true,
    },
    activeSrc: String,
    inactiveSrc: {
      type: String,
      default: null,
    },
    disableSrc: String,
    active: {
      type: Boolean,
      default: true,
    },
    disable: {
      type: Boolean,
      default: false,
    },
    size: {
      type: [Number, String],
      default: 12,
    },
  },
  computed: {
    src() {
      if (this.disable) {
        return this.disableSrc
      } else {
        if (this.toggle) {
          if (this.active) return this.activeSrc
          else return this.inactiveSrc
        } else {
          return this.activeSrc
        }
      }
    },
  },
}
</script>

<style scoped lang="scss">
@import '~assets/style/mixin';
.toggle-button {
  background: center/100% no-repeat;
  @include ir();
  &.toggle-header {
    background-size: 120%;
  }
  &.toggle-header__small {
    background-size: 70%;
  }
}
</style>
