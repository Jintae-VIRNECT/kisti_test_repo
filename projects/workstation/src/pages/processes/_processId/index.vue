<template>
  <div id="process">
    <h3>{{ process }}</h3>
    <p v-for="subProcess in subProcesses" :key="subProcess.id">
      <nuxt-link :to="`/processes/${process.id}/${subProcess.id}`">
        {{ subProcess }}
      </nuxt-link>
    </p>
  </div>
</template>

<script>
import processService from '@/services/process'
import subProcessService from '@/services/subProcess'

export default {
  async asyncData({ params }) {
    return {
      process: await processService.getProcessInfo(params.processId),
      subProcesses: await subProcessService.searchChildSubProcesses(
        params.processId,
      ),
    }
  },
}
</script>
