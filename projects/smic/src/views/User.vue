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
      )
        template(slot="id-alert")
          h1 asdsad
        template(slot="password-alert")
          h1 asdsad
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
}
</style>
<script>
import UserScaffold from 'scaffold-modules-user'

export default {
  components: { UserScaffold },
  data() {
    return {
      currentPage: 'SIGNIN',
      options: {
        isFindPassword: false,
        isSignUp: false,
        placeholder: {
          id: '아이디를 입력해주세요',
          password: '비밀번호를 입력해주세요',
          passwordConfirm: '비밀번호를 입력해주세요',
        },
      },
    }
  },
  methods: {
    async onSubmit(form) {
      try {
        const { email, password } = form
        await this.$store.dispatch('USER_LOGIN', {
          email,
          password,
        })
        this.$router.push({ path: '/' })
      } catch (e) {
        console.log(e)
      }
    },
  },
}
</script>
