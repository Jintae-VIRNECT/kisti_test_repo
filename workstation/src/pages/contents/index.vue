<template>
  <div id="contents">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.contents') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('contents.allContents.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('contents.allContents.title') }}</h2>
      </div>
      <!-- 검색 영역 -->
      <el-row class="navbar">
        <el-col class="left">
          <span>{{ $t('navbar.sort.title') }}:</span>
          <navbar-sort
            ref="sort"
            :value.sync="contentsSort.value"
            :options="contentsSort.options"
          />
          <span>{{ $t('navbar.filter.title') }}:</span>
          <navbar-filter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
        </el-col>
        <el-col class="right">
          <navbar-search ref="search" :value.sync="contentsSearch" />
        </el-col>
      </el-row>
      <!-- 리스트 -->
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>{{ $t('contents.allContents.workspaceContentsList') }}</h3>
          </div>
          <el-table :data="contentsList" v-loading="loading">
            <el-table-column type="selection" width="55" />
            <column-default
              :label="$t('contents.allContents.column.id')"
              prop="contentUUID"
              :width="120"
            />
            <column-default
              :label="$t('contents.allContents.column.name')"
              prop="contentName"
            />
            <column-default
              :label="$t('contents.allContents.column.targetType')"
              prop="targets"
              :width="100"
            />
            <column-user
              :label="$t('contents.allContents.column.uploader')"
              prop="uploaderUUID"
              nameProp="uploaderName"
              imageProp="uploaderProfile"
              :width="140"
            />
            <column-date
              :label="$t('contents.allContents.column.createdDate')"
              prop="createdDate"
              :width="100"
            />
            <column-default
              :label="$t('contents.allContents.column.shareStatus')"
              prop="shared"
              :width="120"
            />
            <template slot="empty">
              <img src="~assets/images/empty/img-content-empty.jpg" />
            </template>
          </el-table>
        </el-card>
      </el-row>
      <el-row type="flex" justify="center">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="contentsTotal"
          @current-change="contentsPageChange"
        />
      </el-row>
    </div>
  </div>
</template>

<script>
import contentService from '@/services/content'
import searchMixin from '@/mixins/search'
import {
  filter as contentsFilter,
  sort as contentsSort,
} from '@/models/content/Content'

import ColumnDefault from '@/components/common/tableColumn/ColumnDefault'
import ColumnUser from '@/components/common/tableColumn/ColumnUser'
import ColumnDate from '@/components/common/tableColumn/ColumnDate'

export default {
  mixins: [searchMixin],
  components: {
    ColumnDefault,
    ColumnUser,
    ColumnDate,
  },
  data() {
    return {
      loading: false,
      contentsFilter,
      contentsSort,
      contentsSearch: '',
      contentsSearchParams: {},
      contentsList: [],
      contentsTotal: 0,
      contentStatistics: {},
    }
  },
  methods: {
    async searchContents(params) {
      const contents = await contentService.searchContents(params)
      this.contentsList = contents.list
      this.contentsTotal = contents.total
      this.contentsSearchParams = params
    },
    async contentsPageChange(page) {
      const params = { page, ...this.contentsSearchParams }
      const contents = await contentService.searchContents(params)
      this.contentsList = contents.list
    },
  },
  beforeMount() {
    this.searchContents()
  },
}
</script>
