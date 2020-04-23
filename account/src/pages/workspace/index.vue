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
      <el-card class="el-card--table">
        <div slot="header">
          <h3>전체 워크스페이스 정보</h3>
        </div>
        <el-table :data="workspaces">
          <column-default
            :label="$t('workspace.column.name')"
            prop="name"
            sortable
          />
          <column-user
            :label="$t('workspace.column.master')"
            prop="masterName"
            imageProp="masterProfile"
            :width="120"
            sortable
          />
          <column-date
            :label="$t('workspace.column.joinDate')"
            prop="joinDate"
            :width="100"
            sortable
          />
          <column-role
            :label="$t('workspace.column.role')"
            prop="role"
            :width="150"
            sortable
          />
          <column-plan
            :label="$t('workspace.column.plan')"
            nameProp="planName"
            gradeProp="planGrade"
            :width="170"
          />
        </el-table>
        <el-row type="flex" justify="center">
          <el-pagination
            layout="prev, pager, next"
            :total="workspacesTotal"
            @current-change="getWorkspaces"
          >
          </el-pagination>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnUser from '@/components/common/tableColumn/ColumnUser'
import ColumnDate from '@/components/common/tableColumn/ColumnDate'
import ColumnRole from '@/components/common/tableColumn/ColumnRole'
import ColumnPlan from '@/components/common/tableColumn/ColumnPlan'

export default {
  components: {
    ColumnDefault,
    ColumnUser,
    ColumnDate,
    ColumnRole,
    ColumnPlan,
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
    }
  },
  methods: {
    async getWorkspaces() {
      const { list, total } = await workspaceService.getWorkspaceList(
        this.searchParams,
      )
      this.workspaces = list
      // this.workspacesTotal = total
    },
  },
  beforeMount() {
    this.getWorkspaces()
  },
}
</script>

<style lang="scss">
#workspace {
  .el-table {
    min-height: 450px;
  }
}
</style>
