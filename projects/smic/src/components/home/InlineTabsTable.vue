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
        .replace('{issueId}', row.issueId)
      this.$router.push(url)
    },
  },
}
</script>
