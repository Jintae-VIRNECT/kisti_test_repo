<template>
  <div id="members-log">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.members') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('members.activity.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('members.activity.title') }}</h2>
      </div>
      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left"> </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="activetySearch" />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <el-table
            class="clickable"
            ref="table"
            :data="activityList"
            v-loading="loading"
            @row-click="rowClick"
          >
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page
        ref="page"
        :value.sync="activityPage"
        :total="activetyTotal"
      />
    </div>
  </div>
</template>

<script>
import searchMixin from '@/mixins/search'

export default {
  mixins: [searchMixin],
  data() {
    return {
      loading: false,
      activityList: [],
      activityPage: 1,
      activetyTotal: 0,
      activetySearch: '',
    }
  },
  methods: {
    rowClick() {},
  },
}
</script>
