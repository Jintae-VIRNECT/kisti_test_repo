<template lang="pug">
	div
		sign-in(
			v-if='watchedCurrentPage === page.SIGNIN' 
			:key="page.SIGNIN" 
			:options="options" 
			@pageSignUp="pageSignIn"
			@pageFindPassword="pageFindPassword"
			@onSubmit="onSubmit"
		)
		sign-up(
			v-if='watchedCurrentPage === page.SIGNUP' 
			:key="page.SIGNUP" 
			:options="options" 
			@pageSignIn="pageSignIn"
			@pageFindPassword="pageFindPassword"
		)
		find-password(
			v-if='watchedCurrentPage === page.FINDPASSWORD' 
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
	props: ['currentPage', 'options'],
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
			console.log('here u r too!!!')
			console.log('e : ', params)
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
