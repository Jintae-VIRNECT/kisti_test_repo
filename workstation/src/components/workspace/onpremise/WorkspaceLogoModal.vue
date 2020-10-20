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
      <div class="preview">
        <div class="area">
          <span class="editable">
            <img :src="file || defaultlogo" />
          </span>
          <span class="sub-title">
            <el-divider direction="vertical" />
            <div class="avatar">
              <div
                class="image"
                :style="
                  `background-image: url('${activeWorkspace.profile}'), url('${$defaultWorkspaceProfile}')`
                "
              />
            </div>
            {{ activeWorkspace.name }}
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
      <el-upload
        ref="upload"
        action="#"
        :auto-upload="false"
        :on-change="imageSelected"
        :show-file-list="false"
      >
        <el-button type="info">
          {{ $t('workspace.onpremiseSetting.favicon.upload') }}
        </el-button>
      </el-upload>
      <el-button type="text" @click="deleteImage" :disabled="!file">
        {{ $t('common.delete') }}
      </el-button>
      <el-button type="primary" @click="submit">
        {{ $t('common.update') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'

export default {
  mixins: [modalMixin],
  data() {
    return {
      defaultlogo: require('assets/images/logo/logo-gnb-ci.png'),
      file: null,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      logo: 'layout/logo',
    }),
  },
  methods: {
    opened() {
      this.file = this.logo
    },
    imageSelected(file) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.file = reader.result
      }
    },
    deleteImage() {
      this.$refs.upload.clearFiles()
      this.file = null
    },
    async submit() {
      try {
        const { uploadFiles } = this.$refs.upload
        const raw = uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null
        await workspaceService.setWorkspaceLogo(raw)
        this.$store.commit('layout/SET_LOGO', this.file)
        this.showMe = false
      } catch (e) {
        this.$message.error({
          message: e,
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
  }
  .caution {
    margin-top: 8px;
    margin-bottom: 76px;
  }
}
</style>
