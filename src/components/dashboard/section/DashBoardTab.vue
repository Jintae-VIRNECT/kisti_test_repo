<template>
  <div class="dashboard-tab">
    <nav
      class="dashboard-tab__nav"
      :class="{ fix: !!fix, nolicense: !(hasWorkspace && !expireLicense) }"
    >
      <ul class="flex">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="hasWorkspace && component === tab.name"
          :text="tab.text"
          :images="tab.images"
          @click.native="tabChange(tab.name)"
        ></tab-button>
      </ul>
    </nav>
    <tab-refresh-button v-if="component === 'board'"></tab-refresh-button>
    <component :is="component" :class="{ fix: fix }"></component>
  </div>
</template>

<script>
import TabButton from 'components/partials/TabButton'
import TabBoard from 'components/tab/TabBoard'
import TabCollabo from 'components/tab/TabCollabo'
import TabWorkspaceSubGroup from 'components/tab/TabWorkspaceSubGroup'
import TabRefreshButton from 'components/partials/TabRefreshButton'
import { WORKSPACE_ROLE } from 'configs/status.config'
import { getMemberInfo } from 'api/http/member'

import { mapGetters } from 'vuex'
export default {
  name: 'DashBoardTab',
  components: {
    TabButton,
    TabRefreshButton,
    board: TabBoard,
    collabo: TabCollabo,
    subGroup: TabWorkspaceSubGroup,
  },
  props: {
    fix: {
      type: [Number, Boolean],
      default: false,
    },
  },
  data() {
    return {
      component: 'board',
      isMaster: false,
    }
  },
  computed: {
    ...mapGetters(['expireLicense']),

    hasWorkspace() {
      if (this.workspace && this.workspace.uuid && this.hasLicense) {
        return true
      } else {
        return false
      }
    },
    tabComponents() {
      const tabs = [
        {
          name: 'board',
          text: this.$t('common.dashboard'),
          images: {
            off: require('assets/image/tab/ic_dashboard_off.svg'),
            on: require('assets/image/tab/ic_dashboard_on.svg'),
          },
        },
        {
          name: 'collabo',
          text: this.$t('common.collabo_list'),
          images: {
            off: require('assets/image/tab/ic_collabo_list_off.svg'),
            on: require('assets/image/tab/ic_collabo_list_on.svg'),
          },
        },
      ]
      const subGroupTab = {
        name: 'subGroup',
        text: this.$t('그룹 설정'),
        images: {
          off: require('assets/image/tab/ic_workspace_sub_group_off.svg'),
          on: require('assets/image/tab/ic_workspace_sub_group_on.svg'),
        },
      }

      if (this.isMaster) {
        tabs.push(subGroupTab)
      }

      return tabs
    },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        this.checkMaster()
      }
    },
  },
  methods: {
    tabChange(tabName) {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        this.component = tabName
        this.$emit('tabChange')
      })
    },

    async checkMaster() {
      const memberInfo = await getMemberInfo({
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      if (memberInfo.role === WORKSPACE_ROLE.MASTER) {
        this.isMaster = true
      } else {
        this.isMaster = false
      }
    },
  },
  mounted() {
    this.checkMaster()
  },
}
</script>
