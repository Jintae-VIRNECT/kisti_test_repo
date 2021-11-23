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
              @click="projectsDownload"
              type="text"
              :disabled="!canRemove"
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
            placeholder="projectTitle"
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
            <el-table-column
              prop="modeList"
              :label="$t('projects.allprojects.column.mode')"
              :width="290"
            >
              <template slot-scope="scope">
                <ProjectMode :modeList="scope.row['modeList']" />
              </template>
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
import { Loading } from 'element-ui'
import { projectFilterList, memberRoleFilter } from '@/models/project/Project'

export default {
  mixins: [searchMixin, columnsMixin, utils],
  data() {
    return {
      loading: false,
      projectFilterList,
      projectsSearch: '',
      projectsSort: 'createdDate,desc',
      projectsPage: 1,
      canRemove: false,
      projectsList: [],
      projectsTotal: 0,
      pageMeta: {},
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
      this.projectsSort = this.searchParams.sort || this.projectsSort
      // this.searchParams 는 searchMixin에서 가져와 사용
      const { list, pageMeta } = await this.$store.dispatch(
        'project/getProjectList',
        Object.assign(
          {},
          {
            ...this.searchParams,
            sort: this.projectsSort,
          },
        ),
      )
      this.projectsList = list
      this.pageMeta = pageMeta
      this.projectsTotal = pageMeta.totalElements
    },
    rowClick(row) {
      // 현재 프로젝트 리스트 목록을 store에 저장합니다.
      this.$store.dispatch('project/setProjectState', {
        list: this.projectsList,
        pageMeta: this.pageMeta,
        searchParams: Object.assign(
          {},
          {
            ...this.searchParams,
            sort: this.projectsSort,
          },
        ),
      })
      // 클릭한 프로젝트가, 페이징된 프로젝트 목록에서 몇 번째 인덱스인지 계산.
      this.$store.commit('project/SET_CURRENT_INDEX', row.uuid)
      // 클릭한 프로젝트까지의 총 프로젝트 갯수를 저장.
      this.$store.commit('project/SET_CURRENT_TOTAL_ELEMENTS')

      this.$router.push(`/projects/${row.uuid}`)
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
        const selectedProjects = this.$refs.table.selection.map(
          project => project.uuid,
        )
        await projectService.deleteProject(selectedProjects)

        this.$message.success({
          message: this.$t('projects.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
      } catch (errors) {
        this.$message.error({
          message:
            this.$tc('projects.info.message.deleteFail', errors.length) +
            `<br>${this.$t('projects.info.message.deleteFailsPhrase')}`,
          dangerouslyUseHTMLString: true,
          duration: 4000,
          showClose: true,
        })
      }
      this.emitChangedSearchParams()
    },
    async projectsDownload() {
      let loadingInstance = Loading.service({ fullscreen: true })
      try {
        const selectedProjects = this.$refs.table.selection.map(
          project => project.uuid,
        )
        const { url, fileName } = await projectService.downloadProjects(
          selectedProjects,
        )
        this.download(url, fileName)
      } catch (e) {
        this.$message.error({
          message: this.$t('common.error'),
          duration: 4000,
          showClose: true,
        })
      }
      loadingInstance.close()
    },
    /**
     * @description 데이터 조회 조건 초기화
     */
    refreshParams() {
      this.projectFilterList.map(filter => (filter.type.value = ['ALL']))
      this.projectsSort = 'createdDate,desc'
      this.projectsSearch = ''
      this.projectsPage = 1

      this.searchParams.mine = false

      this.changeMemberFilerOptions()
      this.getWorkspacePlansInfo()
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
    /**
     * 워크스페이스 플랜 정보 조회
     */
    async getWorkspacePlansInfo() {
      await this.$store.dispatch('plan/getPlansInfo')
    },
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  async beforeMount() {
    // searchMixin.js: emitChangedSearchParams 실행 > 현재 페이지의 changedSearchParams 실행
    this.emitChangedSearchParams()
    this.changeMemberFilerOptions()
    this.getWorkspacePlansInfo()
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
