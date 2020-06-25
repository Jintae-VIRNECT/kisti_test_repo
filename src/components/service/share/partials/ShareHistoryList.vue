<template>
  <div class="share-body">
    <div class="share-body__history">
      <vue2-scrollbar>
        <ol class="upload-list">
          <history-image
            v-for="img of historyList"
            :key="img.id"
            :imgInfo="img"
            @selected="addSelect"
            @unSelected="delSelect"
          ></history-image>
        </ol>
      </vue2-scrollbar>
      <button
        @click="download"
        class="share-save btn small"
        :class="{
          disabled: disabled,
        }"
      >
        <span>저장하기</span>
      </button>
    </div>
  </div>
</template>

<script>
import HistoryImage from './ShareHistoryImage'
import { mapGetters } from 'vuex'
import { base64urlToBlob } from 'utils/blobs'
import JSZip from 'jszip'
import FileSaver from 'file-saver'
import confirmMixin from 'mixins/confirm'
import mimeTypes from 'mime-types'

export default {
  name: 'ShareHistoryList',
  mixins: [confirmMixin],
  components: {
    HistoryImage,
  },
  data() {
    return {
      selected: [],
      zipName: 'vremote_img.zip',
    }
  },
  computed: {
    ...mapGetters(['historyList', 'shareFile']),
    disabled() {
      if (this.selected.length > 0) {
        return false
      } else {
        return true
      }
    },
  },
  watch: {},
  methods: {
    download() {
      if (!this.disabled) {
        this.confirmCancel(
          '해당 저작된 이미지를 저장하시겠습니까?​',
          {
            text: '확인',
            action: this.save,
          },
          {
            text: '취소',
          },
        )
      }
    },
    async save() {
      const downFile = []

      //convert to blob(file)
      if (this.selected.length > 0) {
        for (const his of this.historyList) {
          //find selected file
          for (const selId of this.selected) {
            if (his.id === selId) {
              const spts = his.img.split(',')
              const dataType = spts[0]
                .replace('data:', '')
                .replace(';base64', '')

              const file = await base64urlToBlob(
                his.img,
                dataType,
                his.fileName,
              )
              downFile.push(file)
            }
          }
        }
        if (downFile.length === 1) {
          FileSaver.saveAs(downFile[0], downFile[0].name)
        } else {
          this.downloadZip(downFile)
        }
      }
    },

    addSelect(imgId) {
      this.selected.push(imgId)
    },
    delSelect(imgId) {
      const idx = this.selected.findIndex(id => {
        return id === imgId
      })

      this.selected.splice(idx, 1)
    },
    downloadZip(files) {
      const zip = new JSZip()

      const fileNameMap = new Map()
      files.forEach(file => {
        if (fileNameMap.has(file.name)) {
          const spts = file.name.split('.')
          let dupName = ''

          if (spts.length >= 2) {
            //for file name duplication
            //asdf.aa.bb.cc.jpg -> get asdf.aa.bb.cc
            const name = file.name.slice(
              0,
              file.name.length - (spts[spts.length - 1].length + 1),
            )

            //increase file count
            //a.jpg a (1).jpg
            dupName = `${name} (${fileNameMap.get(file.name)}).${
              spts[spts.length - 1]
            }`
          } else {
            dupName = `${name} (${fileNameMap.get(
              file.name,
            )}).${mimeTypes.extension(file.type)}`
          }

          zip.file(dupName, file)

          fileNameMap.set(file.name, fileNameMap.get(file.name) + 1)
        } else {
          fileNameMap.set(file.name, 1)
          if (file.name.split('.').length === 1) {
            const fullName = `${file.name}.${mimeTypes.extension(file.type)}`
            zip.file(fullName, file)
          } else {
            zip.file(file.name, file)
          }
        }
      })

      zip.generateAsync({ type: 'blob' }).then(content => {
        FileSaver.saveAs(content, this.zipName)
      })
    },
  },
}
</script>
