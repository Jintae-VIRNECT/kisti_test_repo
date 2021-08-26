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
      <!-- 전체 구성 정보 -->
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
            @click="download(content.target.imgPath, content.contentName)"
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
            :default-expanded-keys="[content.contentUUID]"
          />
        </el-row>
        <el-row class="properties" v-show="activeTab == 'target'">
          <div class="qr" v-show="content.target.imgPath">
            <img :src="content.target.imgPath" />
          </div>
          <div class="no-target" v-show="!content.target.imgPath">
            <span>{{ $t('projects.info.target.noImage') }}</span>
          </div>
        </el-row>
      </el-col>
      <!-- 오른쪽 Tabs -->
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
        <el-row v-show="activeTab === 'project'">
          <dl>
            <dt>{{ $t('projects.info.project.name') }}</dt>
            <dd class="content-uuid">{{ content.contentUUID }}</dd>
            <dt>{{ $t('projects.info.project.tracking') }}</dt>
            <dd>{{ content.targetType }}</dd>
            <dt>{{ $t('projects.info.project.mode') }}</dt>
            <dd>
              <div class="column-modes">
                <span>3D+2D</span>
                <span>3D</span>
                <span>2D</span>
              </div>
            </dd>
          </dl>
          <!-- 프로젝트 씬 정보 -->
          <dl class="gray-row-project">
            <div>
              <dt>{{ $t('projects.info.project.sceneGroup') }}</dt>
              <dd>243</dd>
            </div>
            <div>
              <dt>{{ $t('projects.info.project.scene') }}</dt>
              <dd>89</dd>
            </div>
            <div>
              <dt>{{ $t('projects.info.project.object') }}</dt>
              <dd>24</dd>
            </div>
            <div>
              <dt>{{ $t('projects.info.project.asset') }}</dt>
              <dd>12</dd>
            </div>
          </dl>
          <!-- 프로젝트 파일 크기 정보 -->
          <dl class="row">
            <div>
              <dt>{{ $t('projects.info.project.filesize') }}</dt>
              <dd>{{ content.contentSize | byte2mb }}</dd>
              <!-- <dt>{{ $t('contents.info.type') }}</dt>
            <dd></dd> -->
            </div>
            <div>
              <dt>{{ $t('projects.info.project.update') }}</dt>
              <dd>{{ content.createdDate | localTimeFormat }}</dd>
              <!-- <dt>{{ $t('contents.info.device') }}</dt>
            <dd></dd> -->
            </div>
          </dl>
          <!-- 프로젝트 공유/편집 정보 -->
          <projectMemberSelect
            v-for="form in forms"
            :key="form.key"
            :propsKey="form.key"
            :selectLabel="form.name"
            :selectTypes="form.selectTypes"
            :propMemberType="form.memberType"
            :propMembers="form.members"
            :options="options"
            @update:memberType="setMemberType"
            @update:members="setMembers"
            @deleteFirstUser="deleteFirstUser"
          ></projectMemberSelect>
        </el-row>
        <!-- 타겟 정보 -->
        <el-row v-show="activeTab === 'target' && content.target.imgPath">
          <dl>
            <dt>{{ $t('projects.info.target.targetType') }}</dt>
            <dd>
              <span>{{ targetType2label(content.targetType) }}</span>
              <img
                v-if="content.target.imgPath"
                src="~assets/images/icon/ic-print.svg"
                @click="print(content.target.imgPath, content.targetSize)"
              />
              <img
                v-if="content.target.imgPath"
                src="~assets/images/icon/ic-file-download.svg"
                @click="download(content.target.imgPath, content.contentName)"
              />
            </dd>
          </dl>

          <dt>{{ $t('projects.info.target.targetSize') }}</dt>
          <dd>
            <!-- 타겟 이미지 정보 -->
            <dl class="gray-row-target">
              <div>
                <dt>{{ $t('projects.info.target.width') }}:</dt>
                <dd>{{ content.targetSize }} cm</dd>
              </div>
              <div>
                <dt>{{ $t('projects.info.target.height') }}:</dt>
                <dd>{{ content.targetSize }} cm</dd>
              </div>
            </dl>
          </dd>
        </el-row>
        <!-- 활동 정보 -->
        <el-row v-show="activeTab === 'activity'">
          <div
            class="activity"
            v-for="(activity, index) in activityList"
            :key="index"
          >
            <el-col :span="4">
              <VirnectThumbnail :size="36" :image="activity.img" />
            </el-col>
            <el-col :span="21">
              <dl>
                <dt>
                  {{
                    $t('projects.info.activity.nickname', {
                      nickname: activity.nickname,
                    })
                  }}
                </dt>
                <dd>
                  {{
                    $t(`${activityLabel(activity)}`, {
                      member: activity.member,
                    })
                  }}
                </dd>
                <span>
                  {{ activity.updated | localTimeFormat }}
                </span>
              </dl>
              <el-divider />
            </el-col>
          </div>
          <div v-if="!activityList.length">
            <img src="~assets/images/empty/img-content-empty.jpg" />
            <p>{{ $t('projects.info.activity.empty') }}</p>
          </div>
        </el-row>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import contentService from '@/services/content'
import { sharedTypes, editTypes, activityTypes } from '@/models/project/Project'
import filters from '@/mixins/filters'
import utils from '@/mixins/utils'

export default {
  mixins: [filters, utils],
  async asyncData({ params, store }) {
    console.log('params.projectId', params.projectId)
    console.log('storeuuid', store.getters['auth/myProfile'].uuid)

    const promise = {
      content: contentService.getContentInfo(params.projectId),
      properties: contentService.getContentProperties(
        params.projectId,
        store.getters['auth/myProfile'].uuid,
      ),
    }
    return {
      content: await promise.content,
      properties: await promise.properties,
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
      // 공유 드롭메뉴
      sharedTypes,
      // 편집 드롭메뉴
      editTypes,
      // 프로젝트의 공유, 편집 관련된 info.
      activityTypes,
      // ex) selectTypes: 멤버, 지정멤버.. 등 드롭메뉴, members: [선택한 지정 멤버 유저 리스트]
      forms: [
        {
          key: 'shared',
          name: 'projects.info.project.shared',
          selectTypes: [],
          memberType: null,
          members: [],
        },
        {
          key: 'edited',
          name: 'projects.info.project.edit',
          selectTypes: [],
          memberType: null,
          members: [],
        },
      ],
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
      // 지정 멤버 리스트에 들어갈 유저 목록.
      options: [
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'value',
          label: 'User1',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: '버넥트',
          label: 'User2',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: '버넥트짱짱짱짱짱짱짱짱짱짱짱짱',
          label: 'User3',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'Option4',
          label: 'User4',
        },
        {
          img: 'https://192.168.6.3:2838/virnect-platform/profile/2021-06-15_VByqxZGplpVkdlGpucmL.jpg',
          value: 'Option5',
          label: 'User5',
        },
      ],
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
  watch: {
    async activeTab() {
      this.searchTabItems()
    },
  },
  computed: {
    isDirty() {
      return this.form.shared !== this.content.shared
    },
  },
  methods: {
    // TODO: closed 함수 실행 후 이전 페이지로 되돌아갈 때, 페이징 넘버도 유지되는지 확인 필요.
    closed() {
      this.showMe = false
      this.$router.push('/projects')
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
        await contentService.deleteContent([this.content.contentUUID])
        this.$message.success({
          message: this.$t('contents.info.message.deleteSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$router.replace('/projects')
      } catch (errors) {
        const e = errors[0]
        this.$message.error({
          message:
            e.code === 4020
              ? this.$t('contents.info.message.deleteShared')
              : this.$t('contents.info.message.deleteFail') + `\n(${e.msg})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    async update() {
      try {
        await contentService.updateContent(this.content.contentUUID, this.form)
        this.$message.success({
          message: this.$t('contents.info.message.updateSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('contents.info.message.updateFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    async searchTabItems() {
      if (this.activeTab === 'project') {
        // this.contents = (await contentService.searchContents({ size: 4 })).list
      } else if (this.activeTab === 'target') {
        // this.tasks = (await taskService.searchTasks({ size: 4 })).list
      } else if (this.activeTab === 'activity') {
        // this.tasks = (await taskService.searchTasks({ size: 4 })).list
      }
    },
    // 선택한 지정 멤버 리스트에서, 첫 번째 인덱스 멤버를 삭제한다. 라벨의 X 버튼 클릭시 실행.
    deleteFirstUser({ key, members }) {
      if (!members.length) {
        return null
      }
      // key 값을 통해, 공유 / 편집 members 를 구분.
      this.forms.filter(form => form.key == key)[0].members.shift()
    },
    // 자식 컴포넌트에서 forms.memberType data 변경시 실행하는 메소드.
    setMemberType({ key, value }) {
      this.forms.filter(form => form.key == key)[0].memberType = value
    },
    // 자식 컴포넌트에서 forms.members data 변경시 실행하는 메소드.
    setMembers({ key, value }) {
      this.forms.filter(form => form.key == key)[0].members = value
    },
    // 유저의 활동 타입에 따라, 맞는 label 값 반환.
    activityLabel(activity) {
      return activityTypes.find(a => {
        return a.value == activity.value
      }).label
    },
  },
  beforeMount() {
    this.$store.commit('auth/SET_ACTIVE_WORKSPACE', this.content.workspaceUUID)
  },
  mounted() {
    this.activeTab = 'project'
  },
  created() {
    // 공유 sharedType 에서 'ALL' value 를 가진 항목을 없앤 후, 드롭 메뉴 데이터인 forms.selectTypes 에 sharedTypes을 대입한다.
    let types = this.sharedTypes.filter(type => type.value != 'ALL')
    this.forms.map(form => (form.selectTypes = types))
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
