<template lang="pug">
  div
    page-tab-nav
      template(slot='page-nav--right')
        router-link(to="/process/new")
          button.enroll-new-process 신규 공정 등록
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: "/process"}') 공정
    process-dash-banner(:data="tableData" initTopic="table")
    .page-nav
      search-tab-nav.search-wrapper.text-right(placeholder="공정 이름, 담당자 이름" :search="searchInput" :filter="filter" :sort="sort" @change="onChangeData")
    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title 공정 목록
        .vn-label.toggle-topic-btn
          a(v-show="topic === 'table'" href="#" @click.prevent="toggleGraphTable") 
            img(src="~@/assets/image/ic-graph.svg")
            span 일자별 공정 진행률 그래프
          a(v-show="topic === 'graph' " href="#" @click.prevent="toggleGraphTable") 
            img(src="~@/assets/image/ic-list.svg")
            span 리스트
      template(slot="header-right")
        .inline-table__header.text-right
          span.sub-title 등록된 공정 수 
          span.value {{tableData.length}}
      template(slot="body")
        div(v-if="topic === 'table'")
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
                table-column(:prop="prop" :data="tableData[scope.$index]")
            el-table-column(:width="50" class-name="control-col")
              template(slot-scope='scope')
                process-control-dropdown(
                  :target="tableData[scope.$index]"
                  @onChangeData="onChangeData"
                  @onCreateData="onCreateData"
                  @onDeleteData="onDeleteData")
        div(v-else)
          process-list-graph
</template>
<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import ProcessDashBanner from '@/components/process/ProcessDashBanner.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown.vue'
import SearchTabNav from '@/components/common/SearchTabNav.vue'
import ProcessListGraph from '@/components/process/ProcessListGraph.vue'
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

// lib
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [dayjs],
  components: {
    ProgressCard,
    InlineTable,
    ProcessDashBanner,
    PageTabNav,
    PageBreadCrumb,
    ProcessControlDropdown,
    SearchTabNav,
    ProcessListGraph,
    TableColumn,
  },
  data() {
    return {
      tableData: this.$store.getters.currentReportedDetailProcess,
      searchInput: null,
      filter: {
        options: [
          {
            value: 'All',
            label: '전체',
          },
          ...processStatus,
        ],
        value: ['All'],
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
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      this.$store.commit('LAST_PROCESS', row)
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
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
  created() {
    // route query check
    const filterQuery = this.$router.currentRoute.query.filter
    if (filterQuery) {
      this.filter.value = [
        this.filter.options.find(option => option.label === filterQuery).value,
      ]
    }
  },
}
</script>
