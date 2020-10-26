<template>
  <div class="legend" :class="[shape, customClass]">
    <p class="legend--text">
      {{ text }}
    </p>
  </div>
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
      default: 'round',
      validate(value) {
        return ['round', 'square'].indexOf(value) >= 0
      },
    },
    customClass: {
      type: String,
      default: '',
    },
  },
  computed: {},
}
</script>

<style lang="scss">
.legend {
  position: relative;
  margin-right: 1.5714rem;

  &.round {
    &::before {
      position: absolute;
      top: 0.3571rem;
      left: 0.4286rem;
      width: 0.3571rem;
      height: 0.3571rem;
      background-color: #ffffff;
      border: 0.2857rem solid #0f75f5;
      border-radius: 50%;
      content: '';
    }
  }

  &.square {
    &::before {
      position: absolute;
      top: 0.3571rem;
      left: 0.4286rem;
      width: 0.8571rem;
      height: 0.8571rem;
      background-color: #0f75f5;
      content: '';
    }
  }
}

.legend--text {
  padding-left: 1.7857rem;
  color: rgb(122, 122, 122);
  font-weight: normal;
  font-size: 1rem;
  letter-spacing: 0px;
}
</style>
