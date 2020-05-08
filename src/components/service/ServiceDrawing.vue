<template>
  <main class="main-wrapper drawing">
    <div class="drawing-body">
      <drawing-tools></drawing-tools>
      <drawing-canvas></drawing-canvas>
    </div>
  </main>
</template>

<script>
import DrawingTools from './tools/DrawingTools'
import DrawingCanvas from './drawing/DrawingCanvas'

export default {
  name: 'ServiceDrawing',
  components: {
    DrawingTools,
    DrawingCanvas,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
