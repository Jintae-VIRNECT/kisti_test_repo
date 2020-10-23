<template>
  <div class="dashboard-layout">
    <dash-board-header></dash-board-header>
    <vue2-scrollbar classes="dashboard-wrapper">
      <div class="dashboard-layout__contents">
        <dash-board-tab
          ref="tabSection"
          @tabChange="tabChange"
        ></dash-board-tab>
      </div>
    </vue2-scrollbar>
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
  height: 100%;
  overflow: hidden;
  background: #f8f8fa;
  > .vue-scrollbar__wrapper {
    height: 100%;
    margin-right: 0px;
    margin-bottom: 0px;

    > .vue-scrollbar__area {
      height: 100%;
      transition: none;
    }
  }
}
.dashboard-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  width: 100%;
  height: 100%;
  margin-right: 0;
  padding-bottom: 0;
}

.dashboard-layout__contents {
  position: relative;
  // height: calc(100% - #{$header_height});
  // margin-top: $header_height;
  padding: 68px 319.9994px 171px 319.9994px;
  overflow: hidden;
}
</style>
