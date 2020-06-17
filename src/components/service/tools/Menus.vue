<template>
  <div class="stream-menu menus">
    <capture :disabled="!isMainView"></capture>
    <record :disabled="!isMainView"></record>
    <local-record :disabled="!isMainView"></local-record>
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
import { mapGetters } from 'vuex'

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
  computed: {
    ...mapGetters(['mainView']),
    isMainView() {
      if (!(this.mainView && this.mainView.id)) {
        return false
      } else {
        return true
      }
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
