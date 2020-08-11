<template>
  <div class="workspace-tab">
    <nav
      class="workspace-tab__nav"
      :class="{ fix: !!fix, nolicense: !(hasLicense && !expireLicense) }"
    >
      <ul class="flex offsetwidth">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="hasLicense && component === tab.name"
          :text="tab.text"
          @click.native="tabChange(tab.name)"
        ></tab-button>
        <transition name="opacity">
          <li class="workspace-tab__side" v-if="fix">
            <button
              v-if="hasLicense && !expireLicense"
              class="btn"
              @click="createRoom"
            >
              {{ $t('workspace.create_room') }}
            </button>
          </li>
        </transition>
      </ul>
    </nav>
    <component
      v-if="hasLicense && !expireLicense"
      :is="component"
      :class="{ fix: fix }"
    ></component>
    <workspace-license v-else-if="!hasLicense"></workspace-license>
    <workspace-expire v-else></workspace-expire>
  </div>
</template>

<script>
import TabButton from '../partials/WorkspaceTabButton'
import WorkspaceHistory from '../tab/WorkspaceHistory'
import WorkspaceRemote from '../tab/WorkspaceRemote'
import WorkspaceUser from '../tab/WorkspaceUser'
import WorkspaceSetting from '../tab/WorkspaceSetting'
import WorkspaceLicense from './WorkspaceLicense'
import WorkspaceExpire from './WorkspaceExpire'
import ListBadge from 'ListBadge'
import { mapGetters } from 'vuex'
export default {
  name: 'WorkspaceTab',
  components: {
    TabButton,
    history: WorkspaceHistory,
    remote: WorkspaceRemote,
    user: WorkspaceUser,
    setting: WorkspaceSetting,
    ListBadge,
    WorkspaceLicense,
    WorkspaceExpire,
  },
  data() {
    return {
      tabComponents: [
        {
          name: 'history',
          text: this.$t('workspace.history'),
        },
        {
          name: 'remote',
          text: this.$t('workspace.remote'),
        },
        {
          name: 'user',
          text: this.$t('workspace.user'),
        },
        {
          name: 'setting',
          text: this.$t('workspace.setting'),
        },
      ],
      component: 'history',
    }
  },
  computed: {
    ...mapGetters(['expireLicense']),
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
    createRoom() {
      this.$eventBus.$emit('openCreateRoom')
    },
  },
}
</script>
