<template>
  <el-dialog
    id="issue-modal"
    class="result-modal"
    :visible.sync="showMe"
    :title="$t('results.issueDetail.title')"
    width="460px"
    top="11vh"
    @close="closed"
  >
    <div v-if="issue.photoFilePath" class="image-container">
      <el-image
        :src="issue.photoFilePath"
        :preview-src-list="[issue.photoFilePath]"
      />
      <i class="el-icon-full-screen" />
    </div>
    <dl>
      <dt>{{ $t('results.issueDetail.reporter') }}</dt>
      <dd class="column-user">
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${issue.workerProfile})`"
          />
        </div>
        <span>{{ issue.workerName }}</span>
      </dd>
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
