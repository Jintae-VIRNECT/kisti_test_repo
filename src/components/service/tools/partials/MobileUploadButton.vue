<template>
  <button class="mobile-upload-btn" @click="upload">
    <input
      type="file"
      ref="uploadFile"
      style="display:none;"
      :accept="acceptType"
      @change="fileChangeHandler"
    />
  </button>
</template>

<script>
import shareFileUploadMixin from 'mixins/shareFileUpload'
import { FILE_TYPE } from 'configs/remote.config'
export default {
  name: 'MobileUploadButton',
  mixins: [shareFileUploadMixin],
  props: {
    fileType: {
      type: String,
      default: FILE_TYPE.SHARE,
    },
  },
  computed: {
    acceptType() {
      if (this.fileType === FILE_TYPE.SHARE) {
        return `image/gif,image/bmp,image/jpeg,image/png,application/pdf`
      } else {
        return ''
      }
    },
  },
  methods: {
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.$emit('uploading', file.name)
      this.loadFile(file, () => this.$emit('uploaded'), this.fileType)
    },
    upload() {
      this.$refs['uploadFile'].click()
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-upload-btn {
  @include mobile-circle-btn($new_color_bg_button_sub2);
  @include mobile-circle-btn-icon(
    '~assets/image/call/mdpi_icon_upload_new.svg'
  );
}
</style>
