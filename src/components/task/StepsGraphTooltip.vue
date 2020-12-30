<template>
  <el-card class="steps-graph-tooltip">
    <div slot="header">
      <h3>{{ $t('task.subTaskDetail.stepTooltip.title') }}</h3>
    </div>
    <dl>
      <dt>{{ $t('task.subTaskDetail.stepsColumn.id') }}</dt>
      <dd>{{ data.id }}</dd>
      <dt>{{ $t('task.subTaskDetail.stepsColumn.name') }}</dt>
      <dd>{{ data.name }}</dd>
      <el-divider />
      <el-row type="flex" class="progress">
        <el-col>
          <dt>{{ $t('task.subTaskDetail.stepsColumn.status') }}</dt>
          <dd class="column-status">
            <span :class="`color-bg-${status(data.conditions).color}`">
              {{ $t(status(data.conditions).label) }}
            </span>
          </dd>
        </el-col>
        <el-col>
          <dt>{{ $t('task.subTaskDetail.stepsColumn.progressRate') }}</dt>
          <dd>{{ data.progressRate }}%</dd>
        </el-col>
      </el-row>
      <el-row type="flex">
        <el-col v-if="data.issue">
          <dt>{{ $t('task.subTaskDetail.stepsColumn.issue') }}</dt>
          <dd>
            <el-button data-type="issue" :data-id="data.issue.id">
              {{ $t('task.subTaskDetail.showIssue') }}
            </el-button>
          </dd>
        </el-col>
        <el-col v-if="data.paper">
          <dt>{{ $t('task.subTaskDetail.stepsColumn.paper') }}</dt>
          <dd>
            <el-button data-type="paper" :data-id="data.paper.id">
              {{ $t('task.subTaskDetail.showPaper') }}
            </el-button>
          </dd>
        </el-col>
      </el-row>
    </dl>
  </el-card>
</template>

<script>
import filters from '@/mixins/filters'
import { conditions } from '@/models/task/Task'
export default {
  mixins: [filters],
  props: {
    data: Object,
  },
  methods: {
    booleanText(bool) {
      return this.boolCheck(bool)
        ? this.$t('task.list.hasIssue.yes')
        : this.$t('task.list.hasIssue.no')
    },
    booleanClass(bool) {
      return this.boolCheck(bool) ? 'true' : 'false'
    },
    status(code) {
      return conditions.find(status => status.value === code)
    },
  },
}
</script>

<style lang="scss">
.steps-graph-tooltip {
  height: 370px;
}
</style>
