<template>
  <full-screen-modal
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
        <service-mobile-set-translate v-else></service-mobile-set-translate>
      </div>
    </div>
  </full-screen-modal>
</template>

<script>
import FullScreenModal from '../../modules/FullScreenModal'
import ServiceSetPointing from './partials/ServiceSetPointing'
import ServiceMobileSetTranslate from './partials/ServiceMobileSetTranslate'
import { ROLE } from 'configs/remote.config'

export default {
  components: {
    FullScreenModal,
    ServiceSetPointing,
    ServiceMobileSetTranslate,
  },
  props: {
    visible: {
      type: Boolean,
      dafault: true,
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
          key: 'translate',
          text: this.$t('service.setting_translate'),
        },
      ]
      if (!this.isLeader) menus = menus.filter(item => item.key === 'translate')
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
