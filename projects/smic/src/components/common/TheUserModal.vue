<template lang="pug">
  el-dialog(:visible.sync='dialogFormVisible')
    user-scaffold(:currentPage="currentPage" :options="options" @onSubmit="onSubmit")
</template>
<script>
// import UserScaffold from '@virnect/scaffold-modules-user'
import UserScaffold from 'scaffold-modules-user'
import EventBus from '@/utils/eventBus.js'

export default {
	components: { UserScaffold },
	data() {
		return {
			dialogFormVisible: false,
			currentPage: 'SIGNIN',
			options: {
				placeholder: {
					id: '이메일을 입력해주세요',
					password: '비밀번호를 입력해주세요',
					passwordConfirm: '비밀번호를 입력해주세요',
				},
			},
			defaultUrl: 'http://localhost:3000',
		}
	},
	mounted() {
		EventBus.$on(
			'toggleUserModal',
			boolean => (this.dialogFormVisible = boolean),
		)
	},
	methods: {
		onSubmit(params) {
			console.log('params : ', params)
			console.log('finally!!!!')
		},
	},
}
</script>
