<template>
  <div id="results">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('results.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('results.title') }}</h2>
      </div>
      <!-- 검색 영역 -->
      <el-row class="searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('contents.allContents.myContents') }}
          </el-button>
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="resultsFilter.value"
            :options="resultsFilter.options"
          />
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="resultsSearch" />
        </el-col>
      </el-row>
      <!-- 테이블 -->
      <el-row>
        <el-card class="el-card--table">
          <el-tabs slot="header" v-model="activeTab">
            <el-tab-pane
              v-for="tab in tabs"
              :key="tab.name"
              :name="tab.name"
              :label="$t(tab.label)"
            >
            </el-tab-pane>
          </el-tabs>
          <el-table class="clickable" ref="table"> </el-table>
        </el-card>
      </el-row>
    </div>
  </div>
</template>

<script>
import search from '@/mixins/search'
import { filter as resultsFilter, tabs } from '@/models/result'

export default {
  mixins: [search],
  data() {
    return {
      tabs,
      activeTab: 'task',
      resultsSearch: '',
      resultsFilter,
    }
  },
  methods: {
    showAll() {},
    showMine() {},
  },
}
</script>

<style lang="scss">
#results {
  .el-card__header {
    padding-top: 0;
    padding-bottom: 0;

    .el-tabs .el-tabs__item {
      height: 56px;
      line-height: 56px;
    }
  }
}
</style>
