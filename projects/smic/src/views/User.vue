<template lang="pug">
  div
    .top-nav.border
      .top-nav__center
        router-link(to="/")
          img.logo-img(src="~@/assets/image/logo-smart-factory.png") 
        .divider
        label.workspace Workstation
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
<style lang="scss">
.login-form-wrapper {
  width: 380px;
  margin-left: auto;
  margin-right: auto;
  margin-top: 80px;
  .title {
    margin-bottom: 40px;
    text-align: center;
    font-size: 28px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: normal;
    text-align: center;
    color: #0d2a58;
  }
  .error-text {
    font-size: 14px;
    color: #db1717;
  }
}
</style>
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
        const response = await this.$store.dispatch('USER_LOGIN', {
          email,
          password,
        })
        console.log('---response : ', response)
        if (response.isLogin === false) return (this.onError = true)
        this.$router.push({ path: '/' })
      } catch (e) {
        console.log(e)
      }
    },
  },
}
</script>
