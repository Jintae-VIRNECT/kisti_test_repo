<template>
  <div id="projects">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.projects') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('projects.allprojects.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('projects.allprojects.title') }}</h2>
        <ProjectStoragePopup />
      </div>
      <!-- 검색 영역 -->
      <el-row class="searchbar">
        <el-col class="left">
          <span>{{ $t('searchbar.filter.target') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
          <span>{{ $t('searchbar.filter.mode') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
          <span>{{ $t('searchbar.filter.share') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
          <span>{{ $t('searchbar.filter.edit') }}:</span>
          <SearchbarFilter
            ref="filter"
            :value.sync="contentsFilter.value"
            :options="contentsFilter.options"
          />
        </el-col>
        <el-col class="right">
          <el-button-group>
            <el-button
              @click="download(content.target.imgPath, content.contentName)"
              type="text"
              :disabled="!true"
            >
              <img src="~assets/images/icon/ic-file-download.svg" />
            </el-button>
            <el-button @click="remove" type="text" :disabled="!true">
              <img src="~assets/images/icon/ic-delete.svg" />
            </el-button>
          </el-button-group>
          <SearchbarKeyword
            ref="keyword"
            :value.sync="contentsSearch"
            placeholder="contentTitle"
          />
        </el-col>
      </el-row>
      <el-row>
        <el-card class="el-card--table">
          <el-table
            class="clickable"
            ref="table"
            :data="contentsList"
            v-loading="loading"
            @row-click="rowClick"
            @selection-change="selectionChanged"
          >
            <el-table-column type="selection" width="55" />
            <ColumnDefault
              :label="$t('projects.allprojects.column.name')"
              prop="contentName"
              sortable="custom"
            />
            <ColumnDefault
              :label="$t('projects.allprojects.column.tracking')"
              prop="targetType"
              customFilter="targetType2label"
              sortable="custom"
              :width="120"
            />
            <el-table-column
              :label="$t('projects.allprojects.column.mode')"
              :width="290"
            >
              <div class="column-modes">
                <span>3D+2D</span>
                <span>3D</span>
                <span>2D</span>
              </div>
            </el-table-column>
            <ColumnUser
              :label="$t('projects.allprojects.column.uploader')"
              prop="uploaderUUID"
              nameProp="uploaderName"
              imageProp="uploaderProfile"
              :width="160"
            />
            <ColumnBoolean
              :label="$t('projects.allprojects.column.sharedStatus')"
              prop="shared"
              :trueText="$t('contents.sharedStatus.shared')"
              :falseText="$t('contents.sharedStatus.noShared')"
              sortable="custom"
              :width="130"
            />
            <ColumnFileSize
              :label="$t('projects.allprojects.column.Size')"
              prop="contentSize"
              sortable="custom"
              :width="130"
            />
            <ColumnDate
              :label="$t('projects.allprojects.column.createdDate')"
              prop="createdDate"
              sortable="custom"
              :width="140"
            />
            <template slot="empty">
              <img src="~assets/images/empty/img-content-empty.jpg" />
              <p>{{ $t('contents.allContents.empty') }}</p>
            </template>
          </el-table>
        </el-card>
      </el-row>
      <SearchbarPage
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
import columnsMixin from '@/mixins/columns'
import utils from '@/mixins/utils'
import {
  filter as contentsFilter,
  sort as contentsSort,
} from '@/models/content/Content'
// import {
//   targetTypes,
//   modeTypes,
//   sharedTypes,
//   editTypes,
// } from '@/models/project/Project'

export default {
  mixins: [searchMixin, columnsMixin, utils],
  async asyncData({ query }) {
    const contentsSearch = query.search || ''
    const { list, total } = await contentService.searchContents({
      search: contentsSearch,
    })
    return {
      contentsList: list,
      contentsTotal: total,
      contentsSearch,
    }
  },
  data() {
    return {
      loading: false,
      contentsFilter,
      contentsSort,
      contentsPage: 1,
      canRemove: false,
    }
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      this.searchContents()
    },
    async searchContents() {
      // this.searchParams 는 searchMixin에서 가져와 사용
      const { list, total } = await contentService.searchContents(
        this.searchParams,
      )
      this.contentsList = list
      this.contentsTotal = total
    },
    rowClick(row) {
      this.$router.push(`/projects/${row.contentUUID}`)
    },
    selectionChanged(selection) {
      this.canRemove = selection.length ? true : false
    },
    async remove() {
      try {
        await this.$confirm(
          this.$t('contents.info.message.deleteSure'),
          this.$t('contents.info.message.delete'),
        )
      } catch (e) {
        return false
      }
      try {
        const selectedContents = this.$refs.table.selection.map(
          content => content.contentUUID,
        )
        await contentService.deleteContent(selectedContents)
        this.$message.success({
          message: this.$t('contents.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
      } catch (errors) {
        errors.forEach((e, index) => {
          setTimeout(() => {
            this.$message.error({
              message:
                e.code === 4020
                  ? this.$t('contents.info.message.deleteShared')
                  : this.$t('contents.info.message.deleteFail') +
                    `\n(${e.msg} / contentUUID: ${e.contentUUID})`,
              duration: 4000,
              showClose: true,
            })
          }, index * 100)
        })
      }
      this.emitChangedSearchParams()
    },
    /**
     * @description 데이터 조회 조건 초기화
     * @author YongHo Kim <yhkim@virnect.com>
     */
    refreshParams() {
      this.contentsSort.value = 'createdDate,desc'
      this.contentsFilter.value = ['ALL']
      this.contentsSearch = ''
      this.contentsPage = 1

      this.searchParams.mine = false
    },
  },
  beforeMount() {
    // searchMixin.js: emitChangedSearchParams 실행 > 현재 페이지의 changedSearchParams 실행
    this.emitChangedSearchParams()
    workspaceService.watchActiveWorkspace(this, () => {
      this.refreshParams()
      this.emitChangedSearchParams()
    })
  },
}
</script>

<style lang="scss">
#projects {
  .title {
    position: relative;
    .el-button {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 190px;
      height: 34px;
      margin-bottom: 7px;
    }
  }
  .searchbar {
    .el-button {
      height: 34px;
      margin-left: 4px;
      padding: 7px 10px;
    }
    .el-button-group {
      margin-right: 20px;
    }
  }
}
</style>
