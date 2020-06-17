<template>
  <div id="workspace">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.workspace') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.workspace') }}</h2>
        <p>{{ $t('workspace.desc') }}</p>
      </div>
      <!-- 워크스페이스 리스트 -->
      <el-card class="el-card--table">
        <div slot="header">
          <h3>{{ $t('workspace.list.title') }}</h3>
        </div>
        <workspace-list :workspaces="workspaces" />
        <el-row type="flex" justify="center">
          <el-pagination
            layout="prev, pager, next"
            :total="workspacesTotal"
            @current-change="searchWorkspaces"
          >
          </el-pagination>
        </el-row>
      </el-card>
      <!-- 플랜 리스트 -->
      <el-card class="el-card--table">
        <div slot="header">
          <h3>{{ $t('workspace.usingPlanList.title') }}</h3>
        </div>
        <using-plan-list :plans="plans" />
        <el-row type="flex" justify="center">
          <el-pagination
            layout="prev, pager, next"
            :total="plansTotal"
            @current-change="searchUsingPlans"
          >
          </el-pagination>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
import WorkspaceList from '@/components/workspace/WorkspaceList'
import UsingPlanList from '@/components/workspace/UsingPlanList'

export default {
  components: {
    WorkspaceList,
    UsingPlanList,
  },
  data() {
    return {
      searchParams: {
        page: 1,
        size: 10,
        sort: null,
      },
      workspaces: [],
      workspacesTotal: 0,
      plans: [],
      plansTotal: 0,
    }
  },
  methods: {
    async searchWorkspaces() {
      const { list, total } = await workspaceService.searchWorkspaces(
        this.searchParams,
      )
      this.workspaces = list
      this.workspacesTotal = total
    },
    async searchUsingPlans() {
      const { list, total } = await workspaceService.searchUsingPlans(
        this.searchParams,
      )
      this.plans = list
      this.plansTotal = total
    },
  },
  beforeMount() {
    this.searchWorkspaces()
    this.searchUsingPlans()
  },
}
</script>

<style lang="scss">
#__nuxt #workspace {
  .el-table__body-wrapper {
    min-height: 384px;
  }
}
</style>
