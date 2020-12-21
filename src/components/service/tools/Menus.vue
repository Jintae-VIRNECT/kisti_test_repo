<template>
  <div class="stream-menu menus">
    <div class="menus-box">
      <template v-if="isLeader">
        <capture :disabled="!isMainViewOn"></capture>
        <server-record :disabled="!hasMainView"></server-record>
      </template>
      <template v-if="!isSafari">
        <local-record :disabled="!hasMainView"></local-record>
        <local-record-list></local-record-list>
      </template>
      <setting v-if="!isSafari || isLeader" :viewType="viewType"></setting>
    </div>
  </div>
</template>

<script>
import {
  Capture,
  ServerRecord,
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
    ServerRecord,
    LocalRecord,
    LocalRecordList,
    Setting,
  },
  data() {
    return {
      active: 'pointing',
    }
  },
  computed: {
    ...mapGetters(['mainView', 'useRecording']),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
    isMainViewOn() {
      return this.mainView && this.mainView.id && this.mainView.video
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  props: {
    viewType: String,
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
