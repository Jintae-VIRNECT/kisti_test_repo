<template>
  <main class="login-body">
    <div class="login-center">
      <h2 class="login--title">로그인</h2>

      <fieldset class="login-form" :class="{ login__fail: validFail }">
        <el-form>
          <el-form-item :class="{ 'is-error': validId === false }">
            <el-input
              type="text"
              v-model.trim="form.userId"
              :validate-event="false"
              placeholder="아이디"
              @keyup.enter.native="doLogin($event)"
            ></el-input>
          </el-form-item>
          <el-form-item :class="{ 'is-error': validPw === false }">
            <el-input
              type="password"
              v-model.trim="form.userPw"
              placeholder="비밀번호"
              @keyup.enter.native="doLogin($event)"
            ></el-input>
          </el-form-item>
        </el-form>

        <p v-if="true === validFail" class="login-form--failmsg">
          {{ $t('login.fail') }}
        </p>

        <el-button
          type="primary"
          size="full"
          class="login-form--submit"
          @click="doLogin($event)"
          >로그인</el-button
        >
      </fieldset>
    </div>
  </main>
</template>

<script>
import { login } from 'api/common/account'
import { sessionStorage, localStorage } from 'utils/storage'
import * as Regexp from 'utils/regexp'
import Cookies from 'js-cookie'

export default {
  data() {
    const cookie = localStorage.getItem('ServiceCookiesAgree')
    const presetKeep = localStorage.getItem('keepLogin')
    const currentId = localStorage.getItem('auto_id')
    const lang = this.$i18n.locale

    return {
      requestBlocking: false,
      validId: true,
      validPw: true,
      keep: !!presetKeep,
      form: {
        userId: currentId,
        userPw: '',
      },
      rules: {
        userId: [{ required: true, trigger: 'blur' }],
        userPw: [{ required: true, trigger: 'blur' }],
      },
      lang,
      showCookie: !cookie,
    }
  },
  computed: {
    validFail() {
      return !(this.validId && this.validPw)
    },
  },
  watch: {
    keep(value) {
      localStorage.setItem('keepLogin', value)
    },
    isLogin(value) {
      if (value) {
        this.$router.push({ name: 'service' })
      }
    },
    lang(lang) {
      this.mx_changeLanguage(lang)
    },
  },
  methods: {
    checkInputs() {
      if (
        !Regexp.validEmail(this.form.userId) &&
        !Regexp.validId(this.form.userId)
      ) {
        this.validId = false
        return false
      }
      if (this.form.userPw === '') {
        this.validPw = false
        return false
      }

      this.validId = true
      this.validPw = true
      return true
    },
    setTokensFromCookies(access, refresh) {
      Cookies.set('accessToken', access)
      Cookies.set('refreshToken', refresh)
      location.href = '/home'
    },
    async doLogin() {
      try {
        const params = {
          email: this.form.userId,
          password: this.form.userPw,
          rememberMe: false,
        }
        const rtn = await login(params)
        console.log(rtn)
        this.setTokensFromCookies(rtn.accessToken, rtn.refreshToken)
      } catch (err) {
        console.log(err)
      }
    },
    enrollInfo(result) {
      this.$store.dispatch('updateAccount', {
        ...result.user,
        accessToken: result.accessToken,
        refreshToken: result.refreshToken,
        credentials: result.credentials,
        groups: result.groups,
        recent: result.recentCall,
        license: result.license,
      })

      // 세션스토리지 토큰 저장
      sessionStorage.setItem('account', {
        sId: result.user.sId,
        accessToken: result.accessToken,
        refreshToken: result.refreshToken,
        credentials: result.credentials,
      })

      // 마지막 접속한 그룹 설정.
      if (result.groups.length > 0) {
        let recentGroup = result.user.lastActiveGroup

        if (!!result.groups.find(_ => _.groupId === recentGroup) === false) {
          recentGroup = result.groups[0].groupId || null
        }

        this.$store.dispatch('changeMainGroup', recentGroup)
      }
    },
  },

  /* Lifecycles */
  created() {
    if (this.keep === true) {
      this.autoLogin()
    }
  },
  mounted() {},
}
</script>
