<template>
  <div id="processes">
    <h2>Process</h2>
    <h3>total: {{ processStatistics.totalProcesses }}</h3>
    <search-tab-nav
      :filter="searchFilter"
      :sort="searchSort"
      @submit="searchProcesses"
    />
    <p v-for="process in processes" :key="process.id">
      <nuxt-link :to="`/processes/${process.id}`">{{ process }}</nuxt-link>
    </p>
  </div>
</template>

<script>
// models
import {
  filter as searchFilter,
  sort as searchSort,
} from '@/models/process/Process'
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
      processes: await processService.searchProcesses(),
      processStatistics: await processService.getProcessStatistics(),
    }
  },
  methods: {
    async searchProcesses(params) {
      this.processes = await processService.searchProcesses(params)
    },
  },
}
</script>
