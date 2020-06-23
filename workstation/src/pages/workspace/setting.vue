<template>
  <div id="workspace-setting">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{
            $t('menu.workspaceSetting')
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{
            $t('workspace.info.title')
          }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('workspace.info.title') }}</h2>
      </div>
      <el-row>
        <!-- 왼쪽 -->
        <el-col class="container__left">
          <el-card class="el-card--table">
            <workspace-info />
          </el-card>
        </el-col>
        <!-- 가운데 -->
        <el-col class="container__center">
          <el-card>
            <div slot="header">
              <h3>{{ $t('workspace.setting.title') }}</h3>
            </div>
            <div>
              <el-row>
                <el-col class="left">
                  <dl>
                    <dt>{{ $t('workspace.setting.createdDate') }}</dt>
                    <dd>{{ activeWorkspace.createdDate | dateFormat }}</dd>
                    <dt>{{ $t('workspace.master') }}</dt>
                    <dd class="column-user">
                      <div class="avatar">
                        <div
                          class="image"
                          :style="
                            `background-image: url(${activeWorkspace.masterProfile})`
                          "
                        />
                      </div>
                      <span>{{ activeWorkspace.masterName }}</span>
                    </dd>
                  </dl>
                </el-col>
                <el-col class="right">
                  <el-form
                    ref="form"
                    class="virnect-workstation-form"
                    :model="form"
                    @submit.native.prevent="submit"
                  >
                    <el-form-item
                      class="horizon"
                      :label="$t('workspace.setting.image')"
                    >
                      <el-upload
                        ref="upload"
                        action="#"
                        :auto-upload="false"
                        :on-change="imageSelected"
                        :show-file-list="false"
                        drag
                      >
                        <div class="avatar">
                          <div
                            class="image"
                            :style="
                              `background-image: url('${file || defaultFile}')`
                            "
                          />
                          <i>
                            <img src="~assets/images/icon/ic-camera-alt.svg" />
                          </i>
                        </div>
                        <div class="el-upload__tip" slot="tip">
                          <ul v-html="$t('workspace.setting.imageComment')" />
                        </div>
                      </el-upload>
                    </el-form-item>
                    <el-form-item
                      class="horizon"
                      :label="$t('workspace.setting.name')"
                    >
                      <el-input
                        v-model="form.name"
                        :disabled="activeWorkspace.role !== 'MASTER'"
                        :placeholder="
                          $t('workspace.setting.namePlaceholder', {
                            nickname: myProfile.nickname,
                          })
                        "
                        :maxlength="30"
                      />
                      <span v-if="activeWorkspace.role === 'MASTER'">{{
                        $t('workspace.setting.nameComment')
                      }}</span>
                    </el-form-item>
                    <el-form-item
                      class="horizon"
                      :label="$t('workspace.setting.desc')"
                    >
                      <el-input
                        type="textarea"
                        v-model="form.description"
                        :disabled="activeWorkspace.role !== 'MASTER'"
                        :placeholder="$t('workspace.setting.descPlaceholder')"
                        :maxlength="40"
                        :show-word-limit="true"
                      />
                      <span v-if="activeWorkspace.role === 'MASTER'">{{
                        $t('workspace.setting.descComment')
                      }}</span>
                    </el-form-item>
                    <el-button
                      type="primary"
                      @click="submit"
                      v-if="activeWorkspace.role === 'MASTER'"
                    >
                      {{ $t('workspace.setting.update') }}
                    </el-button>
                    <el-button @click="showAddModal = true" v-else>
                      {{ $t('workspace.setting.leave') }}
                    </el-button>
                  </el-form>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <workspace-leave-modal
      :visible.sync="showAddModal"
      :activeWorkspace="activeWorkspace"
      :myProfile="myProfile"
    />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import WorkspaceInfo from '@/components/workspace/WorkspaceInfo'
import WorkspaceLeaveModal from '@/components/workspace/WorkspaceLeaveModal'
import filters from '@/mixins/filters'
import workspaceService from '@/services/workspace'

export default {
  mixins: [filters],
  components: {
    WorkspaceInfo,
    WorkspaceLeaveModal,
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
  data() {
    return {
      file: null,
      defaultFile: require('assets/images/workspace-profile.png'),
      form: {
        name: '',
        description: '',
      },
      showAddModal: false,
    }
  },
  methods: {
    imageSelected(file) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.file = reader.result
      }
    },
    async submit() {
      const { uploadFiles } = this.$refs.upload
      const form = {
        ...this.form,
        profile: uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null,
        userId: this.myProfile.uuid,
        workspaceId: this.activeWorkspace.uuid,
      }

      try {
        await workspaceService.updateWorkspaceInfo(form)
        this.$message.success({
          message: this.$t('workspace.setting.message.updateSuccess'),
          duration: 2000,
          showClose: true,
        })
      } catch (e) {
        this.$message.error({
          message: this.$t('workspace.setting.message.updateFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    setWorkspaceInfo() {
      this.form.name = this.activeWorkspace.name
      this.form.description = this.activeWorkspace.description
      this.file = this.activeWorkspace.profile
    },
  },
  beforeMount() {
    this.setWorkspaceInfo()
    workspaceService.watchActiveWorkspace(this, this.setWorkspaceInfo)
  },
}
</script>

<style lang="scss">
#workspace-setting .container__center.el-col-24 {
  width: 732px;

  .el-card__body {
    padding: 0;
  }
  .left.el-col-24 {
    width: 240px;
    padding: 28px 30px;
  }
  .right.el-col-24 {
    width: 490px;
    padding: 28px 50px;
  }
  dt {
    margin-bottom: 4px;
    color: #445168;
    font-size: 13px;
  }
  dd {
    margin-bottom: 24px;
    font-size: 15px;
  }
  dd.column-user {
    margin-top: 6px;
    span {
      font-size: 16px;
    }
  }
  .el-button {
    margin: 42px 0 12px;
  }
  .el-input__inner {
    height: 38px;
  }
  .el-textarea__inner {
    height: 80px;
  }
  .el-textarea .el-input__count {
    background: none;
  }
}
</style>
