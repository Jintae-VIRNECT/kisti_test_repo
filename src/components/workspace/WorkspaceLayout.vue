<template>
  <section class="remote-layout">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper"
      ref="wrapperScroller"
      @onScroll="onScroll"
    >
      <div class="workspace-wrapper">
        <workspace-welcome ref="welcomeSection"></workspace-welcome>
        <workspace-tab
          ref="tabSection"
          :fix="tabFix"
          @tabChange="tabChange"
        ></workspace-tab>
      </div>
      <cookie-policy
        v-if="showCookie"
        :visible.sync="showCookie"
      ></cookie-policy>
      <record-list :visible.sync="showList"></record-list>
    </vue2-scrollbar>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
import { mapActions } from 'vuex'
import auth from 'utils/auth'
import RecordList from 'LocalRecordList'
export default {
  name: 'WorkspaceLayout',
  async beforeRouteEnter(to, from, next) {
    const account = await auth.init()
    if (!auth.isLogin) {
      auth.login()
    } else {
      next(vm => {
        vm.init(account)
      })
    }
  },
  components: {
    HeaderSection,
    WorkspaceWelcome,
    WorkspaceTab,
    RecordList,
    CookiePolicy: () => import('CookiePolicy'),
  },
  data() {
    const cookie = localStorage.getItem('ServiceCookiesAgree')
    return {
      tabFix: false,
      tabTop: 0,
      showCookie: !cookie,
      showList: false,
    }
  },
  methods: {
    ...mapActions([
      'updateAccount',
      'initWorkspace',
      'setDevices',
      'setRecord',
      'setAllow',
    ]),
    init(account) {
      this.updateAccount(account.myInfo)
      this.initWorkspace(account.myWorkspaces)
    },
    onScroll(scrollX, scrollY) {
      if (scrollY > this.tabTop) {
        this.tabFix = true
      } else {
        this.tabFix = false
      }
    },
    tabChange() {
      this.$refs['wrapperScroller'].scrollToY(0)
      this.tabFix = false
    },
    toggleList() {
      this.showList = true
    },
    savedStorageDatas() {
      const deviceInfo = this.$localStorage.getItem('deviceInfo')
      if (deviceInfo) {
        this.setDevices(deviceInfo)
      }
      const recordInfo = this.$localStorage.getItem('recordInfo')
      if (recordInfo) {
        this.setRecord(recordInfo)
      }
      const allow = this.$localStorage.getItem('allow')
      console.log(allow)
      if (allow) {
        this.setAllow(allow)
      }
    },
  },

  /* Lifecycles */
  created() {
    this.savedStorageDatas()
  },
  mounted() {
    this.tabTop = this.$refs['tabSection'].$el.offsetTop
    this.$eventBus.$on('filelist:open', this.toggleList)
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
