<template>
  <div id="tasks">
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
            {{ $t('contents.allContents.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('contents.allContents.myContents') }}
          </el-button>
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="taskFilter.value"
            :options="taskFilter.options"
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
            <column-default
              :label="$t('task.list.column.endedSubTasks')"
              prop="doneCount"
            />
            <column-date
              :label="$t('task.list.column.scedule')"
              type="time"
              prop="startDate"
              prop2="endDate"
            />
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page ref="page" :value.sync="taskPage" :total="taskTotal" />
    </div>
  </div>
</template>

<script>
import { filter as taskFilter, tabs } from '@/models/task/Task'
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
      taskFilter,
      taskSearch: '',
      taskPage: 1,
      taskTotal: 0,
      loading: false,
    }
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
    showAll() {},
    showMine() {},
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, this.searchTasks)
  },
}
</script>

<style lang="scss">
#tasks {
  .title {
    position: relative;
    .el-button {
      position: absolute;
      right: 0;
      bottom: 0;
    }
  }
  .searchbar .left > .el-button:nth-child(2) {
    margin: 0 20px 0 4px;
  }
  .tab-wrapper {
    margin-bottom: 20px;

    .searchbar__keyword {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      height: 34px;
      margin: auto 0;
    }
  }
  .btn-wrapper {
    margin-bottom: 16px;
  }
}
</style>
