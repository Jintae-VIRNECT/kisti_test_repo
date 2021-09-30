<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('login.title') }}</h2>
        <p class="input-title">
          {{ $env !== 'onpremise' ? $t('login.email') : 'ID' }}
        </p>
        <el-input
          ref="focusOut"
          :placeholder="
            $env !== 'onpremise'
              ? $t('login.emailPlaceholder')
              : $t('onpremise.resetPass.input.placeholder')
          "
          v-model="login.email"
          name="email"
          :class="{ 'input-danger': message }"
          @input="emailRemember(login.email, login.rememberMe)"
          clearable
          v-validate="'required'"
        >
        </el-input>

        <p class="input-title">{{ $t('login.password') }}</p>
        <el-input
          :placeholder="$t('login.passwordPlaceholder')"
          v-model="login.password"
          type="password"
          clearable
          name="password"
          :class="{ 'input-danger': message }"
          v-validate="'required'"
          @keyup.enter.native="handleLogin"
        ></el-input>

        <p class="warning-msg danger-color" v-if="message">{{ message }}</p>

        <div class="checkbox-wrap">
          <el-checkbox
            v-model="login.rememberMe"
            @change="emailRemember(login.email, login.rememberMe)"
            >{{
              $env !== 'onpremise'
                ? $t('login.remember')
                : $t('onpremise.login.remember')
            }}</el-checkbox
          >
          <el-checkbox
            @change="autoLogin(login.autoLogin)"
            v-model="login.autoLogin"
            >{{ $t('login.autoLogin') }}</el-checkbox
          >
        </div>

        <el-button
          class="next-btn block-btn"
          type="info"
          @click="handleLogin"
          :disabled="isDisable"
          >{{ $t('login.title') }}</el-button
        >
        <div class="find-wrap">
          <router-link
            :to="{ name: 'findTab', params: { findCategory: 'email' } }"
            v-if="$env !== 'onpremise'"
            >{{ $t('login.findAccount') }}</router-link
          >
          <router-link :to="resetPasswordPath">{{
            $t('login.resetPassword')
          }}</router-link>
          <router-link to="/terms" v-if="$env !== 'onpremise'">{{
            $t('login.signUp')
          }}</router-link>
        </div>
      </el-col>
    </el-row>
    <footer-section v-if="$env !== 'onpremise'"></footer-section>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import AuthService from 'service/auth-service'
import mixin from 'mixins/mixin'

import footerSection from '../../common/Footer'
const cookieOption = {
  domain:
    location.hostname.split('.').length === 3
      ? location.hostname.replace(/.*?\./, '')
      : location.hostname,
}
export default {
  name: 'login',
  mixins: [mixin],
  components: {
    footerSection,
  },
  props: {
    auth: {
      default() {
        return {
          env: '',
          urls: {},
          myInfo: {},
          isLogin: false,
        }
      },
    },
  },
  data() {
    return {
      login: {
        email: '',
        password: '',
        rememberMe: null,
        autoLogin: null,
      },
      loading: false,
      message: '',
      token: Cookies.get('accessToken'),
      rememberEmail: Cookies.get('email'),
      rememberLogin: Cookies.get('auto'),
    }
  },
  computed: {
    isDisable() {
      return (
        this.loading ||
        this.login.email == '' ||
        this.login.password == '' ||
        this.login.email.replace(/ /gi, '') === '' ||
        this.login.password.replace(/ /gi, '') === '' ||
        this.login.email.length < 5 ||
        this.login.password.length < 1
      )
    },
    resetPasswordPath() {
      if (this.$env !== 'onpremise') {
        return {
          name: 'findTab',
          params: { findCategory: 'reset_password' },
        }
      } else {
        return {
          name: 'reset_password',
        }
      }
    },
  },
  methods: {
    async checkToken() {
      const redirectTarget = this.$route.query.continue
      if (!this.auth.isLogin) return false

      if (redirectTarget) {
        location.href = /^https?:/.test(redirectTarget)
          ? redirectTarget
          : `//${redirectTarget}`
        // } else if (this.login.autoLogin) {
      } else if (this.auth.isLogin) {
        // console.log(redirectTarget)
        location.href = this.$urls['workstation']
      }
    },
    emailRemember(email, check) {
      if (check == true) {
        this.rememberEmail = true
        Cookies.set('email', email, cookieOption)
      } else {
        Cookies.remove('email', cookieOption)
      }
    },
    autoLogin(check) {
      if (check == true) {
        this.rememberLogin = true
        Cookies.set('auto', check, {
          domain:
            location.hostname.split('.').length === 3
              ? location.hostname.replace(/.*?\./, '')
              : location.hostname,
        })
      } else {
        Cookies.remove('auto')
      }
    },
    async handleLogin() {
      if (this.login.password.length < 1) return
      this.loading = true
      this.$validator.validateAll()
      this.$refs.focusOut.focus()
      try {
        let res = await AuthService.login({ params: this.login })
        if (res.code === 200) {
          const cookieOption = {
            secure: true,
            sameSite: 'None',
            expires: res.data.expireIn / 3600000,
            domain:
              location.hostname.split('.').length === 3
                ? location.hostname.replace(/.*?\./, '')
                : location.hostname,
          }
          Cookies.set('accessToken', res.data.accessToken, cookieOption)
          Cookies.set('refreshToken', res.data.refreshToken, cookieOption)

          this.redirection(res.data)
        } else throw res
      } catch (e) {
        const failCount = e.failCount || 0
        this.loading = false

        // 일반 에러
        if (e.code === 2000 && failCount < 2) {
          this.message =
            this.$env !== 'onpremise'
              ? this.$t('login.accountError.contents')
              : this.$t('onpremise.login.error')
        }
        // 2회 이상 실패
        else if (1 < failCount && failCount < 5) {
          this.$confirm(
            this.$tc('login.securityError.contents', failCount),
            this.$t('login.securityError.title'),
            {
              confirmButtonText: this.$t('login.resetPassword'),
              cancelButtonText: this.$t('login.accountError.btn'),
              dangerouslyUseHTMLString: true,
            },
          ).then(() => this.$router.push(this.resetPasswordPath))
        }
        // 계졍 잠김
        else if (e.code === 2002 || failCount === 5) {
          this.$confirm(
            this.$t('login.lockedError.contents'),
            e.code === 2002
              ? this.$t('login.lockedError.title')
              : this.$t('login.lockedError.changed'),
            {
              confirmButtonText: this.$t('login.resetPassword'),
              cancelButtonText: this.$t('login.accountError.btn'),
              dangerouslyUseHTMLString: true,
            },
          ).then(() => this.$router.push(this.resetPasswordPath))
        }
        // 기타
        else {
          this.alertMessage(
            this.$t('login.networkError.title'),
            this.$t('login.networkError.contents'),
            'error',
          )
          this.message = e.message
        }
      }
    },
    async redirection(res) {
      let redirectTarget = this.$route.query.continue
      if (!res.passwordInitialized) {
        try {
          await this.$confirm(
            this.$t('onpremise.login.redirect.disc'),
            this.$t('onpremise.login.redirect.title'),
            {
              confirmButtonText: this.$t('onpremise.login.redirect.confirm'),
              cancelButtonText: this.$t('onpremise.login.redirect.cancel'),
            },
          )
          location.replace(this.$urls.account)
        } catch (e) {
          location.replace(this.$urls.workstation)
        }
      } else {
        if (redirectTarget) {
          location.href = /^https?:/.test(redirectTarget)
            ? redirectTarget
            : `//${redirectTarget}`
        } else {
          location.href = this.$urls['workstation']
        }
      }
    },
  },
  beforeMount() {
    this.checkToken()
  },
  mounted() {
    if (this.rememberLogin === 'true') {
      this.login.autoLogin = true
    }

    if (this.rememberEmail) {
      this.login.rememberMe = true
      this.login.email = this.rememberEmail
    }
  },
}
</script>

<style lang="scss" scoped>
h1 {
  margin-right: -2px;
  margin-bottom: 39px;
  margin-left: -2px;
}

.el-button {
  margin-top: 30px;
}

@media (max-width: $mobile) {
  .el-button.next-btn {
    margin-top: 40px;
  }
}
</style>
