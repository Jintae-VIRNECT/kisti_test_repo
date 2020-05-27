<template>
  <main class="main-wrapper drawing">
    <div class="drawing-body">
      <drawing-tools></drawing-tools>
      <menus></menus>
      <drawing-canvas></drawing-canvas>
    </div>
  </main>
</template>

<script>
import DrawingTools from './tools/DrawingTools'
import Menus from './tools/Menus'
import DrawingCanvas from './drawing/DrawingCanvas'

export default {
  name: 'ServiceDrawing',
  components: {
    DrawingTools,
    Menus,
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
