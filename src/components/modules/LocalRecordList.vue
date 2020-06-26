<template>
  <modal
    :visible.sync="visibleFlag"
    :title="'로컬 녹화 파일'"
    :showClose="true"
    :width="'67.1429rem'"
    :height="'50.5714rem'"
    :beforeClose="beforeClose"
    :class="'record-list-modal'"
  >
    <div class="record-list">
      <div class="record-list__paragraph">
        <p class="paragraph--text">
          ᛫ 완료된 로컬 녹화 파일은 사용하시는 PC의 시스템에 귀속되어 있습니다.
        </p>
        <p class="paragraph--text">
          ᛫ <strong>[다운로드]</strong> 시, 다운로드 경로는 브라우저의 설정을
          따릅니다.
        </p>
        <p class="paragraph--text">
          ᛫ 브라우저의 Private 모드로 로컬 녹화를 진행할 경우, 브라우저를 닫으면
          녹화 파일은 자동 삭제됩니다.
        </p>
      </div>

      <div class="record-list__table">
        <remote-table
          :showToggleHeader="true"
          :headers="headers"
          :columns="columns"
          :datas="datas"
          :showTools="true"
          :renderOpts="getRenderOpts()"
          :emptyText="'녹화 파일이 없습니다.'"
        >
          <div class="table__header">
            <div class="table__title">
              {{ tableTitle }}
            </div>
            <div class="table__tools">
              <icon-button
                text="선택 다운로드"
                :imgSrc="require('assets/image/ic_download.svg')"
                @click="download"
              ></icon-button>
              <icon-button
                text="선택 삭제"
                :imgSrc="require('assets/image/ic_delete.svg')"
                @click="deleteItems"
              ></icon-button>
            </div>
          </div>
        </remote-table>
      </div>
    </div>
  </modal>
</template>
<script>
import Modal from 'Modal'
import RemoteTable from 'RemoteTable'
import IconButton from 'IconButton'
import IDBHelper from 'utils/idbHelper'
import RecordRTC from 'recordrtc'
import JSZip from 'jszip'
import FileSaver from 'file-saver'

export default {
  name: 'LocalRecordList',
  data() {
    return {
      visibleFlag: false,
      headers: ['파일명', '녹화된 시간', '파일 용량', '협업 이름'],
      columns: ['fileName', 'playTime', 'fileSize', 'roomName'],
      datas: [],
      tableTitle: '로컬 녹화 파일 리스트',
      selectedArray: [],
      zipName: 'vremote_rec.zip',
    }
  },
  components: {
    Modal,
    RemoteTable,
    IconButton,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    async visible(flag) {
      this.visibleFlag = flag
      if (flag) {
        this.datas = await this.getList()
      }
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },

    async download() {
      const uuids = []
      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          uuids.push(this.datas[index].uuid)
        }
      })

      const chunks = await IDBHelper.getMediaChunkArrays(uuids)
      if (chunks && chunks.length > 0) {
        if (chunks.length === 1) {
          RecordRTC.invokeSaveAsDialog(chunks[0].blob, chunks[0].fileName)
        } else {
          this.downloadZip(chunks)
        }
      }
    },
    downloadZip(chunks) {
      const zip = new JSZip()

      chunks.forEach(chunk => {
        zip.file(chunk.fileName, chunk.blob)
      })

      zip.generateAsync({ type: 'blob' }).then(content => {
        FileSaver.saveAs(content, this.zipName)
      })
    },
    deleteItems() {
      console.log('deleteItems :: called')

      const uuids = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          uuids.push(this.datas[index].uuid)
          this.datas[index] = null
        }
      })

      this.datas = this.datas.filter(obj => {
        return obj !== null
      })

      IDBHelper.deleteMediaChunk(uuids)
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },

    async getList() {
      const dataHandler = record => {
        if (record.playTime <= 1) {
          record.playTime = 0
          record.fileSize = 0
          record.disable = true
        } else {
          record.disable = false
        }
        return record
      }

      return await IDBHelper.getDataWithUserId(this.account.uuid, dataHandler)
    },

    //for record render
    getRenderOpts() {
      const renderOpts = []

      //display h hour m min s sec
      const playTimeRender = playTime => {
        let sec_num = Number.parseInt(playTime, 10)
        let hours = Math.floor(sec_num / 3600)
        let minutes = Math.floor((sec_num - hours * 3600) / 60)
        let seconds = sec_num - hours * 3600 - minutes * 60

        let hText = '시간'
        let mText = '분'
        let sText = '초'

        if (hours === 0 && minutes === 0 && seconds < 1) {
          hours = ''
          hText = ''

          minutes = ''
          mText = ''

          seconds = '0'
        } else {
          if (hours === 0) {
            hours = ''
            hText = ''
          }
          if (minutes === 0) {
            minutes = ''
            mText = ''
          }
          if (seconds === 0) {
            seconds = ''
            sText = ''
          }
        }

        return hours + hText + minutes + mText + seconds + sText
      }

      const fileSizeRender = fileSize => {
        const fileSizeMB = (fileSize / 1024 / 1024).toFixed(1)
        return `${fileSizeMB}MB`
      }

      renderOpts.push(
        {
          column: 'playTime',
          render: playTimeRender,
        },
        { column: 'fileSize', render: fileSizeRender },
      )

      return renderOpts
    },
  },
  mounted() {
    this.$eventBus.$on('table:selectedarray', this.refreshSelectedArray)
  },
  async created() {
    // await IDBHelper.initIDB()
  },
  beforeDestroy() {
    this.$eventBus.$off('table:selectedarray')
  },
}
</script>

<style lang="scss">
.modal.record-list-modal .modal--inner {
  border: none;
  border-radius: 0.4286rem;
}
.modal.record-list-modal .modal--header {
  border-bottom: 1px solid rgba(128, 128, 128, 0.27);
  border-radius: 0.4286rem 0.4286rem 0px 0px;
}

.record-list {
  width: 100%;
  height: 100%;
  padding: 0.5714rem 1.5714rem 1.5714rem 1.5714rem;
}

.record-list__paragraph {
  margin: 0px 0px 3.1429rem 0px;
}

.paragraph--text {
  color: #b7b7b7;
  font-size: 1rem;
  & > strong {
    color: #b7b7b7;
    font-weight: 500;
  }
}
.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 1rem 0px;
}

.table__title {
  color: #dedede;
  font-weight: normal;
  font-size: 1.2857rem;
}

.table__tools {
  display: flex;

  > button.icon-button {
    opacity: 0.6;

    &:hover {
      opacity: 1;
    }
    &:active {
      opacity: 0.8;
    }
  }
}
</style>
