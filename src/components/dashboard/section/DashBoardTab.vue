<template>
  <div class="dashboard-tab">
    <nav
      class="dashboard-tab__nav"
      :class="{ fix: !!fix, nolicense: !(hasWorkspace && !expireLicense) }"
    >
      <ul class="flex offsetwidth">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="hasWorkspace && component === tab.name"
          :text="tab.text"
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
import TabRefreshButton from 'components/partials/TabRefreshButton'
import { mapGetters } from 'vuex'
export default {
  name: 'DashBoardTab',
  components: {
    TabButton,
    TabRefreshButton,
    board: TabBoard,
    collabo: TabCollabo,
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
      return [
        {
          name: 'board',
          text: this.$t('common.dashboard'),
        },
        {
          name: 'collabo',
          text: this.$t('common.collabo_list'),
        },
      ]
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
  },
}
</script>
