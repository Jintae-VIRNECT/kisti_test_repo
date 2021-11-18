<template>
  <div class="home-section">
    <el-dialog
      class="login-service-modal"
      :visible.sync="show"
      width="420px"
      top="11vh"
      v-if="show"
    >
      <img src="~assets/images/common/img-moveto-login@2x.png" />
      <p class="popup-title">
        <strong>{{ message.title }}</strong>
      </p>
      <p>{{ message.contents }}</p>
    </el-dialog>
    <template v-else>
      <VirnectHeader
        v-if="$env !== 'onpremise'"
        :showStatus="showStatus"
        :env="$env"
        :urls="$urls"
        :subTitle="$t('login.subTitle')"
        @isMobile="isMobile"
      />
      <VirnectHeader
        v-else
        :showStatus="showStatus"
        :env="$env"
        :urls="$urls"
        :subTitle="$t('login.subTitle')"
        :logo="logo"
        @isMobile="isMobile"
      />
      <transition name="app-fade" mode="out-in">
        <router-view :auth="auth" />
      </transition>
    </template>
  </div>
</template>

<script>
export default {
  props: {
    showStatus: Object,
    auth: Object,
  },
  data() {
    return {
      show: false,
      logo: {},
      message: {},
    }
  },
  watch: {
    customInfo() {
      this.logo = {
        default: this.customInfo.default,
        white: this.customInfo.white,
      }
    },
  },
  computed: {
    customInfo() {
      return this.$store.getters.customInfo
    },
  },
  methods: {
    isMobile(str) {
      this.$store.dispatch('IS_MOBILE', str)
    },
    loginService() {
      this.show = true
      setTimeout(() => {
        this.show = false
      }, 1500)
    },
  },
  mounted() {
    // 서버 푸시 메세지
    if (this.$route && this.$route.query !== undefined) {
      const messageKey = this.$route.query.message
      if (messageKey) {
        this.message.title = this.$t(`messages.${messageKey}.title`)
        this.message.contents = this.$t(`messages.${messageKey}.contents`)
        this.show = true
      }

      const redirectTarget = this.$route.query.continue
      // 자신으로 리다이렉트 제외
      if (redirectTarget === undefined) return
      // 로그인 필요 다이얼로그
      if (redirectTarget.split(this.$urls.www).length == 1) {
        this.message.title = this.$t('login.needTo.title')
        this.message.contents = this.$t('login.needTo.contents')
        this.loginService()
      }
    }
  },
}
</script>

<style lang="scss" scoped></style>
