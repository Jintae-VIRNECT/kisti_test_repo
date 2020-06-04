<template>
  <div class="share-body">
    <vue2-scrollbar>
      <ol class="upload-list">
        <li>
          <button class="upload-list__button" @click="addFileClick">
            파일추가
          </button>
          <input
            type="file"
            ref="uploadFile"
            style="display:none;"
            accept="image/gif,image/jpeg,image/png,application/pdf"
            @change="fileChangeHandler"
          />
        </li>
        <template v-for="(file, idx) of sharingList">
          <sharing-pdf
            v-if="file.pdf"
            :key="'sharing' + idx"
            :fileInfo="file"
            @pdfView="id => $emit('pdfView', id)"
          ></sharing-pdf>
          <sharing-image
            v-else
            :key="'sharing' + idx"
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
const maxFileSize = 1024 * 1024 * 20
export default {
  name: 'ShareFileList',
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
  watch: {},
  methods: {
    ...mapActions(['addFile']),
    addFileClick() {
      this.$refs['uploadFile'].click()
    },
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file)
    },
    loadFile(file) {
      if (file) {
        if (file.size > maxFileSize) {
          alert(this.$t('service.call_upload_text2'))
          this.clearUploadFile()
          return false
        }

        if (
          ['image/jpeg', 'image/png', 'image/bmp', 'application/pdf'].includes(
            file.type,
          )
        ) {
          const docItem = {
            id: Date.now(),
            filedata: '',
            pages: new Array(1),
            loaded: 1,
          }
          console.log(file)

          docItem.filedata = file
          docItem.loaded = 0
          docItem.pdf = false
          if (file.type === 'application/pdf') {
            docItem.pdf = true
          }
          this.addFile(docItem)
          // this.sharingList.push(docItem)
        } else {
          alert(this.$t('service.call_upload_text3'))
          return false
        }
      }
    },
    clearUploadFile() {
      this.$refs['uploadFile'].value = ''
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
