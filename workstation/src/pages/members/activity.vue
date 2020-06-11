<template>
  <div id="members-log">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.members') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('members.activity.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('members.activity.title') }}</h2>
      </div>
      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left"> </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="activetySearch" />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <el-table
            ref="table"
            :data="activityList"
            v-loading="loading"
            @row-click="rowClick"
          >
            <column-user
              :label="$t('members.activity.column.member')"
              prop="workerName"
              nameProp="workerName"
              imageProp="workerProfile"
            />
            <column-count
              :label="$t('members.activity.column.contentsCount')"
              prop="countContent"
              :width="150"
            />
            <column-done
              :label="$t('members.activity.column.subTasks')"
              :tooltip="$t('members.activity.column.subTasksTooltip')"
              prop="countProgressing"
              maxProp="countAssigned"
              :width="150"
            />
            <column-progress
              :label="$t('members.activity.column.progress')"
              prop="percent"
              :width="170"
            />
            <column-date
              :label="$t('members.activity.column.reportedDate')"
              type="time"
              prop="lastestReportedTime"
              :width="180"
            />
            <el-table-column
              :label="$t('members.activity.column.link')"
              :width="290"
            >
              <template slot-scope="scope">
                <div class="column-links">
                  <router-link :to="`/contents?search=${scope.row.workerUUID}`">
                    <img src="~assets/images/icon/ic-contents.svg" />
                    <span>{{ $t('members.card.contents') }}</span>
                  </router-link>
                  <router-link :to="`/tasks?search=${scope.row.workerUUID}`">
                    <img src="~assets/images/icon/ic-work.svg" />
                    <span>{{ $t('members.card.work') }}</span>
                  </router-link>
                  <router-link
                    :to="`/tasks/results/papers?search=${scope.row.workerUUID}`"
                  >
                    <img src="~assets/images/icon/ic-report.svg" />
                    <span>{{ $t('members.card.paper') }}</span>
                  </router-link>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page
        ref="page"
        :value.sync="activityPage"
        :total="activetyTotal"
      />
    </div>
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'
import workspaceService from '@/services/workspace'

export default {
  mixins: [columnMixin, searchMixin],
  async asyncData() {
    return {
      activityList: await workspaceService.searchMembersActivity(),
    }
  },
  data() {
    return {
      loading: false,
      activityPage: 1,
      activetyTotal: 0,
      activetySearch: '',
    }
  },
  methods: {
    rowClick() {},
    async searchMembersActivity() {
      this.activityList = await workspaceService.searchMembersActivity()
    },
  },
}
</script>
