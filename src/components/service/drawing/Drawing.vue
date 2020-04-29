<template>
  <main class="main-wrapper drawing">
    <div class="drawing-body">
      <drawing-tools></drawing-tools>
      <drawing-canvas></drawing-canvas>
    </div>
  </main>
</template>

<script>
import DrawingTools from './DrawingTools'
import DrawingCanvas from './DrawingCanvas'

export default {
  name: 'Drawing',
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
