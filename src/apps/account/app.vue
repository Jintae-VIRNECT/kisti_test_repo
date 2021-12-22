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
import { login } from 'api/http/account'
import Cookies from 'js-cookie'
import { COOKIE_EXPIRE_UNIT } from 'utils/auth'

export default {
  name: 'container',
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
    setTokensFromCookies(response) {
      const { accessToken, refreshToken, expireIn } = response
      console.error(expireIn, expireIn / COOKIE_EXPIRE_UNIT)
      const cookieOption = {
        expires: expireIn / COOKIE_EXPIRE_UNIT,
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
      }
      Cookies.set('accessToken', accessToken, cookieOption)
      Cookies.set('refreshToken', refreshToken, cookieOption)
      location.href = '/home'
    },
    async doLogin() {
      try {
        const params = {
          email: this.form.userId,
          password: this.form.userPw,
          rememberMe: false,
        }
        const response = await login(params)
        this.setTokensFromCookies(response)
      } catch (err) {
        alert(JSON.stringify(err))
      }
    },
  },
}
</script>

<style lang="scss">
@charset "utf-8";

$footer_height: 100px;
$header_m_height: 56px;
.account-wrapper {
  height: 100vh;
  max-height: -webkit-fill-available;
  background: #f8f8f8;
}
.login-body {
  position: relative;
  display: flex;
  height: calc(100vh - 180px);
  min-height: calc(700px - 180px);
  margin: 0 auto;
  text-align: center;
}
//타이틀
.login--title {
  padding: 15px 0 25px;
  color: #222;
  font-size: 2.71rem;
  text-align: center;
}

//정보입력 영역
.login-form {
  display: flex;

  > input {
    width: calc(100% - 30px);
    margin-top: 20px;
    padding: 10px 15px;
    font-size: 16px;
    border: solid 1px #939393;
    border-radius: 4px;
  }

  &--submit {
    width: 100%;
    margin-top: 15px;
    padding: 10px;
    background-color: #0f75f5;
    border-radius: 4px;
  }
}
</style>
