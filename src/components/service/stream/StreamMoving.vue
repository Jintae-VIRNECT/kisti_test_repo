<template>
  <div
    :class="{
      grab: cursorGrab && viewForce,
      grabbing: cursorGrabbing && viewForce,
    }"
    @mousedown="mousedown"
    @mouseup="mouseup"
    @mouseleave="mouseup"
  >
    <pano-video
      videoElementId="main-video"
      targetRef="mainVideo"
      :connectionId="mainView.connectionId"
      type="control"
    ></pano-video>
  </div>
</template>

<script>
import PanoVideo from 'PanoVideo'

import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'

import { ROLE } from 'configs/remote.config'
export default {
  name: 'Moving',
  components: {
    PanoVideo,
  },

  data() {
    return {
      cursorGrabbing: false,
      shift: false,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'viewAction', 'viewForce']),
    viewMoving() {
      return this.viewAction === ACTION.STREAM_MOVING
    },
    viewPointing() {
      return this.viewAction === ACTION.STREAM_POINTING
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
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {
    viewForce() {
      if (!this.viewForce) {
        this.$eventBus.$emit('panoview:toggle', false)
      } else if (this.viewMoving && this.viewForce && this.isLeader) {
        this.$eventBus.$emit('panoview:toggle', true)
      }
    },
    viewAction() {
      if (this.viewMoving && this.viewForce && this.isLeader) {
        this.$eventBus.$emit('panoview:toggle', true)
      } else {
        this.$eventBus.$emit('panoview:toggle', false)
      }
    },

    shift() {
      if (this.viewPointing) {
        if (this.shift && this.viewPointing) {
          this.$eventBus.$emit('panoview:toggle', true)
        } else {
          this.$eventBus.$emit('panoview:toggle', false)
        }
      }
    },
  },
  methods: {
    ...mapActions(['setAction']),
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

    if (this.viewAction === ACTION.STREAM_MOVING) {
      console.log('set to default')
      this.setAction('default')
    }
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
  opacity: 0;

  &.upper {
    z-index: 1;
  }
}
.grab {
  border: solid 1px #fff;
  cursor: grab;
  &.grabbing {
    cursor: grabbing;
  }
}
</style>
