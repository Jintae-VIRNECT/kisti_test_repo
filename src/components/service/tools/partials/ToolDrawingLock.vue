<template>
  <button class="drawing-lock-btn" @click="clickHandler">
    <div class="layer" :class="{ active: isDrawingLock }"></div>
    <img src="~assets/image/call/drawing_lock_new.svg" alt="lock" />
  </button>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'

export default {
  computed: {
    ...mapGetters(['viewAction']),
    isDrawingLock() {
      if (this.viewAction === ACTION.DRAWING_LOCK) return true
      else return false
    },
  },
  methods: {
    ...mapActions(['setAction']),
    clickHandler() {
      const action = this.isDrawingLock
        ? ACTION.DRAWING_LINE
        : ACTION.DRAWING_LOCK

      this.setAction(action)
    },
  },
}
</script>

<style lang="scss">
.drawing-lock-btn {
  width: 4rem;
  height: 4rem;
  position: relative;

  .layer {
    display: none;
    &.active {
      display: block;
      position: absolute;
      top: 50%;
      left: 50%;
      width: 3.2rem;
      height: 3.2rem;
      background-color: #677280;
      border-radius: 6px;
      transform: translate(-50%, -50%);
    }
  }

  img {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }
}
</style>
