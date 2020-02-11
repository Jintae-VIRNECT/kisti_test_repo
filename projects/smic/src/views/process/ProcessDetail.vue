<template lang="pug">
	div
		el-breadcrumb.header__bread-crumb(separator="/")
			el-breadcrumb-item(:to='{path: `/process/${processId}`}') 공정({{tableData[0].processName}})
			el-breadcrumb-item 세부공정
		inline-table(:setSubHeader="true")
			template(slot="header--secondary")
				span.title 공정 목록
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
		inline-table(:setMainHeader="true")
			template(slot="header-left")
				span.title {{topic === 'table' ? '세부공정 목록' : '세부공정 진행률 그래프'}}
				.vn-label.toggle-topic-btn
					a(v-show="topic === 'graph'" href="#" @click.prevent="toggleGraphTable") 
						img(src="~@/assets/image/ic-graph.svg")
						span 일자별 공정 진행률 그래프
					a(v-show="topic === 'table' " href="#" @click.prevent="toggleGraphTable") 
						img(src="~@/assets/image/ic-list.svg")
						span 리스트
			template(slot="body")
				div(v-if="topic === 'table'")
					el-table.inline-table(
						:data='detailTableData' 
						style='width: 100%'
						@cell-click="onClickCell")
						el-table-column(
							v-for="{label, width, prop} in detailColSetting" 
							:key="prop" 
							:prop="prop" 
							:label="label" 
							:width="width || ''") 
							template(slot-scope='scope')
								.process-percent(v-if="prop === 'processPercent'")
									el-progress(:percentage="detailTableData[scope.$index][prop]" :show-text="true")
								div(v-else-if="prop === 'numOfDetailProcess'")
									span.nums {{detailTableData[scope.$index][prop]}}								
								div(v-else-if="prop === 'issue'")
									.blub(:class="detailTableData[scope.$index][prop] ? 'on' : 'off'")
									span {{detailTableData[scope.$index][prop] ? "있음" : "없음"}}
								div(v-else-if="prop === 'auths'")
									span {{detailTableData[scope.$index][prop] | limitAuthsLength}}
								//- schedule = (startAt ~ endAt)
								.total-done(v-else-if="prop === 'schedule'")
									span {{detailTableData[scope.$index]['startAt'] | dayJs_FilterDateTime}} 
									span &nbsp;~ {{detailTableData[scope.$index]['endAt'] | dayJs_FilterDateTime}}
								div(v-else-if="prop === 'status'")
									button.btn.btn--status(
										size="mini" 
										:class="detailTableData[scope.$index][prop]" 
										plain
									) {{ detailTableData[scope.$index][prop] | statusFilterName }}
								div(v-else)
									
									span {{ detailTableData[scope.$index][prop] }}
						el-table-column(:width="50" class-name="control-col")
							template(slot-scope='scope')
								process-control-dropdown(
									:target="detailTableData[scope.$index]"
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
				div(v-else)
					process-detail-graph
</template>
<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import ProcessDashBanner from '@/components/process/ProcessDashBanner.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown.vue'
import ProcessDetailGraph from '@/components/process/ProcessDetailGraph.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

const detailColSetting = [
  {
    prop: 'name',
    label: '공정 이름',
  },
  {
    prop: 'numOfDetailProcess',
    label: '세부공정 수',
    width: 100,
  },
  {
    prop: 'schedule',
    label: '공정 일정',
  },
  {
    prop: 'processPercent',
    label: '진행률',
    width: 150,
  },
  {
    prop: 'status',
    label: '진행 상태',
    width: 100,
  },
  {
    prop: 'auths',
    label: '세부 공정 담당자',
    width: 200,
  },
  {
    prop: 'issue',
    label: '작업 이슈',
    width: 80,
  },
]

// lib
import dayjs from '@/utils/dayjs'

// mixins
import filters from '@/mixins/filters'

// tmp data
import sceneGroup from '@/data/sceneGroup'

export default {
  mixins: [dayjs, filters],
  components: {
    ProgressCard,
    InlineTable,
    ProcessDashBanner,
    PageTabNav,
    PageBreadCrumb,
    ProcessControlDropdown,
    ProcessDetailGraph,
  },
  data() {
    return {
      detailTableData: sceneGroup.tableData,
      tableData: [
        this.$store.getters.currentReportedDetailProcess.find(
          c => c.id === this.$route.params.id,
        ),
      ],
      processId: this.$route.params.id,
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
      topic: 'table',
    }
  },
  computed: {
    colSetting() {
      return colSetting
    },
    detailColSetting() {
      return detailColSetting
    },
  },
  methods: {
    onClickCell(row, column) {
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
        return (
          row.processName.includes(searchInput) ||
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
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
}
</script>
