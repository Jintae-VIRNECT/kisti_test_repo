<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: `/process`}') 공정({{ subProcessDetail.info.processName }})
      el-breadcrumb-item(:to='{path: `/process/${processId}`}') 세부공정({{subProcessDetail.info.name}})
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
    issue-modal(:toggleIssueModal="toggleIssueModal" :issueId="issueId" @handleCancel="onHandleCancel")
    report-modal(:toggleReportModal="toggleReportModal" :reportId="reportId" @handleCancel="onHandleCancel")
    smart-tool-modal(:toggleSmartToolModal="toggleSmartToolModal" :jobId="jobId" @handleCancel="onHandleCancel")
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
      jobId: null,
      reportId: null,
      issueId: null,
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
    onRowButtonClick({ prop, data }) {
      if (prop === 'issue') {
        this.issueId = data.id
        this.toggleIssueModal = true
      } else if (prop === 'report') {
        this.reportId = data.id
        this.toggleReportModal = true
      } else if (prop === 'smartTool') {
        this.jobId = data.id
        this.toggleSmartToolModal = true
      }
    },
    onHandleCancel() {
      this.toggleIssueModal = false
      this.toggleReportModal = false
      this.toggleSmartToolModal = false
    },
  },
  async created() {
    const { subProcessId } = this.$route.params
    this.$store.dispatch('getSubProcessDetail', subProcessId)
    const { jobs } = await this.$store.dispatch('getJobsList', subProcessId)
    if (jobs.some(job => job.smartTool)) {
      await this.$store.dispatch('getSmartToolList', { subProcessId })
    }
    // modal open
    const { modal, jobId } = this.$router.currentRoute.query
    this.jobId = jobId * 1
    if (modal) {
      setTimeout(() => {
        this[`toggle${modal}Modal`] = true
      }, 100)
    }
  },
}
</script>
