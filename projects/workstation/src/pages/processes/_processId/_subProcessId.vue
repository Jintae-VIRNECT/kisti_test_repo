<template>
  <div id="subProcess">
    <h3>{{ subProcess }}</h3>
    <p v-for="job in jobs" :key="job.id">
      {{ job }}
    </p>
  </div>
</template>

<script>
import subProcessService from '@/services/subProcess'
import jobService from '@/services/job'

export default {
  async asyncData({ params }) {
    return {
      subProcess: await subProcessService.getSubProcessInfo(
        params.subProcessId,
      ),
      jobs: await jobService.searchChildJobs(params.subProcessId),
    }
  },
}
</script>
