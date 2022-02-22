<template>
  <div id="members-activity">
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
        <!-- <el-col class="left"> </el-col>
        <el-col class="right">
          <SearchbarKeyword ref="keyword" :value.sync="activitySearch" @change="activityPage = 1" />
        </el-col> -->
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <el-table ref="table" :data="activityList" v-loading="loading">
            <ColumnUser
              :label="$t('members.activity.column.member')"
              prop="workerName"
              nameProp="workerName"
              imageProp="workerProfile"
            />
            <ColumnDefault
              :label="$t('members.activity.column.contentsCount')"
              prop="countContent"
              :width="150"
            />
            <ColumnDone
              :label="$t('members.activity.column.subTasks')"
              :tooltip="$t('members.activity.column.subTasksTooltip')"
              prop="countProgressing"
              maxProp="countAssigned"
              :width="150"
            />
            <ColumnProgress
              :label="$t('members.activity.column.progress')"
              prop="percent"
              :width="170"
            />
            <ColumnDate
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
                  <router-link
                    :to="`/contents?search=${scope.row.workerEmail}`"
                  >
                    <img src="~assets/images/icon/ic-contents.svg" />
                    <span>{{ $t('members.card.contents') }}</span>
                  </router-link>
                  <router-link :to="`/tasks?search=${scope.row.workerEmail}`">
                    <img src="~assets/images/icon/ic-work.svg" />
                    <span>{{ $t('members.card.work') }}</span>
                  </router-link>
                  <router-link
                    :to="`/tasks/results/papers?search=${scope.row.workerEmail}`"
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
      <SearchbarPage
        ref="page"
        :value.sync="activityPage"
        :total="activityTotal"
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
    /**
     * 최근활동의 경우 별도의 검색 조건이 없어서 asyncData 조회,
      전체 멤버 목록 처럼 정렬,필터, 검색 등이 있다면 data 값이 필요하기 때문에
      asyncData 사용불가 ( asyncData 는 컴포넌트가 생성되기 전에 동작)
     */
    const { list, total } = await workspaceService.searchMembersActivity()
    return {
      activityList: list,
      activityTotal: total,
    }
  },
  data() {
    return {
      loading: false,
      activityPage: 1,
      activitySearch: '',
    }
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      this.searchMembersActivity()
    },
    async searchMembersActivity() {
      const { list, total } = await workspaceService.searchMembersActivity(
        this.searchParams,
      )
      this.activityList = list
      this.activityTotal = total
    },
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      this.activityPage = 1
    },
  },

  beforeMount() {
    workspaceService.watchActiveWorkspace(this, () => {
      this.refreshParams()
      // searchMixin.js: emitChangedSearchParams 실행 > 현재 페이지의 changedSearchParams 실행
      this.emitChangedSearchParams()
    })
  },
}
</script>

<style lang="scss">
#__nuxt #members-activity {
  .column-links {
    overflow: visible;
  }
}
</style>
