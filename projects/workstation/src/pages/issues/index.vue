<template>
  <div id="issues">
    <h2>Issues</h2>
    <div v-for="issue in issues" :key="issue.id">
      <p>{{ issue }}</p>
    </div>
  </div>
</template>

<script>
import issueService from '@/services/issue'

export default {
  async asyncData() {
    return {
      issues: await issueService.searchIssues(),
    }
  },
}
</script>
