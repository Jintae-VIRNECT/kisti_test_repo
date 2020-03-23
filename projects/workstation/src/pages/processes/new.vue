<template>
  <div id="process-new">
    <h3>new process</h3>
    <el-form ref="form" :model="form" label-width="70px" style="width: 550px">
      <el-form-item label="name">
        <span>{{ form.name }}</span>
      </el-form-item>
      <el-form-item label="date">
        <el-col :span="11">
          <el-date-picker
            placeholder="startDate"
            v-model="form.startDate"
          ></el-date-picker>
        </el-col>
        <el-col class="line" :span="2">-</el-col>
        <el-col :span="11">
          <el-date-picker
            placeholder="endDate"
            v-model="form.endDate"
          ></el-date-picker>
        </el-col>
      </el-form-item>
      <el-form-item label="position">
        <el-input v-model="form.position"></el-input>
      </el-form-item>
      <el-divider />
      <div v-for="subProcess in form.subProcessList" :key="subProcess.id">
        <el-form-item label="name">
          <span>{{ subProcess.name }}</span>
        </el-form-item>
        <el-form-item label="date">
          <el-col :span="11">
            <el-date-picker
              placeholder="startDate"
              v-model="subProcess.startDate"
            ></el-date-picker>
          </el-col>
          <el-col class="line" :span="2">-</el-col>
          <el-col :span="11">
            <el-date-picker
              placeholder="endDate"
              v-model="subProcess.endDate"
            ></el-date-picker>
          </el-col>
        </el-form-item>
        <el-form-item label="worker">
          <el-input v-model="subProcess.workerUUID"></el-input>
        </el-form-item>
      </div>
      <el-button @click="createProcess(form)">
        {{ $t('buttons.processCreate') }}
      </el-button>
    </el-form>
  </div>
</template>

<script>
import contentService from '@/services/content'
import processService from '@/services/process'

// models
import Process from '@/models/process/Process'
import SubProcess from '@/models/process/SubProcess'
import RegisterNewProcess from '@/models/process/RegisterNewProcess'

export default {
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
