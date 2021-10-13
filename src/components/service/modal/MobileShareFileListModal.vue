<template>
  <full-screen-modal
    v-show="modalShow"
    :visible="true"
    :title="$t('service.file_list')"
    @close="close"
  >
    <transition name="mobile-file-list__left">
      <ul v-show="!pdfFile || !pdfFile.id" class="mobile-file-list">
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
    </transition>
    <transition name="mobile-file-list__right">
      <pdf-view
        v-show="pdfFile && pdfFile.id"
        :file="pdfFile"
        @back="backToFileList"
      ></pdf-view>
    </transition>
  </full-screen-modal>
</template>

<script>
import SharingImage from '../share/partials/SharingImage'
import SharingPdf from '../share/partials/SharingPdf'
import PdfView from '../share/partials/SharePdfView'

import FullScreenModal from 'FullScreenModal'
export default {
  components: { FullScreenModal, SharingImage, SharingPdf, PdfView },
  props: {
    modalShow: {
      type: Boolean,
      dafault: true,
    },
    fileList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      pdfFile: {},
    }
  },
  methods: {
    close() {
      this.$emit('update:modalShow', false)
      //this.$emit('update:visible', false)
    },
    shareImage() {
      this.close()
    },
    pdfView(file) {
      this.pdfFile = {
        id: file.objectName,
        name: file.name,
        objectName: file.objectName,
        contentType: file.contentType,
      }
    },
    backToFileList() {
      this.pdfFile = {}
    },
    addPdfHistory(data) {
      console.log(this.$refs['sharing_' + data.objectName][0])
      this.$refs['sharing_' + data.objectName][0].addPdfHistory(data.index + 1)
      this.backToFileList()
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

.mobile-file-list__left-enter-active,
.mobile-file-list__left-leave-active,
.mobile-file-list__right-enter-active,
.mobile-file-list__right-leave-active {
  transition: all ease 0.4s;
}
.mobile-file-list__left-enter {
  left: -100%;
  opacity: 1;
}
.mobile-file-list__left-enter-to {
  left: 0;
  opacity: 1;
}
.mobile-file-list__left-leave {
  left: 0;
  opacity: 1;
}
.mobile-file-list__left-leave-to {
  left: -100%;
  opacity: 0;
}
.mobile-file-list__right-enter {
  left: 100%;
  opacity: 0;
}
.mobile-file-list__right-enter-to {
  left: 0;
  opacity: 1;
}
.mobile-file-list__right-leave {
  left: 0;
  opacity: 1;
}
.mobile-file-list__right-leave-to {
  left: 100%;
  opacity: 0;
}
</style>
