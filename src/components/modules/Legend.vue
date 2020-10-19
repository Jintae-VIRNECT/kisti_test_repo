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
  margin-right: 22px;

  &.round {
    &::before {
      position: absolute;
      top: 5px;
      left: 6px;
      width: 5px;
      height: 5px;
      background-color: #ffffff;
      border: 4px solid #0f75f5;
      border-radius: 50%;
      content: '';
    }
  }

  &.square {
    &::before {
      position: absolute;
      top: 5px;
      left: 6px;
      width: 12px;
      height: 12px;
      background-color: #0f75f5;
      content: '';
    }
  }
}

.legend--text {
  padding-left: 25px;
  color: rgb(122, 122, 122);
  font-weight: normal;
  font-size: 14px;
  letter-spacing: 0px;
}
</style>
