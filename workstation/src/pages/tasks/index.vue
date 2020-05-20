<template>
  <div id="tasks" class="task">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('task.list.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('task.list.title') }}</h2>
        <el-button type="primary" @click="$router.push('/tasks/new')">
          {{ $t('task.list.newTask') }}
        </el-button>
      </div>

      <!-- 탭 -->
      <el-row class="tab-wrapper searchbar">
        <el-tabs v-model="activeTab">
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.name"
            :name="tab.name"
            :label="$t(tab.label)"
          />
        </el-tabs>
        <searchbar-keyword ref="keyword" :value.sync="taskSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('task.list.myTasks') }}
          </el-button>
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
            @change="filterChanged"
          />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('task.list.allTasksList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="taskList"
            v-loading="loading"
            @row-click="moveToSubTask"
          >
            <column-default
              :label="$t('task.list.column.id')"
              prop="id"
              :width="140"
            />
            <column-default
              :label="$t('task.list.column.name')"
              prop="name"
              sortable="custom"
            />
            <column-count
              :label="$t('task.list.column.endedSubTasks')"
              prop="doneCount"
              maxProp="subTaskTotal"
              :width="120"
            />
            <column-date
              :label="$t('task.list.column.schedule')"
              type="time"
              prop="startDate"
              prop2="endDate"
              :width="250"
            />
            <column-progress
              :label="$t('task.list.column.progressRate')"
              prop="progressRate"
              :width="150"
            />
            <column-status
              :label="$t('task.list.column.status')"
              prop="conditions"
              :statusList="taskConditions"
              :width="100"
            />
            <column-date
              :label="$t('task.list.column.reportedDate')"
              type="time"
              prop="reportedDate"
              :width="130"
            />
            <column-boolean
              :label="$t('task.list.column.issue')"
              prop="issuesTotal"
              :trueText="$t('task.list.hasIssue.yes')"
              :falseText="$t('task.list.hasIssue.no')"
              :width="80"
            />
            <column-default
              :label="$t('task.list.column.endStatus')"
              prop="state"
              :width="100"
            />
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="taskPage" :total="taskTotal" />
    </div>
  </div>
</template>

<script>
import {
  conditions as taskConditions,
  filter as taskFilter,
  tabs,
} from '@/models/task/Task'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'
import taskService from '@/services/task'
import workspaceService from '@/services/workspace'

export default {
  mixins: [searchMixin, columnMixin],
  async asyncData({ store }) {
    const promise = {
      tasks: taskService.searchTasks(
        store.getters['workspace/activeWorkspace'].uuid,
      ),
    }
    return {
      taskList: (await promise.tasks).list,
      taskTotal: (await promise.tasks).total,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allTasks',
      taskConditions,
      taskFilter: { ...taskFilter },
      taskSearch: '',
      taskPage: 1,
      taskTotal: 0,
      loading: false,
    }
  },
  watch: {
    activeTab(tabName) {
      const enableFilter = tabs.find(tab => tab.name === tabName).filter
      this.taskFilter.value = tabName === 'allTasks' ? ['ALL'] : enableFilter
      this.taskFilter.options = taskFilter.options.map(option => ({
        ...option,
        disabled: !enableFilter.includes(option.value),
      }))
      this.emitChangedSearchParams()
    },
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchTasks(searchParams)
    },
    async searchTasks() {
      const { list, total } = await taskService.searchTasks(
        this.$store.getters['workspace/activeWorkspace'].uuid,
        this.searchParams,
      )
      this.taskList = list
      this.taskTotal = total
    },
    filterChanged(filter) {
      if (!filter.length) this.activeTab = 'allTasks'
    },
    showAll() {},
    showMine() {},
    moveToSubTask({ id }) {
      this.$router.push(`/tasks/${id}`)
    },
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, this.searchTasks)
  },
}
</script>
