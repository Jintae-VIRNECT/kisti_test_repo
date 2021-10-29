<template>
  <button class="mobile-upload-btn" @click="upload">
    <input
      type="file"
      ref="uploadFile"
      style="display:none;"
      accept="image/gif,image/bmp,image/jpeg,image/png,application/pdf"
      @change="fileChangeHandler"
    />
  </button>
</template>

<script>
import shareFileUploadMixin from 'mixins/shareFileUpload'

export default {
  mixins: [shareFileUploadMixin],
  methods: {
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file, () => this.$emit('uploaded'))
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
