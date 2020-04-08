<template>
  <el-dialog
    class="image-change-modal"
    :title="$t('profile.imageChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.imageChangeModal.desc')"></p>
      <el-upload
        ref="upload"
        action="#"
        :auto-upload="false"
        :on-change="imageSelected"
        :show-file-list="false"
        drag
      >
        <div class="avatar">
          <img v-if="file" :src="file" />
        </div>
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
import profileService from '@/services/profile'

export default {
  mixins: [dialogMixin],
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
      return this.file === this.$props.me.image
    },
  },
  watch: {
    visible() {
      this.file = this.$props.me.image
    },
  },
  methods: {
    imageSelected(file) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.file = reader.result
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
        profile: uploadFiles[uploadFiles.length - 1].raw,
      }
      try {
        await profileService.updateMyImage(form)
        this.$notify.success({
          message: this.$t('profile.imageChangeModal.message.success'),
          position: 'bottom-left',
        })
        this.$emit('changedImage', this.file)
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: this.$t('profile.imageChangeModal.message.fail'),
          position: 'bottom-left',
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
  .avatar {
    width: 160px;
    height: 160px;
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
