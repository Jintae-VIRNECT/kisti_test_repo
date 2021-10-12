<template>
  <full-screen-modal
    :visible="visible"
    :title="$t('service.file_list')"
    @close="close"
  >
    <ul class="mobile-file-list">
      <template v-for="file of fileList">
        <sharing-pdf
          v-if="file.contentType === 'application/pdf'"
          :ref="'sharing_' + file.objectName"
          :key="'sharing_' + file.objectName"
          :fileInfo="file"
          @pdfView="pdfView(file)"
        ></sharing-pdf>
        <sharing-image
          v-else
          :key="'sharing_' + file.objectName"
          :fileInfo="file"
          @shareImage="shareImage"
        ></sharing-image>
      </template>
    </ul>
  </full-screen-modal>
</template>

<script>
import SharingImage from '../share/partials/SharingImage'
import SharingPdf from '../share/partials/SharingPdf'

import FullScreenModal from 'FullScreenModal'
export default {
  components: { FullScreenModal, SharingImage, SharingPdf },
  props: {
    visible: {
      type: Boolean,
      dafault: true,
    },
    fileList: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    close() {
      this.$emit('update:visible', false)
    },
    shareImage() {
      this.close()
    },
  },
}
</script>

<style lang="scss" scoped>
.mobile-file-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: auto;
  padding: 1.8rem 1.6rem;
  column-gap: 0.2rem;
  row-gap: 1.3rem;
}
</style>
