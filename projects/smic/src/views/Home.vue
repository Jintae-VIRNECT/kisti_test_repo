<template lang="pug">
	div
		h1.admin-body__header 홈
		process-inprogress-status-graph
		el-row(:gutter="20")
			el-col(:span="24")
				inline-table(
					:setHeader='true'
					dataType="contents"
					:tableData="currentUploadedContent" 
					:colSetting="tableColSettings.contents"
					:tableOption="currentUploadedContentTableOption"
					:toolCol="true")
					template(slot="header-left")
						span.title 최근 업로드된 콘텐츠
					template(slot="header-right")
						.text-right
							router-link.more-link(type="text" to="/contents") 더보기
		el-row(:gutter="0")
			el-col(:span="24")
				inline-table(
					:setHeader='true'
					:tableData="currentReportedDetailProcess" 
					:colSetting="tableColSettings[activeTab]")
					template(slot="header-left")
						span.title 최근 보고 정보
					template(slot="header-right")
						.text-right
							router-link.more-link(type="text" :to="currentUploadedContentTableOption.subdomain") 더보기
					template(slot="tabs")
						el-tabs(v-model='activeTab' @tab-click="setInlineTableByTabs")
							el-tab-pane(
								v-for="(category, index) in currentReportedInformationTabs" 
								:key="index" 
								:label="category.label" 
								:name="category.name")

</template>

<script>
// UI component
import ProcessInprogressStatusGraph from '@/components/common/ProcessInprogressStatusGraph.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import { currentReportedInformationTabs, tableColSettings } from '@/models/home'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'

const currentUploadedContentTableOption = {
	rowIdName: 'contentId',
	subdomain: '/contents',
}

export default {
	components: { ProgressCard, InlineTable, ProcessInprogressStatusGraph },
	data() {
		return {
			value1: '',
			currentUploadedContent,
			currentReportedDetailProcess,
			currentUploadedContentTableOption,
			progressData: {
				overallProgressPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
			currentReportedInformationTabs,
			activeTab: currentReportedInformationTabs[0].name,
			tableColSettings,
			inlineTable: {
				dataSet: currentReportedDetailProcess,
			},
		}
	},
	methods: {
		setInlineTableByTabs(e) {
			console.log('e : ', e)
			this.currentUploadedContentTableOption.subdomain =
				currentReportedInformationTabs[e.index].link
		},
	},
}
</script>
