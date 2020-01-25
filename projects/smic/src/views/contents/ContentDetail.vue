<template lang="pug">
  div
    page-bread-crumb(title='공정 콘텐츠')
    inline-table(:setHeader="true")
      template(slot="header-left")
        span.title 공정 콘텐츠 목록
      template(slot="header-right")
        .inline-table__header--right
          span.prefix 업로드된 컨텐츠 
          span.value {{processContent.tableData | countAllContents}}
          span.suffix &nbsp;projects
          .divider
          span.prefix 배포중인 컨텐츠 수 컨텐츠 
          span.value {{processContent.tableData | countStopOfContentPublish}}
          span.suffix &nbsp;projects
      template(slot="body")
        el-table.inline-table(
          :data='processContent.tableData' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in processContent.colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              .content-name(v-if="prop === 'name'")
                img.prefix-img(src="~@/assets/image/ic-content.svg")
                span {{processContent.tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'contentPublish'")
                span.publish-boolean(:class="processContent.tableData[scope.$index][prop]") {{processContent.tableData[scope.$index][prop] | publishBoolean}}
              .auth-wrapper(v-else-if="prop === 'auth'")
                .auth-img(:style="{'background-image': `url(${processContent.tableData[scope.$index]['profileImg']})`}")
                span {{processContent.tableData[scope.$index][prop]}}
              div(v-else-if="prop === 'uploadedAt'")
                span {{processContent.tableData[scope.$index][prop] | filterDateTime}}
              div(v-else)
                span {{ processContent.tableData[scope.$index][prop]}}
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              content-control-dropdown(
                :contentPublish="processContent.tableData[scope.$index].contentPublish"
                @onChangeData="data => onChangeData(data,processContent.tableData[scope.$index].id)")

    inline-table(:setHeader="true")
      template(slot="header-left")
        span.title 세부공정 콘텐츠 목록 
      template(slot="body")
        el-table.inline-table(
          :data='detailProcessContents.tableData' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in detailProcessContents.colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              div(v-if="prop == 'index'") 
                span {{scope.$index + 1}}.
              div(v-else)
                span {{ detailProcessContents.tableData[scope.$index][prop] }}
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
</template>

<script>
import InlineTable from '@/components/common/InlineTable'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'

import sceneGroup from '@/data/sceneGroup'
import { tableColSettings } from '@/models/home'

// mixin
import contentList from '@/mixins/contentList'

// utils
import dayjs from '@/utils/dayjs'

export default {
  components: {
    InlineTable,
    ContentControlDropdown,
    PageBreadCrumb,
  },
  mixins: [contentList, dayjs],
  created() {
    this.processContent.tableData = [
      this.$store.getters.currentUploadedContent.find(
        c => c.id === this.$route.params.id,
      ),
    ]
  },
  data() {
    return {
      processContent: {
        tableData: this.$store.getters.currentUploadedContent,
        tableOption: {
          rowIdName: 'id',
          subdomain: '/contents',
        },
        search: null,
        colSetting: tableColSettings.contents,
      },
      detailProcessContents: {
        sceneGroup,
        tableData: sceneGroup.tableData,
        colSetting: sceneGroup.tableOption.colSetting,
      },
    }
  },
  methods: {
    onChangeData(data, id) {
      this.processContent.tableData[0].contentPublish = data
      let updatedTable = this.$store.getters.currentUploadedContent
      updatedTable = updatedTable.map(row => {
        if (row.id === id) {
          row.contentPublish = data
        }
        return row
      })
      this.$store.commit('set_currentUploadedContent', updatedTable)
    },
  },
}
</script>
