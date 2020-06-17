<template>
  <div class="ar-view">
    <ar-drawing v-if="isDrawing"></ar-drawing>
    <ar-video v-else></ar-video>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
import ArVideo from './ArVideo'
import ArDrawing from './ArDrawing'
export default {
  name: 'ARView',
  components: {
    ArVideo,
    ArDrawing,
  },
  computed: {
    ...mapGetters(['viewAction']),
    isDrawing() {
      if (this.viewAction === ACTION.AR_DRAWING) {
        return true
      } else {
        return false
      }
    },
  },
}
</script>
