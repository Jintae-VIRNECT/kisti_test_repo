<template lang="pug">
	div
		inline-table(
			:tableData="currentReportedProcess" 
			:tableOption="currentReportedProcessTableOption"
		)
</template>

<script>
// UI component
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'

/// data
import currentReportedProcess from '@/data/currentReportedProcess'

const currentReportedProcessTableOption = {
	rowIdName: 'processId',
	subdomain: '/process',
	colOptions: true,
	colSetting: [
		{
			prop: 'index',
			label: '순번',
		},
		{
			prop: 'processId',
			label: '공정 ID',
		},
		{
			prop: 'processName',
			label: '공정 이름',
		},
		{
			prop: 'reportedAt',
			label: '보고 날짜',
		},
		{
			prop: 'status',
			label: '상태',
		},
		{
			prop: 'auth',
			label: '보고자',
		},
		{
			prop: 'processPercent',
			label: '진행률',
		},
		{
			prop: 'workerNum',
			label: '작업수',
		},
	],
}

export default {
	components: { ProgressCard, InlineTable },
	data() {
		return {
			value1: '',
			progressData: {
				overallProcessPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
			currentReportedProcess,
			currentReportedProcessTableOption,
		}
	},
}
</script>
