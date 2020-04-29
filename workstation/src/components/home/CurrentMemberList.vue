<template>
  <el-card class="current-member-list el-card--table">
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
      <template slot="empty">
        <img src="~assets/images/empty/img-member-empty.jpg" />
        <p>{{ $t('home.memberList.empty') }}</p>
      </template>
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
      this.members = await workspaceService.getNewMembers()
    },
  },
  beforeMount() {
    this.getMemberList()
    workspaceService.watchActiveWorkspace(
      'workspaceMemberList',
      this.getMemberList,
    )
  },
  beforeDestroy() {
    console.log('destroy')
    workspaceService.unwatchActiveWorkspace('workspaceMemberList')
  },
}
</script>

<style lang="scss"></style>
