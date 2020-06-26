<template>
  <div class="subview-wrapper">
    <transition name="subview">
      <sub-video v-if="subView"></sub-video>
    </transition>
    <chat :class="{ subView }"></chat>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { VIEW } from 'configs/view.config'
import SubVideo from './SubVideo'
import Chat from './Chat'
export default {
  name: 'SubViewLayout',
  components: {
    SubVideo,
    Chat,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['view']),
    subView() {
      if ([VIEW.DRAWING, VIEW.AR].includes(this.view)) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
<style>
.subview-enter-active,
.subview-leave-active {
  transition: left ease 0.4s;
}
.subview-enter,
.subview-leave-to {
  opacity: 0;
}
.subview-enter-to,
.subview-leave {
  opacity: 1;
}
</style>
