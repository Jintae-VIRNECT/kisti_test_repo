<template>
  <transition name="tools">
    <div class="video-tools">
      <div class="video-tools__box">
        <flash></flash>
        <zoom></zoom>
      </div>
    </div>
  </transition>
</template>

<script>
import Flash from './tools/Flash'
import Zoom from './tools/Zoom'
export default {
  name: 'MainVideoTools',
  components: {
    Flash,
    Zoom,
  },
  props: {
    hover: Boolean,
  },
  data() {
    return {}
  },
  methods: {},

  /* Lifecycling */
}
</script>
<style lang="scss">
.video-tools {
  position: absolute;
  bottom: 0;
  left: 0;
  display: flex;
  width: 100%;
  height: 80px;
  background: linear-gradient(-180deg, rgba(#000, 0) 0%, rgba(#000, 0.7) 100%);
}
.video-tools__box {
  display: flex;
  width: fit-content;
  margin: auto;
}
</style>

<style>
.tools-enter-active,
.tools-leave-active {
  transition: opacity ease 0.4s;
}
.tools-enter {
  opacity: 0;
}
.tools-enter-to {
  opacity: 1;
}
.tools-leave {
  opacity: 1;
}
.tools-leave-to {
  opacity: 0;
}
</style>
