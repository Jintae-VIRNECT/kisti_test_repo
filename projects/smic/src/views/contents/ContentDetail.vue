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
              table-column(type="contents" :prop="prop" :data="processContent.tableData[scope.$index]")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              content-control-dropdown(
                :data="processContent.tableData[scope.$index]"
                @onChangeData="data => onChangeData(data,processContent.tableData[scope.$index].id)")

    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title 세부공정 콘텐츠 목록 
      template(slot="body")
        el-table.inline-table(
          :data='sceneGroupData' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in sceneGroupColSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              div(v-if="prop == 'index'") 
                span {{scope.$index + 1}}.
              div(v-else)
                span {{ sceneGroupData[scope.$index][prop] }}
</template>
<style lang="scss">
.content-detail {
  .card__header {
    padding: 9px 16px !important;
    .header--before {
      .title {
        color: #0d2a58;
        font-weight: 500;
        font-size: 14px;
        line-height: 2;
        vertical-align: middle;
      }
      i {
        margin-right: 12px;
        font-size: 16px;
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
import TableColumn from '@/components/common/TableColumn.vue'

import { cols as sceneGroupColSetting } from '@/models/sceneGroup'

import { tableColSettings } from '@/models/home'

// mixin
import contentList from '@/mixins/contentList'

// utils
import dayjs from '@/plugins/dayjs'

export default {
  components: {
    InlineTable,
    ContentControlDropdown,
    PageBreadCrumb,
    TableColumn,
  },
  mixins: [contentList, dayjs],
  data() {
    return {
      processContent: {
        tableData: [
          this.$store.getters.getContentsList.find(
            c => c.contentUUID === this.$route.params.id,
          ),
        ],
        tableOption: {
          rowIdName: 'id',
          subdomain: '/contents',
        },
        search: null,
        colSetting: tableColSettings.contents,
      },
      sceneGroupColSetting,
    }
  },
  computed: {
    sceneGroupData() {
      return this.$store.getters.getSceneGroupList
    },
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
  created() {
    const contentUUID = this.$route.params.id
    this.$store.dispatch('SCENE_GROUP_LIST', contentUUID)
  },
}
</script>
