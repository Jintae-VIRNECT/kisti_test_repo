<template>
  <div class="subview-wrapper">
    <transition name="subvideo">
      <sub-video v-if="subView"></sub-video>
    </transition>
    <chat :class="{ subView }"></chat>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { VIEW, ACTION } from 'configs/view.config'
import SubVideo from './SubVideo'
import Chat from './Chat'
export default {
  name: 'SubViewLayout',
  components: {
    SubVideo,
    Chat,
  },
  computed: {
    ...mapGetters(['view', 'viewAction']),
    subView() {
      if (!this.isMobileSize)
        return (
          VIEW.DRAWING === this.view || ACTION.AR_DRAWING === this.viewAction
        )
      else return false
    },
  },
}
</script>
<style>
.subvideo-enter-active,
.subvideo-leave-active {
  transition: left ease 0.4s;
}
.subvideo-enter,
.subvideo-leave-to {
  opacity: 0;
}
.subvideo-enter-to,
.subvideo-leave {
  opacity: 1;
}
</style>
