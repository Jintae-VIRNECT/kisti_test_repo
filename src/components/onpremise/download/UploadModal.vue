<template>
  <div>
    <el-dialog
      class="upload-modal onpremise-setting-modal"
      :visible.sync="showMe"
      :close-on-click-modal="false"
      :title="
        $tc('workspace.onpremiseSetting.upload.modal.title', file.category)
      "
      width="440px"
      top="11vh"
    >
      <div>
        <div class="upload-modal__descriptsion">
          <h4>파일명 규칙</h4>
          <ol>
            <li>
              {{ $t('workspace.onpremiseSetting.upload.modal.rule1') }}
            </li>
            <li>
              {{ $t('workspace.onpremiseSetting.upload.modal.rule2') }}
            </li>
          </ol>
          <ul>
            <li>{{ $t('workspace.onpremiseSetting.upload.modal.product') }}</li>
            <li>
              {{ $t('workspace.onpremiseSetting.upload.modal.deviceType') }}
            </li>
            <li>
              {{ $t('workspace.onpremiseSetting.upload.modal.versionCode') }}
            </li>
            <li>
              {{ $t('workspace.onpremiseSetting.upload.modal.extension') }}
            </li>
          </ul>
        </div>
        <el-divider></el-divider>
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item class="horizon" ref="files">
            <template slot="label">
              <span>{{
                $t('workspace.onpremiseSetting.upload.modal.attach')
              }}</span>
            </template>
            <OnpremiseDownloadDragZone
              v-if="visible"
              :file="file"
              :extensionList="file.extensionList"
              @setSubmitDisable="setSubmitDisable"
              @fileTypeError="fileError"
              @fileData="fileData"
            />
          </el-form-item>
        </el-form>
        <p v-if="file.updateStatus === 'ACTIVE'">
          {{ $t('workspace.onpremiseSetting.upload.modal.explanation') }}
        </p>
      </div>
      <div slot="footer">
        <el-button type="primary" @click="submit" :disabled="submitDisabled">
          {{ $t('workspace.onpremiseSetting.upload.modal.submit') }}
        </el-button>
      </div>
    </el-dialog>
    <OnpremiseDownloadProgressModal
      :visible.sync="showProgressModal"
      @cancel="cancelFileUpload"
    />
  </div>
</template>

<script>
import modalMixin from '@/mixins/modal'
import formRulesMixin from '@/mixins/formRules'
import messgeMixin from '@/mixins/message'
import workspaceService from '@/services/workspace'

export default {
  mixins: [modalMixin, formRulesMixin, messgeMixin],
  props: {
    file: {
      type: Object,
      default: () => ({}),
      required: true,
    },
  },
  data() {
    return {
      form: {
        files: [],
        version: '',
      },
      showProgressModal: false,
      submitDisabled: true,
    }
  },
  methods: {
    setSubmitDisable(value) {
      this.submitDisabled = value
    },
    fileError() {
      this.$refs['files'].$el.classList.add('is-error')
    },
    fileData(files) {
      this.$refs['files'].$el.classList.remove('is-error')
      this.form.files = files
    },
    getType(target) {
      return Object.prototype.toString.call(target).slice(8, -1)
    },
    getFileExtension(str) {
      if (this.getType(str) !== 'String') return false

      const array = str.split('.')
      return array[array.length - 1].toUpperCase()
    },
    cancelFileUpload() {
      this.showProgressModal = false
      this.closed()
    },
    opened() {
      this.logoFile = this.logo
      this.whiteLogoFile = this.whiteLogo
    },
    closed() {
      this.$refs.form.resetFields()
      this.$refs.form.clearValidate()
      this.showMe = false
      this.form.files = []
      this.submitDisabled = true
    },
    async submit() {
      try {
        await this.$refs.form.validate()

        if (!this.form.files.length) return this.fileError()
      } catch (e) {
        this.errorMessage(e)
        return false
      }

      try {
        this.showProgressModal = true
        await workspaceService.setWorkspaceDownloadFile({
          ...this.file,
          ...this.form,
        })
        this.$emit('refresh')
        this.successMessage(
          this.$t('workspace.onpremiseSetting.upload.success'),
        )
      } catch (e) {
        this.errorMessage(e)
      }
      this.showProgressModal = false
      this.closed()
    },
  },
  watch: {
    showProgressModal(v) {
      if (v === false) this.showMe = false
    },
  },
}
</script>

<style lang="scss">
#__nuxt {
  .upload-modal {
    .upload-modal__descriptsion span {
      color: var(--color-blue-70);
    }
    .el-dialog__body {
      height: 507px;
    }
    &__descriptsion {
      @include fontLevel(100);
      line-height: 1.71;
      ol {
        margin-left: 15px;
        list-style: decimal;
      }
      ul {
        margin-left: 15px;
        list-style: disc;
      }
    }
    .el-divider--horizontal {
      margin: 12px 0 0 0;
    }
    .el-divider {
      background-color: #e6e9ee;
    }
    .el-form {
      margin: 16px 0;
    }
    .el-form-item {
      margin-bottom: 16px;
    }
    .el-form-item__label {
      margin-bottom: 8px;
      line-height: 20px;
    }
    .is-error .el-input__inner {
      padding: 23px 20px;
      border: solid 2px #f64f4e;
      border-radius: 3px;
    }
  }
}
</style>
