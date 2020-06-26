<template>
  <div class="stream-menu menus">
    <div class="menus-box">
      <template v-if="isLeader">
        <capture :disabled="!isMainView"></capture>
        <record :disabled="!isMainView"></record>
      </template>
      <local-record :disabled="!isMainView"></local-record>
      <local-record-list></local-record-list>
      <setting></setting>
    </div>
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
import { ROLE } from 'configs/remote.config'

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
    isLeader() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
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
