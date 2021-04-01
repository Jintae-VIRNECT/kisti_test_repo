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
    <el-collapse v-model="activeIssues" accordion>
      <el-collapse-item
        v-for="(issue, index) in issues"
        :key="issue.issueId"
        :name="issue.issueId"
      >
        <template slot="title">
          <span>{{ $t('results.issue') }} {{ issues.length - index }}</span>
          <el-divider direction="vertical" />
          <span>{{ issue.reportedDate | localTimeFormat }}</span>
        </template>
        <div v-if="issue.photoFilePath" class="image-container">
          <el-image
            :src="cdn(issue.photoFilePath)"
            :preview-src-list="[cdn(issue.photoFilePath)]"
          />
          <i class="el-icon-full-screen" />
        </div>
        <dl>
          <dt>{{ $t('results.issueDetail.reporter') }}</dt>
          <dd class="column-user">
            <VirnectThumbnail :image="cdn(issue.workerProfile)" />
            <span>{{ issue.workerName }}</span>
          </dd>
          <dt>{{ $t('results.issueDetail.reportedDate') }}</dt>
          <dd>{{ issue.reportedDate | localTimeFormat }}</dd>
          <dt>{{ $t('results.issueDetail.content') }}</dt>
          <dd>
            <span>{{ issue.caption }}</span>
            <img
              v-if="issue.photoFilePath"
              src="~assets/images/icon/ic-file-download.svg"
              @click="download(cdn(issue.photoFilePath), issue.caption)"
            />
          </dd>
        </dl>
      </el-collapse-item>
    </el-collapse>
  </el-dialog>
</template>

<script>
import resultService from '@/services/result'
import filters from '@/mixins/filters'
import utils from '@/mixins/utils'

export default {
  mixins: [filters, utils],
  async asyncData({ params }) {
    const { list } = await resultService.searchIssues({
      stepId: params.stepId,
      size: 50,
    })
    return {
      issues: list,
      activeIssues: [list[0].issueId],
    }
  },
  data() {
    return {
      showMe: true,
    }
  },
  methods: {
    closed() {
      const { taskId, subTaskId } = this.issues[0]
      this.showMe = false
      this.$router.replace(`/tasks/${taskId}/${subTaskId}`)
    },
  },
}
</script>

<style lang="scss">
#__nuxt #issue-modal {
  .el-collapse-item__header {
    padding-left: 47px;
    .el-collapse-item__arrow {
      left: 26px;
    }
  }
  .image-container {
    padding-top: 0;
    & > .el-icon-full-screen {
      top: 0;
    }
  }
  dl {
    padding-bottom: 0;
  }
}
</style>
