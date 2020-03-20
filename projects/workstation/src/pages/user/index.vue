<template lang="pug">
  div
    .login-form-wrapper
      h1.title 로그인
      user-scaffold(
        :currentPage="currentPage" 
        :options="options" 
        @onSubmit="onSubmit"
        :onError="onError")
        template(slot="error-alert")
          p.error-text(v-if="onError") 아이디 또는 비밀번호가 일치하지 않습니다.
</template>

<script>
import UserScaffold from 'scaffold-modules-user/src/views/SignIn.vue'

export default {
  components: { UserScaffold },
  data() {
    return {
      currentPage: 'SIGNIN',
      options: {
        isFindPassword: false,
        isSignUp: false,
        placeholder: {
          email: '아이디를 입력해주세요',
          password: '비밀번호를 입력해주세요',
          passwordConfirm: '비밀번호를 입력해주세요',
        },
      },
      onError: false,
    }
  },
  methods: {
    async onSubmit(form) {
      try {
        const { email, password } = form
        const response = await this.$store.dispatch('user/USER_LOGIN', {
          email,
          password,
        })
        if (response.isLogin === false) return (this.onError = true)
        this.$store.dispatch('user/getMemberList')
        this.$router.push({ path: '/' })
      } catch (e) {
        throw new Error(e)
      }
    },
  },
}
</script>

<style lang="scss">
.login-form-wrapper {
  width: 380px;
  margin-top: 80px;
  margin-right: auto;
  margin-left: auto;
  .title {
    margin-bottom: 40px;
    color: #0d2a58;
    font-weight: 500;
    font-size: 28px;
    font-style: normal;
    font-stretch: normal;
    line-height: normal;
    letter-spacing: normal;
    text-align: center;
    text-align: center;
  }
  .error-text {
    color: #db1717;
    font-size: 14px;
  }
}
</style>
