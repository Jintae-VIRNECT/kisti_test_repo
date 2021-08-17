<template>
  <div class="workspace-tab">
    <nav
      class="workspace-tab__nav"
      :class="{
        fix: !!fix,
        nolicense: !(hasWorkspace && !expireLicense),
        'tab-mobile': !(hasWorkspace && !expireLicense),
      }"
    >
      <ul class="flex offsetwidth">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="hasWorkspace && component === tab.name"
          :text="tab.text"
          @click.native="tabChange(tab.name)"
        ></tab-button>
        <transition name="opacity">
          <li class="workspace-tab__side" v-if="fix">
            <button
              v-if="hasWorkspace && !expireLicense"
              class="btn"
              @click="createRoom"
            >
              {{ $t('workspace.create_room') }}
            </button>
            <button
              v-if="useOpenRoom && hasWorkspace && !expireLicense"
              class="btn workspace-welcome__open"
              @click="createOpenRoom"
            >
              {{ $t('workspace.create_open_room') }}
            </button>
          </li>
        </transition>
      </ul>
    </nav>
    <template v-if="inited">
      <workspace-license v-if="!hasLicense"></workspace-license>
      <workspace-expire v-else-if="expireLicense"></workspace-expire>
      <workspace-select
        v-else-if="!workspace || !workspace.uuid"
      ></workspace-select>
      <keep-alive include="WorkspaceHistory" v-else>
        <component :is="component" :class="{ fix: fix }"></component>
      </keep-alive>
    </template>
    <transition name="opacity">
      <workspace-skeleton v-if="!inited"></workspace-skeleton>
    </transition>
  </div>
</template>

<script>
import TabButton from '../partials/WorkspaceTabButton'
import WorkspaceSkeleton from '../tab/WorkspaceSkeleton'
import WorkspaceHistory from '../tab/WorkspaceHistory'
import WorkspaceRemote from '../tab/WorkspaceRemote'
import WorkspaceUser from '../tab/WorkspaceUser'
import WorkspaceSetting from '../tab/WorkspaceSetting'
import WorkspaceLicense from '../tab/WorkspaceLicense'
import WorkspaceExpire from '../tab/WorkspaceExpire'
import WorkspaceSelect from '../tab/WorkspaceSelect'
import { mapGetters } from 'vuex'
export default {
  name: 'WorkspaceTab',
  components: {
    TabButton,
    WorkspaceSkeleton,
    history: WorkspaceHistory,
    remote: WorkspaceRemote,
    user: WorkspaceUser,
    setting: WorkspaceSetting,
    WorkspaceLicense,
    WorkspaceExpire,
    WorkspaceSelect,
  },
  data() {
    return {
      component: 'remote',
    }
  },
  computed: {
    ...mapGetters(['expireLicense', 'useOpenRoom']),
    hasWorkspace() {
      return this.workspace && this.workspace.uuid && this.hasLicense
    },
    tabComponents() {
      return [
        {
          name: 'remote',
          text: this.$t('workspace.remote'),
        },
        {
          name: 'history',
          text: this.$t('workspace.history'),
        },
        {
          name: 'user',
          text: this.$t('workspace.user'),
        },
        {
          name: 'setting',
          text: this.$t('workspace.setting'),
        },
      ]
    },
  },
  props: {
    fix: {
      type: [Number, Boolean],
      default: false,
    },
    inited: {
      type: Boolean,
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
      this.$eventBus.$emit('open:modal:create')
    },
    createOpenRoom() {
      this.$eventBus.$emit('open:modal:createOpen')
    },
  },
}
</script>
