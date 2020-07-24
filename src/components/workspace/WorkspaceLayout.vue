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
          :license="hasLicense"
        ></workspace-welcome>

        <workspace-tab
          ref="tabSection"
          :fix="tabFix"
          :license="hasLicense"
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
import { getLicense } from 'api/workspace/license'
import RecordList from 'LocalRecordList'
import confirmMixin from 'mixins/confirm'

import { mapActions } from 'vuex'
export default {
  name: 'WorkspaceLayout',
  async beforeRouteEnter(to, from, next) {
    const authInfo = await auth.init()
    if (!auth.isLogin) {
      auth.login()
    } else {
      // const res = await getLicense(
      //   authInfo.workspace[0].uuid,
      //   authInfo.account.uuid,
      // )
      next(vm => {
        // vm.hasLicense =
        vm.init(authInfo)
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
      hasLicense: true,
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
    init(authInfo) {
      this.updateAccount(authInfo.account)
      this.initWorkspace(authInfo.workspace)
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
    // this.$nextTick(async () => {
    //   const license = await getLicense(
    //     this.workspace.uuid,
    //     await this.account.uuid,
    //   )
    //   this.hasLicense = license

    //   if (!license) {
    //     this.confirmDefault('라이선스가 만료되어 서비스 사용이 불가 합니다.​', {
    //       text: '확인',
    //       action: () => {
    //         this.$eventBus.$emit('showLicensePage')
    //       },
    //     })
    //     return false
    //   }
    // })

    this.tabTop = this.$refs['tabSection'].$el.offsetTop
    this.$eventBus.$on('filelist:open', this.toggleList)
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
