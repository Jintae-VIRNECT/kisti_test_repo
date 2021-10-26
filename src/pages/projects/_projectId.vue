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
          <el-button type="text" :disabled="!true">
            <img id="back" />
          </el-button>
          <el-button type="text" :disabled="!true">
            <img id="foward" />
          </el-button>
          <el-divider direction="vertical"></el-divider>
          <el-button
            @click="download(project.targetInfo.path, project.name)"
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
            :data="properties"
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
      <el-col :span="9" class="infos">
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
        />
      </el-col>
      <!-- 오른쪽 Tabs 끝 -->
    </el-row>
  </el-dialog>
</template>

<script>
import projectService from '@/services/project'
import workspaceService from '@/services/workspace'
import { memberRoleFilter } from '@/models/project/Project'
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  async asyncData({ params, store }) {
    // 해당 모달창에서 보여줄 프로젝트 정보를 불러옵니다.
    const project = await projectService.getProjectInfo(
      params.projectId,
      store.getters['auth/myProfile'].uuid,
    )

    // 지정 멤버 드롭다운 메뉴에 들어갈 멤버 리스트를 불러옵니다.
    const { list } = await workspaceService.searchMembers()

    return {
      project: project,
      properties: project.property,
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
      activityList: [
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'UPDATE',
          nickname: 'User1',
          updated: '2020-05-26T10:43:20',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'UPLOAD',
          nickname: 'User2',
          updated: '2020-05-26T10:43:20',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'EDIT',
          nickname: 'User3',
          updated: '2020-05-26T10:43:20',
          member: '멤버',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'SHARED',
          nickname: 'User4',
          updated: '2020-05-26T10:43:20',
          member: '지정 멤버',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'TRASH',
          nickname: 'User5',
          updated: '2020-05-26T10:43:20',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'DELETE',
          nickname: 'User5',
          updated: '2020-05-26T10:43:20',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'BACKUP',
          nickname: 'User5',
          updated: '2020-05-26T10:43:20',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'DOWNLOAD',
          nickname: 'User5',
          updated: '2020-05-26T10:43:20',
        },
      ],
    }
  },
  methods: {
    closed() {
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
        this.$message.success({
          message: this.$t('projects.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$router.replace('/projects')
      } catch (errors) {
        const e = errors[0]
        this.$message.error({
          message: this.$t('projects.info.message.deleteFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
  beforeMount() {
    // TODO: 모달창에 들어온 유저의 워크스페이스 설정을 해주자
    // this.$store.commit('auth/SET_ACTIVE_WORKSPACE', this.project.workspaceUUID)

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
    })
  },
  mounted() {
    this.activeTab = 'project'
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
