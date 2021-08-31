<template>
  <section class="remote-layout">
    <header-section></header-section>
    <vue2-scrollbar
      classes="remote-wrapper"
      ref="wrapperScroller"
      :onMaxScroll="handleMaxScroll"
    >
      <div class="workspace-wrapper">
        <guest-welcome></guest-welcome>
        <guest-tab ref="tabSection"></guest-tab>
      </div>
    </vue2-scrollbar>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'

import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'

import errorMsgMixin from 'mixins/errorMsg'
import { MyStorage } from 'utils/storage'

import GuestWelcome from './section/GuestWelcome'
import GuestTab from './section/GuestTab'
import { getCompanyInfo } from 'api/http/account'

import { getGuestInfo } from 'api/http/guest'

import { getConfigs } from 'utils/auth'

import { mapActions } from 'vuex'

import { getWorkspaceInfo } from 'api/http/workspace'

import Cookies from 'js-cookie'
export default {
  name: 'GuestLayout',
  async beforeRouteEnter(to, from, next) {
    await getConfigs()
    next()
  },
  // async beforeRouteEnter(to, from, next) {
  //   console.log(to)
  //   console.log(from)
  //   console.log(next)
  //   next()
  // },
  mixins: [confirmMixin, langMixin, toastMixin, errorMsgMixin],
  components: {
    HeaderSection,
    GuestWelcome,
    GuestTab,
  },
  data() {
    return {
      url: '',
      workspaceId: '',
      sessionId: '',
      uuid: '',
    }
  },
  methods: {
    ...mapActions(['setCompanyInfo', 'updateAccount', 'changeWorkspace']),
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
    async initGuestMember() {
      const guestInfo = await getGuestInfo({ workspaceId: this.workspaceId })

      this.updateAccount({
        ...guestInfo,
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
  },

  async mounted() {
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
  },
  beforeDestroy() {},
}
</script>

<style lang="scss" src="assets/style/guest.scss"></style>
