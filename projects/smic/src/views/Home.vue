<template lang="pug">
	el-main
		the-page-header(title="Home")
		el-row(:gutter="20")
			el-col(:span="16")
				inline-table(:tableData="currentUploadedContent.tableData" :tableOption="currentUploadedContent.tableOption")
			el-col(:span="8")
				progress-card(:progressData='progressData')
		el-row(:gutter="0")
			el-col(:span="24")
				inline-table(:tableData="currentReportedProgress.tableData" :tableOption="currentReportedProgress.tableOption")
</template>

<script>
// UI component
import ThePageHeader from '@/components/common/ThePageHeader.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedProgress from '@/data/currentReportedProgress'

export default {
	components: { ThePageHeader, ProgressCard, InlineTable },
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
		}
	},
}
</script>
