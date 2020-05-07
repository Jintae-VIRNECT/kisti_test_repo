<template>
  <div class="drawing-tools tools">
    <line-mode></line-mode>
    <text-mode></text-mode>
    <line-color></line-color>
    <line-width></line-width>
    <text-size></text-size>
  </div>
</template>

<script>
import { LineMode, TextMode, LineColor, LineWidth, TextSize } from './partials'

export default {
  name: 'DrawingTools',
  components: {
    LineMode,
    TextMode,
    LineColor,
    LineWidth,
    TextSize,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
