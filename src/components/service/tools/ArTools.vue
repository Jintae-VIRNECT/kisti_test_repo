<template>
  <div class="ar-tools tools">
    <pointing></pointing>
    <capture></capture>
    <record></record>
  </div>
</template>

<script>
import { Pointing, Capture, Record } from './partials'

export default {
  name: 'ArTools',
  components: {
    Pointing,
    Capture,
    Record,
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
