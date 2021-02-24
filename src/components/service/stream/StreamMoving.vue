<template>
  <div
    :class="{ grabbing: grabbing }"
    @mousedown="mousedown"
    @mouseup="mouseup"
    @mouseleave="mouseup"
  >
    <canvas></canvas>
  </div>
</template>

<script>
export default {
  name: 'Moving',
  data() {
    return {
      grabbing: false,
    }
  },
  methods: {
    mousedown() {
      if (!this.grabbing) {
        this.grabbing = true
      }
    },
    mouseup() {
      this.grabbing = false
    },
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
  border: solid 1px #fff;
  cursor: grab;
  &.grabbing {
    cursor: grabbing;
  }
}
</style>
