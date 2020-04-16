<template>
  <div class="workspace-tab">
    <nav class="workspace-tab__nav" :class="{ fix: fix }">
      <ul class="flex">
        <tab-button
          v-for="tab of tabComponents"
          :key="tab.name"
          :active="component === tab.name"
          :text="tab.text"
          @click.native="tabChange(tab.name)"
        ></tab-button>
      </ul>
    </nav>
    <vue2-scrollbar classes="workspace-scroller" :canScroll="canScroll">
      <component :is="component"></component>
    </vue2-scrollbar>
  </div>
</template>

<script>
import TabButton from '../partials/WorkspaceTabButton'
import WorkspaceHistory from '../tab/WorkspaceHistory'
import WorkspaceRemote from '../tab/WorkspaceRemote'
import WorkspaceUser from '../tab/WorkspaceUser'
import WorkspaceSetting from '../tab/WorkspaceSetting'
export default {
  name: 'WorkspaceTab',
  components: {
    TabButton,
    history: WorkspaceHistory,
    remote: WorkspaceRemote,
    user: WorkspaceUser,
    setting: WorkspaceSetting,
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
    canScroll: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    fix() {
      if (this.$el && this.$el.offsetTop < 56) {
        return true
      } else {
        return false
      }
    },
    marginTop() {
      return this.$el ? this.$el.offsetTop : 0
    },
  },
  watch: {
    marginTop(val) {
      console.log(val)
      console.log(this.fix)
    },
  },
  methods: {
    tabChange(tabName) {
      this.component = tabName
      console.log(this.fix)
      console.log(this.$el.offsetTop)
    },
  },

  /* Lifecycles */
  mounted() {
    console.log(this.$el)
    window.addEventListener('scroll', e => {
      console.log(this.$el.offsetTop)
    })
  },
}
</script>
