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

import { getConfigs } from 'utils/auth'

import { mapActions } from 'vuex'
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
      workspaceId: 'test-workspaceId',
      sessionId: 'test-sessionId',
      userId: 'test-userId',
    }
  },
  methods: {
    ...mapActions(['setCompanyInfo']),
    handleMaxScroll(event) {
      this.$eventBus.$emit('scroll:end', event)
    },
    async checkCompany(workspaceId) {
      //@TODO:workspaceId, 및 userId 적용
      const res = await getCompanyInfo({
        // userId: this.account.uuid,
        userId: this.userId,
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
  },

  async mounted() {
    //@TODO:seat member의 uuid 넣기
    window.myStorage = new MyStorage('testuserid')
    console.log(this.$route.query)

    this.checkCompany(this.workspaceId)
  },
  beforeDestroy() {},
}
</script>

<style lang="scss" src="assets/style/guest.scss"></style>
