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
		inline-table(:setHeader="true")
			template(slot="header-left")
				span.title 공정 목록
			template(slot="header-right")
				.inline-table__header.text-right
					span.sub-title 등록된 공정 수 
					span.value 102
			template(slot="body")
				el-table.inline-table(
					:data='tableData' 
					style='width: 100%'
					@cell-click="onClickCell")
					el-table-column(
						v-for="{label, width, prop} in colSetting" 
						:key="prop" 
						:prop="prop" 
						:label="label" 
						:width="width || ''") 
						template(slot-scope='scope')
							.process-percent(v-if="prop === 'processPercent'")
								el-progress(:percentage="tableData[scope.$index][prop]" :show-text="true")
							div(v-else-if="prop === 'numOfDetailProcess'")
								span.nums {{tableData[scope.$index][prop]}}								
							div(v-else-if="prop === 'issue'")
								.blub(:class="tableData[scope.$index][prop] ? 'on' : 'off'")
								span {{tableData[scope.$index][prop] ? "있음" : "없음"}}
							div(v-else-if="prop === 'auths'")
								span {{tableData[scope.$index][prop] | limitAuthsLength}}
							//- schedule = (startAt ~ endAt)
							.total-done(v-else-if="prop === 'schedule'")
								span {{tableData[scope.$index]['startAt']}} 
								span &nbsp;~ {{tableData[scope.$index]['endAt']}}
							div(v-else-if="prop === 'status'")
								button.btn.btn--status(
									size="mini" 
									:class="tableData[scope.$index][prop]" 
									plain
								) {{ tableData[scope.$index][prop] | statusFilterName }}
							div(v-else)
								span {{ tableData[scope.$index][prop] }}
					el-table-column(:width="50" class-name="control-col")
						template(slot-scope='scope')
							process-control-dropdown(
								:data="tableData"
								:targetId="tableData[scope.$index].id" 
								@onChangeData="onChangeData"
								@onCreateData="onCreateData")
			//- el-pagination.inline-table-pagination(
			//- 	v-if='setPagination'
			//- 	:hide-on-single-page='false' 
			//- 	:page-size="pageSize" 
			//- 	:pager-count="tableOption ? tableOption.pagerCount : 5"
			//- 	:total='tableData.length' 
			//- 	layout='prev, jumper, next'
			//- 	:current-page='currentPage'
			//- 	@prev-click='currentPage -= 1'
			//- 	@next-click='currentPage += 1'
			//- )
</template>
<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import ProcessDashBanner from '@/components/process/ProcessDashBanner.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown'

// model
import { cols } from '@/models/process'

export default {
  components: {
    ProgressCard,
    InlineTable,
    ProcessDashBanner,
    PageTabNav,
    PageBreadCrumb,
    ProcessControlDropdown,
  },
  created() {
    // 시연용
    this.tableData = this.$store.getters.currentReportedDetailProcess
  },
  data() {
    return {
      tableData: null,
      search: null,
      tableOption: {
        rowIdName: 'processId',
        subdomain: '/process',
      },
      colSetting: cols,
    }
  },
  computed: {
    cols() {
      return cols
    },
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      const { rowIdName, subdomain } = this.tableOption
      if (!rowIdName) return false
      this.$router.push(`${subdomain}/${row[rowIdName]}`)
    },
    onChangeData(data) {
      // const updatedTableData = this.tableData.map(row => {
      // 	if (row.id === data.id) {
      // 		for (let prop in data) {
      // 			row[prop] = data[prop]
      // 		}
      // 	}
      // 	return row
      // })
      // this.tableData = updatedTableData
      // this.$store.commit('set_currentReportedDetailProcess', updatedTableData) // 시연용
      this.tableData = data
      this.$store.commit('set_currentReportedDetailProcess', data)
    },
    onCreateData(data) {
      this.tableData.push(data)
      this.$store.commit('set_currentReportedDetailProcess', this.tableData)
    },
  },
  filters: {
    statusFilterName(value) {
      if (value == 'complete') return '완료'
      else if (value == 'progress') return '진행'
      else if (value == 'idle') return '미진행'
      else if (value == 'imcomplete') return '미흡'
    },
    limitAuthsLength(array) {
      let answer = ''
      let sumOfStrings = 0
      const divider = ', '
      for (let i = 0; i < array.length; i++) {
        answer += array[i]
        sumOfStrings += array[i].length + divider.length
        if (sumOfStrings > 13) return answer + '...'
        if (array.length - 1 === i) break
        answer += divider
      }
      return answer
    },
  },
}
</script>
