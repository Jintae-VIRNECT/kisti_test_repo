<template>
  <section class="remote-layout">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper workspace"
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
        v-if="!isOnpremise && showCookie"
        :visible.sync="showCookie"
      ></cookie-policy>
      <record-list
        v-if="useLocalRecording"
        :visible.sync="showList"
      ></record-list>
      <device-denied :visible.sync="showDenied"></device-denied>
    </vue2-scrollbar>
    <plan-overflow :visible.sync="showPlanOverflow"></plan-overflow>
    <room-loading
      :visible.sync="showLoading"
      :isOpenRoom="isOpenRoom"
      :isJoin="isJoin"
    ></room-loading>
    <openroom-float-button
      v-if="workspace && workspace.uuid && tabName === 'user'"
    ></openroom-float-button>
    <collabo-float-button
      v-if="workspace && workspace.uuid && tabName === 'user'"
    ></collabo-float-button>
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
import authStatusCallbackMixin from 'mixins/authStatusCallback'
import errorMsgMixin from 'mixins/errorMsg'

import DeviceDenied from './modal/WorkspaceDeviceDenied'
import PlanOverflow from './modal/WorkspacePlanOverflow'
import RoomLoading from './modal/WorkspaceRoomLoading'
import { mapActions, mapGetters } from 'vuex'

import { PLAN_STATUS } from 'configs/status.config'
import { ERROR } from 'configs/error.config'
import { URLS } from 'configs/env.config'
import { USER_TYPE } from 'configs/remote.config'

import { MyStorage } from 'utils/storage'
import { initAudio } from 'plugins/remote/tts/audio'

export default {
  name: 'WorkspaceLayout',
  async beforeRouteEnter(to, from, next) {
    const hasToken = await auth.check()
    if (!hasToken) {
      auth.login()
    } else {
      next(vm => {
        vm.$call.leave()
        if (to.name === 'workspace' && from.name === 'service') {
          vm.$data.showLeaveMessage = false
        } else {
          vm.$data.showLeaveMessage = true
        }
      })
    }
  },
  mixins: [
    confirmMixin,
    langMixin,
    toastMixin,
    authStatusCallbackMixin,
    errorMsgMixin,
  ],
  components: {
    HeaderSection,
    WorkspaceWelcome,
    WorkspaceTab,
    RecordList,
    DeviceDenied,
    PlanOverflow,
    RoomLoading,
    CookiePolicy: () => import('CookiePolicy'),
    CollaboFloatButton: () => import('./partials/CollaboFloatButton'),
    OpenroomFloatButton: () => import('./partials/OpenroomFloatButton'),
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
      showLoading: false,
      isOpenRoom: false,
      isJoin: false,
      inited: false,
      tabName: 'remote',

      showLeaveMessage: true,
    }
  },
  watch: {
    async workspace(val, oldVal) {
      if (val.uuid && val.uuid !== oldVal.uuid) {
        await getSettings({ workspaceId: val.uuid })
        this.$eventBus.$emit('update:settings')

        this.checkPlan(val)
        this.checkCompany(val.uuid)
        this.checkLicense(val.uuid)

        //멤버 상태 유저 정보 등록/워크스페이스 업데이트
        auth.initAuthConnection(
          val.uuid,
          this.onDuplicatedRegistration,
          this.onRemoteExitReceived,
          this.onForceLogoutReceived,
          this.onWorkspaceDuplicated,
          this.onRegistrationFail,
        )
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
    ...mapGetters([
      'useLocalRecording',
      'statusSessionId', //멤버 상태 소켓에서 발급받은 session id
    ]),
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
      'setAutoServerRecord',
    ]),

    async init() {
      this.inited = false
      const authInfo = await auth.init()
      if (!auth.isLogin) {
        auth.login()
        return
      } else {
        this.logoutGuest(authInfo.account.userType)
        this.initMyStorage(authInfo.account.uuid)
        this.getSavedStorageDatas()

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
        await getSettings({ workspaceId: this.workspace.uuid })
        this.$eventBus.$emit('update:settings')

        this.$nextTick(async () => {
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
        if (err.code === ERROR.NO_LICENSE) {
          this.clearWorkspace(this.workspace.uuid)
          this.showErrorToast(err.code)
        }
      }
    },
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
    onScroll(scrollX, scrollY) {
      this.$eventBus.$emit('scroll:memberlist')

      if (scrollY === 0 && this.tabFix) {
        this.scrollTop()
        return
      }
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
    tabChange(tabName) {
      this.scrollTop()
      this.tabName = tabName
    },
    toggleList() {
      this.showList = true
    },
    initMyStorage(uuid) {
      window.myStorage = new MyStorage(uuid)
    },
    getSavedStorageDatas() {
      const settingMap = {
        deviceInfo: this.setDevices,
        recordInfo: this.setRecord,
        translate: this.setTranslate,
        serverRecordInfo: this.setServerRecord,
        screenStrict: this.setScreenStrict,
        autoServerRecord: this.setAutoServerRecord,
      }

      Object.keys(settingMap).forEach(key => {
        const setting = window.myStorage.getItem(key)
        if (setting) {
          const setFunc = settingMap[key]
          setFunc(setting)
        }
      })
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
        timeout: res.timeout !== undefined ? res.timeout : 60, //협업 연장 질의 팝업 싸이클을 정하는 값. 분 단위
      })
    },
    showRoomLoading({ toggle, isOpenRoom = false, isJoin = false }) {
      this.showLoading = toggle
      if (this.showLoading) {
        this.isOpenRoom = isOpenRoom
        this.isJoin = isJoin
      }
    },
    setTabTop() {
      this.tabTop = this.$refs['tabSection'].$el.offsetTop
    },
    async logoutGuest(userType) {
      if (userType === USER_TYPE.GUEST_USER) {
        //no redirect
        await auth.logout(false)
        location.href = `${URLS['console']}`
      }
    },
    initHistoryBackEvent() {
      window.history.pushState({}, '', document.location.href)

      window.onpopstate = () => {
        //service -> home 화면 접근시 호출되는 경우를 방지하기 위함.
        if (!this.showLeaveMessage) {
          this.showLeaveMessage = true
          return
        }

        this.confirmCancel(
          this.$t('workspace.confirm_leave_service'),
          {
            text: this.$t('button.confirm'),
            action: async () => {
              const redirect = false
              await auth.logout(redirect)
              location.href = `${URLS['console']}`
            },
          },
          {
            text: this.$t('button.cancel'),
            action: () => {
              window.history.pushState({}, '', document.location.href)
            },
          },
        )
      }
    },
  },

  /* Lifecycles */
  async created() {
    this.init()
    if (this.workspace && this.workspace.uuid) {
      this.checkLicense(this.workspace.uuid)
    }
  },
  async mounted() {
    initAudio()
    this.mx_changeLang()
    this.setTabTop()

    //반응형 대응 : 모바일 레이아웃으로 접근 후 PC화면으로 스크린 크기 변화 시 tabTop값이 업데이트 되어야 sticky header가 정상 동작하므로
    window.addEventListener('resize', this.setTabTop)

    this.$eventBus.$on('scroll:reset:workspace', this.scrollTop)
    this.$eventBus.$on('filelist:open', this.toggleList)
    this.$eventBus.$on('devicedenied:show', this.showDeviceDenied)
    this.$eventBus.$on('roomloading:show', this.showRoomLoading)

    if (this.isMobileSize) {
      this.initHistoryBackEvent()
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.setTabTop)
    this.$eventBus.$off('scroll:reset:workspace', this.scrollTop)
    this.$eventBus.$off('filelist:open')
    this.$eventBus.$off('devicedenied:show')
    this.$eventBus.$off('roomloading:show', this.showRoomLoading)
  },
}
</script>

<style lang="scss" src="assets/style/workspace.scss"></style>
