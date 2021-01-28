<template>
  <div class="slider" :class="{ disabled }" @click="toggleSlider">
    <span
      class="slider-bg"
      :class="{ 'silder-first': isFirst, 'slider-second': !isFirst }"
    ></span>
    <span class="slider-option" :class="{ active: isFirst }">{{ first }}</span>
    <span class="slider-option" :class="{ active: !isFirst }">{{
      second
    }}</span>
  </div>
</template>

<script>
export default {
  name: 'Slider',
  components: {},
  data() {
    return {}
  },
  props: {
    first: String,
    second: String,
    isFirst: {
      type: Boolean,
      default: true,
    },
    disabled: {
      type: Boolean,
      dfault: false,
    },
  },
  methods: {
    toggleSlider() {
      if (this.disabled) {
        this.$emit('slider:disable')
        return
      }
      this.$emit('update:isFirst', !this.isFirst)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style scoped lang="scss">
@import '~assets/style/mixin';
.slider {
  position: relative;
  display: flex;
  max-width: 28.571rem;
  min-height: 2.571rem;
  background-color: #1d1d1f;
  border: solid 1px #1d1d1f;
  border-radius: 3px;
  cursor: pointer;
  transition: opacity 0.3s;
  &.disabled {
    cursor: default;
    opacity: 0.3;
  }
}
.slider-option {
  z-index: 1;
  flex: 1;
  align-self: center;
  color: #909090;
  font-weight: 400;
  text-align: center;
  transition: color 0.3s;
  &.active {
    color: #ffffff;
  }
}
.slider-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  background: #676773;
  border-radius: 3px;
  transition: left 0.3s;
  &.slider-first {
    left: 0;
  }
  &.slider-second {
    left: 50%;
  }
}
</style>
