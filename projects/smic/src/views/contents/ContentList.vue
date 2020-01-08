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
import currentReportedProgress from '@/data/currentReportedProgress'

const currentUploadedContentTableOption = {
	rowIdName: 'contentId',
	subdomain: '/contents',
	colOptions: true,
	colSetting: [
		{
			prop: 'contentId',
			label: '콘텐츠 ID',
		},
		{
			prop: 'contentName',
			label: '콘텐츠 이름',
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
			label: '콘텐츠 배포',
		},
		{
			prop: 'progressRegister',
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
				overallProgressPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
			currentUploadedContent,
			currentReportedProgress,
			currentUploadedContentTableOption,
		}
	},
}
</script>
