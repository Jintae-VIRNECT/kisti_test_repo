<template>
  <el-dialog
    class="workspace-logo-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.logo.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <p>{{ $t('workspace.onpremiseSetting.logo.desc') }}</p>

      <div class="theme">
        <h5>{{ $t('workspace.onpremiseSetting.logo.workstation') }}</h5>
        <el-upload
          ref="logoUpload"
          action="#"
          accept=".jpg,.png"
          :auto-upload="false"
          :on-change="logoImageSelected"
          :show-file-list="false"
        >
          {{ $t('workspace.onpremiseSetting.favicon.upload') }}
        </el-upload>
      </div>
      <div class="preview">
        <div class="area">
          <span class="editable">
            <img :src="logoFile || defaultlogo" />
          </span>
          <span class="sub-title">
            <el-divider direction="vertical" />
            <VirnectThumbnail
              :size="22"
              :image="activeWorkspace.profile"
              :defaultImage="$defaultWorkspaceProfile"
            />
            <span>{{ activeWorkspace.name }}</span>
          </span>
        </div>
        <div class="tooltip">
          {{ $t('workspace.onpremiseSetting.logo.tooltip') }}
        </div>
      </div>

      <div class="theme">
        <h5>{{ $t('workspace.onpremiseSetting.logo.remote') }}</h5>
        <el-upload
          ref="remoteLogoUpload"
          action="#"
          :auto-upload="false"
          :on-change="remoteLogoImageSelected"
          :show-file-list="false"
        >
          {{ $t('workspace.onpremiseSetting.favicon.upload') }}
        </el-upload>
      </div>
      <div class="preview dark">
        <div class="area">
          <span class="editable">
            <img :src="remoteLogoFile || defaultRemoteLogo" />
          </span>
          <span class="sub-title">
            <el-divider direction="vertical" />
            <VirnectThumbnail
              :size="22"
              :image="activeWorkspace.profile"
              :defaultImage="$defaultWorkspaceProfile"
            />
            <span>{{ activeWorkspace.name }}</span>
          </span>
        </div>
        <div class="tooltip">
          {{ $t('workspace.onpremiseSetting.logo.tooltip') }}
        </div>
      </div>

      <p
        class="caution"
        v-html="$t('workspace.onpremiseSetting.logo.caution')"
      />
    </div>
    <div slot="footer">
      <el-button
        type="text"
        @click="deleteImage"
        :disabled="!logoFile && !remoteLogoFile"
      >
        {{ $t('common.delete') }}
      </el-button>
      <el-button type="primary" @click="submit">
        {{ $t('workspace.onpremiseSetting.logo.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import modalMixin from '@/mixins/modal'
import utilsMixin from '@/mixins/utils'
import workspaceService from '@/services/workspace'
import axios from 'axios'

export default {
  mixins: [modalMixin, utilsMixin],
  data() {
    return {
      defaultlogo: require('assets/images/logo/logo-gnb-ci.png'),
      defaultRemoteLogo: require('assets/images/logo/default_remote.svg'),
      logoFile: null,
      remoteLogoFile: null,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      logo: 'layout/logo',
      remoteLogo: 'layout/remoteLogo',
    }),
  },
  methods: {
    opened() {
      this.logoFile = this.logo
      this.remoteLogoFile = this.remoteLogo
    },
    logoImageSelected(file) {
      if (this.isImageFile(file)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.logoFile = reader.result
        }
      }
    },
    remoteLogoImageSelected(file) {
      if (this.isImageFile(file)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.remoteLogoFile = reader.result
        }
      }
    },
    deleteImage() {
      this.$refs.logoUpload.clearFiles()
      this.$refs.remoteLogoUpload.clearFiles()
      this.logoFile = null
      this.remoteLogoFile = null
    },
    async urlToFile(url) {
      const { data } = await axios({
        url,
        method: 'GET',
        responseType: 'blob',
      })
      return new File([data], 'img.png', { type: data.type })
    },
    async submit() {
      try {
        const logoUploadFiles = this.$refs.logoUpload.uploadFiles
        let logoRaw = logoUploadFiles.length
          ? logoUploadFiles[logoUploadFiles.length - 1].raw
          : null
        const remoteLogoUploadFiles = this.$refs.remoteLogoUpload.uploadFiles
        let remoteLogoRaw = remoteLogoUploadFiles.length
          ? remoteLogoUploadFiles[remoteLogoUploadFiles.length - 1].raw
          : null

        if (!logoRaw && this.logoFile)
          logoRaw = await this.urlToFile(this.logoFile)
        if (!remoteLogoRaw && this.remoteLogoFile)
          remoteLogoRaw = await this.urlToFile(this.remoteLogoFile)

        await workspaceService.setWorkspaceLogo(logoRaw, remoteLogoRaw)
        this.$store.commit('layout/SET_LOGO', {
          logo: this.logoFile,
          remoteLogo: this.remoteLogoFile,
        })
        this.showMe = false
      } catch (e) {
        const message =
          {
            3000: this.$t('common.message.notAllowFileExtension'),
            3001: this.$t('common.message.notAllowFileSize'),
          }[e.code] || e
        this.$message.error({
          message,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .workspace-logo-modal {
  .theme {
    margin-top: 28px;
    text-align: right;
    h5 {
      float: left;
      font-weight: 500;
      font-size: 16px;
    }
    .el-upload {
      color: #0052cc;
      font-weight: 500;
      font-size: 12px;
    }
  }
  .preview {
    height: 60px;

    .area {
      top: 6px;
      left: 9px;
      width: calc(100% - 20px);
      height: 46px;
    }
    .tooltip {
      top: -13px;
      left: 9px;
    }
    .area > span {
      top: 0;
      vertical-align: middle;
    }
    .area > .editable {
      display: inline-block;
      height: 100%;
      padding: 4px;
      img {
        height: 100%;
      }
    }
    .sub-title .virnect-thumbnail {
      display: inline-block;
      margin-right: 4px;
      vertical-align: middle;
    }
  }
  .preview.dark {
    background: #242427;
    .area {
      background: transparent;
    }
    .area:after {
      background: linear-gradient(90deg, rgba(0, 0, 0, 0), #242427);
    }
    .el-divider {
      opacity: 0.5;
    }
    .sub-title {
      color: #f1f1f1;
    }
  }
  .caution {
    margin-top: 30px;
    margin-bottom: 30px;
  }
}
</style>
