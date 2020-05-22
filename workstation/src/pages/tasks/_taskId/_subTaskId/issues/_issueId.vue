<template>
  <el-dialog
    id="issue-modal"
    :visible.sync="showMe"
    :title="$t('results.issueDetail.title')"
    width="540px"
    top="11vh"
    @close="closed"
  >
    <dl>
      <dt>{{ $t('results.issueDetail.reporter') }}</dt>
      <dd>{{ issue.workerName }}</dd>
      <dt>{{ $t('results.issueDetail.reportedDate') }}</dt>
      <dd>{{ issue.reportedDate }}</dd>
      <dt>{{ $t('results.issueDetail.content') }}</dt>
      <dd>{{ issue.caption }}</dd>
    </dl>
  </el-dialog>
</template>

<script>
import resultService from '@/services/result'

export default {
  async asyncData({ params }) {
    return {
      issue: await resultService.getIssueDetail(params.issueId),
    }
  },
  data() {
    return {
      showMe: true,
    }
  },
  methods: {
    closed() {
      const { taskId, subTaskId } = this.issue
      this.showMe = false
      this.$router.replace(`/tasks/${taskId}/${subTaskId}`)
    },
  },
}
</script>

<style lang="scss">
#__nuxt #contents-info-modal .el-dialog__body {
  height: 700px;
}
</style>
