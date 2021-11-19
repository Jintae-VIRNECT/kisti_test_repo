<template>
  <div>
    <el-dialog
      class="upload-modal onpremise-setting-modal"
      :visible.sync="showMe"
      title="iOS 설치 파일 등록"
      width="440px"
      top="11vh"
    >
      <div>
        <p class="upload-modal__descriptsion">
          <span>iOS</span> 설치 파일 등록 시 <span>IPA</span>,
          <span>PLIST</span> 파일과<br />버전 정보를 등록해주세요
        </p>
        <el-divider></el-divider>
        <el-form ref="form" :model="form" :rules="rules">
          <el-form-item class="horizon" prop="file" required>
            <template slot="label">
              <span>파일첨부</span>
            </template>
            <OnpremiseDownloadDragZone fileType="ipa" />
          </el-form-item>
          <el-form-item class="horizon" prop="version" required>
            <template slot="label">
              <span>버전정보</span>
            </template>
            <el-input
              class="full"
              v-model="form.version"
              placeholder="버전 정보를 입력해 주세요 (e.g. X.X.XX)"
            />
          </el-form-item>
        </el-form>
        <p>
          설치 파일 업로드를 완료하면 워크스페이스 멤버들에게 자동 업데이트
          알림이 전송됩니다.
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
    fileName: {
      type: String,
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
