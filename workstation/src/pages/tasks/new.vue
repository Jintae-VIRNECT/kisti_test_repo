<template>
  <div id="contents">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item to="/tasks">{{
            $t('task.list.title')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('task.new.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('task.new.title') }}</h2>
      </div>
      <!-- 검색 영역 -->
      <el-row class="searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('contents.allContents.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('contents.allContents.myContents') }}
          </el-button>
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="contentsSearch" />
        </el-col>
      </el-row>
      <!-- 리스트 -->
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('contents.allContents.workspaceContentsList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="contentsList"
            v-loading="loading"
            @row-click="rowClick"
          >
            <column-default
              :label="$t('contents.allContents.column.id')"
              prop="contentUUID"
              :width="140"
            />
            <column-default
              :label="$t('contents.allContents.column.name')"
              prop="contentName"
              sortable="custom"
            />
            <column-default
              :label="$t('contents.allContents.column.targetType')"
              prop="targets"
              sortable="custom"
              :width="120"
            />
            <column-user
              :label="$t('contents.allContents.column.uploader')"
              prop="uploaderUUID"
              nameProp="uploaderName"
              imageProp="uploaderProfile"
              :width="160"
            />
            <column-date
              :label="$t('contents.allContents.column.createdDate')"
              prop="createdDate"
              sortable="custom"
              :width="140"
            />
            <column-boolean
              :label="$t('contents.allContents.column.sharedStatus')"
              prop="shared"
              :trueText="$t('contents.sharedStatus.shared')"
              :falseText="$t('contents.sharedStatus.noShared')"
              sortable="custom"
              :width="120"
            />
            <template slot="empty">
              <img src="~assets/images/empty/img-content-empty.jpg" />
            </template>
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page
        ref="page"
        :value.sync="contentsPage"
        :total="contentsTotal"
      />
    </div>
    <!-- 생성 모달 -->
    <set-task-info
      :contentId="selectedContentId"
      :visible.sync="showNewTaskInfo"
      @next="taskInfoEnded"
    />
    <set-task-manage
      :contentInfo="selectedContentInfo"
      :properties="selectedContentProperties"
      :visible.sync="showNewTaskManage"
      @next="taskManageEnded"
    />
    <set-task-target
      :form="registerTaskForm"
      :visible.sync="showNewTaskTarget"
      @prev="canceledManageTarget"
    />
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
import contentService from '@/services/content'
import searchMixin from '@/mixins/search'
import columnsMixin from '@/mixins/columns'
import {
  filter as contentsFilter,
  sort as contentsSort,
} from '@/models/content/Content'

import SetTaskInfo from '@/components/task/SetTaskInfo'
import SetTaskManage from '@/components/task/SetTaskManage'
import SetTaskTarget from '@/components/task/SetTaskTarget'

export default {
  mixins: [searchMixin, columnsMixin],
  components: {
    SetTaskInfo,
    SetTaskManage,
    SetTaskTarget,
  },
  data() {
    return {
      // 검색
      loading: false,
      contentsList: [],
      contentsTotal: 0,
      contentsFilter,
      contentsSort,
      contentsSearch: '',
      contentsPage: 1,
      // 생성
      selectedContentId: null,
      selectedContentInfo: null,
      selectedContentProperties: null,
      registerTaskForm: null,
      showNewTaskInfo: false,
      showNewTaskManage: false,
      showNewTaskTarget: false,
    }
  },
  methods: {
    // 검색
    changedSearchParams(searchParams) {
      this.searchContents(searchParams)
    },
    async searchContents(params) {
      const { list, total } = await contentService.searchContents(params)
      this.contentsList = list
      this.contentsTotal = total
    },
    rowClick(row) {
      this.selectedContentId = row.contentUUID
      this.showNewTaskInfo = true
    },
    showAll() {
      this.searchParams.mine = false
      this.emitChangedSearchParams()
    },
    showMine() {
      this.searchParams.mine = true
      this.emitChangedSearchParams()
    },
    // 생성
    taskInfoEnded(contentInfo, properties) {
      this.selectedContentInfo = contentInfo
      this.selectedContentProperties = properties
      this.showNewTaskManage = true
      setTimeout(() => (this.showNewTaskInfo = false), 100)
    },
    taskManageEnded(form) {
      this.registerTaskForm = form
      this.showNewTaskTarget = true
      setTimeout(() => (this.showNewTaskManage = false), 100)
    },
    canceledManageTarget() {
      this.showNewTaskManage = true
      setTimeout(() => (this.showNewTaskTarget = false), 100)
    },
  },
  beforeMount() {
    this.searchContents()
    workspaceService.watchActiveWorkspace(this, () =>
      this.searchContents(this.searchParams),
    )
  },
}
</script>
