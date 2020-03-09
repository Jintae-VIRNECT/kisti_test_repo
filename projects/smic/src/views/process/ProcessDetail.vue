<template lang="pug">
  .content-detail.process-detail
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: `/process`}') 공정({{processDetail.info.name}})
      el-breadcrumb-item 세부공정
    inline-table(:setMainHeader="true")
      .header--before(slot="header-left")
        router-link.title(to="/process")
          i.el-icon-back
          | 선택 공정 정보
          span.position(v-if="processDetail.info.position") 공정 위치 : {{ processDetail.info.position }}
      template(slot="body")
        el-table.inline-table(
          :data='[processDetail.info]' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(:prop="prop" :data="processDetail.info")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              process-control-dropdown(
                :target="processDetail.info"
                @onChangeData="onChangeData"
                @onCreateData="onCreateData"
                @onDeleteData="onDeleteData")
    inline-table.detail-table(:setMainHeader="true")
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
            :data='processDetail.subProcessList' 
            style='width: 100%'
            @cell-click="onClickCell")
            el-table-column(
              v-for="{label, width, prop} in subColSetting" 
              :key="prop" 
              :prop="prop" 
              :label="label" 
              :width="width || ''") 
              template(slot-scope='scope')
                table-column(:prop="prop" :data="processDetail.subProcessList[scope.$index]")
            el-table-column(:width="50" class-name="control-col")
              template(slot-scope='scope')
                sub-process-control-dropdown(
                  :target="processDetail.subProcessList[scope.$index]"
                  :processId="processId"
                  @onChangeData="onChangeData"
                  @onCreateData="onCreateData"
                  @onDeleteData="onDeleteData")
        div(v-else)
          process-detail-graph
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
import SubProcessControlDropdown from '@/components/process/SubProcessControlDropdown.vue'
import ProcessDetailGraph from '@/components/process/ProcessDetailGraph.vue'
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols as colSetting, processStatus } from '@/models/process'
import { cols as subColSetting } from '@/models/subProcess'

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
    SubProcessControlDropdown,
    ProcessDetailGraph,
    TableColumn,
  },
  data() {
    return {
      processId: this.$route.params.id,
      topic: 'table',
      colSetting,
      subColSetting,
    }
  },
  computed: {
    ...mapGetters(['processDetail']),
  },
  methods: {
    async onClickCell(row, column) {
      if (column.className === 'control-col') return false
      this.$store.commit('SET_SUB_PROCESS_INFO', row)
      this.$router.push(`/process/${this.processId}/${row.subProcessId}`)
    },
    onChangeData(data) {},
    onCreateData(data) {},
    onDeleteData(data) {},
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
  },
  created() {
    this.$store.commit('SET_SUB_PROCESS_LIST', [])
    this.$store.dispatch('getProcessInfo', this.processId)
    this.$store.dispatch('getSubProcessList', { processId: this.processId })
  },
}
</script>
