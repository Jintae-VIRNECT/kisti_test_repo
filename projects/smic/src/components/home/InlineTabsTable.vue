<template lang="pug">
  .card
    .card__header
      .card__header--left
        slot(name="header-left")
      .card__header--right
        slot(name="header-right")
    .card__body
      slot(name="tabs")
      el-table.inline-table(
        :data='tableData' 
        style='width: 100%'
        @cell-click="onClickCell")
        el-table-column(
          v-for="{label, width, prop} in colSetting" 
          :key="prop" 
          :prop="prop" 
          :label="label" 
          :width="width || ''") 
          template(slot-scope='scope')
            table-column(:prop="prop" :data="tableData[scope.$index]")
        el-table-column(v-if="controlCol" :width="50" class-name="tool-col")
          template(slot-scope='scope')
            process-control-dropdown(:processId="tableData[scope.$index].processId")
</template>
<script>
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown'
import dayjs from '@/plugins/dayjs'
import filters from '@/mixins/filters'
import TableColumn from '@/components/common/TableColumn.vue'

export default {
  components: {
    ProcessControlDropdown,
    TableColumn,
  },
  mixins: [dayjs, filters],
  props: {
    activeTab: String,
    tabInfo: Array,
    tableData: Array,
    colSetting: Array,
    controlCol: Boolean,
  },
  methods: {
    onClickCell(row) {
      const tab = this.tabInfo.find(({ name }) => name === this.activeTab)
      const url = tab.link
        .replace('{processId}', row.processId)
        .replace('{subProcessId}', row.subProcessId)
        .replace('{jobId}', row.jobId)
      this.$router.push(url)
    },
  },
}
</script>

<style lang="scss">
.inline-table-pagination .el-pagination__jump {
  margin-left: 0px !important;
}
.card {
  margin-bottom: 30px;
  background-color: #ffffff;
  border: solid 1px #e6e9ee;
  border-radius: 4px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.04);
}
.card__header {
  .title {
    color: #0d2a58;
    font-size: 16px;
    line-height: 1.75;
    vertical-align: middle;
  }
  .header-left__sub-title {
    display: inline-block;
    margin-left: 16px;
    padding: 0px 6px;
    background-color: #f2f5f9;
    border-radius: 4px;
    .sub-title {
      color: #0d2a58;
      font-weight: 500;
      font-size: 13px;
    }
    img {
      margin-right: 3px;
    }
    & > * {
      vertical-align: middle;
    }
  }
  .header-right__sub-title {
    .sub-title {
      margin-left: 16px;
      color: #455163;
      font-weight: 500;
      font-size: 14px;
      line-height: 2;
      vertical-align: middle;
    }
  }
  &--left {
    display: inline-block;
    width: 40%;
    .title {
      margin-right: 15px;
      color: #0d2a58;
      font-size: 18px;
      line-height: 1.56;
    }
    .sub-title {
      color: #455163;
      font-size: 14px;
      line-height: 2;
    }
  }
  &--right {
    display: inline-block;
    width: 59%;
  }
  .more-link {
    color: #1665d8;
    font-weight: 500;
    font-size: 12px;
    line-height: 1.67;
    text-decoration: underline !important;
  }
  .value {
    color: #0065e0;
    font-weight: 500;
    font-size: 18px;
    line-height: 1.56;
    vertical-align: middle;
  }
}
.inline-table {
  .el-table__body td {
    height: 64px !important;
    &.tool-col {
      padding: 0px;
      .cell {
        height: 100%;
      }
      .cell > div {
        display: flex;
        align-items: center;
        height: 100%;
      }
    }
    .nums {
      height: 28px;
      padding: 6px 10px;
      color: #114997;
      font-weight: 500;
      font-size: 14px;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.57;
      letter-spacing: normal;
      background-color: #fbfbfd;
      border: solid 1px #eaedf3;
      border-radius: 4px;
    }
  }
  &__header .sub-title {
    color: #0d2a58;
    font-weight: 500;
    font-size: 14px;
    font-style: normal;
    font-stretch: normal;
    line-height: 2;
    letter-spacing: normal;
  }
  .process-percent {
    .el-progress-bar__outer {
      margin-right: 8px;
    }
  }
  .el-table__row {
    cursor: pointer;
  }
  .auth-wrapper {
    .auth-img {
      display: inline-block;
      width: 28px;
      height: 28px;
      margin-right: 8px;
      background-repeat: no-repeat;
      background-position: center;
      background-size: cover;
      border-radius: 50%;
      &,
      span {
        vertical-align: middle;
      }
    }
  }
  .total-done .count {
    color: #186ae2;
  }
  .issue-type {
    padding: 4px 8px;
    color: #dc2a2a;
    font-weight: 500;
    font-size: 12px;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.5;
    letter-spacing: normal;
    text-align: center;
    border: solid 1px #dc2a2a;
    border-radius: 4px;
  }
  .content-name {
    img {
      margin-right: 5px;
    }
    span,
    img {
      vertical-align: middle;
    }
  }
  th {
    color: #6d798b;
    font-weight: 500;
    font-size: 12px;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.5;
    letter-spacing: normal;
  }
  tr th:first-child .cell {
    margin-left: 30px !important;
  }
  &.el-table th > .cell,
  &.el-table .cell {
    padding: 0px !important;
    overflow: initial;
  }
  .el-table__row td:first-child .cell {
    margin-left: 30px !important;
  }
  .blub {
    display: inline-block;
    width: 6px;
    height: 6px;
    margin-right: 10px;
    vertical-align: middle;
    border-radius: 50%;
    &.on {
      background-color: #ee5c57;
    }
    &.off {
      background-color: #aabbce;
    }
  }
}
</style>
