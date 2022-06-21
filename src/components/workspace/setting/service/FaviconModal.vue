<template>
  <el-dialog
    class="workspace-favicon-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.favicon.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <p>{{ $t('workspace.onpremiseSetting.favicon.desc') }}</p>
      <div class="preview">
        <img src="~assets/images/workstation-title-example.png" />
        <div class="area">
          <span class="editable">
            <img :src="file || defaultFavicon" />
          </span>
        </div>
        <div class="tooltip">
          {{ $t('workspace.onpremiseSetting.favicon.tooltip') }}
        </div>
      </div>
      <p
        class="caution"
        v-html="$t('workspace.onpremiseSetting.favicon.caution')"
      />
    </div>
    <div slot="footer">
      <el-upload
        ref="upload"
        action="#"
        accept=".jpg,.png,.ico"
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
      <el-button type="primary" @click="submit" :disabled="submitDisabled">
        {{ $t('workspace.onpremiseSetting.favicon.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import utilsMixin from '@/mixins/utils'
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'

export default {
  mixins: [modalMixin, utilsMixin],
  data() {
    return {
      defaultFavicon: require('assets/images/logo/favicon.png'),
      file: null,
      submitDisabled: true,
    }
  },
  computed: {
    ...mapGetters({ favicon: 'layout/favicon' }),
  },
  methods: {
    opened() {
      this.file = this.favicon
    },
    closed() {
      this.submitDisabled = true
    },
    imageSelected(file) {
      if (this.isImageFile(file, 3)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.file = reader.result
          this.submitDisabled = false
        }
      } else {
        this.submitDisabled = true
        this.$refs.upload.clearFiles()
      }
    },
    deleteImage() {
      this.$refs.upload.clearFiles()
      this.file = null
      this.submitDisabled = false
    },
    async submit() {
      try {
        const { uploadFiles } = this.$refs.upload
        const raw = uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null
        await workspaceService.setWorkspaceFavicon(raw)
        this.$store.commit('layout/SET_FAVICON', this.file)
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
#__nuxt .workspace-favicon-modal {
  .preview {
    img {
      width: 100%;
      height: 100%;
    }
    .area {
      top: 11px;
      left: 15px;
      width: auto;
    }
    .tooltip {
      top: -8px;
      left: 15px;
    }
    .area > span {
      top: 0;
    }
    .area > .editable {
      display: block;
      width: 26px;
      height: 26px;
    }
    .area::after {
      display: none;
    }
    .editable {
      padding: 3px;
    }
  }

  .caution {
    margin-top: 8px;
    margin-bottom: 76px;
  }
}
</style>
