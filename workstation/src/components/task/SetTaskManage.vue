<template>
  <el-dialog
    id="set-task-manage-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t(title)"
    width="860px"
    top="11vh"
  >
    <el-row type="flex">
      <el-col :span="12">
        <h4>
          <span>{{ $t(leftHeader) }}</span>
          <el-tooltip
            v-if="leftHeaderTooltip"
            :content="$t(leftHeaderTooltip)"
            placement="right"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </h4>

        <el-divider />
        <p>{{ $t('task.manage.registerTaskInfo') }}</p>
        <dl>
          <dt>{{ $t('task.manage.taskName') }}</dt>
          <dd>{{ contentName }}</dd>
        </dl>
        <el-form
          ref="mainForm"
          class="virnect-workstation-form"
          :model="mainForm"
          :rules="rules"
          :show-message="false"
        >
          <el-form-item
            class="horizon"
            :label="$t('task.manage.taskSchedule')"
            prop="schedule"
            required
          >
            <el-date-picker
              v-model="mainForm.schedule"
              type="datetimerange"
              :start-placeholder="$t('task.manage.scheduleStart')"
              :end-placeholder="$t('task.manage.scheduleEnd')"
              format="yyyy. MM. dd.  HH:mm"
              time-arrow-control
            />
          </el-form-item>
          <el-form-item class="horizon" :label="$t('task.manage.taskWorker')">
            <el-select
              v-model="mainForm.worker"
              :placeholder="$t('task.manage.taskWorkerPlaceholder')"
              filterable
              popper-class="no-data-hidden"
            >
              <el-option
                v-for="worker in workerList"
                :key="worker.uuid"
                :value="worker.uuid"
                :label="worker.nickname"
              />
            </el-select>
            <span>{{ $t('task.manage.taskWorkerDesc') }}</span>
          </el-form-item>
          <el-form-item class="horizon" :label="$t('task.manage.taskPosition')">
            <el-input
              v-model="mainForm.position"
              :placeholder="$t('task.manage.taskPositionPlaceholder')"
              :maxlength="100"
            />
            <span>{{ $t('task.manage.taskPositionDesc') }}</span>
          </el-form-item>
        </el-form>
      </el-col>
      <!-- 하위 작업 -->
      <el-col :span="12">
        <h4>
          <span>{{ $t('task.manage.subTaskCount') }}</span>
          <div class="column-done">
            <div>
              <span>{{ subForm.length }}</span>
            </div>
          </div>
        </h4>
        <el-divider />
        <el-collapse v-model="activeSubForms">
          <el-collapse-item
            v-for="(form, index) in subForm"
            :title="$tc('task.manage.subTaskSetting', index + 1)"
            :name="form.id"
            :key="form.id"
          >
            <el-form
              ref="subForm"
              class="virnect-workstation-form"
              :model="form"
              :rules="rules"
              :show-message="false"
            >
              <dl>
                <dt>{{ $tc('task.manage.subTaskName', index + 1) }}</dt>
                <dd>{{ form.name }}</dd>
              </dl>
              <el-form-item
                class="horizon"
                :label="$tc('task.manage.subTaskSchedule', index + 1)"
                prop="schedule"
                required
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
                :label="$tc('task.manage.subTaskWorker', index + 1)"
                prop="worker"
                required
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
              </el-form-item>
            </el-form>
          </el-collapse-item>
        </el-collapse>
      </el-col>
    </el-row>
    <template slot="footer" v-if="submitFunc">
      <el-button @click="submitFunc" type="primary">
        {{ $t(submitText) }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'
import taskService from '@/services/task'
import contentService from '@/services/content'
import RegisterNewTask from '@/models/task/RegisterNewTask'
import EditTaskFrom from '@/models/task/EditTaskFrom'
import DuplicateTask from '@/models/task/DuplicateTask'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [modalMixin],
  props: {
    taskId: Number,
    type: String,
    contentInfo: Object,
  },
  data() {
    return {
      contentUUID: null,
      contentName: '',
      mainForm: {
        schedule: [],
        worker: '',
        position: '',
      },
      subForm: [],
      activeSubForms: [],
      workerList: [],
      searchLoading: false,
      taskInfo: null,
      rules: {
        schedule: [{ required: true, trigger: 'change' }],
        worker: [{ required: true, trigger: 'change' }],
      },
    }
  },
  // 모달 타입별 분기
  computed: {
    title() {
      return {
        new: 'task.new.title',
        add: 'task.manage.taskAdd',
        edit: 'task.manage.taskEdit',
      }[this.type]
    },
    leftHeader() {
      return {
        new: 'task.manage.registerNewTaskInfo',
        add: 'task.manage.taskAddNotice',
        edit: 'task.manage.taskEditNotice',
      }[this.type]
    },
    leftHeaderTooltip() {
      return {
        new: 'task.manage.registerNewTaskInfoDesc',
        add: 'task.manage.taskAddNoticeDesc',
        edit: 'task.manage.taskEditNoticeDesc',
      }[this.type]
    },
    submitFunc() {
      return {
        new: this.newNext,
        add: this.addNext,
        edit: this.update,
      }[this.type]
    },
    submitText() {
      return {
        new: 'common.next',
        add: 'common.next',
        edit: 'task.manage.update',
      }[this.type]
    },
  },
  watch: {
    'mainForm.schedule'(date) {
      this.subForm.forEach(form => {
        if (!form.schedule.length) form.schedule = date
      })
    },
    'mainForm.worker'(worker) {
      if (worker && worker !== this.$t('task.manage.subTaskWorkerSelected')) {
        this.subForm.forEach(form => (form.worker = worker))
      }
    },
    subForm: {
      deep: true,
      handler(forms) {
        forms.forEach(form => {
          // 하위 작업 담당자가 모두 같은 사람이 아닐 경우
          if (form.worker !== this.mainForm.worker) {
            this.mainForm.worker = this.$t('task.manage.subTaskWorkerSelected')
          }
        })
      },
    },
  },
  methods: {
    async opened() {
      // 신규
      if (this.type === 'new') {
        if (this.contentUUID === this.contentInfo.contentUUID) return false
        else this.contentUUID = this.contentInfo.contentUUID
        const sceneGroups = await contentService.getContentSceneGroups(
          this.contentInfo.contentUUID,
        )
        this.contentName = this.contentInfo.contentName
        this.subForm = sceneGroups.map(sceneGroup => ({
          id: sceneGroup.id,
          name: sceneGroup.name,
          priority: sceneGroup.priority,
          schedule: [],
          worker: '',
        }))
      }
      // 편집 & 추가 생성
      if (this.type === 'edit' || this.type === 'add') {
        const [taskInfo, subTasks] = await Promise.all([
          taskService.getTaskDetail(this.taskId),
          taskService.searchSubTasks(this.taskId, { size: 50 }),
        ])
        this.taskInfo = taskInfo
        this.contentName = taskInfo.name
        this.mainForm = {
          schedule: [
            dayjs.utc(taskInfo.startDate).local(),
            dayjs.utc(taskInfo.endDate).local(),
          ],
          worker: '',
          position: taskInfo.position,
        }
        this.subForm = subTasks.list.map(subTask => ({
          id: subTask.subTaskId,
          name: subTask.subTaskName,
          priority: subTask.priority,
          schedule: [
            dayjs.utc(subTask.startDate).local(),
            dayjs.utc(subTask.endDate).local(),
          ],
          worker: subTask.workerUUID,
        }))
      }
      // 추가 생성 only
      if (this.type === 'add') {
        const sceneGroups = await contentService.getContentSceneGroups(
          this.taskInfo.contentUUID,
        )
        this.subForm = this.subForm.map((subTask, index) => ({
          ...subTask,
          id: sceneGroups[index].id,
        }))
      }
      // 공통
      this.activeSubForms = this.subForm.map(form => form.id)
      this.workerList = await workspaceService.allMembers()
      this.$refs.mainForm.clearValidate()
      this.$refs.subForm.forEach(form => form.clearValidate())
    },
    async validate() {
      try {
        await Promise.all([
          this.$refs.mainForm.validate(),
          ...this.$refs.subForm.map(form => form.validate()),
        ])
        return true
      } catch (e) {
        return false
      }
    },
    // 신규
    async newNext() {
      if (!(await this.validate())) return false

      const form = new RegisterNewTask({
        workspaceUUID: this.$store.getters['auth/activeWorkspace'].uuid,
        content: this.contentInfo,
        task: this.mainForm,
        subTasks: this.subForm,
      })
      this.$emit('next', form)
    },
    // 추가 생성
    async addNext() {
      if (!(await this.validate())) return false

      const form = new DuplicateTask({
        workspaceUUID: this.$store.getters['auth/activeWorkspace'].uuid,
        originTask: this.taskInfo,
        task: this.mainForm,
        subTasks: this.subForm,
      })
      this.$emit('next', form)
    },
    async update() {
      if (!(await this.validate())) return false

      const form = new EditTaskFrom({
        task: { id: this.taskId, ...this.mainForm },
        subTasks: this.subForm,
      })
      try {
        await taskService.updateTask(this.taskId, form)
        this.$message.success({
          message: this.$t('task.manage.message.taskUpdateSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('task.manage.message.taskUpdateFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt #set-task-manage-modal .el-dialog__body {
  .el-col:first-child {
    padding-right: 15px;
  }
  .el-col:last-child {
    padding-left: 15px;
    overflow: auto;
  }
  h4 > .column-done {
    float: right;
    & > div > span:first-child {
      color: #114997;
    }
  }
  p {
    margin-bottom: 16px;
  }
  dt,
  .el-form-item__label {
    font-size: 13px;
  }
  dd {
    font-size: 15px;
  }
  .el-form-item {
    margin-bottom: 20px;
  }
  .el-collapse {
    margin-top: -16px;
    border-top: none;
  }
  .el-collapse-item__content {
    padding-top: 4px;
    padding-bottom: 0;
  }
  .el-select__caret:before {
    content: '\e6e1';
  }
}
#__nuxt #set-task-manage-modal .el-dialog__footer {
  height: 84px;
  padding-top: 24px;
  border-top: solid 1px #edf0f7;
}
.no-data-hidden .el-select-dropdown__empty {
  display: none;
}
</style>
