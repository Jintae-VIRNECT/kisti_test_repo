<template lang="pug">
  .content-detail
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: "/contents"}') 공정 콘텐츠
    inline-table(:setMainHeader="true")
      .header--before(slot="header-left")
        router-link.title(to="/contents")
          i.el-icon-back
          | 선택 공정 콘텐츠 정보
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
                span {{processContent.tableData[scope.$index][prop] | dayJs_FilterDateTime}}
              div(v-else)
                span {{ processContent.tableData[scope.$index][prop]}}
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              content-control-dropdown(
                :contentPublish="processContent.tableData[scope.$index].contentPublish"
                @onChangeData="data => onChangeData(data,processContent.tableData[scope.$index].id)")

    inline-table(:setMainHeader="true")
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
<style lang="scss">
.content-detail {
  .card__header {
    padding: 9px 16px !important;
    .header--before {
      .title {
        font-size: 14px;
        font-weight: 500;
        line-height: 2;
        color: #0d2a58;
        vertical-align: middle;
      }
      i {
        font-size: 16px;
        margin-right: 12px;
        vertical-align: middle;
      }
    }
  }
}
</style>
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
