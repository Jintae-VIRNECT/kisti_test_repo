<template>
  <div class="ar-view">
    <ar-drawing v-if="viewAction === AR_DRAWING"></ar-drawing>
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
  data() {
    return {
      AR_DRAWING: ACTION.AR_DRAWING,
    }
  },
  computed: {
    ...mapGetters(['viewAction']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
