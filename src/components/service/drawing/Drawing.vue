<template>
  <main class="main-wrapper drawing">
    <div class="drawing-body">
      <drawing-canvas></drawing-canvas>
    </div>
  </main>
</template>

<script>
import DrawingCanvas from './DrawingCanvas'

export default {
  name: 'Drawing',
  components: {
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
