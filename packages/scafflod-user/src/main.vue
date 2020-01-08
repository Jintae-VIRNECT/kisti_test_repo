<template lang="pug">
	div
		sign-in(
			v-if='options.isSignIn !== false && watchedCurrentPage === page.SIGNIN' 
			:key="page.SIGNIN" 
			:options="options" 
			@pageSignUp="pageSignIn"
			@pageFindPassword="pageFindPassword"
			@onSubmit="onSubmit"
		)
		sign-up(
			v-if='options.isSignUp !== false && watchedCurrentPage === page.SIGNUP' 
			:key="page.SIGNUP" 
			:options="options" 
			@pageSignIn="options.pageSignIn"
			@pageFindPassword="pageFindPassword"
		)
		find-password(
			v-if='options.isFindPassword !== false && watchedCurrentPage === page.FINDPASSWORD' 
			:key='page.FINDPASSWORD' 
			:options="options" 
			@pageSignIn="pageSignIn" 
			@pageSignUp="pageSignIn"
		)
</template>

<script>
import { page } from './enum'
import SignIn from './views/SignIn.vue'
import SignUp from './views/SignUp.vue'
import FindPassword from './views/FindPassword.vue'

export default {
	name: 'UserScaffold',
	props: {
		customClass: String,
		currentPage: String,
		options: {
			form: {
				isFindPassword: {
					type: Boolean,
					default: true,
				},
				isSignUp: {
					type: Boolean,
					default: true,
				},
				isSignIn: {
					type: Boolean,
					default: true,
				},
				isPreserveLogin: {
					type: Boolean,
					default: true,
				},
			},
			placeholder: {
				id: {
					type: String,
					default: '이메일을 입력해주세요',
				},
				password: {
					type: String,
					default: '비밀번호를 입력해주세요',
				},
				passwordConfirm: {
					type: String,
					default: '비밀번호를 입력해주세요',
				},
			},
		},
	},
	components: {
		SignIn,
		SignUp,
		FindPassword,
	},
	data() {
		return {
			watchedCurrentPage: this.$props.currentPage,
			page,
		}
	},
	methods: {
		pageSignUp() {
			this.watchedCurrentPage = page.SIGNUP
		},
		pageSignIn() {
			this.watchedCurrentPage = page.SIGNIN
		},
		pageFindPassword() {
			this.watchedCurrentPage = page.FINDPASSWORD
		},
		onSubmit(params) {
			this.$emit('onSubmit', params)
		},
	},
	watch: {
		watchedCurrentPage(val) {
			return val
		},
	},
}
</script>
