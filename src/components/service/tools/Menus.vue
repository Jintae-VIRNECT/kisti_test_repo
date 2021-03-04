<template>
  <div class="stream-menu menus">
    <div class="menus-box">
      <template v-if="isLeader">
        <capture :disabled="!isMainViewOn"></capture>
        <server-record
          v-if="useRecording"
          :disabled="!hasMainView"
        ></server-record>
      </template>
      <template v-if="!isSafari">
        <local-record :disabled="!hasMainView"></local-record>
        <local-record-list></local-record-list>
      </template>
      <setting v-if="isSettingVisible"></setting>
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
import { CAMERA as CAMERA_STATUS } from 'configs/device.config'

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
    ...mapGetters(['mainView', 'useRecording', 'useTranslate', 'myInfo']),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
    isMainViewOn() {
      return this.mainView && this.mainView.id && this.mainView.video
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isSettingVisible() {
      if (this.isSafari && !this.isLeader && !this.useTranslate) {
        return false
      } else {
        return true
      }
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
