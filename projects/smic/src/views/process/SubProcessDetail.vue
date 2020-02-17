<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: `/process/${processId}`}') 공정({{ lastProcess.processName }})
      el-breadcrumb-item 세부공정({{tableData[0].subProcessName}})
      el-breadcrumb-item 작업
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        span.title 선택 세부공정 정보
      template(slot="body")
        el-table.inline-table(
          :data='tableData' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in subProcessColSetting" 
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
            :data='taskTableData' 
            style='width: 100%')
            el-table-column(
              v-for="{label, width, prop} in taskColSetting" 
              :key="prop" 
              :prop="prop" 
              :label="label" 
              :width="width || ''")
              template(slot-scope='scope')
                table-column(:prop="prop" :data="taskTableData[scope.$index]")
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
import { cols as subProcessColSetting } from '@/models/subProcess'
import { cols as taskColSetting } from '@/models/task'

// temp data
import sceneGroup from '@/data/sceneGroup'

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
      lastProcess: this.$store.getters.getLastProcess || {},
      taskTableData: this.$store.getters.getTaskList,
      tableData: [
        sceneGroup.tableData.find(
          c => c.id === this.$route.params.subProcessId,
        ),
      ],
      processId: this.$route.params.id,
      topic: 'table',
      subProcessColSetting,
      taskColSetting,
    }
  },
  methods: {
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
