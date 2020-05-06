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
      <!-- 검색 영역 -->
      <el-row class="navbar">
        <el-col class="left">
          <span>{{ $t('navbar.sort.title') }}:</span>
          <navbar-sort
            ref="sort"
            :value.sync="memberSort.value"
            :options="memberSort.options"
          />
          <span>{{ $t('navbar.filter.title') }}:</span>
          <navbar-filter
            ref="filter"
            :value.sync="memberFilter.value"
            :options="memberFilter.options"
          />
        </el-col>
        <el-col class="right">
          <navbar-search ref="search" :value.sync="memberSearch" />
          <el-button type="primary" @click="showAddModal = true">
            {{ $t('members.allMembers.addMember') }}
          </el-button>
        </el-col>
      </el-row>
      <!-- 멤버 목록 -->
      <el-row v-loading="membersLoading">
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
import searchMixin from '@/mixins/search'
import {
  filter as memberFilter,
  sort as memberSort,
} from '@/models/workspace/Member'

export default {
  mixins: [searchMixin],
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
      memberFilter,
      memberSort,
      memberSearch: '',
      membersLoading: false,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchMembers(searchParams)
    },
    async searchMembers(searchParams) {
      this.membersLoading = true
      const { list, total } = await workspaceService.searchMembers(searchParams)
      this.members = list
      this.membersLoading = false
    },
  },
  beforeMount() {
    this.searchMembers()
    workspaceService.watchActiveWorkspace(this, () =>
      this.searchMembers(this.searchParams),
    )
  },
  mounted() {
    // modal query
    const { path, query } = this.$router.currentRoute
    if (query.modal && query.modal === 'memberAdd') {
      this.showAddModal = true
      this.$router.replace(path)
    }
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
