<template>
  <el-dialog
    id="set-task-manage-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t('task.new.title')"
    width="860px"
    top="11vh"
  >
    <el-row type="flex">
      <el-col :span="12">
        <h4>{{ $t('task.manage.registerNewTaskInfo') }}</h4>
        <el-divider />
        <p>{{ $t('task.manage.registerTaskInfo') }}</p>
        <dl>
          <dt>{{ $t('task.manage.taskName') }}</dt>
          <dd>{{ contentName }}</dd>
        </dl>
        <el-form class="virnect-workstation-form">
          <el-form-item class="horizon" :label="$t('task.manage.taskSchedule')">
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
        <h4>{{ $t('task.manage.subTaskCount') }}</h4>
        <el-divider />
        <el-collapse v-model="activeSubForms">
          <el-collapse-item
            v-for="(form, index) in subForm"
            :title="$tc('task.manage.subTaskSetting', index + 1)"
            :name="form.id"
            :key="form.id"
          >
            <el-form class="virnect-workstation-form">
              <dl>
                <dt>{{ $tc('task.manage.subTaskName', index + 1) }}</dt>
                <dd>{{ form.name }}</dd>
              </dl>
              <el-form-item
                class="horizon"
                :label="$tc('task.manage.subTaskSchedule', index + 1)"
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
    <template slot="footer">
      <el-button @click="next" type="primary">
        {{ $t('common.next') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'
import RegisterNewTask from '@/models/task/RegisterNewTask'

export default {
  mixins: [modalMixin],
  props: {
    contentInfo: Object,
    properties: Array,
  },
  data() {
    return {
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
    }
  },
  watch: {
    'mainForm.schedule'(date) {
      this.subForm.map(form => (form.schedule = date))
    },
    'mainForm.worker'(worker) {
      if (worker !== this.$t('task.manage.subTaskWorkerSelected')) {
        this.subForm.map(form => (form.worker = worker))
      }
    },
    subForm: {
      deep: true,
      handler(forms) {
        forms.forEach(form => {
          // 공정 일정 자동 조정
          if (this.mainForm.schedule[0] > form.schedule[0]) {
            this.mainForm.schedule = [
              form.schedule[0],
              this.mainForm.schedule[1],
            ]
          }
          if (this.mainForm.schedule[1] < form.schedule[1]) {
            this.mainForm.schedule = [
              this.mainForm.schedule[0],
              form.schedule[1],
            ]
          }
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
      this.contentName = this.contentInfo.contentName
      this.subForm = this.properties[0].children.map(sceneGroup => ({
        id: sceneGroup.id,
        name: sceneGroup.label,
        schedule: [],
        worker: '',
      }))
      this.activeSubForms = this.subForm.map(form => form.id)
      this.workerList = await workspaceService.allMembers()
    },
    next() {
      const form = new RegisterNewTask({
        workspaceUUID: this.$store.getters['workspace/activeWorkspace'].uuid,
        content: this.contentInfo,
        task: this.mainForm,
        subTasks: this.subForm,
      })
      this.$emit('next', form)
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
