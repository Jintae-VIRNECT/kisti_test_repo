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
      <el-row class="navbar">
        <el-button type="primary" class="right" @click="showAddModal = true">
          {{ $t('members.allMembers.addMember') }}
        </el-button>
      </el-row>
      <el-row>
        <el-col class="profile" v-for="member in members" :key="member.uuid">
          <member-profile-card :data="member" />
        </el-col>
      </el-row>
    </div>
    <member-add-modal :visible.sync="showAddModal" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import MemberProfileCard from '@/components/member/MemberProfileCard'
import MemberAddModal from '@/components/member/MemberAddModal'
import workspaceService from '@/services/workspace'

export default {
  components: {
    MemberProfileCard,
    MemberAddModal,
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
      showAddModal: false,
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
  .title {
    margin-bottom: 20px;
  }
  .el-col.el-col-24.profile {
    width: 320px;
    margin-right: 28px;

    &:nth-child(5) {
      margin-right: 0;
    }
  }
}
</style>
