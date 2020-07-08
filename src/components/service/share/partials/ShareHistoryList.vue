<template>
  <div class="share-body">
    <div class="share-body__history">
      <vue2-scrollbar>
        <ol class="upload-list">
          <history-image
            v-for="img of historyList"
            :key="img.id"
            :imgInfo="img"
            :selected="selected.findIndex(id => id === img.id) > -1"
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
import JSZip from 'jszip'
import FileSaver from 'file-saver'
import { base64urlToBlob } from 'utils/blobs'
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
      if (this.selected.length === 0) return

      if (this.selected.length === 1) {
        const history = this.historyList.find(
          history => history.id === this.selected[0],
        )
        FileSaver.saveAs(history.img, history.fileName)
        this.selected = []
        return
      }

      //convert to blob(file)
      for (const his of this.historyList) {
        //find selected file
        for (const selId of this.selected) {
          if (his.id === selId) {
            const spts = his.img.split(',')
            const dataType = spts[0].replace('data:', '').replace(';base64', '')
            const file = await base64urlToBlob(his.img, dataType, his.fileName)
            downFile.push(file)
          }
        }
      }
      this.downloadZip(downFile)
      this.selected = []
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
      const nameMap = new Map()

      files.forEach(file => {
        const fileName = file.name
        let fullName = ''
        const checkDup = nameMap.has(fileName)

        if (checkDup) {
          const count = nameMap.get(fileName)
          fullName = this.getFileName(fileName, count)
          nameMap.set(fileName, count + 1)
        } else {
          const noExt = fileName.split('.').length === 1

          if (noExt) {
            const ext = mimeTypes.extension(file.type)
            fullName = this.getFileName(fileName, 0, ext)
          } else {
            fullName = fileName
          }
          nameMap.set(fileName, 1)
        }

        zip.file(fullName, file)
      })

      zip.generateAsync({ type: 'blob' }).then(content => {
        FileSaver.saveAs(content, this.zipName)
      })
    },
    getFileName(fileName, count, ext) {
      let fullName = ''

      if (count) {
        const idx = fileName.lastIndexOf('.')
        const name = fileName.slice(0, idx)
        const ext = fileName.slice(idx)
        fullName = `${name} (${count})${ext}`
      } else {
        const name = fileName
        fullName = `${name}.${ext}`
      }

      return fullName
    },
  },
}
</script>
