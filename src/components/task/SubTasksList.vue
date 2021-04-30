<template>
  <div>
    <el-table
      :class="clickable ? 'clickable' : ''"
      :data="data"
      @row-click="moveToSubTaskDetail"
      @sort-change="sortChange"
    >
      <ColumnDefault
        :label="$t('task.detail.subTaskColumn.no')"
        prop="priority"
        :width="80"
      />
      <ColumnDefault
        :label="$t('task.detail.subTaskColumn.id')"
        prop="subTaskId"
        :width="140"
      />
      <ColumnDefault
        :label="$t('task.detail.subTaskColumn.name')"
        prop="subTaskName"
        :sortable="sortable"
      />
      <ColumnDone
        :label="$t('task.detail.subTaskColumn.endedSteps')"
        :tooltip="$t('task.detail.subTaskColumn.endedStepsTooltip')"
        prop="doneCount"
        maxProp="stepTotal"
        :width="130"
      />
      <ColumnDate
        :label="$t('task.detail.subTaskColumn.schedule')"
        type="time"
        prop="startDate"
        prop2="endDate"
        :width="250"
      />
      <ColumnProgress
        :label="$t('task.detail.subTaskColumn.progressRate')"
        prop="progressRate"
        :width="150"
      />
      <ColumnStatus
        :label="$t('task.detail.subTaskColumn.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="120"
      />
      <ColumnDate
        :label="$t('task.detail.subTaskColumn.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="130"
      />
      <ColumnBoolean
        :label="$t('task.detail.subTaskColumn.issue')"
        prop="issuesTotal"
        :trueText="$t('task.list.hasIssue.yes')"
        :falseText="$t('task.list.hasIssue.no')"
        :width="80"
      />
      <ColumnUser
        :label="$t('task.detail.subTaskColumn.worker')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="70"
      />
      <ColumnDropdown :width="60" v-slot:default="{ row }">
        <el-dropdown-item @click.native="edit(row)">
          {{ $t('task.detail.dropdown.subTaskEdit') }}
        </el-dropdown-item>
      </ColumnDropdown>
      <template slot="empty">
        <img src="~assets/images/empty/img-work-empty.jpg" />
        <p>{{ $t('task.detail.empty') }}</p>
      </template>
    </el-table>
    <TaskSetSubTaskManage
      :taskInfo="taskInfo"
      :subTaskInfo="activeSubTask"
      :visible.sync="showSubTaskManageModal"
      @updated="updated"
    />
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import { conditions as taskConditions } from '@/models/task/Task'

export default {
  mixins: [columnMixin],
  props: {
    data: Array,
    clickable: Boolean,
    taskInfo: Object,
  },
  data() {
    return {
      taskConditions,
      activeSubTask: {},
      showSubTaskManageModal: false,
    }
  },
  computed: {
    sortable() {
      return this.clickable ? 'custom' : null
    },
  },
  methods: {
    sortChange(params) {
      this.$emit('sort-change', params)
    },
    moveToSubTaskDetail({ subTaskId }) {
      if (!this.clickable) return false
      this.$router.push(`/tasks/${this.taskInfo.id}/${subTaskId}`)
    },
    edit(subTask) {
      this.activeSubTask = subTask
      this.showSubTaskManageModal = true
    },
    updated() {
      this.showSubTaskManageModal = false
      this.$emit('updated')
    },
  },
}
</script>
