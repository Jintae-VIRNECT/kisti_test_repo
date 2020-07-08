<template>
  <section class="remote-layout">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper"
      ref="wrapperScroller"
      @onScroll="onScroll"
    >
      <div class="workspace-wrapper">
        <workspace-welcome
          ref="welcomeSection"
          :license="license"
        ></workspace-welcome>

        <workspace-tab
          ref="tabSection"
          :fix="tabFix"
          :license="license"
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
import auth from 'utils/auth'
import { checkLicense } from 'utils/license'
import RecordList from 'LocalRecordList'
import confirmMixin from 'mixins/confirm'

import { mapActions } from 'vuex'
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
  mixins: [confirmMixin],
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
      license: true,
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
    this.$nextTick(async () => {
      const noLicenseCallback = () => {
        this.confirmDefault('라이선스가 만료되어 서비스 사용이 불가 합니다.​', {
          text: '확인',
          action: () => {
            this.$eventBus.$emit('showLicensePage')
          },
        })
      }
      const license = await checkLicense(
        this.workspace.uuid,
        await this.account.uuid,
        noLicenseCallback,
      )
      this.license = license
    })

    this.tabTop = this.$refs['tabSection'].$el.offsetTop
    this.$eventBus.$on('filelist:open', this.toggleList)
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
