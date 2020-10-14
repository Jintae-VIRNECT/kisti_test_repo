<template>
  <section class="remote-layout">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper"
      ref="wrapperScroller"
      @onScroll="onScroll"
      :onMaxScroll="handleMaxScroll"
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
      <device-denied :visible.sync="showDenied"></device-denied>
    </vue2-scrollbar>
    <plan-overflow :visible.sync="showPlanOverflow"></plan-overflow>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
import auth from 'utils/auth'
import { getLicense } from 'api/http/account'
import RecordList from 'LocalRecordList'
import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import DeviceDenied from './modal/WorkspaceDeviceDenied'
import PlanOverflow from './modal/WorkspacePlanOverflow'
import { mapActions } from 'vuex'
import { PLAN_STATUS } from 'configs/status.config'
import { USE_TRANSLATE } from 'configs/env.config'

export default {
  name: 'WorkspaceLayout',
  async beforeRouteEnter(to, from, next) {
    const authInfo = await auth.init()
    if (!auth.isLogin) {
      auth.login()
    } else {
      const res = await getLicense({ userId: authInfo.account.uuid })
      const workspaces = res.myPlanInfoList.filter(
        plan => plan.planProduct === 'REMOTE',
      )
      if (workspaces.length === 0) {
        next(vm => {
          vm.license = false
          vm.init(authInfo)
        })
      } else {
        next(vm => {
          vm.license = true
          vm.init(authInfo, workspaces)
        })
      }
    }
  },
  mixins: [confirmMixin, langMixin],
  components: {
    HeaderSection,
    WorkspaceWelcome,
    WorkspaceTab,
    RecordList,
    DeviceDenied,
    PlanOverflow,
    CookiePolicy: () => import('CookiePolicy'),
  },
  data() {
    const cookie = localStorage.getItem('ServiceCookiesAgree')
    return {
      tabFix: false,
      tabTop: 0,
      showCookie: !cookie,
      showList: false,
      license: true,
      showDenied: false,
      showPlanOverflow: false,
    }
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        this.checkPlan(val)
      }
    },
  },
  methods: {
    ...mapActions([
      'updateAccount',
      'initWorkspace',
      'changeWorkspace',
      'setDevices',
      'setRecord',
      'setAllow',
      'setTranslate',
    ]),
    init(authInfo, workspaces) {
      this.updateAccount({
        ...authInfo.account,
        licenseEmpty: this.license,
      })
      if (workspaces) {
        for (let workspace of workspaces) {
          const info = authInfo.workspace.find(
            work => work.uuid === workspace.workspaceId,
          )
          // if (!info || !info.workspaceId) continue
          workspace['role'] = info.role
        }
        this.initWorkspace(workspaces)
      }
    },
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
    onScroll(scrollX, scrollY) {
      if (scrollY > this.tabTop) {
        this.tabFix = true
      } else {
        this.tabFix = false
      }
    },
    scrollTop() {
      this.$refs['wrapperScroller'].scrollToY(0)
      this.tabFix = false
    },
    tabChange() {
      this.scrollTop()
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
      if (allow) {
        this.setAllow(allow)
      }
      // TODO: KINTEX
      if (USE_TRANSLATE) {
        const translateInfo = this.$localStorage.getItem('translate')
        if (translateInfo) {
          this.setTranslate(translateInfo)
        }
      } else {
        this.setTranslate({
          flag: false,
        })
      }
    },
    showDeviceDenied() {
      this.showDenied = true
    },
    async checkPlan(workspace) {
      if (workspace.planStatus === PLAN_STATUS.EXCEEDED) {
        this.showPlanOverflow = true
      } else {
        this.showPlanOverflow = false
      }
    },
  },

  /* Lifecycles */
  created() {
    this.savedStorageDatas()
  },
  mounted() {
    this.mx_changeLang()
    this.tabTop = this.$refs['tabSection'].$el.offsetTop
    this.$eventBus.$on('scroll:reset:workspace', this.scrollTop)
    this.$eventBus.$on('filelist:open', this.toggleList)
    this.$eventBus.$on('devicedenied:show', this.showDeviceDenied)
  },
  beforeDestroy() {
    this.$eventBus.$off('scroll:reset:workspace', this.scrollTop)
    this.$eventBus.$off('filelist:open')
    this.$eventBus.$off('devicedenied:show')
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
