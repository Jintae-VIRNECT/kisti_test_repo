<template>
  <div class="stream-menu menus">
    <capture></capture>
    <record></record>
    <local-record></local-record>
    <local-record-list></local-record-list>
    <setting></setting>
  </div>
</template>

<script>
import {
  Capture,
  Record,
  LocalRecord,
  Setting,
  LocalRecordList,
} from './partials'

export default {
  name: 'Menus',
  components: {
    Capture,
    Record,
    LocalRecord,
    LocalRecordList,
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
