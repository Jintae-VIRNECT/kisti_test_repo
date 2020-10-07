<template>
  <div class="dashboard--wrapper">
    <dash-board-header></dash-board-header>
    <div class="content">content</div>
    <dash-board-footer></dash-board-footer>
  </div>
</template>

<script>
import DashBoardHeader from 'components/header/Header'
import DashBoardFooter from 'components/footer/Footer'
import auth from 'utils/auth'
import { mapActions } from 'vuex'
import { getLicense } from 'api/http/account'
export default {
  name: 'DashBoardLayout',

  // async beforeRouteEnter(to, from, next) {},

  components: {
    DashBoardHeader,
    DashBoardFooter,
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
  },
}
</script>

<style lang="scss">
@import '~assets/style/layout.scss';
@import '~assets/style/dashboard.scss';
.dashboard--wrapper {
  position: relative;
  width: 100%;
  // height: 100%;
  height: 100vh;
}

.content {
  height: 100%;
  background: #f8f8fa;
}
</style>
