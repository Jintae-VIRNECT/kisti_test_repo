<template lang="pug">
  div
    h1.admin-body__header--margin 홈
    process-dash-banner(initTopic="graph")
    //- process-inprogress-status-graph
    el-row(:gutter="20")
      el-col(:span="24")
        inline-table(:setMainHeader="true")
          template(slot="header-left")
            span.title 최근 등록된 공정
          template(slot="header-right")
            .text-right
              router-link.more-link(type="text" to="/process") 더보기
          template(slot="body")
            el-table.inline-table(
              :data='processList' 
              style='width: 100%'
              @cell-click="onClickProcess")
              el-table-column(
                v-for="{label, width, prop} in processColSetting" 
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
    el-row(:gutter="20")
      el-col(:span="24")
        inline-table(:setMainHeader="true")
          template(slot="header-left")
            span.title 최근 등록된 공정 콘텐츠
          template(slot="header-right")
            .text-right
              router-link.more-link(type="text" to="/contents") 더보기
          template(slot="body")
            el-table.inline-table(
              :data='contentsList' 
              style='width: 100%'
              @cell-click="onClickContent")
              el-table-column(
                v-for="{label, width, prop} in contentsColSetting" 
                :key="prop" 
                :prop="prop" 
                :label="label" 
                :width="width || ''") 
                template(slot-scope='scope')
                  table-column(type="contents" :prop="prop" :data="contentsList[scope.$index]")
              el-table-column(:width="50" class-name="control-col")
                template(slot-scope='scope')
                  content-control-dropdown(:status="contentsList[scope.$index].status")
    el-row(:gutter="0")
      el-col(:span="24")
        //- inline-tabs-table(
        //-   :tableData="currentReportedDetailProcess" 
        inline-tabs-table(
          :activeTab="activeTab"
          :tabInfo="currentReportedInformationTabs"
          :tableData="tabletabsData[activeTab]" 
          :colSetting="tableColSettings[activeTab]"
        )
          template(slot="header-left")
            span.title 최근 보고 정보
          template(slot="tabs")
            el-tabs(v-model='activeTab' @tab-click="setInlineTableByTabs")
              el-tab-pane(
                v-for="(category, index) in currentReportedInformationTabs" 
                :key="index" 
                :label="category.label" 
                :name="category.name")
</template>

<script>
import { mapGetters } from 'vuex'
// UI component
import ContentList from '@/views/contents/ContentList'
// import ProcessInprogressStatusGraph from '@/components/home/ProcessInprogressStatusGraph.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import InlineTabsTable from '@/components/home/InlineTabsTable.vue'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown'
import ProcessDashBanner from '@/components/process/ProcessDashBanner'
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { currentReportedInformationTabs, tableColSettings } from '@/models/home'
import { cols as processColSetting } from '@/models/process'
import { cols as contentsColSetting } from '@/models/contents'

// mixin
import contentList from '@/mixins/contentList'

import dayjs from '@/plugins/dayjs'

const currentUploadedContentTableOption = {
  rowIdName: 'contentId',
  subdomain: '/contents',
}
export default {
  mixins: [contentList, dayjs],
  components: {
    ProgressCard,
    InlineTable,
    InlineTabsTable,
    // ProcessInprogressStatusGraph,
    ContentList,
    ContentControlDropdown,
    ProcessControlDropdown,
    ProcessDashBanner,
    TableColumn,
  },
  data() {
    return {
      currentReportedInformationTabs,
      tableColSettings,
      processColSetting,
      contentsColSetting,
      activeTab: currentReportedInformationTabs[0].name,
      processData: this.$store.getters.currentReportedDetailProcess,
    }
  },
  computed: {
    ...mapGetters([
      'processList',
      'contentsList',
      'subProcessListAll',
      'reportList',
      'issueList',
      'smartToolList',
    ]),
    tabletabsData() {
      return {
        subProcessAll: this.subProcessListAll,
        report: this.reportList,
        issue: this.issueList,
        smartTool: this.smartToolList,
      }
    },
  },
  methods: {
    setInlineTableByTabs(e) {
      const tab = currentReportedInformationTabs[e.index]
      currentUploadedContentTableOption.subdomain = tab.link

      const upperStart = tab.name[0].toUpperCase() + tab.name.slice(1)
      this.$store.dispatch(`get${upperStart}List`, { size: 5 })
    },
    onClickProcess(row, column) {
      if (column.className === 'control-col') return false
      this.$router.push(`process/${row.id}`)
    },
    onClickContent(row, column) {
      if (column.className === 'control-col') return false
      this.$router.push(`contents/${row.contentUUID}`)
    },
    onChangeData(data) {},
    onCreateData(data) {},
    onDeleteData(data) {
      this.$store.dispatch('deleteProcess', data.id)
    },
  },
  created() {
    this.$store.dispatch('getProcessList', { size: 10 })
    this.$store.dispatch('getContentsList', { size: 5 })
    this.$store.dispatch('getSubProcessListAll', { size: 5 })
  },
}
</script>
