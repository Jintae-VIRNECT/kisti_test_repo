<template>
  <div class="workspace-tab">
    <nav class="workspace-tab__nav" :class="{ fix: !!fix }">
      <ul class="flex offsetwidth">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="component === tab.name"
          :text="tab.text"
          @click.native="tabChange(tab.name)"
        ></tab-button>
      </ul>
    </nav>
    <component :is="component" :class="{ fix: fix }"></component>
  </div>
</template>

<script>
import TabButton from '../partials/WorkspaceTabButton'
import WorkspaceHistory from '../tab/WorkspaceHistory'
import WorkspaceRemote from '../tab/WorkspaceRemote'
import WorkspaceUser from '../tab/WorkspaceUser'
import WorkspaceSetting from '../tab/WorkspaceSetting'
import ListBadge from 'ListBadge'
export default {
  name: 'WorkspaceTab',
  components: {
    TabButton,
    history: WorkspaceHistory,
    remote: WorkspaceRemote,
    user: WorkspaceUser,
    setting: WorkspaceSetting,
    ListBadge,
  },
  data() {
    return {
      tabComponents: [
        {
          name: 'history',
          text: '최근 기록',
        },
        {
          name: 'remote',
          text: '원격 협업',
        },
        {
          name: 'user',
          text: '멤버',
        },
        {
          name: 'setting',
          text: '환경 설정',
        },
      ],
      component: 'history',
    }
  },
  props: {
    fix: {
      type: [Number, Boolean],
      default: false,
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

  /* Lifecycles */
  mounted() {},
}
</script>
