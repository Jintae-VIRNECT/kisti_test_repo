<template lang="pug">
  div
    .login-form
      form.el-form(:class="customClass")
        h1 비밀번호 재설정
        .el-form-item
          .el-form-item__content
            .el-input
              slot(name="id")
                input.el-input__inner(v-model="form.id" :placeholder="options.placeholder.id")
    .login-join
      p.login-join--info
        a.ui-anchor.login-join--link(href="#" @click="$emit('pageSignUp')") 회원가입
      p.login-join--info
        a.ui-anchor.login-join--link(href="#" @click="$emit('pageSignIn')") 로그인
</template>

<script>
import {page} from '../enum'
export default {
	props: ['customClass', 'options'],
	data() {
		return {
			form: {
        page: page.FINDPASSWORD,
				id: null,
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
