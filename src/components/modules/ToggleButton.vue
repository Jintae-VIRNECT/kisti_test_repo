<template>
  <button
    class="toggle-button"
    :class="[{ active: active, disabled: disabled }, customClass]"
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
  components: {},
  data() {
    return {}
  },
  props: {
    customClass: {
      type: [String, Object],
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
    active: {
      type: Boolean,
      default: true,
    },
    size: {
      type: [Number, String],
      default: 12,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    src() {
      if (this.toggle) {
        if (this.active) return this.activeSrc
        else return this.inactiveSrc
      } else {
        return this.activeSrc
      }
    },
  },

  /* Lifecycles */
  mounted() {},
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
  &.disabled {
    cursor: default;
    opacity: 0.2;
  }
}
</style>
