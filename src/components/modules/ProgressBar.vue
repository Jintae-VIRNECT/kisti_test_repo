<template>
  <div class="progress" :class="customClass">
    <div class="progress-front" :style="{ width: `${percentage}%` }"></div>
  </div>
</template>

<script>
export default {
  name: 'ProgressBar',
  computed: {
    percentage() {
      if (this.max === 0 || this.max < 0) return 0
      if (this.value > this.max) return 100

      return (this.value / this.max) * 100
    },
  },
  data() {
    return {}
  },
  props: {
    value: {
      type: Number,
      default: 0,
    },
    max: {
      type: Number,
      default: 100,
    },
    customClass: {
      type: String,
      default: '',
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.progress {
  position: relative;
  width: 100%;
  height: 0.571rem;
  background-color: rgba(#d8d8d8, 0.17);
  border-radius: 4px;
}
.progress-front {
  height: 100%;
  overflow: hidden;
  background: linear-gradient(-90deg, #22adfb 0%, #78f50f 100%);
  border-radius: 4px;
  transition: all 0.5s;
}
</style>
