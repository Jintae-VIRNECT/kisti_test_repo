<template>
  <div id="process-edit">
    <h3>edit process</h3>
    <process-form
      :form="form"
      :buttonName="$t('buttons.edit')"
      @submit="editProcess"
    />
  </div>
</template>

<script>
import processService from '@/services/process'
import subProcessService from '@/services/subProcess'

// models
import EditProcessRequest from '@/models/process/EditProcessRequest'

// components
import ProcessForm from '@/components/process/ProcessForm'

export default {
  components: {
    ProcessForm,
  },
  async asyncData({ params }) {
    const process = await processService.getProcessInfo(params.processId)
    const subProcesses = await subProcessService.searchChildSubProcesses(
      params.processId,
    )
    return {
      processId: params.processId,
      form: new EditProcessRequest({ process, subProcesses }),
    }
  },
  methods: {
    async editProcess(form) {
      try {
        await this.$confirm(this.$t('questions.editConfirm'))
      } catch (e) {
        // 취소
        return false
      }

      try {
        await processService.editProcess(form)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.editSuccess'),
        })
        this.$router.push(`/processes/${this.processId}`)
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
