<template lang="pug">
  div
    .login-form
      form.el-form(:class="customClass")
        h1 로그인
        .el-form-item
          .el-form-item__content
            .el-input
              slot(name="id")
                input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id" name="id" v-validate="'id|2,20'")
                p(v-if="$errors.has('id')") {{ $errors.message('id') }}
                p {{errorBag}}
        .el-form-item
          .el-form-item__content
            .el-input
              slot(name="password")
                input.el-input__inner(v-model="form.password" :placeholder="options.placeholder.password" type="password" name="password" v-validate="'password|2,20'")
                p(v-if="$errors.has('password')") {{ $errors.message('password') }}
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
import validateDirective from 'user-form-validate/src/directive'
import validateMixin from 'user-form-validate/src/mixin'

export default {
	props: ['customClass', 'options'],
	directives: {
		...validateDirective,
	},
	mixins: [validateMixin],
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
			this.$validator.validateAll()
			console.log('this : ', this)
			console.log('this.$errors : ', this.$errors)
			console.log(
				"this.$errors.has('password') : ",
				this.$errors.has('password'),
			)
		},
	},
}
</script>
