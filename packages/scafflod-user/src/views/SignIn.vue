<template lang="pug">
  div
    .login-form
      form.el-form
        slot(name="head")
        slot(name="body")
          .el-form-item
            .el-form-item__content
              label.el-input--label 아이디
              .el-input
                slot(name="email")
                  //- input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id" name="id" v-validate="'id|2,20'")
                  input.el-input__inner(v-model="form.email" :placeholder="options.placeholder.email" name="email")
                  //- p(v-if="$errors.has('id')") {{ $errors.message('id') }}
                  //- p {{errorBag}}
                  slot(name="id-alert")
          .el-form-item
            .el-form-item__content
              label.el-input--label 비밀번호
              .el-input.el-input--suffix
                slot(name="password")
                  //- input.el-input__inner(v-model="form.password" :placeholder="options.placeholder.password" type="password" name="password" v-validate="'password|2,20'")
                  div
                    input.el-input__inner(v-model="form.password" autocomplete="off" :placeholder="options.placeholder.password" :type="passwordInputType" name="password" @focus="isPasswordView = true" @blur="isPasswordView = false;")
                    span.el-input__suffix(v-if="form.password || isPasswordView" @click="onChangePasswordView")
                      span.el-input__suffix-inner
                        i.el-input__icon.el-icon-view.el-input__clear
                    slot(name="password-alert")
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
            button.el-button.login-form--submit(type="button" @click="onSubmit")
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
				email: 'smic1',
				password: 'smic1234',
				preserveLogin: false,
			},
			isPasswordView: false,
			passwordInputType: 'password',
		}
	},
	methods: {
		onSubmit() {
			this.$emit('onSubmit', this.form)
		},
		onChangePasswordView() {
			this.isPasswordView = !this.isPasswordView
			if (this.isPasswordView) this.passwordInputType = 'text'
			else this.passwordInputType = 'password'
		},
	},
}
</script>
