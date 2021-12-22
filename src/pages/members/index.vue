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
          <SearchbarSort
            ref="sort"
            :value.sync="memberSort.value"
            :options="memberSort.options"
          />
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="memberFilter.value"
            :options="memberFilter.options"
          />
        </el-col>
        <el-col class="right">
          <SearchbarKeyword ref="keyword" :value.sync="memberSearch" />
          <el-button type="primary" @click="addMember" v-if="canAddMember">
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
          <MemberProfileCard :data="member" @refresh="searchMembers()" />
        </el-col>
        <template slot="empty">
          <img src="~assets/images/empty/img-member-empty.jpg" />
          <p>{{ $t('home.memberList.empty') }}</p>
        </template>
      </el-row>
      <SearchbarPage
        ref="page"
        :value.sync="membersPage"
        :total="membersTotal"
      />
    </div>
    <MemberAddModal
      v-if="showAddModal"
      :membersTotal="memberAmount"
      @refresh="searchMembers()"
      @close="closeMemberAddModal"
    />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'
import searchMixin from '@/mixins/search'
import {
  filter as memberFilter,
  sort as memberSort,
} from '@/models/workspace/Member'

export default {
  mixins: [searchMixin],
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
      activeWorkspace: 'auth/activeWorkspace',
    }),
    canAddMember() {
      return this.activeWorkspace.role !== 'MEMBER'
    },
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
      memberAmount: 0,
    }
  },
  methods: {
    closeMemberAddModal() {
      this.showAddModal = false
    },
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      // 워크스테이션 정보 갱신
      this.getWorkspacePlansInfo()
      this.searchMembers()
      this.getWorkspaceInfo()
    },
    async searchMembers() {
      this.loading = true
      const { list, total } = await workspaceService.searchMembers(
        this.searchParams,
      )
      this.membersList = list
      this.membersTotal = total
      this.loading = false
    },
    /**
     * 워크스페이스 플랜 정보 업데이트
     */
    async getWorkspacePlansInfo() {
      await this.$store.dispatch('plan/getPlansInfo')
    },
    /**
     * 워크스페이스 정보 가져오기
     */
    async getWorkspaceInfo() {
      const { memberAmount } = await workspaceService.getWorkspaceInfo(
        this.activeWorkspace.uuid,
      )
      // 현재 워크스페이스의 등록된 유저 수
      this.memberAmount = memberAmount
    },
    addMember() {
      this.showAddModal = true
    },
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      this.memberSort.value = 'role,asc'
      this.memberFilter.value = ['ALL']
      this.memberSearch = ''
      this.membersPage = 1
    },
  },
  beforeMount() {
    // searchMixin.js: emitChangedSearchParams 실행 > index.vue:changedSearchParams 실행
    this.refreshParams()
    this.emitChangedSearchParams()
    workspaceService.watchActiveWorkspace(this, () => {
      this.refreshParams()
      this.emitChangedSearchParams()
    })
  },
  mounted() {
    // modal query
    const { path, query } = this.$router.currentRoute

    if (query.modal && query.modal === 'memberAdd') {
      this.showAddModal = true
      this.$router.replace(path)
    }
    // searchMixin.js에서도 mounted 실행
  },
}
</script>

<style lang="scss">
#members {
  .title {
    margin-bottom: 20px;
  }
  .members-list {
    display: inline-flex;
    flex-wrap: wrap;
    margin-right: -28px;
  }
  .el-col.el-col-24.profile {
    width: 320px;
    margin-right: 28px;
  }
}
</style>
