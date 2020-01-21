<template lang="pug">
	div
		h1.admin-body__header 홈
		process-inprogress-status-graph
		el-row(:gutter="20")
			el-col(:span="24")
				inline-table(:setHeader="true")
					template(slot="header-left")
						span.title 최근 등록된 공정 콘텐츠
					template(slot="header-right")
						.text-right
							router-link.more-link(type="text" to="/contents") 더보기
					template(slot="body")
						el-table.inline-table(
							:data='currentContent.tableData' 
							style='width: 100%'
							@cell-click="onClickCell")
							el-table-column(
								v-for="{label, width, prop} in currentContent.colSetting" 
								:key="prop" 
								:prop="prop" 
								:label="label" 
								:width="width || ''") 
								template(slot-scope='scope')
									//- 이슈 타입
									.content-name(v-if="prop === 'name'")
										img.prefix-img(src="~@/assets/image/ic-content.svg")
										span {{currentContent.tableData[scope.$index][prop]}}
									div(v-else-if="prop === 'contentPublish'")
										span.publish-boolean(:class="currentContent.tableData[scope.$index][prop]") {{currentContent.tableData[scope.$index][prop] | publishBoolean}}
									.auth-wrapper(v-else-if="prop === 'auth'")
										.auth-img(:style="{'background-image': `url(${currentContent.tableData[scope.$index]['profileImg']})`}")
										span {{currentContent.tableData[scope.$index][prop]}}
									div(v-else)
										span {{ currentContent.tableData[scope.$index][prop]}}
							el-table-column(:width="50" class-name="control-col")
								template(slot-scope='scope')
									content-control-dropdown(:contentPublish="currentContent.tableData[scope.$index].contentPublish")
		el-row(:gutter="0")
			el-col(:span="24")
				inline-tabs-table(
					:setHeader='true'
					:tableData="currentReportedDetailProcess" 
					:colSetting="tableColSettings[activeTab]")
					template(slot="header-left")
						span.title 최근 보고 정보
					template(slot="header-right")
						.text-right
							router-link.more-link(type="text" :to="currentReportedInformationTabs.find(c => c.prop === activeTab || 'process' ).link") 더보기
					template(slot="tabs")
						el-tabs(v-model='activeTab' @tab-click="setInlineTableByTabs")
							el-tab-pane(
								v-for="(category, index) in currentReportedInformationTabs" 
								:key="index" 
								:label="category.label" 
								:name="category.name")
</template>

<script>
import ContentList from '@/views/contents/ContentList'
// UI component
import ProcessInprogressStatusGraph from '@/components/home/ProcessInprogressStatusGraph.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import InlineTabsTable from '@/components/home/InlineTabsTable.vue'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'

// model
import { currentReportedInformationTabs, tableColSettings } from '@/models/home'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'

// mixin
import contentList from '@/mixins/contentList'

const currentUploadedContentTableOption = {
	rowIdName: 'contentId',
	subdomain: '/contents',
}

export default {
	mixins: [contentList],
	components: {
		ProgressCard,
		InlineTable,
		InlineTabsTable,
		ProcessInprogressStatusGraph,
		ContentList,
		ContentControlDropdown,
	},
	data() {
		return {
			currentReportedDetailProcess,
			currentUploadedContentTableOption,
			currentReportedInformationTabs,
			tableColSettings,
			currentContent: {
				tableData: currentUploadedContent,
				tableOption: {
					rowIdName: 'contentId',
					subdomain: '/contents',
				},
				search: null,
				colSetting: tableColSettings.contents,
			},
			activeTab: currentReportedInformationTabs[0].name,
			currentProcess: {
				tableData: currentReportedDetailProcess,
				tableOption: {
					rowIdName: 'processId',
					subdomain: '/process',
				},
				search: null,
				colSetting: tableColSettings[currentReportedInformationTabs[0].name],
			},
		}
	},
	methods: {
		setInlineTableByTabs(e) {
			this.currentUploadedContentTableOption.subdomain =
				currentReportedInformationTabs[e.index].link
		},
		onClickCell(row, column) {
			if (column.className === 'control-col') return false
			const { rowIdName, subdomain } = this.currentContent.tableOption
			if (!rowIdName) return false
			this.$router.push(`${subdomain}/${row[rowIdName]}`)
		},
	},
}
</script>
