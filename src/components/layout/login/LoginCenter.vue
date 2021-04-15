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
        <strong>{{ $t('login.needTo.title') }}</strong>
      </p>
      <p>{{ $t('login.needTo.contents') }}</p>
    </el-dialog>
    <template v-else>
      <Header
        v-if="$env !== 'onpremise'"
        :showStatus="showStatus"
        :env="$env"
        :urls="$urls"
        :subTitle="$t('login.subTitle')"
        @isMobile="isMobile"
      />
      <Header
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
import { Header } from '@virnect/components'
export default {
  components: {
    Header,
  },
  props: {
    showStatus: Object,
    auth: Object,
  },
  data() {
    return {
      show: false,
      logo: {},
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
      setTimeout(() => {
        this.show = false
      }, 1500)
    },
  },
  mounted() {
    const redirectTarget = this.$route.query.continue
    // 자신으로 리다이렉트 제외
    if (redirectTarget === undefined) return

    // 로그인 필요 다이얼로그
    if (redirectTarget.split(this.$urls.www).length == 1) {
      this.show = true
      this.loginService()
    }
  },
}
</script>

<style lang="scss" scoped></style>
