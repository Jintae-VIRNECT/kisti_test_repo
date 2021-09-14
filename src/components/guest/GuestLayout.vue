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
import Cookies from 'js-cookie'

import confirmMixin from 'mixins/confirm'
import { getAllAppList, getLatestAppInfo } from 'api/http/download'
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

import auth from 'utils/auth'

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
      url: '',
      workspaceId: '',
      sessionId: '',
      uuid: '',
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
      const guestInfo = await getGuestInfo({ workspaceId: this.workspaceId })

      // auth.initAuthConnection(
      //   this.workspaceId,
      //   this.onDuplicatedRegistration,
      //   this.onRemoteExitReceived,
      //   this.onForceLogoutReceived,
      //   this.onWorkspaceDuplicated,
      //   this.onRegistrationFail,
      // )

      // const authInfo = await auth.init()
      // console.log('authInfo::', authInfo)

      this.updateAccount({
        ...guestInfo,
        roleType: ROLE.GUEST,
      })

      //토큰 설정
      const accessToken = guestInfo.accessToken
      const refreshToken = guestInfo.refreshToken

      this.setToken(accessToken, refreshToken)

      const wsInfo = await getWorkspaceInfo({ workspaceId: this.workspaceId })

      this.changeWorkspace(wsInfo)

      this.checkCompany(guestInfo.uuid, this.workspaceId)
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
    setToken(accessToken, refreshToken) {
      const cookieOption = {
        expires: 1,
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
      }

      Cookies.set('accessToken', accessToken, cookieOption)
      Cookies.set('refreshToken', refreshToken, cookieOption)
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
      const appInfo = await getLatestAppInfo({ productName: 'remote' })
      const aosAppInfo = appInfo.appInfoList.find(info => {
        return info.deviceType === 'Mobile'
      })

      const appList = await getAllAppList()
      const aosApp = appList.appInfoList.find(app => {
        return app.uuid === aosAppInfo.uuid
      })

      this.packageName = aosApp.packageName

      const relatedApps = await navigator.getInstalledRelatedApps()
      console.log(relatedApps)
      const relatedApp = relatedApps.find(app => {
        console.log(app.id, app.platform, app.url)
        return app.url === this.packageName
      })

      return relatedApp ? true : false
    },
    runApp() {
      const intentLink = `intent://remote?workspaceId=${this.workspaceId}&sessionId=${this.$route.query.sessionId}#$d#Intent;scheme=virnect;action=android.intent.action.VIEW;category=android.intent.category.BROWSABLE;package=${this.packageName};end`
      console.log(intentLink)
      window.open(intentLink)
    },
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
  },

  async mounted() {
    try {
      this.workspaceId = this.$route.query.workspaceId
      this.sessionId = this.$route.query.sessionId

      //파라미터 유효성 체크
      if (this.workspaceId === undefined || this.sessionId === undefined) {
        location.href = `${URLS['console']}/?continue=${location.href}`
        console.error('invalid params')
        return
      }

      window.myStorage = new MyStorage(this.workspaceId)

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
