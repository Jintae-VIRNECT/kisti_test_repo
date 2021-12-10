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
        <p v-html="getDescription()" class="upload-modal__descriptsion"></p>
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
              :formatList="getFormatList(file.format)"
              @fileTypeError="fileError"
              @fileData="fileData"
            />
          </el-form-item>
          <el-form-item class="horizon" prop="version">
            <template slot="label">
              <span>{{
                $t('workspace.onpremiseSetting.upload.modal.version')
              }}</span>
            </template>
            <el-input
              class="full"
              v-model="form.version"
              :placeholder="
                $t('workspace.onpremiseSetting.upload.modal.placeholder')
              "
            />
          </el-form-item>
        </el-form>
        <p>
          {{ $t('workspace.onpremiseSetting.upload.modal.explanation') }}
        </p>
      </div>
      <div slot="footer">
        <el-button type="primary" @click="submit">
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
    }
  },
  methods: {
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
    getDescription() {
      let result = ''
      if (this.getType(this.file.format) === 'Undefined') return result

      let format = ''
      if (this.getType(this.file.format) === 'Array') {
        this.file.format.forEach(item => {
          format += ` <span>${this.getFileExtension(item)}</span>,`
        })
        format = format.slice(0, -1)
      } else if (this.getType(this.file.format) === 'String') {
        format += `<span>${this.getFileExtension(this.file.format)}</span>`
      }

      result = this.$t('workspace.onpremiseSetting.upload.modal.description', {
        category: `<span>${this.file.category}</span>`,
        format: format,
      })
      return result
    },
    getFileExtension(str) {
      const array = str.split('.')
      return array[array.length - 1].toUpperCase()
    },
    getFormatList(value) {
      let setData = null
      if (this.getType(value) === 'Array') {
        setData = value.map(val => val.slice(val.indexOf('.') + 1))
      } else {
        setData = [this.getFileExtension(value)]
      }
      return setData
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
        this.showProgressModal = false
        this.closed()
        this.$emit('refresh')
        this.successMessage(
          this.$t('workspace.onpremiseSetting.upload.success'),
        )
      } catch (e) {
        this.me
        console.log(e)
      }
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
      line-height: 20px;
      margin-bottom: 8px;
    }
    .is-error .el-input__inner {
      border-radius: 3px;
      border: solid 2px #f64f4e;
      padding: 23px 20px;
    }
  }
}
</style>
