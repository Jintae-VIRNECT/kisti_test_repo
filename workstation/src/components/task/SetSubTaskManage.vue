<template>
  <el-dialog
    id="set-sub-task-manage-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t('task.detail.dropdown.subTaskEdit')"
    width="445px"
    top="11vh"
  >
    <el-row>
      <!-- 작업 정보 -->
      <el-col :span="24">
        <h4>{{ $t('task.manage.taskInfo') }}</h4>
        <el-divider />
        <dl>
          <dt>{{ $t('task.manage.taskName') }}</dt>
          <dd>{{ taskName }}</dd>
        </dl>
        <el-form class="virnect-workstation-form">
          <el-form-item class="horizon" :label="$t('task.manage.taskSchedule')">
            <el-date-picker
              v-model="taskSchedule"
              type="datetimerange"
              format="yyyy. MM. dd.  HH:mm"
              disabled
            />
          </el-form-item>
        </el-form>
      </el-col>
      <!-- 하위 작업 -->
      <el-col :span="24">
        <el-form class="virnect-workstation-form">
          <h4>{{ $t('task.detail.dropdown.subTaskEdit') }}</h4>
          <el-divider />
          <dl>
            <dt>{{ $tc('task.manage.subTaskName', subTaskInfo.priority) }}</dt>
            <dd>{{ subTaskInfo.subTaskName }}</dd>
          </dl>
          <el-form-item
            class="horizon"
            :label="$tc('task.manage.subTaskSchedule', subTaskInfo.priority)"
          >
            <el-date-picker
              v-model="form.schedule"
              type="datetimerange"
              :start-placeholder="$t('task.manage.scheduleStart')"
              :end-placeholder="$t('task.manage.scheduleEnd')"
              format="yyyy. MM. dd.  HH:mm"
              time-arrow-control
            />
          </el-form-item>
          <el-form-item
            class="horizon"
            :label="$tc('task.manage.subTaskWorker', subTaskInfo.priority)"
          >
            <el-select
              v-model="form.worker"
              :placeholder="$t('task.manage.subTaskWorkerPlaceholder')"
              filterable
            >
              <el-option
                v-for="worker in workerList"
                :key="worker.uuid"
                :value="worker.uuid"
                :label="worker.nickname"
              />
            </el-select>
            <span>{{ $t('task.manage.subTaskWorkerChangeDesc') }}</span>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <template slot="footer">
      <el-button @click="submit" type="primary">
        {{ $t('task.manage.update') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'
import taskService from '@/services/task'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [modalMixin],
  props: {
    taskInfo: Object,
    subTaskInfo: Object,
  },
  data() {
    return {
      taskName: '',
      taskSchedule: [],
      form: {
        schedule: [],
        worker: null,
      },
      workerList: [],
      searchLoading: false,
    }
  },
  methods: {
    async opened() {
      if (!this.taskInfo) {
        this.taskInfo = await taskService.getTaskDetail(this.subTaskInfo.taskId)
      }
      this.taskName = this.taskInfo.name
      this.taskSchedule = [
        dayjs.utc(this.taskInfo.startDate).local(),
        dayjs.utc(this.taskInfo.endDate).local(),
      ]
      this.form.schedule = [
        dayjs.utc(this.subTaskInfo.startDate).local(),
        dayjs.utc(this.subTaskInfo.endDate).local(),
      ]
      this.form.worker = this.subTaskInfo.workerUUID
      this.workerList = await workspaceService.allMembers()
    },
    async submit() {
      const form = {
        subTaskId: this.subTaskInfo.subTaskId,
        startDate: dayjs.utc(this.form.schedule[0]),
        endDate: dayjs.utc(this.form.schedule[1]),
        workerUUID: this.form.worker,
      }
      try {
        await taskService.updateSubTask(form.subTaskId, form)
        this.$message.success({
          message: this.$t('task.manage.message.subTaskUpdateSuccess'),
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('task.manage.message.subTaskUpdateFail'),
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt #set-sub-task-manage-modal {
  .el-dialog__body {
    .el-col:first-child,
    .el-col:last-child {
      padding: 0;
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;
  }
}
</style>
