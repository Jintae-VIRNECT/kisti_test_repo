<template lang="pug">
	div
		page-tab-nav
			template(slot='page-nav--right')
				router-link(to="/process/new")
					button.enroll-new-process 신규 공정 등록
		page-bread-crumb(title='공정')
		process-dash-banner(:data="tableData")
		.page-nav
			.search-wrapper.text-right
				el-input.tool.search(placeholder='공정 이름, 담당자 이름' v-model='searchInput' @change="onChangeSearch(searchInput, filter.value, sort.value)")
					el-button(slot='append' icon='el-icon-search')
				span 필터 : 
				el-select(v-model='filter.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
					el-option(v-for='item in filter.options' :key='item.value' :label='item.label' :value='item.value')
				span 정렬 : 
				el-select(v-model='sort.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
					el-option(v-for='item in sort.options' :key='item.value' :label='item.label' :value='item.value')
		inline-table(:setMainHeader="true")
			template(slot="header-left")
				span.title 공정 목록
			template(slot="header-right")
				.inline-table__header.text-right
					span.sub-title 등록된 공정 수 
					span.value {{tableData.length}}
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
								span {{tableData[scope.$index]['startAt'] | dayJs_FilterDateTime}} 
								span &nbsp;~ {{tableData[scope.$index]['endAt'] | dayJs_FilterDateTime}}
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
								:target="tableData[scope.$index]"
								@onChangeData="onChangeData"
								@onCreateData="onCreateData"
								@onDeleteData="onDeleteData")
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
import { cols as colSetting, processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

// lib
import dayjs from '@/utils/dayjs'

export default {
  mixins: [dayjs],
  components: {
    ProgressCard,
    InlineTable,
    ProcessDashBanner,
    PageTabNav,
    PageBreadCrumb,
    ProcessControlDropdown,
  },
  data() {
    return {
      tableData: null,
      searchInput: null,
      filter: {
        options: [
          {
            value: null,
            label: '전체',
          },
          ...processStatus,
        ],
        value: null,
      },
      sort: {
        options: sortOptions,
        value: null,
      },
    }
  },
  mounted() {
    this.tableData = this.$store.getters.currentReportedDetailProcess
  },
  computed: {
    colSetting() {
      return colSetting
    },
  },
  methods: {
    onClickCell(row, column) {
      console.log('row : ', row)
      if (column.className === 'control-col') return false
      this.$router.push(`/process/${row.id}`)
    },
    onChangeData(data) {
      const updatedTableData = this.tableData.map(row => {
        if (row.id === data.id) {
          row = data
        }
        return row
      })
      this.tableData = updatedTableData
      this.$store.commit('set_currentReportedDetailProcess', this.tableData) // v2 에 axios로 수정
    },
    onCreateData(data) {
      this.tableData.push(data)
      this.$store.commit('set_currentReportedDetailProcess', this.tableData) // v2 에 axios로 수정
    },
    onDeleteData(data) {
      this.tableData = this.tableData.filter(row => row.id !== data.id)
      this.$store.commit('set_currentReportedDetailProcess', this.tableData) // v2 에 axios로 수정
    },
    async onChangeSearch(searchInput, filterValue, sortValue) {
      let tmpTableData = this.$store.getters.currentReportedDetailProcess
      tmpTableData = await this.onChangeSearchText(tmpTableData, searchInput)
      tmpTableData = await this.onChangeFilter(tmpTableData, filterValue)
      tmpTableData = await this.onChangeSort(tmpTableData, sortValue)
      this.tableData = tmpTableData
    },
    onChangeSearchText(tableData, searchInput) {
      return tableData.filter(row => {
        console.log('row : ', row)
        return (
          row.processName.includes(searchInput) ||
          // row.auth.includes(searchInput)
          row.auths.some(a => a.includes(searchInput))
        )
      })
    },
    onChangeFilter(tableData, filterValue) {
      if (!filterValue) return tableData
      return tableData.filter(row => row.status === filterValue)
    },
    onChangeSort(tableData, sortValue) {
      if (!sortValue) return tableData
      if (sortValue === 'alphabetDesc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? 1 : -1,
        )
      else if (sortValue === 'alphabetAsc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? -1 : 1,
        )
      else if (sortValue === 'createdAtDesc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? 1 : -1))
      else if (sortValue === 'createdAtAsc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? -1 : 1))
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
        if (sumOfStrings > 13) {
          const midfix = sumOfStrings - divider.length <= 13 ? '' : '...'
          const suffix =
            array.length - 1 > i ? `+${array.length - (i + 1)}` : ''
          return answer.slice(0, 13) + midfix + suffix
        } else if (array.length - 1 === i) break
        answer += divider
      }
      return answer
    },
  },
}
</script>
