<template>
  <div class="range-slider">
    <div ref="value-wrapper" class="range-slider--value"></div>
    <input
      ref="slider"
      class="range-slider--input"
      type="range"
      :min="min"
      :max="max"
      :step="step"
      v-model="value"
      @click="setCSSProperty"
      @mousemove="handleMove"
      @mousedown="handleDown"
      @mouseup="handleUpAndLeave"
      @mouseleave="handleUpAndLeave"
      @touchmove="handleMove"
      @touchend="handleUpAndLeave"
      @touchstart="handleDown"
      @input="setValue"
    />
  </div>
</template>

<script>
export default {
  name: 'RangeSlider',
  props: {
    min: {
      type: Number,
      default: 1,
    },
    step: {
      type: Number,
      default: 1,
    },
    max: {
      type: Number,
      required: true,
    },
    initValue: {
      type: Number,
    },
  },
  data() {
    return {
      value: 1,
      isChanging: false,
    }
  },
  watch: {
    value() {
      this.$emit('update:value', Number.parseInt(this.value, 10))
      this.setCSSProperty()
    },
  },
  methods: {
    setCSSProperty() {
      const slider = this.$refs['slider']
      const percent = ((this.value - this.min) / (this.max - this.min)) * 100

      // Here comes the magic 🦄🌈
      slider.style.setProperty('--webkitProgressPercent', `${percent}%`)
    },
    setValue() {
      const value_wrapper = this.$refs['value-wrapper']
      const newValue = Number(
          ((this.value - this.min) * 100) / (this.max - this.min),
        ),
        newPosition = 10 - newValue * 0.2
      value_wrapper.innerHTML = `<span>${this.value}</span>`
      value_wrapper.style.left = `calc(${newValue}% + (${newPosition}px))`
    },

    //event listener
    handleMove() {
      if (!this.isChanging) return
      this.setCSSProperty()
    },
    handleUpAndLeave() {
      this.isChanging = false
    },
    handleDown() {
      this.isChanging = true
    },
  },
  mounted() {
    this.value = this.initValue

    // Init input
    this.setCSSProperty()
    this.setValue()
  },
}
</script>

<style lang="scss">
.range-slider--input {
  $thumbSize: 1.2857rem;
  $trackSize: 0.5714rem;
  $thumbBg: #fff;
  $trackBg: #f2f2f2;
  $progressBg: #262626;
  --webkitProgressPercent: 0%;

  width: 100%;

  margin: 0;
  padding: 0;
  background: transparent;
  -webkit-appearance: none;
  -moz-appearance: none;

  &:focus {
    outline: none;
  }

  //Thumb
  &::-webkit-slider-thumb {
    width: 1.4286rem;
    height: 1.4286rem;
    margin-top: -0.3571rem;
    background: rgb(255, 255, 255);

    border: none;
    border-radius: 50%;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.7);
    cursor: pointer;
    -webkit-appearance: none;
    appearance: none;
  }
  //Track
  &::-webkit-slider-runnable-track {
    height: 0.5714rem;
    background: linear-gradient(
      90deg,
      #0f75f5 calc(var(--webkitProgressPercent) / 2),
      #00b6ff var(--webkitProgressPercent),
      #5e5e62 var(--webkitProgressPercent)
    );

    border-radius: 4px;
  }
}

.range-slider {
  position: relative;
  width: 100%;
}
.range-slider--value {
  position: absolute;
  top: -2.2857rem;
}
.range-slider--value span {
  position: absolute;
  left: 50%;
  display: block;
  width: 2.1429rem;
  height: 1.7143rem;
  color: #fff;

  font-weight: 400;
  font-size: 0.9286rem;

  line-height: 1.7143rem;
  letter-spacing: 0px;
  text-align: center;
  background: #0f75f5;
  border-radius: 4px;
  transform: translate(-50%, 0);
}
.range-slider--value span:before {
  position: absolute;
  top: 100%;
  left: 50%;
  width: 0;
  height: 0;
  margin-top: -0.0714rem;
  margin-left: -0.3571rem;
  color: #ffffff;
  border-top: 6px solid #0f75f5;
  border-right: 5px solid transparent;
  border-left: 5px solid transparent;
  content: '';
}
</style>
