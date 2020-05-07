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
      <el-row class="searchbar">
        <el-col class="left">
          <span>{{ $t('searchbar.sort.title') }}:</span>
          <searchbar-sort
            ref="sort"
            :value.sync="memberSort.value"
            :options="memberSort.options"
          />
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="memberFilter.value"
            :options="memberFilter.options"
          />
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="memberSearch" />
          <el-button type="primary" @click="showAddModal = true">
            {{ $t('members.allMembers.addMember') }}
          </el-button>
        </el-col>
      </el-row>
      <!-- 멤버 목록 -->
      <el-row class="members-list" v-loading="loading">
        <el-col
          class="profile"
          v-for="member in membersList"
          :key="member.uuid"
        >
          <member-profile-card :data="member" />
        </el-col>
      </el-row>
      <searchbar-page :value.sync="membersPage" :total="membersTotal" />
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
      membersList: [],
      membersPage: 1,
      membersTotal: 0,
      showAddModal: false,
      memberFilter,
      memberSort,
      memberSearch: '',
      loading: false,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchMembers(searchParams)
    },
    async searchMembers(searchParams) {
      this.loading = true
      const { list, total } = await workspaceService.searchMembers(searchParams)
      this.membersList = list
      this.membersTotal = total
      this.loading = false
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
  .members-list {
    margin-right: -28px;
  }
  .el-col.el-col-24.profile {
    width: 320px;
    margin-right: 28px;
  }
}
</style>
