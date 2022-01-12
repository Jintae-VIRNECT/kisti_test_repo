<template>
  <el-dialog
    id="projects-infos-dialog"
    class="project-info-modal"
    :visible.sync="showMe"
    :title="$t('projects.info.title')"
    width="860px"
    top="11vh"
    @close="closed"
  >
    <el-row type="flex">
      <!-- 왼쪽 Tabs 시작 -->
      <el-col :span="15">
        <el-row class="actionbar">
          <el-button type="text" :disabled="isPrevDisable" @click="prev">
            <img id="back" />
          </el-button>
          <el-button type="text" :disabled="isNextDisable" @click="next">
            <img id="foward" />
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button
            @click="projectsDownload(project.targetInfo.path, project.name)"
            type="text"
          >
            <img src="~assets/images/icon/ic-file-download.svg" />
          </el-button>
          <el-button @click="remove" type="text">
            <img src="~assets/images/icon/ic-delete.svg" />
          </el-button>
        </el-row>
        <!-- 왼쪽 트리 뷰 -->
        <el-row class="properties" v-show="activeTab !== 'target'">
          <el-tree
            :data="project.property"
            :props="propertiesProps"
            node-key="id"
            :default-expanded-keys="[1]"
          />
        </el-row>
        <!-- 프로젝트 타겟 이미지 -->
        <el-row class="properties" v-show="activeTab == 'target'">
          <div class="qr" v-show="project.targetInfo.path">
            <img :src="project.targetInfo.path" />
          </div>
          <div class="no-target" v-show="!project.targetInfo.path">
            <span>{{ $t('projects.info.target.noImage') }}</span>
          </div>
        </el-row>
      </el-col>
      <!-- 왼쪽 Tabs 끝 -->
      <!-- 오른쪽 Tabs 시작 -->
      <el-col :span="9" class="infos" ref="projectModal">
        <el-tabs v-model="activeTab">
          <el-tab-pane
            v-for="tab in tabs"
            :key="tab.name"
            :name="tab.name"
            :label="$t(tab.label)"
          >
          </el-tab-pane>
        </el-tabs>
        <!-- 프로젝트 정보 -->
        <ProjectModalInfoPane
          v-show="activeTab === 'project'"
          :project="project"
          :forms="forms"
          :members="members"
          @updated="updated"
          @closed="closed"
          @update:originalPermissionData="setOriginalPermissionData"
          @undo="undo"
        />
        <!-- 타겟 정보 -->
        <ProjectModalTargetPane
          v-show="activeTab === 'target' && project.targetInfo.path"
          :project="project"
        />
        <!-- 활동 정보 -->
        <ProjectModalActivityPane
          v-show="activeTab === 'activity'"
          :activityList="activityList"
          @update:updateProjectActivityLogs="updateProjectActivityLogs"
        />
      </el-col>
      <!-- 오른쪽 Tabs 끝 -->
    </el-row>
    <FileProgressModal
      :visible.sync="showProgressModal"
      :progress="progress"
      :title="$t('projects.info.download.title')"
      :desc="$t('projects.info.download.desc')"
      cancelTitle="common.message.fileDownloadCancelTitle"
      @cancel="cancelFileUpload"
    />
  </el-dialog>
</template>

<script>
import projectService from '@/services/project'
import workspaceService from '@/services/workspace'
import { memberRoleFilter } from '@/models/project/Project'
import filters from '@/mixins/filters'
import utils from '@/mixins/utils'
import { mapGetters } from 'vuex'

export default {
  mixins: [filters, utils],
  async asyncData({ params, store }) {
    // 해당 모달창에서 보여줄 프로젝트 정보를 불러옵니다.
    const project = await projectService.getProjectInfo(
      params.projectId,
      store.getters['auth/myProfile'].uuid,
    )

    // 지정 멤버 드롭다운 메뉴에 들어갈 멤버 리스트를 불러옵니다.
    const list = await workspaceService.allMembers()

    const projectActivityLogs = await projectService.searchProjectActivities(
      params.projectId,
      { page: 1 },
    )
    return {
      project: project,
      activityList: projectActivityLogs.list,
      activityPageMeta: projectActivityLogs.pageMeta,
      members: list.map(({ profile, nickname, uuid }) => {
        return {
          img: profile,
          value: uuid,
          label: nickname,
        }
      }),
    }
  },
  data() {
    return {
      // 현재 보는 탭
      activeTab: '',
      // 탭 리스트
      tabs: [
        {
          name: 'project',
          label: 'projects.info.project.title',
        },
        {
          name: 'target',
          label: 'projects.info.target.title',
        },
        {
          name: 'activity',
          label: 'projects.info.activity.title',
        },
      ],
      showMe: true,
      // 프로젝트 정보
      project: {},
      // 공유/편집 드롭메뉴
      memberRoleFilter,
      // ex) selectOptions: 멤버, 지정멤버.. 등 드롭메뉴 옵션 리스트, memberPermission:드롭메뉴에서 선택한 권한, selectMembers: [선택한 지정 멤버 유저 리스트]
      forms: [
        {
          key: 'share',
          name: 'projects.info.project.shared',
          selectOptions: [],
          memberPermission: null,
          selectMembers: [],
        },
        {
          key: 'edit',
          name: 'projects.info.project.edit',
          selectOptions: [],
          memberPermission: null,
          selectMembers: [],
        },
      ],
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
      // 활동 탭의 활동 리스트 데이터.
      activityList: [],
      activityPageMeta: {},
      loading: false,
      // 현재 모달창에서 보고있는 프로젝트 페이징 수
      modalProjectCurrentPage: 0,
      // 현재 모달창에서 보고있는 특정 페이징 프로젝트 리스트
      modalProjectsList: [],
      // 현재 모달창에서 보고있는 프로젝트까지의 토탈 수.
      modaltotal: 0,
      // 현재 모달창에서 보고있는 프로젝트 검색옵션.
      modalSearchParams: {},
      // 돌려놓기시 사용할 원본 share form 데이터
      originalShareData: {},
      // 돌려놓기시 사용할 원본 edit form 데이터
      originalEditData: {},
      downloadFileMaxSize: 250,
      // 프로그래스바 모달창에 사용될 데이터.
      progress: 0,
      showProgressModal: false,
      // axios 취소 인스턴스
      cancel: () => {},
    }
  },
  watch: {
    // 프로젝트 변경시, 지정멤버 옵션, 리스트도 재설정.
    project(v) {
      this.setSelectOptionsAndSelectMembers()
      history.replaceState(null, null, `/projects/${v.uuid}`)
      this.initProjectActivitys()
    },
    projectsList: {
      deep: true,
      handler() {
        this.$store.commit('project/SET_CURRENT_INDEX', this.project.uuid)
        // 클릭한 프로젝트까지의 총 프로젝트 갯수를 저장.
        this.$store.commit('project/SET_CURRENT_TOTAL_ELEMENTS')
        this.currentModalProjectDataUpdate()
      },
    },
  },
  methods: {
    updated() {
      this.$emit('updated')
    },
    closed() {
      this.$store.dispatch('project/clearAllProjectData')
      this.showMe = false
      this.$router.push('/projects')
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
        await projectService.deleteProject([this.project.uuid])

        this.$message.success({
          message: this.$t('projects.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$store.dispatch('project/clearAllProjectData')
        this.$router.replace('/projects')
      } catch (errors) {
        this.$message.error({
          message:
            this.$tc('projects.info.message.deleteFail', errors.length) +
            `<br>${this.$t('projects.info.message.deleteFailsPhrase')}`,
          dangerouslyUseHTMLString: true,
          duration: 2000,
          showClose: true,
        })
      }
    },
    // 해당 프로젝트 파일 용량이 Max용량을 초과하는지 유무를 반환한다.
    checkProjectFileSize() {
      const isFileSizeOverThenMaxSize =
        this.project.size / 1024 / 1024 > this.downloadFileMaxSize
      if (isFileSizeOverThenMaxSize) {
        this.$message.error({
          message: this.$t('projects.info.message.downloadFailMaxSize', {
            0: this.downloadFileMaxSize,
          }),
          duration: 4000,
          showClose: true,
        })
        return false
      }
      return true
    },
    // 프로그래스바 상태 init 메서드
    initProgressStatus(isStart) {
      this.loading = isStart
      this.showProgressModal = isStart
      if (isStart) this.progress = 0
    },
    // mars 파일 다운로드 메서드
    async projectsDownload() {
      // 최대 용량 초과시, return
      if (!this.checkProjectFileSize()) return

      this.initProgressStatus(true)
      try {
        const onDownloadProgress = event => {
          this.progress = Math.round((100 * event.loaded) / event.total)
        }
        const cancelEvent = c => {
          this.cancel = c
        }
        const { url, fileName } = await projectService.downloadProjects(
          [this.project.uuid],
          onDownloadProgress,
          cancelEvent,
        )
        setTimeout(() => this.download(url, fileName), 500)
      } catch (e) {
        let message
        if (e.code === 'cancel')
          message = this.$t('common.message.fileRequestCancel')
        else if (e.code === 5000)
          message = this.$t('projects.info.message.deleteNotExist')
        else if (e.code === 5041)
          message = this.$t('projects.info.message.downloadFailsPhrase')
        else message = this.$t('common.error')

        this.$message.error({
          message,
          duration: 4000,
          showClose: true,
        })
      }
      this.initProgressStatus(false)
    },
    // 다운로드 취소 버튼 클릭시, 실행시킬 메서드
    async cancelFileUpload() {
      this.cancel()
    },
    // 공유/편집의 드롭메뉴를 지정하고, 지정멤버가 있을 경우에 지정한 멤버들을 드롭메뉴에 추가하니다
    setSelectOptionsAndSelectMembers() {
      let types = this.memberRoleFilter.options.filter(
        type => type.value != 'ALL',
      )
      // 공유/편집의 정보를 지정합니다. 공유/편집의 옵션메뉴. 지정 멤버 리스트.
      this.forms.map(form => {
        form.selectOptions = types

        const isMemberSpecitic = form.key === 'share'
        form.memberPermission = isMemberSpecitic
          ? this.project.sharePermission
          : this.project.editPermission

        if (form.memberPermission === 'SPECIFIC_MEMBER') {
          form.selectMembers = isMemberSpecitic
            ? this.project.sharedUserList
            : this.project.editUserList
        }
        if (isMemberSpecitic) {
          this.setOriginalPermissionData('share')
        } else {
          this.setOriginalPermissionData('edit')
        }
      })
    },
    setOriginalPermissionData(designationType) {
      if (designationType === 'share') {
        this.originalShareData = Object.assign({}, this.forms[0])
      } else {
        this.originalEditData = Object.assign({}, this.forms[1])
      }
    },
    // 이전 버튼 클릭 메소드
    async prev() {
      if (this.loading) return null

      // 새로운 이전 프로젝트 목록 데이터 가져올 때
      if (this.isGetNewPrevProjects) {
        this.loading = true

        await this.$store.dispatch(
          'project/getProjectList',
          Object.assign(
            {},
            {
              ...this.modalSearchParams,
              page: this.modalSearchParams.page - 1,
            },
          ),
        )
        this.loading = false
        this.setProject(this.projectsList.length - 1)
      }
      // 기존의 프로젝트 목록 안에서 이전 프로젝트 객체로 변경할 때
      else if (this.currentIndex > 0) {
        this.setProject(this.currentIndex - 1)
      }
    },
    // 다음 버튼 클릭 메소드
    async next() {
      if (this.loading) return null

      // 새로운 다음 프로젝트 목록 데이터 가져올 때
      if (this.isGetNewNextProjects) {
        this.loading = true
        await this.$store.dispatch(
          'project/getProjectList',
          Object.assign(
            {},
            {
              ...this.modalSearchParams,
              page: this.modalSearchParams.page + 1,
            },
          ),
        )
        this.loading = false
        this.setProject(0)
      }
      // 기존의 프로젝트 목록 안에서 다음 프로젝트 객체로 변경할 때
      else if (this.currentIndex < this.modalProjectsList.length - 1) {
        this.setProject(this.currentIndex + 1)
      }
    },
    // index 번호로 프로젝트 목록에서 프로젝트 지정.
    setProject(index) {
      this.project = this.modalProjectsList[index]
      this.$store.commit('project/SET_CURRENT_INDEX', this.project.uuid)
      this.$store.commit('project/SET_CURRENT_TOTAL_ELEMENTS')
    },
    // findIndex 메서드를 통해, 0번 이상의 index 값을 가지면 true.
    isProjectItemIncludingList() {
      return this.currentIndex > -1
    },
    // 현재 프로젝트 데이터들을 모달창에서 사용하도록 변수 할당.
    currentModalProjectDataUpdate() {
      this.modalProjectCurrentPage = this.pageMeta.currentPage
      this.modalProjectsList = this.projectsList
      this.modaltotal = this.totalElements
      this.modalSearchParams = this.searchParams
    },
    updateProjectActivityLogs() {
      this.getActivitiesLogs({
        page: this.activityPageMeta.currentPage + 1,
      })
    },
    async getActivitiesLogs({ page = this.activityPageMeta.currentPage }) {
      const projectActivityLogs = await projectService.searchProjectActivities(
        this.project.uuid,
        { page },
      )
      this.activityPageMeta = projectActivityLogs.pageMeta
      this.activityList.push(...projectActivityLogs.list)
    },
    initProjectActivitys() {
      this.$nextTick(() => {
        this.$refs.projectModal.$el.scrollTo({
          top: 0,
          behavior: 'smooth',
        })
      })
      this.activityList = []
      this.getActivitiesLogs({ page: 1 })
    },
    undo(v) {
      let num, originalData
      if (v === 'share') {
        num = 0
        originalData = this.originalShareData
      } else {
        num = 1
        originalData = this.originalEditData
      }
      this.$set(this.forms, num, Object.assign({}, originalData))
    },
  },
  computed: {
    ...mapGetters({
      projectsList: 'project/projectList',
      currentIndex: 'project/currentIndex',
      totalElements: 'project/totalElements',
      pageMeta: 'project/pageMeta',
      currentTotalElements: 'project/currentTotalElements',
      searchParams: 'project/searchParams',
    }),
    // 클릭한 프로젝트가 현재 프로젝트 배열에 포함이 안되었거나 isProjectItemIncludingList() ||
    // 현재 페이징 숫자가 첫 페이징 숫자와 동일할 때 && 또한 현 프로젝트의 index 번호가 0번일 때 Disable.
    isPrevDisable() {
      return (
        !this.isProjectItemIncludingList() ||
        (this.modalProjectCurrentPage === 1 && this.isCurrentProjectStartTotal)
      )
    },
    // 클릭한 프로젝트가 현재 프로젝트 배열에 포함이 안되었거나 isProjectItemIncludingList() ||
    // 현재 페이징 숫자가 마지막 페이징 숫자와 동일할 때 && 또한 총 프로젝트 갯수와 현 프로젝트의 index 번호가 일치할 때 Disable.
    isNextDisable() {
      return (
        !this.isProjectItemIncludingList() ||
        (this.modalProjectCurrentPage === this.pageMeta.totalPage &&
          this.isCurrentProjectEndTotal)
      )
    },
    // 현재 페이징 수가 맨 처음과 맨 마지막 숫자가 아닌 중간 숫자일 때, ex) 1~10페이징 숫자가 있을 때, 2 ~ 9 에 해당되는 페이징수
    isCurrentPageMiddle() {
      return (
        this.modalProjectCurrentPage > 1 &&
        this.pageMeta.totalPage > this.modalProjectCurrentPage
      )
    },
    // 이전 페이징 프로젝트 리스트를 가져올 수 있는가
    isGetNewPrevProjects() {
      return (
        this.isCurrentProjectStartTotal && this.modalProjectCurrentPage !== 1
      )
    },
    // 다음 페이징 프로젝트 리스트를 가져올 수 있는가
    isGetNewNextProjects() {
      return (
        this.isCurrnetProjectListEndIndex &&
        this.pageMeta.totalPage !== this.modalProjectCurrentPage
      )
    },
    // 해당 프로젝트가 총 프로젝트 수의 마지막 자리인가?
    isCurrentProjectEndTotal() {
      return this.currentTotalElements === this.modaltotal - 1
    },
    // 해당 프로젝트가 총 프로젝트 수의 처음 자리인가?
    isCurrentProjectStartTotal() {
      return this.currentIndex === 0
    },
    // 해당 프로젝트가 현재 프로젝트의 마지막 배열 인덱스 값인가?
    isCurrnetProjectListEndIndex() {
      return this.currentIndex === this.modalProjectsList.length - 1
    },
  },
  beforeMount() {
    this.$store.commit('auth/SET_ACTIVE_WORKSPACE', this.project.workspaceUUID)
    this.setSelectOptionsAndSelectMembers()
  },
  mounted() {
    this.activeTab = 'project'
    this.currentModalProjectDataUpdate()
  },
}
</script>

<style lang="scss">
#__nuxt #projects-infos-dialog .el-dialog__body {
  --svg-foward: url('~assets/images/icon/ic-arrow-forward.svg');
  --svg-back: url('~assets/images/icon/ic-arrow-back.svg');
  --svg-color: rgb(110 127 155);
  #back {
    background-color: var(--svg-color);
    -webkit-mask-image: var(--svg-back);
    mask-image: var(--svg-back);
  }
  #foward {
    background-color: var(--svg-color);
    -webkit-mask-image: var(--svg-foward);
    mask-image: var(--svg-foward);
  }
}
</style>
