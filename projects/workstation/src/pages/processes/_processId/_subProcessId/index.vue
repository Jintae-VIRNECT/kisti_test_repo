<template>
  <div id="sub-process">
    <h3>{{ subProcess }}</h3>
    <el-button @click="editSubProcess()">
      {{ $t('buttons.edit') }}
    </el-button>
    <div v-for="job in jobs" :key="job.id">
      <p>{{ job }}</p>
      <el-button v-if="job.report" @click="reportInfo(job.report.id)">
        {{ $t('buttons.reportInfo') }}
      </el-button>
      <el-button v-if="job.issue" @click="issueInfo(job.issue.id)">
        {{ $t('buttons.issueInfo') }}
      </el-button>
      <el-button
        v-if="job.smartTool"
        @click="smartToolInfo(subProcess.id, job.smartTool.id)"
      >
        {{ $t('buttons.smartToolInfo') }}
      </el-button>
    </div>
  </div>
</template>

<script>
import subProcessService from '@/services/subProcess'
import jobService from '@/services/job'
import issueService from '@/services/issue'

export default {
  async asyncData({ params }) {
    const promise = {
      subProcess: subProcessService.getSubProcessInfo(params.subProcessId),
      jobs: jobService.searchChildJobs(params.subProcessId),
    }
    return {
      subProcess: await promise.subProcess,
      jobs: await promise.jobs,
    }
  },
  methods: {
    editSubProcess() {
      this.$router.push(`${this.$route.path}/edit`)
    },
    async reportInfo(reportId) {
      const reportInfo = await jobService.getReportInfo(reportId)
      this.$alert(reportInfo)
    },
    async issueInfo(issueId) {
      const issueInfo = await issueService.getIssueInfo(issueId)
      this.$alert(issueInfo)
    },
    async smartToolInfo(subProcessId, smartToolId) {
      const smartToolInfo = await jobService.getSmartToolInfo(
        subProcessId,
        smartToolId,
      )
      this.$alert(smartToolInfo)
    },
  },
}
</script>
