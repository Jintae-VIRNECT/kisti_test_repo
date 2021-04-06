<template>
  <div class="share-body__file">
    <vue2-scrollbar>
      <ol class="upload-list">
        <li>
          <button class="upload-list__button" @click="addFileClick()">
            {{ $t('service.file_add') }}
          </button>
          <input
            type="file"
            ref="uploadFile"
            style="display:none;"
            accept="image/gif,image/jpeg,image/png,application/pdf"
            @change="fileChangeHandler"
          />
        </li>
        <template v-for="file of sharingList">
          <sharing-pdf
            v-if="file.pdf"
            :key="'sharing_' + file.id"
            :fileInfo="file"
            @pdfView="pdfView(file)"
          ></sharing-pdf>
          <sharing-image
            v-else
            :key="'sharing_' + file.id"
            :fileInfo="file"
          ></sharing-image>
        </template>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import SharingImage from './SharingImage'
import SharingPdf from './SharingPdf'
import toastMixin from 'mixins/toast'
const maxFileSize = 1024 * 1024 * 20
export default {
  name: 'ShareFileList',
  mixins: [toastMixin],
  components: {
    SharingImage,
    SharingPdf,
  },
  data() {
    return {
      // sharingList: [],
    }
  },
  computed: {
    ...mapGetters({
      sharingList: 'fileList',
    }),
  },
  methods: {
    ...mapActions(['addFile']),
    pdfView(file) {
      this.$emit('pdfView', { id: file.id, name: file.filedata.name })
    },
    addFileClick(file) {
      if (file) {
        this.loadFile(file)
      } else {
        this.$refs['uploadFile'].click()
      }
    },
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file)
    },
    loadFile(file) {
      if (file) {
        if (file.size > maxFileSize) {
          this.toastError(this.$t('service.file_maxsize'))
          this.clearUploadFile()
          return false
        }

        if (
          [
            'image/jpeg',
            'image/png',
            'image/bmp',
            'image/gif',
            'application/pdf',
          ].includes(file.type)
        ) {
          const docItem = {
            id: Date.now(),
            filedata: '',
            document: null,
            loaded: 1,
          }

          docItem.filedata = file
          docItem.loaded = 0
          docItem.pdf = file.type === 'application/pdf'
          this.addFile(docItem)
          this.clearUploadFile()
          // this.sharingList.push(docItem)
        } else {
          this.toastError(this.$t('service.file_type'))
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('addFile', this.addFileClick)
  },
  beforeDestroy() {
    this.$eventBus.$off('addFile', this.addFileClick)
  },
}
</script>
