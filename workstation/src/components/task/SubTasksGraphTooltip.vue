<template>
  <el-card class="sub-tasks-graph-tooltip">
    <div slot="header">
      <h3>{{ $t('task.detail.subTaskTooltip.title') }}</h3>
      <router-link :to="`${$router.currentRoute.path}/${data.subTaskId}`">{{
        $t('task.detail.subTaskTooltip.more')
      }}</router-link>
    </div>
    <dl>
      <dt>{{ $t('task.detail.subTaskColumn.id') }}</dt>
      <dd>{{ data.subTaskId }}</dd>
      <dt>{{ $t('task.detail.subTaskColumn.name') }}</dt>
      <dd>{{ data.subTaskName }}</dd>
      <el-divider />
      <dt>{{ $t('task.detail.subTaskColumn.endedSteps') }}</dt>
      <dd class="column-count">
        <div>
          <span>{{ data.doneCount }}</span>
          <span>/{{ data.stepTotal }}</span>
        </div>
      </dd>
      <el-row type="flex" class="progress">
        <el-col>
          <dt>{{ $t('task.detail.subTaskColumn.status') }}</dt>
          <dd class="column-status">
            <span :class="`color-bg-${status(data.conditions).color}`">
              {{ $t(status(data.conditions).label) }}
            </span>
          </dd>
        </el-col>
        <el-col>
          <dt>{{ $t('task.detail.subTaskColumn.progressRate') }}</dt>
          <dd>{{ data.progressRate }}%</dd>
        </el-col>
      </el-row>
      <dt>{{ $t('task.detail.subTaskColumn.schedule') }}</dt>
      <dd>
        {{ data.startDate | localTimeFormat }} -
        {{ data.endDate | localTimeFormat }}
      </dd>
      <dt>{{ $t('task.detail.subTaskTooltip.issue') }}</dt>
      <dd class="column-boolean">
        <span :class="booleanClass(data.issuesTotal)">
          {{ booleanText(data.issuesTotal) }}
        </span>
      </dd>
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
.sub-tasks-graph-tooltip {
  height: 486px;
}
</style>
