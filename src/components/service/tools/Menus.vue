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
      <template v-if="!isSafari && !isMobileDevice && useLocalRecording">
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
    ...mapGetters([
      'mainView',
      'useRecording',
      'useLocalRecording',
      'useTranslate',
    ]),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
    isMainViewOn() {
      const hasMainViewAndValidVideo = this.hasMainView && this.mainView.video
      const hasMainViewAndScreenShare = this.hasMainView && this.screenShare

      return hasMainViewAndValidVideo || hasMainViewAndScreenShare
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isSettingVisible() {
      if (this.isSafari && !this.isLeader && !this.useTranslate) {
        return false
        //safari에서는 로컬녹화 설정이 지원되지 않음. 따라서 사파리 브라우저에서 리더가 아니고 번역기능을 사용하지 않으면 세팅창이 필요가 없으므로 visible false
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
