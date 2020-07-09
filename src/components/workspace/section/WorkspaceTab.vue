<template>
  <div class="workspace-tab">
    <nav class="workspace-tab__nav" :class="{ fix: !!fix }">
      <ul class="flex offsetwidth">
        <tab-button
          v-for="tab of tabComponents"
          :license="license"
          :key="tab.name"
          :active="license && component === tab.name"
          :text="tab.text"
          @click.native="tabChange(tab.name)"
        ></tab-button>
        <transition name="opacity">
          <li class="workspace-tab__side" v-if="fix">
            <button v-if="license" class="btn" @click="createRoom">
              원격 협업 생성
            </button>
          </li>
        </transition>
      </ul>
    </nav>
    <component
      v-if="!showLicensePage"
      :is="component"
      :class="{ fix: fix }"
    ></component>
    <workspace-license v-else></workspace-license>
  </div>
</template>

<script>
import TabButton from '../partials/WorkspaceTabButton'
import WorkspaceHistory from '../tab/WorkspaceHistory'
import WorkspaceRemote from '../tab/WorkspaceRemote'
import WorkspaceUser from '../tab/WorkspaceUser'
import WorkspaceSetting from '../tab/WorkspaceSetting'
import WorkspaceLicense from './WorkspaceLicense'
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
    WorkspaceLicense,
  },
  data() {
    return {
      showLicensePage: false,
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
    license: {
      type: Boolean,
      default: true,
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
    activeLicensePage() {
      this.showLicensePage = true
    },
  },

  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('showLicensePage', this.activeLicensePage)
  },
  beforeDestroy() {
    this.$eventBus.$off('showLicensePage')
  },
}
</script>
