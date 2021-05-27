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
        <workspace-welcome
          ref="welcomeSection"
          :inited="inited"
        ></workspace-welcome>

        <workspace-tab
          ref="tabSection"
          :fix="tabFix"
          :inited="inited"
          @tabChange="tabChange"
        ></workspace-tab>
      </div>
      <cookie-policy
        v-if="!onpremise && showCookie"
        :visible.sync="showCookie"
      ></cookie-policy>
      <record-list
        v-if="useLocalRecording"
        :visible.sync="showList"
      ></record-list>
      <device-denied :visible.sync="showDenied"></device-denied>
    </vue2-scrollbar>
    <plan-overflow :visible.sync="showPlanOverflow"></plan-overflow>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import WorkspaceWelcome from './section/WorkspaceWelcome'
import WorkspaceTab from './section/WorkspaceTab'
import auth, { getSettings } from 'utils/auth'
import { getLicense, workspaceLicense, getCompanyInfo } from 'api/http/account'
import RecordList from 'LocalRecordList'
import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'
import DeviceDenied from './modal/WorkspaceDeviceDenied'
import PlanOverflow from './modal/WorkspacePlanOverflow'
import { mapActions, mapGetters } from 'vuex'
import { PLAN_STATUS } from 'configs/status.config'
import { RUNTIME, RUNTIME_ENV, URLS } from 'configs/env.config'
import { MyStorage } from 'utils/storage'
import { sendPush } from 'api/http/message'
import { EVENT } from 'configs/push.config'

export default {
  name: 'WorkspaceLayout',
  async beforeRouteEnter(to, from, next) {
    const hasToken = await auth.check()
    if (!hasToken) {
      auth.login()
    } else {
      await getSettings()
      next(vm => {
        vm.$call.leave()
      })
    }
  },
  mixins: [confirmMixin, langMixin, toastMixin],
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
      inited: false,
    }
  },
  watch: {
    async workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        this.checkPlan(val)
        this.checkCompany(val.uuid)
        this.checkLicense(val.uuid)
      } else if (!val.uuid) {
        const res = await getLicense({ userId: this.account.uuid })
        const myPlans = res.myPlanInfoList.filter(
          plan => plan.planProduct === 'REMOTE',
        )
        if (myPlans.length === 0) {
          this.license = false
        } else {
          this.license = true
        }
        this.updateAccount({
          licenseEmpty: this.license,
        })
      }
    },
  },
  computed: {
    ...mapGetters(['useLocalRecording']),
    onpremise() {
      return RUNTIME.ONPREMISE === RUNTIME_ENV
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
      'setCompanyInfo',
      'setServerRecord',
      'setScreenStrict',
      'clearWorkspace',
    ]),

    //중복된 기 접속자가 있는 경우 처리 콜백
    onDuplicatedRegistration(
      { currentStatus, sessionId, userId, myInfo },
      socket,
    ) {
      //로그인 된 기 접속자가 있는 경우 : 팝업으로 강제 로그아웃 실행 여부 확인
      if (currentStatus === 'LOGIN') {
        const text = this.$t('workspace.confirm_duplicated_session_logout')
        const contents = { sessionId }
        const { uuid, name, email } = myInfo

        //원격종료
        const confirmAction = () => {
          sendPush(EVENT.FORCE_LOGOUT, [userId], contents) //기 접속자 로그아웃 처리 메시지 전송
          setTimeout(
            () =>
              //재등록 요청
              socket.send(
                JSON.stringify({
                  uuid,
                  name,
                  email,
                }),
              ),
            2000,
          ) //상대방 로그아웃 처리 소요 시간 고려하여 재등록 요청
        }
        //취소 : 로그인 시도했던 사용자 로그아웃 처리 및 로그인 페이지로 리디렉트
        const cancelAction = () => auth.logout()

        const confirm = {
          text: this.$t('button.force_logout'),
          action: confirmAction,
        }

        const cancel = {
          text: this.$t('button.cancel'),
          action: cancelAction,
        }

        this.confirmCancel(text, confirm, cancel)
      }
      //협업 중인 경우 : 팝업 띄운 후 로그인 페이지로 리디렉트
      else {
        const redirect = false
        auth.login(redirect) //바로 로그아웃 처리하고, 리디렉트는 팝업 엑션에서 실행한다

        const text = this.$t('workspace.confirm_duplicated_session_joined')
        const action = () =>
          (location.href = `${URLS['console']}/?continue=${location.href}`) //리디렉트
        this.confirmDefault(text, { action })
      }
    },
    async init() {
      this.inited = false
      const authInfo = await auth.init(this.onDuplicatedRegistration)
      if (!auth.isLogin) {
        auth.login()
        return
      } else {
        this.savedStorageDatas(authInfo.account.uuid)
        const res = await getLicense({ userId: authInfo.account.uuid })
        const myPlans = res.myPlanInfoList.filter(
          plan => plan.planProduct === 'REMOTE',
        )
        if (myPlans.length === 0) {
          this.license = false
        } else {
          this.license = true
        }
        this.updateAccount({
          ...authInfo.account,
          licenseEmpty: this.license,
        })
        if (myPlans.length > 0) {
          const workspaces = []
          for (let workspace of authInfo.workspace) {
            const info = myPlans.find(
              work => work.workspaceId === workspace.uuid,
            )
            if (!info || !info.workspaceId) continue
            workspaces.push({
              ...workspace,
              renewalDate: info.renewalDate,
              productPlanStatus: info.productPlanStatus,
            })
          }
          this.initWorkspace(workspaces)
        }
        this.$nextTick(() => {
          this.inited = true
        })
      }
    },
    async checkLicense(uuid) {
      uuid = uuid || this.workspace.uuid
      if (!uuid) {
        return
      }
      try {
        await workspaceLicense({
          workspaceId: uuid,
          userId: this.account.uuid,
        })
      } catch (err) {
        if (err.code === 5003) {
          this.clearWorkspace(this.workspace.uuid)
          this.toastError(this.$t('workspace.no_license'))
        }
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
      if (this.$el.querySelector('.tab-view__search input')) {
        this.$el.querySelector('.tab-view__search input').blur()
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
    savedStorageDatas(uuid) {
      window.myStorage = new MyStorage(uuid)
      const deviceInfo = window.myStorage.getItem('deviceInfo')
      if (deviceInfo) {
        this.setDevices(deviceInfo)
      }
      const recordInfo = window.myStorage.getItem('recordInfo')
      if (recordInfo) {
        this.setRecord(recordInfo)
      }
      // const allow = this.$localStorage.getItem('allow')
      // if (allow) {
      //   this.setAllow(allow)
      // }
      const translateInfo = window.myStorage.getItem('translate')
      if (translateInfo) {
        this.setTranslate(translateInfo)
      }
      const serverRecordInfo = window.myStorage.getItem('serverRecordInfo')
      if (serverRecordInfo) {
        this.setServerRecord(serverRecordInfo)
      }
      const screenStrict = window.myStorage.getItem('screenStrict')
      if (screenStrict) {
        this.setScreenStrict(screenStrict)
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
    async checkCompany(workspaceId) {
      const res = await getCompanyInfo({
        userId: this.account.uuid,
        workspaceId,
      })

      const languageCodes = res.languageCodes || []
      this.debug(
        'TRANSLATE LANGUAGE LIST::',
        ...languageCodes.map(language => language.text),
      )
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

  /* Lifecycles */
  async created() {
    this.init()
    if (this.workspace && this.workspace.uuid) {
      this.checkLicense(this.workspace.uuid)
    }
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
