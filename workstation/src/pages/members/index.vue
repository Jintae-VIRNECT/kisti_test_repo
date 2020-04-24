<template>
  <div id="members">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.members') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('members.allMembers.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('members.allMembers.title') }}</h2>
      </div>
      <el-row> </el-row>
      <el-row>
        <el-col class="profile" v-for="member in members" :key="member.uuid">
          <member-profile-card :data="member" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import MemberProfileCard from '@/components/member/MemberProfileCard'
import workspaceService from '@/services/workspace'

export default {
  components: {
    MemberProfileCard,
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
  data() {
    return {
      members: [],
    }
  },
  methods: {
    async searchMembers() {
      const { list, total } = await workspaceService.searchMembers()
      this.members = list
    },
  },
  beforeMount() {
    this.searchMembers()
    workspaceService.watchActiveWorkspace(this.searchMembers)
  },
}
</script>

<style lang="scss">
#members {
  .el-col.el-col-24.profile {
    width: 320px;
    margin-right: 28px;

    &:nth-child(5) {
      margin-right: 0;
    }
  }
}
</style>
