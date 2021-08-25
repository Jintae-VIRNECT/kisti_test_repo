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
      <!-- 콘텐츠 구성 정보 -->
      <el-col :span="15">
        <div class="actionbar">
          <el-button @click="remove" type="text" :disabled="!true">
            <img src="~assets/images/icon/ic-arrow-back.svg" />
          </el-button>
          <el-button @click="remove" type="text" :disabled="!true">
            <img src="~assets/images/icon/ic-arrow-forward.svg" />
          </el-button>
          <el-divider direction="vertical"></el-divider>
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
        </div>

        <div class="properties">
          <el-tree
            :data="properties"
            :props="propertiesProps"
            node-key="id"
            :default-expanded-keys="[content.contentUUID]"
          />
        </div>
      </el-col>
      <!-- 콘텐츠 정보 -->
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
        <dl class="sceneRow">
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
        <el-divider />
        <dl class="select">
          <dt>{{ $t('projects.info.project.shared') }}</dt>
          <dd class="virnect-workstation-form">
            <el-select v-model="form.shared" popper-class="select-shared">
              <el-option
                v-for="status in sharedStatus"
                :key="status.value"
                :value="status.value"
                :label="$t(status.label)"
              />
            </el-select>
            <el-button type="is-disabled">
              {{ $t('common.apply') }}
            </el-button>
          </dd>
          <dt>{{ $t('projects.info.project.selectMember') }}</dt>
          <dd class="virnect-workstation-form">
            <el-select
              v-model="form.shared"
              placeholder="$t('projects.info.project.searchMember')"
              popper-class="select-shared"
            >
              <el-option
                v-for="status in sharedStatus"
                :key="status.value"
                :value="status.value"
                :label="$t(status.label)"
              />
            </el-select>
            <el-button type="is-disabled">
              {{ $t('common.apply') }}
            </el-button>
          </dd>
        </dl>
        <el-divider />
        <dl class="select">
          <dt>{{ $t('projects.info.project.edit') }}</dt>
          <dd class="virnect-workstation-form">
            <el-select v-model="form.shared" popper-class="select-shared">
              <el-option
                v-for="status in sharedStatus"
                :key="status.value"
                :value="status.value"
                :label="$t(status.label)"
              />
            </el-select>
            <el-button type="is-disabled">
              {{ $t('common.apply') }}
            </el-button>
          </dd>
          <dt>{{ $t('projects.info.project.selectMember') }}</dt>
          <dd class="virnect-workstation-form">
            <el-select
              v-model="form.shared"
              :placeholder="$t('projects.info.project.searchMember')"
              popper-class="select-shared"
            >
              <el-option
                v-for="status in sharedStatus"
                :key="status.value"
                :value="status.value"
                :label="$t(status.label)"
              />
            </el-select>
            <el-button type="is-disabled">
              {{ $t('common.apply') }}
            </el-button>
          </dd>
        </dl>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import contentService from '@/services/content'
import { sharedStatus } from '@/models/content/Content'
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
      activeTab: '',
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
      sharedStatus,
      form: {
        shared: null,
      },
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
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
    closed() {
      this.showMe = false
      this.$router.push('/projects')
    },
    registerTask() {
      this.$router.replace(`/tasks/new?contentId=${this.content.contentUUID}`)
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
        this.content.shared = this.form.shared
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
  },
  beforeMount() {
    this.form.shared = this.content.shared
    this.$store.commit('auth/SET_ACTIVE_WORKSPACE', this.content.workspaceUUID)
  },
  mounted() {
    this.activeTab = 'project'
  },
}
</script>
