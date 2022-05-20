<template>
  <full-screen-modal
    v-if="menus.length > 0"
    class="service-mobile-setting-modal"
    :title="$t('service.setting_service')"
    :visible="visible"
    @close="close"
  >
    <nav class="setting-nav">
      <ul>
        <button
          v-for="(menu, idx) of menus"
          :key="menu.key"
          :class="{ active: tabView === menu.key }"
          @click="tabChange(menu.key, idx)"
        >
          {{ menu.text }}
        </button>
      </ul>
    </nav>
    <div class="setting-view">
      <div class="setting-view__header">{{ menus[tabIdx].text }}</div>

      <div class="setting-view__body">
        <service-set-pointing
          v-if="tabView === 'pointing'"
        ></service-set-pointing>
        <service-set-server-record
          v-else-if="tabView === 'server-record'"
        ></service-set-server-record>
        <service-set-local-record
          v-else-if="tabView === 'local-record' && isLeader"
        ></service-set-local-record>
        <service-mobile-set-translate v-else></service-mobile-set-translate>
      </div>
    </div>
  </full-screen-modal>
</template>

<script>
import FullScreenModal from '../../modules/FullScreenModal'
import ServiceSetPointing from './partials/ServiceSetPointing'
import ServiceSetServerRecord from './partials/ServiceSetServerRecord'
import ServiceMobileSetTranslate from './partials/ServiceMobileSetTranslate'
import ServiceSetLocalRecord from './partials/ServiceSetLocalRecord'
import { ROLE } from 'configs/remote.config'
import { mapGetters } from 'vuex'

export default {
  name: 'MobileSettingModal',
  components: {
    FullScreenModal,
    ServiceSetPointing,
    ServiceSetServerRecord,
    ServiceMobileSetTranslate,
    ServiceSetLocalRecord,
  },
  props: {
    visible: {
      type: Boolean,
    },
    beforeClose: {
      type: Function,
    },
  },
  data() {
    return {
      tabView: '',
      tabIdx: 0,
    }
  },
  computed: {
    ...mapGetters(['useRecording', 'useTranslate']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    menus() {
      let menus = [
        {
          key: 'pointing',
          text: this.$t('service.setting_pointing'),
        },
        {
          key: 'server-record',
          text: this.$t('service.setting_server_record'),
        },
        {
          key: 'local-record',
          text: this.$t('service.setting_local_record'),
        },
      ]

      if (this.useTranslate) {
        menus.push({
          key: 'translate',
          text: this.$t('workspace.setting_translate'),
        })
      }

      if (!this.isLeader) {
        //리더가 아닌 경우
        menus = menus.filter(item => item.key === 'translate')
      } else if (!this.useRecording) {
        //리더인 경우에서 서버 녹화 활성화 안된 경우
        menus.splice(1, 1)
      }

      return menus
    },
  },
  methods: {
    close() {
      this.tabView = this.menus[0].key
      this.beforeClose()
    },
    tabChange(key, idx) {
      this.tabView = key
      this.tabIdx = idx
    },
  },
  created() {
    this.tabView = this.isLeader ? 'pointing' : 'translate'
  },
}
</script>

<style lang="scss" scoped>
.setting-view {
  flex: unset;
}
</style>
