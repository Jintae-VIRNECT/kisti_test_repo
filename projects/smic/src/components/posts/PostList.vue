<template lang="pug">
  div
    the-page-header(title="Post Detail")
    el-table(:data="tableData" style="width: 100%" @row-click="onClickRowPost")
      el-table-column(prop="date" label="Date" width="180")
      el-table-column(prop="name" label="Name" width="180")
      el-table-column(prop="address" label="Address")
</template>

<script>
import ThePageHeader from '@/components/common/ThePageHeader.vue'

export default {
	components: { ThePageHeader },
	data() {
		return {
			tableData: [
				{
					id: '1',
					date: '2016-05-03',
					name: 'Tom',
					address: 'No. 189, Grove St, Los Angeles',
				},
				{
					id: '2',
					date: '2016-05-02',
					name: 'Tom',
					address: 'No. 189, Grove St, Los Angeles',
				},
				{
					id: '3',
					date: '2016-05-04',
					name: 'Tom',
					address: 'No. 189, Grove St, Los Angeles',
				},
				{
					id: '4',
					date: '2016-05-01',
					name: 'Tom',
					address: 'No. 189, Grove St, Los Angeles',
				},
			],
		}
	},
	methods: {
		onClickRowPost(row) {
			this.$router.push(`/posts/${row.id}`)
		},
	},
}
</script>
