<template lang="pug">
	div
		sign-in(
			v-if='watchedCurrentPage=="SIGNIN"' 
			key="SIGNIN" 
			:options="options" 
			@pageSignUp="watchedCurrentPage = 'SIGNUP'"
			@pageFindPassword="watchedCurrentPage = 'FINDPASSWORD'"
		)
		sign-up(
			v-if='watchedCurrentPage=="SIGNUP"' 
			key="SIGNUP" 
			:options="options" 
			@pageSignIn="watchedCurrentPage = 'SIGNIN'"
			@pageFindPassword="watchedCurrentPage = 'FINDPASSWORD'"
		)
		find-password(
			v-if='watchedCurrentPage=="FINDPASSWORD"' 
			key="FINDPASSWORD" 
			:options="options" 
			@pageSignIn="watchedCurrentPage = 'SIGNIN'" 
			@pageSignUp="watchedCurrentPage = 'SIGNUP'"
		)
</template>

<script>
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
		}
	},
	watch: {
		watchedCurrentPage(val) {
			return val
		},
	},
}
</script>
