<template>
  <el-card class="current-member-list el-card--table">
    <div slot="header">
      <h3>
        <span>{{ $t('home.memberList.title') }}</span>
      </h3>
    </div>
    <el-table :data="members" v-loading="loading">
      <ColumnUser
        :label="$t('home.memberList.column.nickname')"
        prop="uuid"
        nameProp="nickname"
        imageProp="profile"
        :width="170"
      />
      <ColumnDefault :label="$t('home.memberList.column.email')" prop="email" />
      <ColumnRole
        :label="$t('home.memberList.column.role')"
        prop="role"
        :width="124"
      />
      <ColumnDate
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
import columnsMixin from '@/mixins/columns'

export default {
  mixins: [columnsMixin],
  data() {
    return {
      members: [],
      loading: false,
    }
  },
  methods: {
    async getMemberList() {
      this.loading = true
      this.members = await workspaceService.getNewMembers()
      // 게스트 멤버는 uuid 값을 사용자 계정 값으로 보여준다.
      this.members.map(member => {
        if (member.role === 'GUEST') member.email = member.uuid
      })

      this.loading = false
    },
  },
  beforeMount() {
    this.getMemberList()
    workspaceService.watchActiveWorkspace(this, this.getMemberList)
  },
}
</script>

<style lang="scss"></style>
