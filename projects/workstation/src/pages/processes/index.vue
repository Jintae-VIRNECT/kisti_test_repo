<template>
  <div id="processes">
    <h3>total: {{ processesStatistics.totalProcesses }}</h3>
    <search-tab-nav
      :filter="searchFilter"
      :sort="searchSort"
      @submit="searchProcesses"
    />
    <p v-for="process in processes" :key="process.id">
      {{ process }}
    </p>
  </div>
</template>

<script>
// models
import { filter as searchFilter, sort as searchSort } from '@/models/process'
// services
import processService from '@/services/process'
// components
import SearchTabNav from '@/components/common/SearchTabNav'

export default {
  components: {
    SearchTabNav,
  },
  data() {
    return {
      searchFilter,
      searchSort,
    }
  },
  async asyncData() {
    return {
      processes: await processService.getDefaultProcessesList(),
      processesStatistics: await processService.getProcessesStatistics(),
    }
  },
  methods: {
    async searchProcesses(params) {
      this.processes = await processService.searchProcesses(params)
    },
  },
}
</script>
