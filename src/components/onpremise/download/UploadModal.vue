<template>
  <div>
    <el-dialog
      class="upload-modal onpremise-setting-modal"
      :visible.sync="showMe"
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
          <el-form-item class="horizon" prop="file" required>
            <template slot="label">
              <span>{{
                $t('workspace.onpremiseSetting.upload.modal.attach')
              }}</span>
            </template>
            <OnpremiseDownloadDragZone
              v-if="visible"
              :files="setFormat(file.format)"
              @fileData="fileData"
            />
          </el-form-item>
          <el-form-item class="horizon" prop="version" required>
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
          {{ $t('workspace.onpremiseSetting.logo.submit') }}
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
export default {
  mixins: [modalMixin, formRulesMixin],
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
    fileData(files) {
      files.map(v => {
        console.log(v.fileName)
      })
      // api 기다리는 중
    },
    getDescription() {
      let result = ''

      if (this.file.format === undefined) return result

      let format = ''

      this.file.format.forEach(item => {
        format += ` <span>${item
          .slice(item.indexOf('.') + 1)
          .toUpperCase()}</span>,`
      })
      format = format.slice(0, -1)

      result = this.$t('workspace.onpremiseSetting.upload.modal.description', {
        category: `<span>${this.file.category}</span>`,
        format: format,
      })
      return result
    },
    setFormat(str) {
      let setData = null
      if (typeof str === 'object') {
        const arr = []
        for (let val of str) {
          arr.push(val.slice(val.indexOf('.') + 1))
        }
        setData = arr
      } else {
        const format = str.slice(str.indexOf('.') + 1)
        setData = format.toUpperCase()
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
      this.$refs.form.clearValidate()
      this.showMe = false
    },
    async submit() {
      // 유효성 검사
      try {
        // api 연동 작업 시 진행
        //await this.$refs.form.validate()
      } catch (e) {
        return false
      }

      try {
        this.showProgressModal = true
      } catch (e) {
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
