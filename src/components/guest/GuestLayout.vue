<template>
  <section class="remote-layout">
    <guest-web v-if="serviceMode === 'web'"></guest-web>
    <guest-mobile v-else-if="serviceMode === 'mobile'"></guest-mobile>
  </section>
</template>

<script>
import Cookies from 'js-cookie'

import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'

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

export default {
  name: 'GuestLayout',
  async beforeRouteEnter(to, from, next) {
    await getConfigs()

    next()
  },

  mixins: [confirmMixin, langMixin, toastMixin, errorMsgMixin],
  components: {
    GuestWeb,
    GuestMobile,
  },
  data() {
    return {
      url: '',
      workspaceId: '',
      sessionId: '',
      uuid: '',
      serviceMode: '', //web, mobile
    }
  },
  computed: {
    ...mapGetters(['isMobileSize']),
  },
  methods: {
    ...mapActions(['setCompanyInfo', 'updateAccount', 'changeWorkspace']),

    async initGuestMember() {
      const guestInfo = await getGuestInfo({ workspaceId: this.workspaceId })

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
  },

  async mounted() {
    try {
      this.serviceMode = this.isMobileSize ? 'mobile' : 'web'
      this.$eventBus.$on('initGuestMember', this.initGuestMember)

      this.workspaceId = this.$route.query.workspaceId
      this.sessionId = this.$route.query.sessionId

      window.myStorage = new MyStorage(this.workspaceId)

      await this.initGuestMember()

      //앱 테스트 코드
      const relatedApps = await navigator.getInstalledRelatedApps()
      console.log(relatedApps)
      relatedApps.forEach(app => {
        console.log(app.id, app.platform, app.url)
      })
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
  },
}
</script>

<style lang="scss" src="assets/style/guest.scss"></style>
