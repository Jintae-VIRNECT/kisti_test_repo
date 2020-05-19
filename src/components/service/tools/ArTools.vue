<template>
  <div class="ar-tools tools">
    <ar-pointing></ar-pointing>
    <ar-capture></ar-capture>
    <div class="division"></div>
    <line-mode></line-mode>
    <line-width></line-width>
    <color></color>
    <div class="division"></div>
    <undo></undo>
    <redo></redo>
    <clear></clear>
  </div>
</template>

<script>
import {
  ArPointing,
  ArCapture,
  LineMode,
  LineWidth,
  Color,
  Undo,
  Redo,
  Clear,
} from './partials'

export default {
  name: 'ArTools',
  components: {
    ArPointing,
    ArCapture,
    LineMode,
    LineWidth,
    Color,
    Undo,
    Redo,
    Clear,
  },
  data() {
    return {
      active: 'pointing',
      isRecording: false,
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
