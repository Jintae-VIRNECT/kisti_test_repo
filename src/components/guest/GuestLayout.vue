<template>
  <section class="remote-layout">
    <template v-if="serviceMode === 'web'">
      <header-section></header-section>
      <vue2-scrollbar
        classes="remote-wrapper"
        ref="wrapperScroller"
        :onMaxScroll="handleMaxScroll"
      >
        <guest-web></guest-web>
      </vue2-scrollbar>
    </template>
    <guest-mobile v-else-if="serviceMode === 'mobile'"></guest-mobile>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'

import confirmMixin from 'mixins/confirm'

import { getLatestRemoteAosAppInfo, getIntentLink } from 'utils/appCheck'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'
import authStatusCallbackMixin from 'mixins/authStatusCallback'

import { MyStorage } from 'utils/storage'
import { getConfigs } from 'utils/auth'

import { getCompanyInfo } from 'api/http/account'
import { getGuestInfo } from 'api/http/guest'
import { getWorkspaceInfo } from 'api/http/workspace'

import { mapActions, mapGetters } from 'vuex'

import { ROLE } from 'configs/remote.config'
import { ERROR } from 'configs/error.config'
import { URLS } from 'configs/env.config'

import GuestWeb from './GuestWeb'
import GuestMobile from './GuestMobile'

import auth, { setTokensToCookies } from 'utils/auth'

export default {
  name: 'GuestLayout',
  async beforeRouteEnter(to, from, next) {
    await getConfigs()

    next()
  },

  mixins: [
    confirmMixin,
    langMixin,
    toastMixin,
    errorMsgMixin,
    authStatusCallbackMixin,
  ],
  components: {
    GuestWeb,
    GuestMobile,
    HeaderSection,
  },
  data() {
    return {
      workspaceId: '',
      sessionId: '',

      serviceMode: '', //web, mobile
      packageName: '',
    }
  },
  computed: {
    ...mapGetters(['isMobileSize']),
  },
  methods: {
    ...mapActions(['setCompanyInfo', 'updateAccount', 'changeWorkspace']),

    async initGuestMember() {
      await auth.logout(false)

      const guestInfo = await getGuestInfo({ workspaceId: this.workspaceId })

      if (guestInfo.uuid === null) {
        throw { code: ERROR.GUEST_USER_NOT_FOUND }
      }

      this.updateAccount({
        ...guestInfo,
        roleType: ROLE.GUEST,
      })

      setTokensToCookies(guestInfo)

      const wsInfo = await getWorkspaceInfo({ workspaceId: this.workspaceId })

      this.changeWorkspace(wsInfo)

      this.checkCompany(guestInfo.uuid, this.workspaceId)

      await auth.init()

      auth.initAuthConnection(
        this.workspaceId,
        this.onDuplicatedRegistration,
        this.onRemoteExitReceived,
        this.onForceLogoutReceived,
        this.onWorkspaceDuplicated,
        this.onRegistrationFail,
      )
    },
    async checkCompany(uuid, workspaceId) {
      const res = await getCompanyInfo({
        userId: uuid,
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
        localRecording: false, //guest(seat) 멤버는 사용불가
        storage: res.storage,
        sessionType: res.sessionType,
        languageCodes,
        audioRestrictedMode: false, //res.audioRestrictedMode,
        videoRestrictedMode: false, //guest(seat) 멤버는 사용불가
        timeout: res.timeout !== undefined ? res.timeout : 60, //협업 연장 질의 팝업 싸이클을 정하는 값. 분 단위
      })
    },
    showGuestExpiredAlarm() {
      this.confirmDefault(this.$t('guest.guest_license_expired'), {
        action: () => {
          location.href = `${URLS['console']}/?continue=${location.href}`
        },
      })
    },
    updateServiceMode(mode) {
      this.serviceMode = mode
    },
    async checkAppInstalled() {
      const aosApp = await getLatestRemoteAosAppInfo()
      if (!aosApp) return false

      this.packageName = aosApp.packageName

      if (!navigator.getInstalledRelatedApps) {
        console.log('navigator.getInstalledRelatedApps is undefined')
        return false
      }

      const relatedApps = await navigator.getInstalledRelatedApps()
      console.log('installed app list :', relatedApps)
      const relatedApp = relatedApps.find(app => {
        console.log('installed app info ::', app)
        return app.url === this.packageName
      })

      return relatedApp ? true : false
    },
    async runApp() {
      const intentLink = await getIntentLink({
        workspaceId: this.workspaceId,
        sessionId: this.$route.query.sessionId,
        packageName: this.packageName,
      })

      window.open(intentLink)
    },
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
  },

  async created() {
    this.workspaceId = this.$route.query.workspaceId
    this.sessionId = this.$route.query.sessionId

    //게스트 멤버는 할당받는 계정이 수시로 변경되므로,
    //워크스페이스 id로 스토리지 셋팅
    window.myStorage = new MyStorage(this.workspaceId)
  },

  async mounted() {
    try {
      //파라미터 유효성 체크
      if (this.workspaceId === undefined || this.sessionId === undefined) {
        location.href = `${URLS['console']}/?continue=${location.href}`
        console.error('invalid params')
        return
      }

      this.$eventBus.$on('initGuestMember', this.initGuestMember)
      this.$eventBus.$on('updateServiceMode', this.updateServiceMode)

      this.serviceMode = this.isMobileSize ? 'mobile' : 'web'

      if (this.serviceMode === 'mobile') {
        const isAppInstalled = await this.checkAppInstalled()

        if (isAppInstalled) {
          this.runApp()
        }
      }

      await this.initGuestMember()
    } catch (err) {
      if (err.code === ERROR.ASSIGNED_GUEST_USER_IS_NOT_ENOUGH) {
        this.showGuestExpiredAlarm()
      } else if (err.code === ERROR.GUEST_USER_NOT_FOUND) {
        this.showGuestExpiredAlarm()
      } else {
        console.error(err)
      }
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('initGuestMember', this.initGuestMember)
    this.$eventBus.$off('updateServiceMode', this.updateServiceMode)
  },
}
</script>

<style lang="scss" src="assets/style/guest.scss"></style>
