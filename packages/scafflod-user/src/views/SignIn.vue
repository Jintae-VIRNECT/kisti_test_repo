<template lang="pug">
  div
    .login-form
      form.el-form
        slot(name="head")
        slot(name="body")
          .el-form-item
            .el-form-item__content
              .el-input
                slot(name="id")
                  //- input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id" name="id" v-validate="'id|2,20'")
                  input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id" name="id")
                  //- p(v-if="$errors.has('id')") {{ $errors.message('id') }}
                  //- p {{errorBag}}
          .el-form-item
            .el-form-item__content
              .el-input
                slot(name="password")
                  //- input.el-input__inner(v-model="form.password" :placeholder="options.placeholder.password" type="password" name="password" v-validate="'password|2,20'")
                  input.el-input__inner(v-model="form.password" :placeholder="options.placeholder.password" type="password" name="password")
                  //- p(v-if="$errors.has('password')") {{ $errors.message('password') }}
    
        slot(name="foot")
          slot(name="login_util")
            .login-form--util.clearfix
              label.el-checkbox.keep-login(v-if="options.isPreserveLogin")
                span.el-checkbox__input(for="preserveLogin")
                  span.el-checkbox__inner
                  input.el-checkbox__original(type="checkbox" id="preserveLogin" v-model="form.preserveLogin")
                span.el-checkbox__label 로그인 상태 유지
              a.ui-anchor.password-find(
                href="#" 
                @click="$emit('pageFindPassword')"
                v-if="options.isFindPassword"
              ) 비밀번호 재설정
          slot(name="submit")
            //- p(v-if="errorBag.password !== true || errorBag.id !== true") 아이디 또는 비밀번호가 일치하지 않습니다.
            button.el-button.login-form--submit.el-button--primary.el-button--full(type="button" @click="onSubmit")
              span 로그인
          .login-join(v-if="options.isSignUp")
            p.login-join--info
              | 계정이 없으신가요?
              a.ui-anchor.login-join--link(href="#" @click="$emit('pageSignUp')") 회원가입
</template>

<script>
import { page } from '../enum'
// import validateDirective from 'user-form-validate/src/directive'
// import validateMixin from 'user-form-validate/src/mixin'

export default {
	// directives: {
	// 	...validateDirective,
	// },
	// mixins: [validateMixin],
	props: ['options'],
	data() {
		return {
			form: {
				page: page.SIGNIN,
				// id: null,
				// password: null,
				id: 'test1',
				password: '12341234',
				preserveLogin: false,
			},
		}
	},
	methods: {
		onSubmit() {
			this.$emit('onSubmit', this.form)
		},
	},
}
</script>
