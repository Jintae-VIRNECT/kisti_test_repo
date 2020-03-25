<template>
  <div id="processes">
    <h2>Process</h2>
    <h3>total: {{ processStatistics.totalProcesses }}</h3>
    <h3>rate: {{ processTotalRate }}%</h3>
    <search-tab-nav
      :filter="processFilter"
      :sort="processSort"
      @submit="searchProcesses"
    />
    <el-table :data="processList" @row-click="processClick">
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
    <el-row type="flex" justify="center">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="processTotal"
        @current-change="processPageChange"
      />
    </el-row>
  </div>
</template>

<script>
// models
import {
  conditions as processStatusList,
  filter as processFilter,
  sort as processSort,
} from '@/models/process/Process'
// services
import processService from '@/services/process'
// components
import SearchTabNav from '@/components/common/SearchTabNav'
import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnCount from '@/components/common/tableColumn/ColumnCount'
import ColumnProgress from '@/components/common/tableColumn/ColumnProgress'
import ColumnStatus from '@/components/common/tableColumn/ColumnStatus'
import ColumnSchedule from '@/components/common/tableColumn/ColumnSchedule'
import ColumnBoolean from '@/components/common/tableColumn/ColumnBoolean'

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
      processStatusList,
      processFilter,
      processSort,
      processSearchParams: {},
    }
  },
  async asyncData() {
    const promise = {
      processes: processService.searchProcesses(),
      processStatistics: processService.getProcessStatistics(),
      processTotalRate: processService.getTotalRate(),
    }
    return {
      processList: (await promise.processes).list,
      processTotal: (await promise.processes).total,
      processStatistics: await promise.processStatistics,
      processTotalRate: await promise.processTotalRate,
    }
  },
  methods: {
    async searchProcesses(params) {
      const processes = await processService.searchProcesses(params)
      this.processList = processes.list
      this.processTotal = processes.total
      this.processSearchParams = params
    },
    async processPageChange(page) {
      const params = { page, ...this.processSearchParams }
      const processes = await processService.searchProcesses(params)
      this.processList = processes.list
    },
    processClick(row) {
      this.$router.push(`/processes/${row.id}`)
    },
  },
}
</script>
