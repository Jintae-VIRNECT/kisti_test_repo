<template>
  <div>
    <el-table ref="table" :data="workspaces">
      <column-user
        :label="$t('workspace.list.column.name')"
        prop="name"
        nameProp="name"
        imageProp="profile"
        sortable
      />
      <column-user
        :label="$t('workspace.list.column.master')"
        prop="masterNickName"
        nameProp="masterNickName"
        imageProp="masterProfile"
        :width="120"
        sortable
      />
      <column-date
        :label="$t('workspace.list.column.joinDate')"
        prop="joinDate"
        :width="100"
        sortable
      />
      <column-role
        :label="$t('workspace.list.column.role')"
        prop="role"
        :width="150"
        sortable
      />
    </el-table>
    <searchbar-page
      v-if="!isHome"
      ref="page"
      :value.sync="workspacesPage"
      :pageSize="6"
      :total="workspacesTotal"
    />
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import searchMixin from '@/mixins/search'
import workspaceService from '@/services/workspace'

export default {
  mixins: [columnMixin, searchMixin],
  props: {
    isHome: Boolean,
  },
  data() {
    return {
      workspaces: [],
      workspacesPage: 1,
      workspacesTotal: 0,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchWorkspaces(searchParams)
    },
    async searchWorkspaces(searchParams) {
      const { list, total } = await workspaceService.searchWorkspaces(
        searchParams,
      )
      this.workspaces = list
      this.workspacesTotal = total
    },
  },
  beforeMount() {
    this.searchWorkspaces({
      size: this.isHome ? 5 : 6,
    })
  },
}
</script>
