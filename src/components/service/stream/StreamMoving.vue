<template>
  <div
    :class="{ grab: cursorGrab, grabbing: cursorGrabbing }"
    @mousedown="mousedown"
    @mouseup="mouseup"
    @mouseleave="mouseup"
  >
    <canvas></canvas>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
export default {
  name: 'Moving',
  data() {
    return {
      cursorGrabbing: false,
      shift: false,
    }
  },
  computed: {
    ...mapGetters(['viewAction']),
    viewMoving() {
      return this.viewAction === ACTION.STREAM_MOVING
    },
    cursorGrab() {
      if (this.viewMoving) {
        return true
      }
      if (this.shift) {
        return true
      }
      return false
    },
  },
  methods: {
    mousedown() {
      if (!this.cursorGrabbing) {
        this.cursorGrabbing = true
      }
    },
    mouseup() {
      this.cursorGrabbing = false
    },

    keyEventHandler(e) {
      if (this.shift) return
      const keycode = parseInt(e.keyCode)
      if (keycode === 16) {
        this.shift = true
      }
    },
    keyUpEventHandler(e) {
      const keycode = parseInt(e.keyCode)
      if (keycode === 16) {
        this.shift = false
      }
    },
    focusOut() {
      this.shift = false
    },
  },
  mounted() {
    window.addEventListener('keydown', this.keyEventHandler)
    window.addEventListener('keyup', this.keyUpEventHandler)
    window.addEventListener('blur', this.focusOut)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.keyEventHandler)
    window.removeEventListener('keyup', this.keyUpEventHandler)
    window.removeEventListener('blur', this.focusOut)
  },
}
</script>
<style lang="scss">
.main-video__moving {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  max-width: 100%;
  height: 100%;
  max-height: 100%;
}
.grab {
  border: solid 1px #fff;
  cursor: grab;
  &.grabbing {
    cursor: grabbing;
  }
}
</style>
