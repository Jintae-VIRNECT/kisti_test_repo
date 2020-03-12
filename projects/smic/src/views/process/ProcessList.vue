<template lang="pug">
  div
    page-tab-nav
      template(slot='page-nav--right')
        router-link(to="/process/new")
          button.enroll-new-process 신규 공정 등록
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: "/process"}') 공정
    process-dash-banner(initTopic="table")
    .page-nav
      search-tab-nav.search-wrapper.text-right(placeholder="공정 이름, 담당자 이름" :search="params.search" :filter="filter" :sort="sort" @change="onChangeSearch")
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
          span.sub-title 등록된 공정 수 &nbsp;&nbsp;
          span.value {{processTotal}}
      template(slot="body")
        div(v-if="topic === 'table'")
          el-table.inline-table(
            :data='processList' 
            style='width: 100%'
            @cell-click="onClickCell")
            el-table-column(
              v-for="{label, width, prop} in colSetting" 
              :key="prop" 
              :prop="prop" 
              :label="label" 
              :width="width || ''") 
              template(slot-scope='scope')
                table-column(:prop="prop" :data="processList[scope.$index]")
            el-table-column(:width="50" class-name="control-col")
              template(slot-scope='scope')
                process-control-dropdown(
                  :target="processList[scope.$index]"
                  @onChangeData="onChangeData"
                  @onCreateData="onCreateData"
                  @onDeleteData="onDeleteData")
        div(v-else)
          process-list-graph
    pagination(target="process" :params="params")
</template>
<script>
import { mapGetters } from 'vuex'

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
import Pagination from '@/components/common/Pagination.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'

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
    Pagination,
  },
  data() {
    return {
      params: {
        search: '',
        size: 10,
      },
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
        options: [
          {
            value: 'name,asc',
            label: 'ㄱ-ㅎ순',
          },
          {
            value: 'name,desc',
            label: 'ㄱ-ㅎ역순',
          },
          {
            value: 'updated_at,desc',
            label: '최신 보고순',
          },
          {
            value: 'updated_at,asc',
            label: '오래된 보고순',
          },
        ],
        value: 'updated_at,desc',
      },
      topic: 'table',
    }
  },
  computed: {
    colSetting() {
      return colSetting
    },
    ...mapGetters(['processList', 'processTotal']),
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      this.$store.commit('SET_PROCESS_INFO', row)
      this.$router.push(`/process/${row.id}`)
    },
    onChangeSearch(params) {
      this.params = {
        ...this.params,
        ...params,
      }
      this.$store.dispatch('getProcessList', this.params)
    },
    onChangeData(data) {},
    onCreateData(data) {},
    onDeleteData(data) {
      this.$store.dispatch('deleteProcess', data.id)
    },
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
  created() {
    // route query check
    const query = this.$router.currentRoute.query
    if (query && query.search) {
      this.params.search = query.search
    }
    if (query && query.filter) {
      this.filter.value = [
        this.filter.options.find(option => option.label === query.filter).value,
      ]
    }
    this.$store.dispatch('getProcessList', this.params)
  },
}
</script>
