<template>
  <div id="processes">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.processes') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('process.list.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('process.list.title') }}</h2>
        <el-button type="primary" @click="newProcess">
          {{ $t('process.list.newProcess') }}
        </el-button>
      </div>

      <!-- 탭 -->
      <el-row class="tab-wrapper searchbar">
        <el-tabs v-model="activeTab">
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.name"
            :name="tab.name"
            :label="$t(tab.label)"
          />
        </el-tabs>
        <searchbar-keyword ref="keyword" :value.sync="processSearch" />
      </el-row>

      <!-- 버튼 영역 -->
      <el-row class="btn-wrapper searchbar">
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
            :value.sync="processFilter.value"
            :options="processFilter.options"
          />
        </el-col>
      </el-row>

      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('contents.allContents.workspaceContentsList') }}</h3>
          </div>
          <el-table
            class="clickable"
            ref="table"
            :data="processList"
            v-loading="loading"
          >
            <column-default
              :label="$t('process.list.column.id')"
              prop="id"
              :width="140"
            />
            <column-default
              :label="$t('process.list.column.name')"
              prop="name"
              sortable="custom"
            />
          </el-table>
        </el-card>
      </el-row>
      <searchbar-page
        ref="page"
        :value.sync="processPage"
        :total="processTotal"
      />
    </div>
  </div>
</template>

<script>
import { filter as processFilter, tabs } from '@/models/process/Process'
import searchMixin from '@/mixins/search'
import columnMixin from '@/mixins/columns'
import processService from '@/services/process'

export default {
  mixins: [searchMixin, columnMixin],
  async asyncData({ store }) {
    const promise = {
      processes: processService.searchProcesses(
        store.getters['workspace/activeWorkspace'].uuid,
      ),
    }
    return {
      processList: (await promise.processes).list,
      processTotal: (await promise.processes).total,
    }
  },
  data() {
    return {
      tabs,
      activeTab: 'allProcesses',
      processFilter,
      processSearch: '',
      processPage: 1,
      processTotal: 0,
      loading: false,
    }
  },
  methods: {
    showAll() {},
    showMine() {},
    newProcess() {
      this.$router.push('/processes/new')
    },
  },
}
</script>

<style lang="scss">
#processes {
  .title {
    position: relative;
    .el-button {
      position: absolute;
      right: 0;
      bottom: 0;
    }
  }
  .searchbar .left > .el-button:nth-child(2) {
    margin: 0 20px 0 4px;
  }
  .tab-wrapper {
    margin-bottom: 20px;

    .searchbar__keyword {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      height: 34px;
      margin: auto 0;
    }
  }
  .btn-wrapper {
    margin-bottom: 16px;
  }
}
</style>
