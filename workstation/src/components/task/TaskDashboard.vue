<template>
  <el-card id="task-dashboard" class="el-card--table">
    <div slot="header">
      <h3>
        <span>{{ $t('task.list.dashboard.title') }}</span>
        <el-tooltip
          :content="$t('task.list.dashboard.titleDesc')"
          placement="right"
        >
          <img src="~assets/images/icon/ic-error.svg" />
        </el-tooltip>
      </h3>
      <div class="right">
        <el-button @click="graphToggle">
          <img src="~assets/images/icon/ic-timeline.svg" />
          <span v-if="!isGraph">
            {{ $t('task.list.dashboard.graphOn') }}
          </span>
          <span v-else>
            {{ $t('task.list.dashboard.graphOff') }}
          </span>
        </el-button>
      </div>
    </div>
    <el-row>
      <!-- 진행률 -->
      <el-col :span="4" class="rate">
        <h4>{{ $t('task.list.dashboard.allProgress') }}</h4>
        <span class="num">{{ stat.totalRate }}%</span>
        <el-progress :percentage="stat.totalRate" :show-text="false" />
        <div v-if="isGraph">
          <el-divider />
          <dl>
            <dt>{{ $t('task.list.allTasks') }}</dt>
            <dd>{{ stat.totalTasks }}</dd>
            <dt>{{ $t('task.list.waitTasks') }}</dt>
            <dd>{{ stat.categoryWait }}</dd>
            <dt>{{ $t('task.list.startedTasks') }}</dt>
            <dd>{{ stat.categoryStarted }}</dd>
            <dt>{{ $t('task.list.endTasks') }}</dt>
            <dd>{{ stat.categoryEnded }}</dd>
          </dl>
        </div>
      </el-col>
      <!-- 작업 수 -->
      <el-col :span="20" class="tasks">
        <h4>
          <span>{{ $t('task.list.dashboard.countOfConditions') }}</span>
          <el-tooltip
            :content="$t('task.list.dashboard.countOfConditionsDesc')"
            placement="right"
          >
            <img src="~assets/images/icon/ic-help.svg" />
          </el-tooltip>
        </h4>
        <el-row type="flex" v-if="!isGraph">
          <dl>
            <dt>{{ $t('task.list.waitTasks') }}</dt>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.wait.color}`">
                  {{ $t(conditions.wait.label) }}
                </span>
              </span>
              <span>{{ stat.wait }}</span>
            </dd>
          </dl>
          <dl>
            <dt>{{ $t('task.list.startedTasks') }}</dt>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.unprogressing.color}`">
                  {{ $t(conditions.unprogressing.label) }}
                </span>
              </span>
              <span>{{ stat.unprogressing }}</span>
            </dd>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.progressing.color}`">
                  {{ $t(conditions.progressing.label) }}
                </span>
              </span>
              <span>{{ stat.progressing }}</span>
            </dd>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.incompleted.color}`">
                  {{ $t(conditions.incompleted.label) }}
                </span>
              </span>
              <span>{{ stat.incompleted }}</span>
            </dd>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.completed.color}`">
                  {{ $t(conditions.completed.label) }}
                </span>
              </span>
              <span>{{ stat.completed }}</span>
            </dd>
          </dl>
          <dl>
            <dt>{{ $t('task.list.endTasks') }}</dt>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.failed.color}`">
                  {{ $t(conditions.failed.label) }}
                </span>
              </span>
              <span>{{ stat.failed }}</span>
            </dd>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.success.color}`">
                  {{ $t(conditions.success.label) }}
                </span>
              </span>
              <span>{{ stat.success }}</span>
            </dd>
            <dd>
              <span class="column-status">
                <span :class="`color-bg-${conditions.fault.color}`">
                  {{ $t(conditions.fault.label) }}
                </span>
              </span>
              <span>{{ stat.fault }}</span>
            </dd>
          </dl>
        </el-row>
        <!-- 차트 -->
        <task-dashboard-graph v-if="isGraph" :data="stat" />
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import { conditions } from '@/models/task/Task'
import TaskDashboardGraph from '@/components/task/TaskDashboardGraph'

export default {
  components: {
    TaskDashboardGraph,
  },
  props: {
    stat: Object,
  },
  data() {
    return {
      conditions: conditions.reduce((obj, condition) => {
        obj[condition.value.toLowerCase()] = condition
        return obj
      }, {}),
      isGraph: false,
    }
  },
  methods: {
    graphToggle() {
      this.isGraph = !this.isGraph
    },
  },
}
</script>

<style lang="scss">
#task-dashboard .el-card__header {
  .el-button {
    height: 28px;
    margin: 14px 24px;
    padding: 0 10px;
  }
}
#task-dashboard .el-card__body {
  & > .el-row {
    padding: 28px 0;
    & > .el-col {
      padding: 0 36px;
    }
    h4 {
      margin-bottom: 16px;
      & > * {
        vertical-align: middle;
      }
    }
  }
  .el-col,
  .tasks dl {
    border-left: solid 1px rgba(226, 231, 237, 0.8);
    &:first-child {
      border-left: none;
    }
  }
  .tasks dl {
    padding-left: 40px;
    &:first-child {
      padding-left: 0;
    }
    dt {
      margin-bottom: 10px;
      color: $font-color-desc;
      font-size: 12px;
    }
    dd {
      display: inline-block;
      margin-right: 40px;
      span {
        vertical-align: middle;
      }
      & > span:last-child {
        margin-left: 16px;
        color: #385370;
        font-size: 24px;
      }
    }
  }

  .rate {
    .num {
      color: #006deb;
      font-size: 38px;
      line-height: 44px;
    }
    .el-progress {
      margin: 14px 0 8px;
      .el-progress-bar__inner {
        background: $color-primary;
      }
    }
    .el-divider {
      margin: 32px 0;
      background-color: rgba(226, 231, 237, 0.8);
    }
    dl {
      display: flex;
      flex-wrap: wrap;
      dt {
        width: 50%;
        margin-bottom: 16px;
        color: $font-color-desc;
        line-height: 20px;
      }
      dd {
        width: 50%;
        text-align: right;
      }
    }
  }
}
</style>
