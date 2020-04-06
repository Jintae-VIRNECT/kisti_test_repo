<template>
  <div id="workspace">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>계정 관리</el-breadcrumb-item>
          <el-breadcrumb-item>워크스페이스 정보</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>워크스페이스 정보</h2>
        <p>회원님이 참여중인 전체 워크스페이스 목록입니다.</p>
      </div>
      <search-tab-nav placeholder="워크스페이스 이름 검색" />
      <el-card class="el-card--table">
        <div slot="header">
          <h3>전체 워크스페이스 정보</h3>
        </div>
        <el-table :data="workspaces">
          <column-default label="이름" prop="name" sortable />
          <column-default label="마스터" prop="master" :width="120" sortable />
          <column-default
            label="참여일"
            prop="joinDate"
            :width="100"
            sortable
          />
          <column-default
            label="워크스페이스 등급"
            prop="workspaceGrade"
            :width="150"
            sortable
          />
          <column-plan
            label="나의 플랜"
            nameProp="planName"
            gradeProp="planGrade"
            :width="170"
          />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

import SearchTabNav from 'WC-Modules/vue/components/searchTabNav/SearchTabNav'
import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnPlan from '@/components/common/tableColumn/ColumnPlan'

export default {
  components: {
    SearchTabNav,
    ColumnDefault,
    ColumnPlan,
  },
  data() {
    return {
      workspaces: workspaceService.getWorkspaceList(),
    }
  },
}
</script>

<style lang="scss">
#workspace {
  .search-tab-nav {
    position: relative;
    bottom: 52px;
    height: 0;
    text-align: right;
  }
}
</style>
