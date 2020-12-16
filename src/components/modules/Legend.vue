<template>
  <button class="legend" @click="clickListener" :class="[shape, customClass]">
    <p class="legend--text" :class="{ toggle: toggle }">
      {{ text }}
    </p>
  </button>
</template>

<script>
export default {
  name: 'Legend',
  props: {
    text: {
      type: String,
      default: '',
    },
    shape: {
      type: String,
      default: 'double-circle',
      validate(value) {
        return ['double-circle', 'circle'].indexOf(value) >= 0
      },
    },
    customClass: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      toggle: false,
    }
  },
  methods: {
    clickListener() {
      if (typeof this.$listeners['click'] === 'function') {
        this.toggle = !this.toggle
        this.$listeners['click']()
        this.animateClass = this.animation
        setTimeout(() => {
          this.animateClass = ''
        }, 400)
      }
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
.legend {
  position: relative;
  margin-right: 1.5714rem;

  &.double-circle {
    &::before {
      position: absolute;
      top: 0.3571rem;
      left: 0.4286rem;
      width: 0.3571rem;
      height: 0.3571rem;
      background-color: $color_white;
      border: 4px solid $color_primary;
      border-radius: 50%;
      content: '';
    }
  }

  &.circle {
    &::before {
      position: absolute;
      top: 0.3571rem;
      left: 0.4286rem;
      width: 0.8571rem;
      height: 0.8571rem;
      background-color: #203cdd;
      border-radius: 50%;
      content: '';
    }
  }
}

.legend--text {
  padding-left: 1.7857rem;
  color: #7a7a7a;
  font-weight: normal;
  font-size: 1rem;
  letter-spacing: 0px;

  &.toggle {
    text-decoration: line-through;
  }
}
</style>
