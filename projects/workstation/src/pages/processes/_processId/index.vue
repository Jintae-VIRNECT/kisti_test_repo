<template>
  <div id="process">
    <h3>{{ process }}</h3>
    <el-button @click="closeProcess(process.id)">
      {{ $t('buttons.close') }}
    </el-button>
    <el-button @click="deleteProcess(process.id)">
      {{ $t('buttons.delete') }}
    </el-button>
    <p v-for="subProcess in subProcesses" :key="subProcess.id">
      <nuxt-link :to="`/processes/${process.id}/${subProcess.id}`">
        {{ subProcess }}
      </nuxt-link>
    </p>
  </div>
</template>

<script>
import processService from '@/services/process'
import subProcessService from '@/services/subProcess'

export default {
  async asyncData({ params }) {
    return {
      process: await processService.getProcessInfo(params.processId),
      subProcesses: await subProcessService.searchChildSubProcesses(
        params.processId,
      ),
    }
  },
  methods: {
    async deleteProcess(processId) {
      try {
        await this.$confirm(this.$t('questions.deleteConfirm'))
      } catch (e) {
        // 취소
        return false
      }

      try {
        await processService.deleteProcess(processId)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.deleteSuccess'),
        })
        this.$router.push('/processes')
      } catch (e) {
        // 실패
        this.$notify.error({
          title: 'Error',
          message: e,
        })
      }
    },
    async closeProcess(processId) {
      try {
        await this.$confirm(this.$t('questions.closeConfirm'))
      } catch (e) {
        // 취소
        return false
      }

      try {
        await processService.closeProcess(processId)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.closedSuccess'),
        })
        this.$router.push('/processes')
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
