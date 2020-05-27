<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color></color>
  </div>
</template>

<script>
import { Pointing, Color } from './partials'

export default {
  name: 'StreamTools',
  components: {
    Pointing,
    Color,
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
