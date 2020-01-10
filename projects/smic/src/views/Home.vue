<template lang="pug">
	div
		h1.admin-body__header 홈
		process-inprogress-status-graph
		el-row(:gutter="20")
			el-col(:span="24")
				inline-table(:tableData="currentUploadedContent" :tableOption="currentUploadedContentTableOption")
		el-row(:gutter="0")
			el-col(:span="24")
				inline-table(:tableData="currentReportedProcess" :tableOption="currentReportedProcessTableOption")
</template>

<script>
// UI component
import ProcessInprogressStatusGraph from '@/components/common/ProcessInprogressStatusGraph.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedProcess from '@/data/currentReportedProcess'

const currentUploadedContentTableOption = {
	title: '최근 업로드된 콘텐츠',
	moreHref: '/contents',
	rowIdName: 'contentId',
	subdomain: '/contents',
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
	],
}

const currentReportedProcessTableOption = {
	title: '최근 보고된 공정   | 2020.03.20 14:30',
	moreHref: '/progress',
	colOptions: true,
	colSetting: [
		{
			prop: 'progressId',
			label: '공정 ID',
		},
		{ prop: 'progressName', label: '공정 이름' },
		{ prop: 'reportedAt', label: '보고 시간' },
		{
			prop: 'status',
			label: '상태',
		},
		{ prop: 'auth', label: '진행률' },
		{ prop: 'progressPercent', label: '작업 수' },
		{ prop: 'workerNum', label: '작업자' },
	],
}

export default {
	components: { ProgressCard, InlineTable, ProcessInprogressStatusGraph },
	data() {
		return {
			value1: '',
			currentUploadedContent,
			currentReportedProcess,
			currentUploadedContentTableOption,
			currentReportedProcessTableOption,
			progressData: {
				overallProgressPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
		}
	},
}
</script>
