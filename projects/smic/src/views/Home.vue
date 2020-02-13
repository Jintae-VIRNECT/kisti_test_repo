<template lang="pug">
  div
    h1.admin-body__header 홈
    process-dash-banner(:data="processData" initTopic="graph")
    process-inprogress-status-graph
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
              :data='contentsTableData.slice(0,5)' 
              style='width: 100%'
              @cell-click="onClickCell")
              el-table-column(
                v-for="{label, width, prop} in currentContent.colSetting" 
                :key="prop" 
                :prop="prop" 
                :label="label" 
                :width="width || ''") 
                template(slot-scope='scope')
                  //- 이슈 타입
                  .content-name(v-if="prop === 'contentName'")
                    img.prefix-img(src="~@/assets/image/ic-content.svg")
                    span {{contentsTableData[scope.$index][prop]}}
                  div(v-else-if="prop === 'status'")
                    span.publish-boolean(:class="contentsTableData[scope.$index][prop]") {{contentsTableData[scope.$index][prop] | publishBoolean}}
                  .auth-wrapper(v-else-if="prop === 'uploaderName'")
                    .auth-img(:style="{'background-image': `url(${contentsTableData[scope.$index]['uploaderProfile']})`}")
                    span {{contentsTableData[scope.$index][prop]}}
                  div(v-else-if="prop === 'uploadDate'")
                    span {{contentsTableData[scope.$index][prop] | dayJs_FilterDateTime}}
                  div(v-else-if="prop === 'contentSize'")
                    span.nums {{ contentsTableData[scope.$index][prop]}}
                  div(v-else)
                    span {{ contentsTableData[scope.$index][prop]}}
              el-table-column(:width="50" class-name="control-col")
                template(slot-scope='scope')
                  content-control-dropdown(:status="contentsTableData[scope.$index].status")
    el-row(:gutter="0")
      el-col(:span="24")
        //- inline-tabs-table(
        //-   :tableData="currentReportedDetailProcess" 
        inline-tabs-table(
          :tableData="tabletabsData[activeTab]" 
          :colSetting="tableColSettings[activeTab]")
          template(slot="header-left")
            span.title 최근 보고 정보
          template(slot="header-right")
            .text-right
              router-link.more-link(type="text" :to="currentReportedInformationTabs.find(c => c.prop === activeTab || 'process' ).link") 더보기
          template(slot="tabs")
            el-tabs(v-model='activeTab' @tab-click="setInlineTableByTabs")
              el-tab-pane(
                v-for="(category, index) in currentReportedInformationTabs" 
                :key="index" 
                :label="category.label" 
                :name="category.name")
</template>

<script>
// UI component
import ContentList from '@/views/contents/ContentList'
import ProcessInprogressStatusGraph from '@/components/home/ProcessInprogressStatusGraph.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import InlineTabsTable from '@/components/home/InlineTabsTable.vue'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import ProcessDashBanner from '@/components/process/ProcessDashBanner'

// model
import { currentReportedInformationTabs, tableColSettings } from '@/models/home'

/// data
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'
import tabletabsData from '@/data/tabletabsData'
// mixin
import contentList from '@/mixins/contentList'

import dayjs from '@/utils/dayjs'

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
    ProcessInprogressStatusGraph,
    ContentList,
    ContentControlDropdown,
    ProcessDashBanner,
  },
  data() {
    return {
      tabletabsData,
      currentReportedDetailProcess,
      currentReportedInformationTabs,
      tableColSettings,
      currentContent: {
        tableOption: {
          rowIdName: 'contentUUID',
          subdomain: '/contents',
        },
        search: null,
        colSetting: tableColSettings.contents,
      },
      activeTab: currentReportedInformationTabs[0].name,
      currentProcess: {
        tableData: currentReportedDetailProcess,
        tableOption: {
          rowIdName: 'id',
          subdomain: '/process',
        },
        search: null,
        colSetting: tableColSettings[currentReportedInformationTabs[0].name],
      },
      processData: this.$store.getters.currentReportedDetailProcess,
    }
  },
  computed: {
    contentsTableData() {
      return this.$store.getters.getCurrentContentsList
    },
  },
  methods: {
    setInlineTableByTabs(e) {
      currentUploadedContentTableOption.subdomain =
        currentReportedInformationTabs[e.index].link
    },
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      const { rowIdName, subdomain } = this.currentContent.tableOption
      if (!rowIdName) return false
      this.$router.push(`${subdomain}/${row[rowIdName]}`)
    },
  },
  created() {
    this.$store.dispatch('CURRENT_CONTENTS_LIST')
  },
}
</script>
