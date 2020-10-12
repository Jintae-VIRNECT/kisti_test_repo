<template>
  <div class="collabo-status">
    <span class="collabo-status--text" :class="getClass">{{
      collaboText
    }}</span>
  </div>
</template>

<script>
import { collabo } from 'utils/collabo'
export default {
  name: 'CallaboStatus',
  props: {
    status: {
      type: String,
      default: null,
      required: true,
    },
  },

  computed: {
    collaboText() {
      switch (this.status) {
        case collabo.PROGRESS:
          return '진행중'
        case collabo.FINISHED:
          return '종료'
        default:
          return 'UNKNNOW'
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
  // text-align: left;
}

.collabo-status--text {
  padding-left: 15px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 15px;

  &::before {
    position: absolute;
    top: 8px;
    left: 0px;
    width: 8px;
    height: 8px;
    background-color: #757f91;
    border-radius: 50%;
    content: '';
  }

  &.progress {
    &::before {
      background-color: #0f75f5;
    }
  }
}
</style>
