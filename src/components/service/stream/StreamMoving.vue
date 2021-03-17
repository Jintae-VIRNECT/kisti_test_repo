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
      :activePano.sync="activePano"
    ></pano-video>
  </div>
</template>

<script>
import PanoVideo from 'PanoVideo'

import { mapGetters, mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'

import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'

export default {
  name: 'Moving',
  components: {
    PanoVideo,
  },

  data() {
    return {
      cursorGrabbing: false,
      shift: false,
      activePano: false,
    }
  },
  computed: {
    ...mapGetters(['mainView', 'viewAction', 'viewForce', 'participants']),
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
    isFITT360() {
      return this.mainView.deviceType === DEVICE.FITT360
    },
  },
  watch: {
    viewForce() {
      if (!this.viewForce) {
        this.activePano = false
      } else if (this.viewMoving && this.viewForce && this.isLeader) {
        this.activePano = true
      } else {
        this.sendPanoRotation()
      }
    },
    viewAction() {
      if (this.viewMoving && this.viewForce && this.isLeader) {
        this.activePano = true
      } else {
        this.activePano = false
      }
    },

    shift() {
      if (this.viewPointing) {
        if (this.shift && this.viewPointing) {
          this.activePano = true
        } else {
          this.activePano = false
        }
      }
    },
    'participants.length': {
      handler() {
        this.sendPanoRotation()
      },
    },
    activePano: {
      handler(flag) {
        this.$emit('panoctl', flag)
      },
      immediate: true,
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
    sendPanoRotation() {
      if (this.mainView.rotationPos) {
        this.$call.sendPanoRotation({
          yaw: this.mainView.rotationPos.yaw,
          pitch: this.mainView.rotationPos.pitch,
          origin: this.mainView.connectionId,
        })
      }
    },
  },
  mounted() {
    this.sendPanoRotation()
    window.addEventListener('keydown', this.keyEventHandler)
    window.addEventListener('keyup', this.keyUpEventHandler)
    window.addEventListener('blur', this.focusOut)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.keyEventHandler)
    window.removeEventListener('keyup', this.keyUpEventHandler)
    window.removeEventListener('blur', this.focusOut)

    if (this.viewAction === ACTION.STREAM_MOVING) {
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
