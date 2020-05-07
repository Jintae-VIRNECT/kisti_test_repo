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
      <el-row class="searchbar">
        <el-col class="left">
          <span>{{ $t('searchbar.sort.title') }}:</span>
          <searchbar-sort
            ref="sort"
            :value.sync="contentsSort.value"
            :options="contentsSort.options"
          />
          <span>{{ $t('searchbar.filter.title') }}:</span>
          <searchbar-filter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
        </el-col>
        <el-col class="right">
          <searchbar-keyword ref="keyword" :value.sync="contentsSearch" />
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
      <searchbar-page
        ref="page"
        :value.sync="contentsPage"
        :total="contentsTotal"
      />
    </div>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
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
      contentsList: [],
      contentsTotal: 0,
      contentsFilter,
      contentsSort,
      contentsSearch: '',
      contentsPage: 1,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchContents(searchParams)
    },
    async searchContents(params) {
      const { list, total } = await contentService.searchContents(params)
      this.contentsList = list
      this.contentsTotal = total
    },
  },
  beforeMount() {
    this.searchContents()
    workspaceService.watchActiveWorkspace(this, () =>
      this.searchContents(this.searchParams),
    )
  },
}
</script>
