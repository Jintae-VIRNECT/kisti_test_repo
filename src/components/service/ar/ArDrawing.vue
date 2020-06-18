<template>
  <div class="ar-drawing" style="width: 100%;height: 100%;">
    <drawing-canvas
      style="width: 100%;height: 100%;"
      :file="shareArImage"
    ></drawing-canvas>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import DrawingCanvas from './ardrawing/DrawingCanvas'
export default {
  name: 'ARDrawing',
  components: {
    DrawingCanvas,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['shareArImage']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
<style>
.ar-drawing {
  position: absolute;
  top: 0;
  left: 0;
}
</style>
