<template>
  <div id="processes">
    <h2>Process</h2>
    <h3>total: {{ processStatistics.totalProcesses }}</h3>
    <h3>rate: {{ processTotalRate }}%</h3>
    <search-tab-nav
      :filter="searchFilter"
      :sort="searchSort"
      @submit="searchProcesses"
    />
    <el-table :data="processes" @row-click="rowClick">
      <column-default :label="$t('process.column.name')" prop="name" />
      <column-count
        :label="$t('process.column.subProcessTotal')"
        prop="subProcessTotal"
        :width="100"
      />
      <column-schedule
        :label="$t('process.column.schedule')"
        startProp="startDate"
        endProp="endDate"
        :width="280"
      />
      <column-progress
        :label="$t('process.column.progressRate')"
        prop="progressRate"
        :width="170"
      />
      <column-count
        :label="$t('process.column.doneCount')"
        prop="doneCount"
        :width="130"
      />
      <column-status
        :label="$t('process.column.conditions')"
        prop="conditions"
        :width="100"
        :statusList="processStatusList"
      />
      <column-boolean
        :label="$t('process.column.issuesTotal')"
        prop="issuesTotal"
        :trueText="$t('process.hasIssue')"
        :falseText="$t('process.hasNotIssue')"
        :width="100"
      />
    </el-table>
  </div>
</template>

<script>
// models
import {
  conditions as processStatusList,
  filter as searchFilter,
  sort as searchSort,
} from '@/models/process/Process'
// services
import processService from '@/services/process'
// components
import SearchTabNav from '@/components/common/SearchTabNav'
import ColumnDefault from '@/components/common/column/ColumnDefault'
import ColumnCount from '@/components/common/column/ColumnCount'
import ColumnProgress from '@/components/common/column/ColumnProgress'
import ColumnStatus from '@/components/common/column/ColumnStatus'
import ColumnSchedule from '@/components/common/column/ColumnSchedule'
import ColumnBoolean from '@/components/common/column/ColumnBoolean'

export default {
  components: {
    SearchTabNav,
    ColumnDefault,
    ColumnCount,
    ColumnProgress,
    ColumnStatus,
    ColumnSchedule,
    ColumnBoolean,
  },
  data() {
    return {
      searchFilter,
      searchSort,
      processStatusList,
    }
  },
  async asyncData() {
    return {
      processes: await processService.searchProcesses(),
      processStatistics: await processService.getProcessStatistics(),
      processTotalRate: await processService.getTotalRate(),
    }
  },
  methods: {
    async searchProcesses(params) {
      this.processes = await processService.searchProcesses(params)
    },
    rowClick(row) {
      this.$router.push(`/processes/${row.id}`)
    },
  },
}
</script>
