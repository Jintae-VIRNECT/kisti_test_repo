<template>
  <div id="sub-process-edit">
    <h3>edit subProcess</h3>
    <sub-process-form
      :form="form"
      :buttonName="$t('buttons.edit')"
      @submit="editSubProcess"
    />
  </div>
</template>

<script>
import subProcessService from '@/services/subProcess'

// models
import EditSubProcessRequest from '@/models/process/EditSubProcessRequest'

// components
import SubProcessForm from '@/components/process/SubProcessForm'

export default {
  components: {
    SubProcessForm,
  },
  async asyncData({ params }) {
    const subProcess = await subProcessService.getSubProcessInfo(
      params.subProcessId,
    )
    return {
      processId: params.processId,
      subProcessId: params.subProcessId,
      form: new EditSubProcessRequest(subProcess),
    }
  },
  methods: {
    async editSubProcess(form) {
      try {
        await this.$confirm(this.$t('questions.editConfirm'))
      } catch (e) {
        // 취소
        return false
      }

      try {
        await subProcessService.editSubProcess(form)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.editSuccess'),
        })
        this.$router.push(`/processes/${this.processId}/${this.subProcessId}`)
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
