<template>
  <div class="dashboard-layout">
    <dash-board-header></dash-board-header>
    <vue2-scrollbar ref="dashboardScroller" classes="dashboard-wrapper">
      <div>
        <div class="dashboard-layout__contents offsetwidth">
          <dash-board-tab
            ref="tabSection"
            @tabChange="tabChange"
          ></dash-board-tab>
        </div>
        <dash-board-footer></dash-board-footer>
      </div>
    </vue2-scrollbar>
    <player :url="url" :visible.sync="showPlayer"></player>
    <fab></fab>
  </div>
</template>

<script>
import DashBoardHeader from 'components/header/Header'
import DashBoardFooter from 'components/footer/Footer'
import DashBoardTab from 'components/dashboard/section/DashBoardTab'
import { mapActions } from 'vuex'
import { getLicense, getCompanyInfo } from 'api/http/account'
import langMixin from 'mixins/language'
import auth, { getWsSettings } from 'utils/auth'

import Player from 'components/dashboard/modal/ModalPlayer'
import Fab from 'Fab'

export default {
  name: 'DashBoardLayout',

  async beforeRouteEnter(to, from, next) {
    const authInfo = await auth.init()
    if (!auth.isLogin) {
      await auth.login()
    } else {
      const res = await getLicense({ userId: authInfo.account.uuid })
      const workspaces = res.myPlanInfoList.filter(
        plan => plan.planProduct === 'REMOTE',
      )
      if (workspaces.length === 0) {
        next(vm => {
          location.href = window.urls.remote
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
  mixins: [langMixin],
  components: {
    DashBoardHeader,
    DashBoardFooter,
    DashBoardTab,
    Player,
    Fab,
  },

  data() {
    return {
      showPlayer: false,
      url: null,
    }
  },

  watch: {
    async workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        this.checkCompany(val.uuid)
        await getWsSettings({ workspaceId: val.uuid })
        setTimeout(() => {
          this.$eventBus.$emit('update:settings')
        })
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
      'setCompanyInfo',
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

    tabChange() {},
    openPlayer(url) {
      this.showPlayer = true
      this.url = url
    },
    scrollTop() {
      this.$refs['dashboardScroller'].scrollToY(0)
    },
    async checkCompany(workspaceId) {
      const res = await getCompanyInfo({
        userId: this.account.uuid,
        workspaceId,
      })

      const languageCodes = res.languageCodes || []

      this.setCompanyInfo({
        companyCode: res.companyCode,
        translation: res.translation,
        tts: res.tts,
        sttSync: res.sttSync,
        sttStreaming: res.sttStreaming,
        recording: res.recording,
        localRecording: res.localRecording,
        storage: res.storage,
        sessionType: res.sessionType,
        languageCodes,
        audioRestrictedMode: false, //res.audioRestrictedMode,
        videoRestrictedMode: res.videoRestrictedMode,
      })
    },
  },
  async mounted() {
    this.mx_changeLang()
    this.$eventBus.$on('open::player', this.openPlayer)
    this.$eventBus.$on('scroll:reset:dashboard', this.scrollTop)

    await getWsSettings({ workspaceId: this.workspace.uuid })
    this.$eventBus.$emit('update:settings')
  },
  beforeDestroy() {
    this.$eventBus.$off('open::player')
    this.$eventBus.$off('scroll:reset:dashboard')
  },
}
</script>

<style lang="scss">
@import '~assets/style/dashboard.scss';
</style>
