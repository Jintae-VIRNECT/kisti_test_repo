<template lang="pug">
  .content-detail
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: "/contents"}') 공정 콘텐츠({{ contentDetail.info.contentName }})
      el-breadcrumb-item 선택 공정 콘텐츠 정보
    inline-table(:setMainHeader="true")
      .header--before(slot="header-left")
        router-link.title(to="/contents")
          i.el-icon-back
          | 선택 공정 콘텐츠 정보
      template(slot="body")
        el-table.inline-table(
          :data='[contentDetail.info]' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in contentsColSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(type="contents" :prop="prop" :data="contentDetail.info || {}")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              content-control-dropdown(
                :data="contentDetail.info"
                @onChangeData="data => onChangeData(contentDetail.info.id)")

    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title 세부공정 콘텐츠 목록 
      template(slot="body")
        el-table.inline-table(
          :data='contentDetail.sceneGroupList' 
          style='width: 100%')
          el-table-column(
            v-for="{label, width, prop} in sceneGroupColSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              div(v-if="prop == 'index'") 
                span {{ contentDetail.sceneGroupList[scope.$index].priority }}.
              div(v-else)
                span {{ contentDetail.sceneGroupList[scope.$index][prop] }}
</template>

<script>
import { mapGetters } from 'vuex'
import InlineTable from '@/components/common/InlineTable'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols as contentsColSetting } from '@/models/contents'
import { cols as sceneGroupColSetting } from '@/models/sceneGroup'

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
      contentsColSetting,
      sceneGroupColSetting,
    }
  },
  computed: {
    ...mapGetters(['contentDetail']),
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
    this.$store.dispatch('getContentsDetail', contentUUID)
    this.$store.dispatch('getSceneGroupList', contentUUID)
  },
}
</script>

<style lang="scss">
.content-detail {
  .card__header--left .title {
    font-size: 16px;
  }
  & > .card:nth-child(2) {
    .el-table th .cell {
      padding-top: 16px;
      padding-bottom: 0;
    }
    .el-table th.is-leaf {
      border-bottom: none;
    }
    .card__header {
      padding: 9px 16px !important;

      .header--before {
        .title {
          color: #0d2a58;
          font-weight: 500;
          font-size: 14px;
          line-height: 2;
          vertical-align: middle;
          .position {
            margin-left: 30px;
            color: #566173;
            font-weight: 500;
            font-size: 12px;
          }
        }
        i {
          margin: 0 14px 2px 6px;
          font-size: 16px;
          vertical-align: middle;
        }
      }
    }
  }
  // cursor style off
  &:not(.process-detail),
  &.process-detail .card:not(.detail-table) {
    .el-table--enable-row-hover .el-table__body tr:hover > td {
      background-color: inherit;
      cursor: default;

      &.control-col .el-dropdown {
        cursor: pointer;
      }
    }
  }
}
</style>
