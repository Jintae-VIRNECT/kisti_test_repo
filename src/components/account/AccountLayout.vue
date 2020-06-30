<template>
  <div class="account-wrapper" android:windowSoftInputMode="adjustResize">
    <main class="login-body">
      <div class="login-center">
        <h2 class="login--title">로그인</h2>

        <fieldset class="login-form">
          <input
            type="text"
            v-model.trim="form.userId"
            placeholder="아이디"
            @keyup.enter="doLogin($event)"
          />
          <input
            type="password"
            v-model.trim="form.userPw"
            placeholder="비밀번호"
            @keyup.enter="doLogin($event)"
          />

          <button
            type="primary"
            size="full"
            class="login-form--submit"
            @click="doLogin($event)"
          >
            로그인
          </button>
        </fieldset>
      </div>
    </main>
  </div>
</template>

<script>
import { login } from 'api/common/account'
import Cookies from 'js-cookie'

export default {
  name: 'AccountLayout',
  data() {
    return {
      requestBlocking: false,
      validId: true,
      validPw: true,
      form: {
        userId: '',
        userPw: '',
      },
    }
  },
  methods: {
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
        this.setTokensFromCookies(rtn.accessToken, rtn.refreshToken)
      } catch (err) {
        console.log(err)
      }
    },
  },
}
</script>
<style lang="scss" src="assets/style/account.scss"></style>
