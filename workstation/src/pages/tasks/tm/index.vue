<template>
  <div id="tm">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.tasks') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('tm.list.title') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('tm.list.title') }}</h2>
      </div>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
        <el-col class="left">
          <el-button @click="showAll">
            {{ $t('common.all') }}
          </el-button>
          <el-button @click="showMine">
            {{ $t('tm.list.myTm') }}
          </el-button>
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="tmSearch" />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('tm.list.tmList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="tmList"
            v-loading="loading"
          />
        </el-card>
      </el-row>
    </div>
  </div>
</template>

<script>
import searchMixin from '@/mixins/search'
import columnsMixin from '@/mixins/columns'

export default {
  mixins: [searchMixin, columnsMixin],
  data() {
    return {
      loading: false,
      tmList: [],
      tmSearch: '',
    }
  },
}
</script>
