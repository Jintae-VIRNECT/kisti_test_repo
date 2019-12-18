<template lang="pug">
  div
    .login-form
      form.el-form(:class="customClass")
        h1 로그인
        .el-form-item
          .el-form-item__content
            .el-input
              slot(name="id")
                input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id")
        .el-form-item
          .el-form-item__content
            .el-input
              slot(name="password")
                input.el-input__inner(v-model="form.password" :placeholder="options.placeholder.password")
      .login-form--util.clearfix
        label.el-checkbox.keep-login
          span.el-checkbox__input(for="preserveLogin")
            span.el-checkbox__inner
            input.el-checkbox__original(type="checkbox" id="preserveLogin" v-model="form.preserveLogin")
          span.el-checkbox__label 로그인 상태 유지
        a.ui-anchor.password-find(href="#" @click="$emit('pageFindPassword')") 비밀번호 재설정
      slot(name="submit")
        button.el-button.login-form--submit.el-button--primary.el-button--full(type="button" @click="onSubmit")
          span 로그인
    .login-join
      p.login-join--info
        | 계정이 없으신가요?
        a.ui-anchor.login-join--link(href="#" @click="$emit('pageSignUp')") 회원가입
</template>

<script>
export default {
	props: ['customClass', 'options'],
	data() {
		return {
			form: {
				id: null,
				password: null,
				preserveLogin: true,
			},
		}
	},
	methods: {
		onSubmit() {
			console.log('submited!!!!')
		},
	},
}
</script>
