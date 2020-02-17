<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item 공정({{tableData[0].processName}})
      el-breadcrumb-item 세부공정
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        span.title 공정 목록
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
      //- el-pagination.inline-table-pagination(
      //-   v-if='setPagination'
      //-   :hide-on-single-page='false' 
      //-   :page-size="pageSize" 
      //-   :pager-count="tableOption ? tableOption.pagerCount : 5"
      //-   :total='tableData.length' 
      //-   layout='prev, jumper, next'
      //-   :current-page='currentPage'
      //-   @prev-click='currentPage -= 1'
      //-   @next-click='currentPage += 1'
      //- )
    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title {{topic === 'table' ? '세부공정 목록' : '세부공정 진행률 그래프'}}
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
            :data='subProcessData' 
            style='width: 100%'
            @cell-click="onClickCell")
            el-table-column(
              v-for="{label, width, prop} in subColSetting" 
              :key="prop" 
              :prop="prop" 
              :label="label" 
              :width="width || ''") 
              template(slot-scope='scope')
                table-column(:prop="prop" :data="subProcessData[scope.$index]")
            el-table-column(:width="50" class-name="control-col")
              template(slot-scope='scope')
                process-control-dropdown(
                  :target="subProcessData[scope.$index]"
                  @onChangeData="onChangeData"
                  @onCreateData="onCreateData"
                  @onDeleteData="onDeleteData")
          //- el-pagination.inline-table-pagination(
          //-   v-if='setPagination'
          //-   :hide-on-single-page='false' 
          //-   :page-size="pageSize" 
          //-   :pager-count="tableOption ? tableOption.pagerCount : 5"
          //-   :total='tableData.length' 
          //-   layout='prev, jumper, next'
          //-   :current-page='currentPage'
          //-   @prev-click='currentPage -= 1'
          //-   @next-click='currentPage += 1'
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
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'
import { cols as subColSetting } from '@/models/subProcess'
import { sortOptions } from '@/models/index'

// lib
import dayjs from '@/plugins/dayjs'

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
    TableColumn,
  },
  data() {
    return {
      subProcessData: sceneGroup.tableData,
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
      colSetting,
      subColSetting,
    }
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      this.$router.push(`/process/${this.processId}/${row.id}`)
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
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
}
</script>
