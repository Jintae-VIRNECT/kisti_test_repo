<template>
  <div class="collabo-status" :class="customClass">
    <div class="collabo-status--dot" :class="getClass"></div>
    <span class="collabo-status--text">{{ collaboText }}</span>
  </div>
</template>

<script>
import { collabo } from 'utils/collabo'
export default {
  name: 'CallaboStatus',
  props: {
    status: {
      type: [String, Boolean],
      default: null,
    },
    customClass: {
      type: String,
      default: '',
    },
  },

  computed: {
    collaboText() {
      switch (this.status) {
        case true:
          return this.$t('status.progress')
        case false:
          return this.$t('status.finished')
        default:
          return this.$t('status.finished')
        // return this.$t('status.unknown')
      }
    },
    getClass() {
      switch (this.status) {
        case collabo.PROGRESS:
          return 'progress'
        case collabo.FINISHED:
          return 'finished'
        default:
          return 'finished'
      }
    },
  },
}
</script>

<style lang="scss">
.collabo-status {
  position: relative;
  display: flex;
  align-items: center;
  justify-items: center;
}

.collabo-status--dot {
  width: 0.5714rem;
  height: 0.5714rem;
  background-color: #757f91;
  border-radius: 50%;

  &.progress {
    background-color: #0f75f5;
  }
}

.collabo-status--text {
  padding-left: 0.7143rem;
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.0714rem;
}
</style>
