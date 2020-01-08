<template lang="pug">
  div
    h1 Users Page
    user-scaffold(
      :currentPage="currentPage" 
      :options="options" 
      @onSubmit="onSubmit"
    )
</template>
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
					id: '이메일을 입력해주세요',
					password: '비밀번호를 입력해주세요',
					passwordConfirm: '비밀번호를 입력해주세요',
				},
			},
		}
	},
	methods: {
		onSubmit(form) {
			this.$store.dispatch('USER_LOGIN', { user: form }).then(() => {
				this.$router.push({ path: '/' })
			})
		},
	},
}
</script>
