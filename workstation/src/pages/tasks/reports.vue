<template>
  <div id="reports">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('reports.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('reports.title') }}</h2>
      </div>
      <!-- 검색 영역 -->
      <el-row class="searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('contents.allContents.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('contents.allContents.myContents') }}
          </el-button>
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="reportFilter.value"
            :options="reportFilter.options"
          />
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="reportsSearch" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import search from '@/mixins/search'
import { filter as reportFilter } from '@/models/report'

export default {
  mixins: [search],
  data() {
    return {
      reportsSearch: '',
      reportFilter,
    }
  },
  methods: {
    showAll() {},
    showMine() {},
  },
}
</script>
