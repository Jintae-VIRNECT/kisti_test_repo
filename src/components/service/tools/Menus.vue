<template>
  <div class="stream-menu menus">
    <capture></capture>
    <record></record>
    <local-record></local-record>
    <setting></setting>
  </div>
</template>

<script>
import { Capture, Record, LocalRecord, Setting } from './partials'

export default {
  name: 'Menus',
  components: {
    Capture,
    Record,
    LocalRecord,
    Setting,
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
