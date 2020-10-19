<template>
  <div class="dashboard-layout">
    <dash-board-header></dash-board-header>
    <div class="dashboard-layout__contents">
      <dash-board-tab ref="tabSection" @tabChange="tabChange"></dash-board-tab>
    </div>
    <dash-board-footer></dash-board-footer>
  </div>
</template>

<script>
import DashBoardHeader from 'components/header/Header'
import DashBoardFooter from 'components/footer/Footer'
import DashBoardTab from 'components/section/DashBoardTab'
import { mapActions } from 'vuex'
import { getLicense } from 'api/http/account'
import auth from 'utils/auth'
export default {
  name: 'DashBoardLayout',

  // async beforeRouteEnter(to, from, next) {},

  components: {
    DashBoardHeader,
    DashBoardFooter,
    DashBoardTab,
  },
  async beforeCreate() {
    const authInfo = await auth.init()
    if (!auth.isLogin) {
      auth.login()
    } else {
      const res = await getLicense({ userId: authInfo.account.uuid })
      const workspaces = res.myPlanInfoList.filter(
        plan => plan.planProduct === 'REMOTE',
      )
      if (workspaces.length === 0) {
        this.license = false
        this.init(authInfo)
      } else {
        this.license = true
        this.init(authInfo, workspaces)
      }
    }
  },
  methods: {
    ...mapActions([
      'updateAccount',
      'initWorkspace',
      'changeWorkspace',
      'setDevices',
      'setRecord',
      'setAllow',
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
          if (!info || !info.workspaceId) continue
          workspace['role'] = info.role
        }
        this.initWorkspace(workspaces)
      }
    },

    tabChange() {
      // this.scrollTop()
      console.log('tabChanged')
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/layout.scss';
@import '~assets/style/dashboard.scss';
.dashboard-layout {
  position: relative;
  width: 100%;
  height: 110vh;
}
.dashboard-layout__contents {
  height: 100%;
  padding: 68px 319.9994px 0 319.9994px;
  background: #f8f8fa;
}
</style>
