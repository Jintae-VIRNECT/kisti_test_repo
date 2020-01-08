<template lang="pug">
	div
		inline-table(
			:tableData="currentUploadedContent" 
			:tableOption="currentUploadedContentTableOption"
		)
</template>

<script>
// UI component
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedProcess from '@/data/currentReportedProcess'

const currentUploadedContentTableOption = {
	rowIdName: 'processId',
	subdomain: '/process',
	colOptions: true,
	colSetting: [
		{
			prop: 'index',
			label: '순번',
		},
		{
			prop: 'processName',
			label: '공정 이름',
		},
		{
			prop: 'volume',
			label: '크기',
		},
		{
			prop: 'uploadedAt',
			label: '업로드일',
		},
		{
			prop: 'auth',
			label: '업로드 멤버',
		},
		{
			prop: 'contentPublish',
			label: '공정 배포',
		},
		{
			prop: 'processRegister',
			label: '공정 등록',
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
			currentUploadedContent,
			currentReportedProcess,
			currentUploadedContentTableOption,
		}
	},
}
</script>
