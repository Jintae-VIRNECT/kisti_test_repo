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
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('troubles.list.myTm') }}
          </el-button>
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="troubleSearch" />
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
            <column-default
              :label="$t('troubles.list.column.content')"
              prop="caption"
              sortable="custom"
            />
            <column-user
              :label="$t('troubles.list.column.user')"
              prop="workerUUID"
              nameProp="workerName"
              imageProp="workerProfile"
              sortable="custom"
              :width="140"
            />
            <column-date
              :label="$t('troubles.list.column.reportedDate')"
              prop="reportedDate"
              sortable="custom"
              type="time"
              :width="140"
            />
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page
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
    rowClick(row) {
      this.$router.push(`/tasks/troubles/${row.issueId}`)
    },
    async searchTroubles(searchParams) {
      const { list, total } = await resultService.searchTroubles(searchParams)
      this.troublesList = list
      this.troublesTotal = total
    },
    changedSearchParams(searchParams) {
      this.searchTroubles(searchParams)
    },
    showAll() {},
    showMine() {},
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, () => {
      this.searchTroubles(this.searchParams)
    })
  },
}
</script>
