<template>
  <el-card class="current-contents-list el-card--table">
    <div slot="header">
      <h3>{{ $t('home.contentsList.title') }}</h3>
    </div>
    <el-tabs v-model="activeTab">
      <el-tab-pane
        v-for="tab in tabs"
        :key="tab.name"
        :name="tab.name"
        :label="$t(tab.label)"
      >
      </el-tab-pane>
    </el-tabs>
    <!-- 컨텐츠 -->
    <el-table
      v-if="activeTab === 'contents'"
      :data="contents"
      class="clickable"
      @row-click="moveToContent"
    >
      <column-default
        :label="$t('contents.allContents.column.name')"
        prop="contentName"
      />
      <column-default
        :label="$t('contents.allContents.column.targetType')"
        prop="targets"
        :width="90"
      />
      <column-user
        :label="$t('contents.allContents.column.uploader')"
        type="tooltip"
        prop="uploaderUUID"
        nameProp="uploaderName"
        imageProp="uploaderProfile"
        :width="90"
      />
      <column-date
        :label="$t('contents.allContents.column.createdDate')"
        prop="createdDate"
        :width="100"
      />
      <column-boolean
        :label="$t('contents.allContents.column.sharedStatus')"
        prop="shared"
        :trueText="$t('contents.sharedStatus.shared')"
        :falseText="$t('contents.sharedStatus.noShared')"
        :width="120"
      />
      <template slot="empty">
        <img src="~assets/images/empty/img-content-empty.jpg" />
        <p>{{ $t('home.contentsList.empty') }}</p>
      </template>
    </el-table>
    <!-- 작업 -->
    <el-table
      v-if="activeTab === 'task'"
      :data="tasks"
      class="clickable"
      @row-click="moveToTask"
    >
      <column-default :label="$t('task.list.column.name')" prop="name" />
      <column-count
        :label="$t('task.list.column.endedSubTasks')"
        prop="doneCount"
        maxProp="subTaskTotal"
        :width="100"
      />
      <column-date
        :label="$t('task.list.column.schedule')"
        type="time-narrow"
        prop="startDate"
        prop2="endDate"
        :width="140"
      />
      <column-progress
        :label="$t('task.list.column.progressRate')"
        prop="progressRate"
        :width="130"
      />
      <column-status
        :label="$t('task.list.column.status')"
        prop="conditions"
        :statusList="taskConditions"
        :width="90"
      />
      <column-boolean
        :label="$t('task.list.column.issue')"
        prop="issuesTotal"
        :trueText="$t('task.list.hasIssue.yes')"
        :falseText="$t('task.list.hasIssue.no')"
        :width="70"
      />
      <column-closed
        :label="$t('task.list.column.endStatus')"
        prop="state"
        :width="90"
      />
      <template slot="empty">
        <img src="~assets/images/empty/img-content-empty.jpg" />
        <p>{{ $t('home.contentsList.empty') }}</p>
      </template>
    </el-table>
  </el-card>
</template>

<script>
import contentService from '@/services/content'
import taskService from '@/services/task'
import columnMixin from '@/mixins/columns'
import { conditions as taskConditions } from '@/models/task/Task'

export default {
  mixins: [columnMixin],
  data() {
    return {
      activeTab: '',
      tabs: [
        {
          name: 'contents',
          label: 'home.contentsList.contents',
        },
        {
          name: 'task',
          label: 'home.contentsList.task',
        },
      ],
      contents: [],
      taskConditions,
      tasks: [],
    }
  },
  watch: {
    async activeTab(tab) {
      if (tab === 'contents') {
        this.contents = (await contentService.searchContents({ size: 4 })).list
      } else if (tab === 'task') {
        this.tasks = (await taskService.searchTasks({ size: 4 })).list
      }
    },
  },
  methods: {
    moveToContent({ contentUUID }) {
      this.$router.push(`/contents/${contentUUID}`)
    },
    moveToTask({ id }) {
      this.$router.push(`/tasks/${id}`)
    },
  },
  mounted() {
    this.activeTab = 'contents'
  },
}
</script>

<style lang="scss">
.current-contents-list {
  .el-tabs__nav-wrap {
    padding-left: 24px;
  }
  .el-tabs .el-tabs__item {
    height: 40px;
    font-size: 12.6px;
    line-height: 40px;
  }
  .el-table {
    margin-top: 6px;
  }
  .column-progress .el-progress-bar,
  .column-progress .el-progress__text {
    padding-right: 20px;
  }
}
</style>
