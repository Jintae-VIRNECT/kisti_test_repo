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
          <SearchbarMine
            ref="mine"
            :mineLabel="$t('contents.allContents.myContents')"
          />
          <el-button @click="remove" type="text" :disabled="!canRemove">
            <img src="~assets/images/icon/ic-delete.svg" />
            <span>{{ $t('contents.allContents.delete') }}</span>
          </el-button>
        </el-col>
        <el-col class="right">
          <SearchbarKeyword ref="keyword" :value.sync="contentsSearch" />
        </el-col>
      </el-row>
      <!-- 리스트 -->
      <el-row>
        <el-card class="el-card--table">
          <div slot="header">
            <h3>
              <span>{{
                $t('contents.allContents.workspaceContentsList')
              }}</span>
            </h3>
          </div>
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
              :label="$t('contents.allContents.column.id')"
              prop="contentUUID"
              customFilter="slice8"
              :width="120"
            />
            <ColumnDefault
              :label="$t('contents.allContents.column.name')"
              prop="contentName"
              sortable="custom"
            />
            <ColumnDefault
              :label="$t('contents.allContents.column.targetType')"
              prop="targetType"
              customFilter="targetType2label"
              sortable="custom"
              :width="120"
            />
            <ColumnUser
              :label="$t('contents.allContents.column.uploader')"
              prop="uploaderUUID"
              nameProp="uploaderName"
              imageProp="uploaderProfile"
              :width="160"
            />
            <ColumnDate
              :label="$t('contents.allContents.column.createdDate')"
              prop="createdDate"
              sortable="custom"
              :width="140"
            />
            <ColumnBoolean
              :label="$t('contents.allContents.column.sharedStatus')"
              prop="shared"
              :trueText="$t('contents.sharedStatus.shared')"
              :falseText="$t('contents.sharedStatus.noShared')"
              sortable="custom"
              :width="130"
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
    <nuxt-child @updated="emitChangedSearchParams" />
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
import contentService from '@/services/content'
import searchMixin from '@/mixins/search'
import columnsMixin from '@/mixins/columns'
import {
  filter as contentsFilter,
  sort as contentsSort,
} from '@/models/content/Content'

export default {
  mixins: [searchMixin, columnsMixin],
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
    changedSearchParams(searchParams) {
      this.searchContents(searchParams)
    },
    async searchContents(searchParams) {
      const { list, total } = await contentService.searchContents(searchParams)
      this.contentsPage = searchParams === undefined ? 1 : searchParams.page
      this.contentsList = list
      this.contentsTotal = total
    },
    rowClick(row) {
      this.$router.push(`/contents/${row.contentUUID}`)
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
  },
  beforeMount() {
    workspaceService.watchActiveWorkspace(this, () => {
      this.searchContents({ page: 1 })
    })
  },
}
</script>

<style lang="scss">
#contents {
  .el-radio-group {
    margin-right: 6px;
  }
  .searchbar .el-button {
    height: 34px;
    padding: 7px 10px;
  }
}
</style>
