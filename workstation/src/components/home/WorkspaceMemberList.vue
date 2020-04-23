<template>
  <el-card class="workspace-member-list el-card--table">
    <div slot="header">
      <h3>{{ $t('home.memberList.title') }}</h3>
    </div>
    <el-table :data="members">
      <column-user
        :label="$t('home.memberList.column.nickname')"
        prop="nickname"
        imageProp="profile"
        :width="170"
      />
      <column-default
        :label="$t('home.memberList.column.email')"
        prop="email"
      />
      <column-role
        :label="$t('home.memberList.column.role')"
        prop="role"
        :width="124"
      />
      <column-date
        :label="$t('home.memberList.column.joinDate')"
        prop="joinDate"
        :width="100"
      />
    </el-table>
  </el-card>
</template>

<script>
import workspaceService from '@/services/workspace'
import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnUser from '@/components/common/tableColumn/ColumnUser'
import ColumnRole from '@/components/common/tableColumn/ColumnRole'
import ColumnDate from '@/components/common/tableColumn/ColumnDate'

export default {
  components: {
    ColumnDefault,
    ColumnUser,
    ColumnRole,
    ColumnDate,
  },
  data() {
    return {
      members: [],
    }
  },
  methods: {
    async getMemberList() {
      this.members = (
        await workspaceService.searchMembers({
          size: 4,
        })
      ).list
    },
  },
  beforeMount() {
    this.getMemberList()
    workspaceService.watchActiveWorkspace(this.getMemberList)
  },
}
</script>

<style lang="scss">
.workspace-member-list {
  .el-table__body-wrapper {
    min-height: 256px;
  }
}
</style>
