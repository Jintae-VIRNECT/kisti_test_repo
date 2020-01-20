<template lang="pug">
	div
		page-tab-nav
			template(slot='page-nav--right')
				router-link(to="/process/new")
					button.enroll-new-process 신규 공정 등록
		page-bread-crumb(title='공정')
		process-dash-banner
		.page-nav
			.search-wrapper.text-right
				el-input.tool.search(placeholder='이름 또는 이메일 검색' v-model='search' )
					el-button(slot='append' icon='el-icon-search')
				span 필터 : 
				el-dropdown.tool.filter
					el-button(type='primary')
						| 전체
						i.el-icon-arrow-down.el-icon--right
					el-dropdown-menu(slot='dropdown')
						el-dropdown-item Action 1
						el-dropdown-item Action 2
						el-dropdown-item Action 3
						el-dropdown-item Action 4
						el-dropdown-item Action 5
				span 정렬 : 
				el-dropdown.tool.order
					el-button(type='primary')
						| ㄱ-ㅎ순
						i.el-icon-arrow-down.el-icon--right
					el-dropdown-menu(slot='dropdown')
						el-dropdown-item Action 1
						el-dropdown-item Action 2
						el-dropdown-item Action 3
						el-dropdown-item Action 4
						el-dropdown-item Action 5
		inline-table(
			:setHeader='true'
			:tableData="currentReportedDetailProcess" 
			:tableOption="tableOption"
			:colSetting="cols"
			:controlCol="true")
			template(slot="header-left")
				span.title 공정 목록
			.inline-table__header.text-right(slot="header-right")
				span.sub-title 등록된 공정 수 
				span.value 102
</template>
<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import ProcessDashBanner from '@/components/process/ProcessDashBanner.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'

import { cols } from '@/models/process'

const tableOption = {
	rowIdName: 'processId',
	subdomain: '/process',
}

/// data
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'

export default {
	components: {
		ProgressCard,
		InlineTable,
		ProcessDashBanner,
		PageTabNav,
		PageBreadCrumb,
	},
	data() {
		return {
			value1: '',
			progressData: {
				overallProcessPercent: 90,
				progressByDay: [5, 10, 20, 30, 40, 66, 20],
				progressByDayLastDate: '2020.01.13',
			},
			currentReportedDetailProcess,
			search: null,
			tableOption,
		}
	},
	computed: {
		cols() {
			return cols
		},
	},
}
</script>
