<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: `/process/${processId}`}') 공정({{tableData[0].processName}})
      el-breadcrumb-item(:to='{path: `/process/${processId}`}') 세부공정({{tableData[0].processName}})
      el-breadcrumb-item 작업
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        span.title 선택 세부공정 정보
      template(slot="body")
        el-table.inline-table(
          :data='tableData' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(:prop="prop" :data="tableData[scope.$index]")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              process-control-dropdown(
                :target="tableData[scope.$index]"
                @onChangeData="onChangeData"
                @onCreateData="onCreateData"
                @onDeleteData="onDeleteData")

    // 작업 목록
    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title {{topic === 'table' ? '작업 목록' : '작업 진행률 그래프'}}
        .vn-label.toggle-topic-btn
          a(v-show="topic === 'table'" href="#" @click.prevent="toggleGraphTable") 
            img(src="~@/assets/image/ic-graph.svg")
            span 그래프
          a(v-show="topic === 'graph' " href="#" @click.prevent="toggleGraphTable") 
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
                table-column(:prop="prop" :data="detailTableData[scope.$index]")
            el-table-column(:width="50" class-name="control-col")
              template(slot-scope='scope')
                process-control-dropdown(
                  :target="detailTableData[scope.$index]"
                  @onChangeData="onChangeData"
                  @onCreateData="onCreateData"
                  @onDeleteData="onDeleteData")
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
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

const detailColSetting = [
  {
    prop: 'subProcessName',
    label: '세부공정 이름',
  },
  {
    prop: 'numOfDetailProcess',
    label: '작업 수',
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
    label: '세부공정 담당자',
    width: 200,
  },
  {
    prop: 'issue',
    label: '작업 이슈',
    width: 80,
  },
]

// lib
import dayjs from '@/plugins/dayjs'

// mixins
import filters from '@/mixins/filters'

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
    TableColumn,
  },
  data() {
    return {
      detailTableData: this.$store.getters.getTaskList,
      tableData: [
        this.$store.getters.sceneGroup.tableData.find(
          c => c.id === this.$route.params.subProcessId,
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
      this.$router.push(`/process/${this.processId}/task`)
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
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
}
</script>
