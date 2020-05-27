<template>
  <div class="drawing-tools tools">
    <line-mode></line-mode>
    <line-width></line-width>
    <text-mode></text-mode>
    <text-size></text-size>
    <color></color>
    <div class="division"></div>
    <undo></undo>
    <redo></redo>
    <clear></clear>
  </div>
</template>

<script>
import {
  LineMode,
  TextMode,
  Color,
  LineWidth,
  TextSize,
  Undo,
  Redo,
  Clear,
} from './partials'

export default {
  name: 'DrawingTools',
  components: {
    LineMode,
    TextMode,
    Color,
    LineWidth,
    TextSize,
    Undo,
    Redo,
    Clear,
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
