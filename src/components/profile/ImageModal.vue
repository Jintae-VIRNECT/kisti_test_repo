<template>
  <el-dialog
    class="image-change-modal"
    :title="$t('profile.imageChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p
        v-html="
          $isOnpremise
            ? $t('profile_op.imageChangeModal.desc')
            : $t('profile.imageChangeModal.desc')
        "
      />
      <el-upload
        ref="upload"
        action="#"
        :auto-upload="false"
        :on-change="imageSelected"
        :show-file-list="false"
        accept=".jpg,.png"
        drag
      >
        <VirnectThumbnail :size="160" :image="file" />
        <div class="el-upload__tip" slot="tip">
          {{ $t('profile.imageChangeModal.caution') }}
        </div>
      </el-upload>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="info" @click="uploadImage">
        {{ $t('profile.imageChangeModal.upload') }}
      </el-button>
      <el-button type="text" @click="deleteImage" :disabled="!file">
        {{ $t('profile.imageChangeModal.delete') }}
      </el-button>
      <el-button type="primary" @click="submit" :disabled="disabled">
        {{ $t('profile.imageChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import filterMixin from '@/mixins/filters'
import profileService from '@/services/profile'

export default {
  mixins: [filterMixin, dialogMixin],
  props: {
    me: Object,
  },
  data() {
    return {
      file: null,
    }
  },
  computed: {
    disabled() {
      return this.file === this.cdn(this.$props.me.profile)
    },
  },
  watch: {
    visible() {
      this.file = this.cdn(this.$props.me.profile)
    },
  },
  methods: {
    /**
     * @author YongHo Kim <yhkim@virnect.com>
     * @description 선택한 파일의 조건을 확인하고 조건에 부합하는 파일이라면 true를 반환, 아니라면 false를 반환
     * @param {object} file
     * @returns {boolean} 조건에 부합하는 파일인지 확인하고 결과를 리턴
     */
    checkSelectdFile(file) {
      const isImage =
        file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
      let message = ''
      if (!isImage) {
        message = this.$t(
          'profile.imageChangeModal.message.notAllowFileExtension',
        )
        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
        return false
      }

      const isLimitSize = file.raw.size / 1024 / 1024 < 5 // 서버에서 제한한 파일의 크기 5MB
      if (!isLimitSize) {
        message = this.$t('profile.imageChangeModal.message.notAllowFileSize')
        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
        return false
      }

      return isImage && isLimitSize
    },
    imageSelected(file) {
      if (this.checkSelectdFile(file)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.file = reader.result
        }
      }
    },
    uploadImage() {
      document
        .querySelector('.image-change-modal .el-upload')
        .dispatchEvent(new Event('click'))
    },
    deleteImage() {
      this.$refs.upload.clearFiles()
      this.file = null
    },
    async submit() {
      const { uploadFiles } = this.$refs.upload
      const form = {
        profile: uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null,
      }
      try {
        await profileService.updateMyImage(form)
        this.$notify.success({
          message: this.$t('profile.imageChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedImage', this.file)
      } catch (e) {
        let message =
          this.$t('profile.imageChangeModal.message.fail') + `\n(${e})`
        if (/^Error: 4005/.test(e))
          message = this.$t(
            'profile.imageChangeModal.message.notAllowFileExtension',
          )
        if (/^Error: 4006/.test(e))
          message = this.$t('profile.imageChangeModal.message.notAllowFileSize')

        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.image-change-modal {
  .el-upload {
    display: block;
    margin-top: 24px;
  }
  .el-upload-dragger {
    width: inherit;
    height: inherit;
    border: none;
  }
  .virnect-thumbnail {
    margin: 16px auto;
  }
  .el-upload__tip {
    margin-bottom: 16px;
    color: $font-color-desc;
    font-size: 13px;
    text-align: center;
  }
  .el-button--info {
    float: left;
  }
  .el-button--text {
    color: $font-color-content;
  }
}
</style>
