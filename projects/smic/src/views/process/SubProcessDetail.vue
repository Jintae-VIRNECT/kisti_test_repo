<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: `/process/${processId}`}') 공정({{ processDetail.info.name }})
      el-breadcrumb-item 세부공정({{subProcessDetail.info.name}})
      el-breadcrumb-item 작업
    inline-table(:setSubHeader="true")
      template(slot="header--secondary")
        span.title 선택 세부공정 정보
      template(slot="body")
        el-table.inline-table(
          :data='[subProcessDetail.info]' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in subProcessColSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(:prop="prop" :data="subProcessDetail.info")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              process-control-dropdown(
                :target="subProcessDetail.info"
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
            :data='subProcessDetail.jobsList' 
            style='width: 100%')
            el-table-column(
              v-for="{label, width, prop} in jobsColSetting" 
              :key="prop" 
              :prop="prop" 
              :label="label" 
              :width="width || ''")
              template(slot-scope='scope')
                table-column(:prop="prop" :data="subProcessDetail.jobsList[scope.$index]" @buttonClick="onRowButtonClick")
        div(v-else)
          process-detail-graph
    issue-modal(:toggleIssueModal="toggleIssueModal" @handleCancel="onHandleCancel")
    report-modal(:toggleReportModal="toggleReportModal" @handleCancel="onHandleCancel")
    smart-tool-modal(:toggleSmartToolModal="toggleSmartToolModal" @handleCancel="onHandleCancel")
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
import ProcessDetailGraph from '@/components/process/ProcessDetailGraph.vue'
import TableColumn from '@/components/common/TableColumn.vue'
import IssueModal from '@/components/process/IssueModal.vue'
import ReportModal from '@/components/process/ReportModal.vue'
import SmartToolModal from '@/components/process/SmartToolModal.vue'

// model
import { cols as subProcessColSetting } from '@/models/subProcess'
import { cols as jobsColSetting } from '@/models/jobs'

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
    IssueModal,
    ReportModal,
    SmartToolModal,
  },
  data() {
    return {
      toggleIssueModal: false,
      toggleReportModal: false,
      toggleSmartToolModal: false,
      processId: this.$route.params.id,
      topic: 'table',
      subProcessColSetting,
      jobsColSetting,
    }
  },
  computed: {
    ...mapGetters(['processDetail', 'subProcessDetail']),
  },
  methods: {
    onChangeData(data) {},
    onCreateData(data) {},
    onDeleteData(data) {},
    toggleGraphTable() {
      this.topic = this.topic === 'table' ? 'graph' : 'table'
    },
    onRowButtonClick({ prop }) {
      if (prop === 'issueId') this.toggleIssueModal = true
      else if (prop === 'reportId') this.toggleReportModal = true
      else if (prop === 'smartTool') this.toggleSmartToolModal = true
    },
    onHandleCancel() {
      this.toggleIssueModal = false
      this.toggleReportModal = false
      this.toggleSmartToolModal = false
    },
  },
}
</script>
