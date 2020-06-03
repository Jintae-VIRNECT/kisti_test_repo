<template>
  <div>
    <el-table
      :class="clickable ? 'clickable' : ''"
      :data="data"
      @row-click="moveToSubTaskDetail"
      @sort-change="sortChange"
    >
      <column-default
        :label="$t('task.detail.subTaskColumn.no')"
        prop="priority"
        :width="80"
      />
      <column-default
        :label="$t('task.detail.subTaskColumn.id')"
        prop="subTaskId"
        :width="140"
      />
      <column-default
        :label="$t('task.detail.subTaskColumn.name')"
        prop="subTaskName"
        :sortable="sortable"
      />
      <column-count
        :label="$t('task.detail.subTaskColumn.endedSteps')"
        :tooltip="$t('task.detail.subTaskColumn.endedStepsTooltip')"
        prop="doneCount"
        maxProp="stepTotal"
        :width="120"
      />
      <column-date
        :label="$t('task.detail.subTaskColumn.schedule')"
        type="time"
        prop="startDate"
        prop2="endDate"
        :width="250"
      />
      <column-progress
        :label="$t('task.detail.subTaskColumn.progressRate')"
        prop="progressRate"
        :width="150"
      />
      <column-status
        :label="$t('task.detail.subTaskColumn.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="100"
      />
      <column-date
        :label="$t('task.detail.subTaskColumn.reportedDate')"
        type="time"
        prop="reportedDate"
        :width="130"
      />
      <column-boolean
        :label="$t('task.detail.subTaskColumn.issue')"
        prop="issuesTotal"
        :trueText="$t('task.list.hasIssue.yes')"
        :falseText="$t('task.list.hasIssue.no')"
        :width="80"
      />
      <column-user
        :label="$t('task.detail.subTaskColumn.worker')"
        prop="workerUUID"
        nameProp="workerName"
        imageProp="workerProfile"
        type="tooltip"
        :width="70"
      />
      <column-dropdown :width="60" v-slot:default="{ row }">
        <el-dropdown-item @click.native="edit(row)">
          {{ $t('task.detail.dropdown.subTaskEdit') }}
        </el-dropdown-item>
      </column-dropdown>
    </el-table>
    <set-sub-task-manage
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
import SetSubTaskManage from '@/components/task/SetSubTaskManage'

export default {
  mixins: [columnMixin],
  components: {
    SetSubTaskManage,
  },
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
