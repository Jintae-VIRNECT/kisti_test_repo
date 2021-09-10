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
          <el-col
            v-for="filter in projectFilterList"
            class="filter"
            :key="filter.id"
          >
            <span>{{ $t(`${filter.label}`) }}:</span>
            <SearchbarFilter
              ref="filter"
              :key="filter.id"
              :value.sync="filter.type.value"
              :options="filter.type.options"
            />
          </el-col>
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
            <el-button @click="remove" type="text" :disabled="!canRemove">
              <img src="~assets/images/icon/ic-delete.svg" />
            </el-button>
          </el-button-group>
          <SearchbarKeyword
            ref="keyword"
            :value.sync="projectsSearch"
            placeholder="contentTitle"
          />
        </el-col>
      </el-row>
      <el-row>
        <el-card class="el-card--table">
          <el-table
            class="clickable"
            ref="table"
            :data="projectsList"
            v-loading="loading"
            @row-click="rowClick"
            @selection-change="selectionChanged"
          >
            <el-table-column type="selection" width="55" />
            <ColumnDefault
              :label="$t('projects.allprojects.column.name')"
              prop="name"
              sortable="custom"
            />
            <ColumnDefault
              :label="$t('projects.allprojects.column.tracking')"
              prop="targetType"
              customFilter="targetType2label4project"
              sortable="custom"
              :width="120"
            />
            <ProjectMode
              :label="$t('projects.allprojects.column.mode')"
              prop="modeList"
              :width="290"
            />
            <ColumnUser
              :label="$t('projects.allprojects.column.uploader')"
              prop="uploaderUUID"
              nameProp="uploaderName"
              imageProp="uploaderProfile"
              :width="160"
            />
            <ColumnBoolean
              :label="$t('projects.allprojects.column.sharedStatus')"
              prop="sharePermission"
              :trueText="$t('projects.sharedStatus.shared')"
              :falseText="$t('projects.sharedStatus.noShared')"
              sortable="custom"
              :width="130"
            />
            <ColumnFileSize
              :label="$t('projects.allprojects.column.Size')"
              prop="size"
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
              <p>{{ $t('projects.allprojects.empty') }}</p>
            </template>
          </el-table>
        </el-card>
      </el-row>
      <SearchbarPage
        ref="page"
        :value.sync="projectsPage"
        :total="projectsTotal"
      />
    </div>
    <nuxt-child @updated="emitChangedSearchParams" />
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'
import projectService from '@/services/project'
import { mapGetters } from 'vuex'
import searchMixin from '@/mixins/search'
import columnsMixin from '@/mixins/columns'
import utils from '@/mixins/utils'
import { projectFilterList, memberRoleFilter } from '@/models/project/Project'

export default {
  mixins: [searchMixin, columnsMixin, utils],
  async asyncData({ query }) {
    const projectsSearch = query.search || ''
    const { list, total } = await projectService.searchProjects({
      search: projectsSearch,
    })
    return {
      projectsList: list,
      projectsTotal: total,
      projectsSearch,
    }
  },
  data() {
    return {
      loading: false,
      projectFilterList,
      projectsPage: 1,
      canRemove: false,
    }
  },
  methods: {
    /**
     * searchMixin에서 emitChangedSearchParams 실행시 changedSearchParams 사용
     */
    changedSearchParams() {
      this.searchProjects()
    },
    async searchProjects() {
      // this.searchParams 는 searchMixin에서 가져와 사용
      const { list, total } = await projectService.searchProjects(
        Object.assign({}, this.searchParams),
      )
      this.projectsList = list
      this.projectsTotal = total
    },
    rowClick(row) {
      this.$router.push(`/projects/${row.projectUUID}`)
    },
    selectionChanged(selection) {
      this.canRemove = selection.length ? true : false
    },
    async remove() {
      try {
        await this.$confirm(
          this.$t('projects.info.message.deleteSure'),
          this.$t('projects.info.message.delete'),
          {
            confirmButtonText: this.$t('common.delete'),
            dangerouslyUseHTMLString: true,
          },
        )
      } catch (e) {
        return false
      }
      try {
        this.$message.success({
          message: this.$t('projects.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
      } catch (errors) {
        errors.forEach((e, index) => {
          setTimeout(() => {
            this.$message.error({
              message:
                this.$t('projects.info.message.deleteFail') +
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
     */
    refreshParams() {
      this.projectFilterList.map(filter => (filter.type.value = ['ALL']))
      this.projectsSearch = ''
      this.projectsPage = 1

      this.searchParams.mine = false

      this.changeMemberFilerOptions()
    },
    /**
     * @description 유저권한별 공유/편집 드롭다운메뉴 필터링 메소드
     */
    changeMemberFilerOptions() {
      this.projectFilterList.map(filter => {
        if (filter.id === 'sharedTypes' || filter.id === 'editTypes') {
          filter.type.options = this.checkMemberRole()
        }
      })
    },
    /**
     * @description 멤버권한인 유저는 '매니저' 드롭메뉴가 뜨지않도록, filter Array를 반환한다.
     * @returns {Array} filter options
     */
    checkMemberRole() {
      return this.activeWorkspace.role === 'MEMBER'
        ? memberRoleFilter.options.filter(type => type.value !== 'MANAGER')
        : memberRoleFilter.options
    },
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  beforeMount() {
    // searchMixin.js: emitChangedSearchParams 실행 > 현재 페이지의 changedSearchParams 실행
    this.emitChangedSearchParams()
    this.changeMemberFilerOptions()
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
      width: 220px;
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
    .filter {
      width: 25%;
    }
  }
}
</style>
