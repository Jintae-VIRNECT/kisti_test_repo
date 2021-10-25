<template>
  <div id="tm">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('troubles.list.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('troubles.list.title') }}</h2>
      </div>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <SearchbarMine ref="mine" :mineLabel="$t('troubles.list.myTm')" />
        </el-col>
        <el-col class="right">
          <SearchbarKeyword ref="keyword" :value.sync="troubleSearch" />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('troubles.list.tmList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="troublesList"
            v-loading="loading"
            @row-click="rowClick"
          >
            <ColumnDefault
              :label="$t('troubles.list.column.content')"
              prop="caption"
            />
            <ColumnUser
              :label="$t('troubles.list.column.user')"
              prop="workerUUID"
              nameProp="workerName"
              imageProp="workerProfile"
              sortable="custom"
              :width="140"
            />
            <ColumnDate
              :label="$t('troubles.list.column.reportedDate')"
              prop="reportedDate"
              type="time"
              :width="140"
            />
          </el-table>
        </el-card>
      </el-row>
      <SearchbarPage
        ref="page"
        :value.sync="troublesPage"
        :total="troublesTotal"
      />
    </div>
    <nuxt-child />
  </div>
</template>

<script>
import searchMixin from '@/mixins/search'
import columnsMixin from '@/mixins/columns'
import resultService from '@/services/result'
import workspaceService from '@/services/workspace'

export default {
  mixins: [searchMixin, columnsMixin],
  async asyncData() {
    const { list, total } = await resultService.searchTroubles()
    return {
      troublesList: list,
      troublesTotal: total,
    }
  },
  data() {
    return {
      loading: false,
      troubleSearch: '',
      troublesPage: 1,
    }
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      this.searchTroubles()
    },
    async searchTroubles() {
      const { list, total } = await resultService.searchTroubles()
      this.troublesList = list
      this.troublesTotal = total
    },
    rowClick(row) {
      this.$router.push(`/tasks/troubles/${row.issueId}`)
    },
    showAll() {},
    showMine() {},
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      this.troubleSearch = ''
      this.troublesPage = 1

      this.searchParams.mine = false
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
