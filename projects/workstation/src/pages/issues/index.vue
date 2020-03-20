<template>
  <div id="issues">
    <h2>Issues</h2>
    <p v-for="issue in issues" :key="issue.id">
      <nuxt-link :to="`/issues/${issue.id}`">{{ issue }}</nuxt-link>
    </p>
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
