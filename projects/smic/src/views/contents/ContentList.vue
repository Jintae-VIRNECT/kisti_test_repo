<template lang="pug">
  div
    el-breadcrumb.header__bread-crumb(separator="/")
      el-breadcrumb-item(:to='{path: "/contents"}') 공정 콘텐츠
    inline-table(:setMainHeader="true")
      template(slot="header-left")
        span.title 공정 콘텐츠 목록
      template(slot="header-right")
        .inline-table__header--right
          span.prefix 업로드된 컨텐츠 
          span.value {{contentsTotal}}
          span.suffix &nbsp;projects
          .divider
          span.prefix 배포중인 컨텐츠 수 컨텐츠 
          span.value {{contentsList | countStopOfContentPublish}}
          span.suffix &nbsp;projects
      template(slot="body")
        el-table.inline-table(
          :data='contentsList' 
          style='width: 100%'
          @cell-click="onClickCell")
          el-table-column(
            v-for="{label, width, prop} in colSetting" 
            :key="prop" 
            :prop="prop" 
            :label="label" 
            :width="width || ''") 
            template(slot-scope='scope')
              table-column(type="contents" :prop="prop" :data="contentsList[scope.$index]")
          el-table-column(:width="50" class-name="control-col")
            template(slot-scope='scope')
              content-control-dropdown(
                :data="contentsList[scope.$index]"
                @onChangeData="data => onChangeData(data,contentsList[scope.$index].contentUUID)")
</template>

<script>
import { mapGetters } from 'vuex'
// UI component
import InlineTable from '@/components/common/InlineTable.vue'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'
import TableColumn from '@/components/common/TableColumn.vue'

// model
import { cols } from '@/models/contents'

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
      tableOption: {
        rowIdName: 'contentUUID',
        subdomain: '/contents',
      },
      search: null,
      colSetting: cols,
    }
  },
  computed: {
    ...mapGetters(['contentsList', 'contentsTotal']),
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      const { rowIdName, subdomain } = this.tableOption
      if (!rowIdName) return false
      this.$router.push(`${subdomain}/${row[rowIdName]}`)
    },
    onChangeData(data, id) {
      this.contentsList = this.contentsList.map(row => {
        if (row.id === id) {
          row.contentPublish = data
        }
        return row
      })
      this.$store.commit('set_currentUploadedContent', this.contentsList)
    },
  },
}
</script>

<style lang="scss">
.inline-table__header--right {
  text-align: right;
  > * {
    vertical-align: middle;
  }
  .divider {
    display: inline-block;
    width: 1px;
    height: 20px;
    margin: 0px 20px;
    background-color: #cdd1d6;
    opacity: 0.82;
  }
  .prefix {
    color: #0d2a58;
    font-weight: 500;
    font-size: 14px;
    line-height: 2;
  }
  .value {
    color: #0065e0;
    font-weight: 500;
    font-size: 18px;
    line-height: 1.56;
  }
  .suffix {
    color: #566173;
    font-weight: 500;
    font-size: 12px;
    line-height: normal;
  }
}
</style>
