<template>
  <div>
    <el-table
      :class="clickable ? 'clickable' : ''"
      :data="data"
      @row-click="moveToTaskDetail"
      @sort-change="sortChange"
    >
      <ColumnDefault
        :label="$t('task.list.column.id')"
        prop="id"
        :width="140"
      />
      <ColumnDefault
        :label="$t('task.list.column.name')"
        prop="name"
        :sortable="sortable"
      />
      <ColumnDone
        :label="$t('task.list.column.endedSubTasks')"
        :tooltip="$t('task.list.column.endedSubTasksTooltip')"
        prop="doneCount"
        maxProp="subTaskTotal"
        :width="120"
        :sortable="sortable"
      />
      <ColumnDate
        :label="$t('task.list.column.schedule')"
        type="time"
        prop="startDate"
        prop2="endDate"
        :width="250"
        :sortable="sortable"
      />
      <ColumnProgress
        :label="$t('task.list.column.progressRate')"
        prop="progressRate"
        :width="150"
        :sortable="sortable"
      />
      <ColumnStatus
        :label="$t('task.list.column.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="120"
        :sortable="sortable"
      />
      <ColumnDate
        :label="$t('task.list.column.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="130"
        :sortable="sortable"
      />
      <ColumnBoolean
        :label="$t('task.list.column.issue')"
        prop="issuesTotal"
        :trueText="$t('task.list.hasIssue.yes')"
        :falseText="$t('task.list.hasIssue.no')"
        :width="90"
        :sortable="sortable"
      />
      <ColumnClosed
        :label="$t('task.list.column.endStatus')"
        prop="state"
        :width="110"
        :sortable="sortable"
      />
      <ColumnDropdown :width="60" v-slot:default="{ row }">
        <span class="title">{{ $t('task.list.dropdown.taskSetting') }}</span>
        <el-dropdown-item @click.native="showTarget(row)">
          {{ $t('task.list.dropdown.targetInfo') }}
        </el-dropdown-item>
        <el-dropdown-item
          :disabled="row.state == 'CLOSED'"
          @click.native="edit(row)"
        >
          {{ $t('task.list.dropdown.taskEdit') }}
        </el-dropdown-item>
        <el-dropdown-item @click.native="add(row)">
          {{ $t('task.list.dropdown.taskAdd') }}
        </el-dropdown-item>
        <el-dropdown-item
          v-if="row.state !== 'CLOSED'"
          @click.native="close(row.id)"
        >
          {{ $t('task.list.dropdown.taskClose') }}
        </el-dropdown-item>
        <el-dropdown-item
          class="red"
          @click.native="remove(row.id)"
          v-if="activeWorkspace.role !== 'MEMBER'"
        >
          {{ $t('task.list.dropdown.taskDelete') }}
        </el-dropdown-item>
      </ColumnDropdown>
      <template slot="empty">
        <img src="~assets/images/empty/img-work-empty.jpg" />
        <p>{{ $t('task.list.empty') }}</p>
      </template>
    </el-table>
    <TaskTargetInfo
      :task="activeTask"
      :visible.sync="showTaskTargetInfoModal"
    />
    <TaskManage
      :type="taskManageModalType"
      :taskId="activeTask.id"
      :contentInfo="{ contentUUID: activeTask.contentUUID }"
      :visible.sync="showSetTaskManageModal"
      @updated="updated"
      @next="taskManageEnded"
    />
    <TaskSetTarget
      type="add"
      :form="registerTaskForm"
      :visible.sync="showAddTaskTargetModal"
      @prev="canceledManageTarget"
    />
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import { conditions as taskConditions } from '@/models/task/Task'
import taskService from '@/services/task'
import { mapGetters } from 'vuex'

export default {
  mixins: [columnMixin],
  props: {
    data: Array,
    clickable: Boolean,
  },
  data() {
    return {
      taskConditions,
      activeTask: {},
      showTaskTargetInfoModal: false,
      showSetTaskManageModal: false,
      showAddTaskTargetModal: false,
      taskManageModalType: '',
      registerTaskForm: null,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
    sortable() {
      return this.clickable ? 'custom' : null
    },
  },
  methods: {
    sortChange(params) {
      this.$emit('sort-change', params)
    },
    moveToTaskDetail({ id }) {
      if (!this.clickable) return false
      this.$router.push(`/tasks/${id}`)
    },
    showTarget(task) {
      this.activeTask = task
      this.showTaskTargetInfoModal = true
    },
    // 편집
    edit(task) {
      this.activeTask = task
      this.taskManageModalType = 'edit'
      this.showSetTaskManageModal = true
    },
    updated() {
      this.showSetTaskManageModal = false
      this.$emit('updated')
    },
    // 추가 생성
    add(task) {
      this.activeTask = task
      this.taskManageModalType = 'add'
      this.showSetTaskManageModal = true
    },
    taskManageEnded(form) {
      this.registerTaskForm = form
      this.showAddTaskTargetModal = true
      setTimeout(() => (this.showSetTaskManageModal = false), 100)
    },
    canceledManageTarget() {
      this.showSetTaskManageModal = true
      setTimeout(() => (this.showAddTaskTargetModal = false), 100)
    },
    // 종료
    async close(taskId) {
      try {
        await this.$confirm(
          this.$t('task.list.message.closeSure'),
          this.$t('task.list.dropdown.taskClose'),
          {
            confirmButtonText: this.$t('task.list.close'),
            dangerouslyUseHTMLString: true,
          },
        )
      } catch (e) {
        return false
      }
      try {
        await await taskService.closeTask(taskId)
        this.$message.success({
          message: this.$t('task.list.message.closeSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('task.list.message.closeFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    // 삭제
    async remove(taskId) {
      try {
        await this.$confirm(
          this.$t('task.list.message.deleteSure'),
          this.$t('task.list.dropdown.taskDelete'),
          {
            confirmButtonText: this.$t('common.delete'),
            dangerouslyUseHTMLString: true,
          },
        )
      } catch (e) {
        return false
      }
      try {
        await await taskService.deleteTask(taskId)
        this.$message.success({
          message: this.$t('task.list.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('deleted')
      } catch (e) {
        this.$message.error({
          message: this.$t('task.list.message.deleteFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
}
</script>
