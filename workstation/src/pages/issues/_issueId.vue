<template>
  <div id="issue">
    <h3>{{ issue.id }}</h3>
    <p>
      {{ issue }}
    </p>
  </div>
</template>

<script>
import issueService from '@/services/issue'

export default {
  async asyncData({ params }) {
    return {
      issue: await issueService.getIssueInfo(params.issueId),
    }
  },
}
</script>
