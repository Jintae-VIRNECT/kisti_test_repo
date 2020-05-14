<template>
  <div id="contents">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.processes') }}</el-breadcrumb-item>
          <el-breadcrumb-item to="/processes">{{
            $t('process.list.title')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('process.new.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('process.new.title') }}</h2>
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
    <set-process-info
      :contentId="selectedContentId"
      :visible.sync="showNewProcessInfo"
      @next="processInfoEnded"
    />
    <set-process-manage
      :contentInfo="selectedContentInfo"
      :visible.sync="showNewProcessManage"
      @next="processManageEnded"
    />
    <set-process-target
      :contentInfo="selectedContentInfo"
      :visible.sync="showNewProcessTarget"
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

import SetProcessInfo from '@/components/process/SetProcessInfo'
import SetProcessManage from '@/components/process/SetProcessManage'

export default {
  mixins: [searchMixin, columnsMixin],
  components: {
    SetProcessInfo,
    SetProcessManage,
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
      showNewProcessInfo: false,
      showNewProcessManage: false,
      showNewProcessTarget: false,
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
      this.showNewProcessInfo = true
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
    processInfoEnded(contentInfo) {
      this.selectedContentInfo = contentInfo
      this.showNewProcessManage = true
    },
    processManageEnded() {
      this.showNewProcessTarget = true
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
