<template lang="pug">
  el-dialog(:visible.sync='dialogFormVisible')
    user-scaffold(:currentPage="currentPage" :options="options")
</template>
<script>
import UserScaffold from '@virnect/scaffold-modules-user'
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
		}
	},
	mounted() {
		EventBus.$on(
			'toggleUserModal',
			boolean => (this.dialogFormVisible = boolean),
		)
	},
}
</script>
