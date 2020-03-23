<template>
  <div id="process-new">
    <h3>new process</h3>
    <process-form
      :form="form"
      :buttonName="$t('buttons.processCreate')"
      @submit="createProcess"
    />
  </div>
</template>

<script>
import contentService from '@/services/content'
import processService from '@/services/process'

// models
import Process from '@/models/process/Process'
import SubProcess from '@/models/process/SubProcess'
import RegisterNewProcess from '@/models/process/RegisterNewProcess'

// components
import ProcessForm from '@/components/process/ProcessForm'

export default {
  components: {
    ProcessForm,
  },
  async asyncData({ query }) {
    const content = await contentService.getContentInfo(query.contentId)
    const sceneGroups = await contentService.getSceneGroupsList(query.contentId)
    const process = new Process()
    const subProcesses = sceneGroups.map(
      sceneGroup => new SubProcess(sceneGroup),
    )
    return {
      form: new RegisterNewProcess({
        content,
        sceneGroups,
        process,
        subProcesses,
      }),
    }
  },
  methods: {
    async createProcess(form) {
      try {
        await this.$confirm(this.$t('questions.createConfirm'))
      } catch (e) {
        // 취소
        return false
      }

      try {
        const result = await processService.createProcess(form)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.createSuccess'),
        })
        this.$router.push(`/processes/${result.processId}`)
      } catch (e) {
        // 실패
        this.$notify.error({
          title: 'Error',
          message: e,
        })
      }
    },
  },
}
</script>
