<template>
  <main class="drawing-wrapper">
    <div class="drawing">
      <drawing-tools></drawing-tools>
      <drawing-canvas></drawing-canvas>
      <!-- <video-list></video-list> -->
    </div>
  </main>
</template>

<script>
import DrawingTools from './DrawingTools'
import DrawingCanvas from './DrawingCanvas'
import VideoList from '../mainview/VideoList'

export default {
  name: 'Drawing',
  components: {
    DrawingTools,
    DrawingCanvas,
    VideoList,
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
