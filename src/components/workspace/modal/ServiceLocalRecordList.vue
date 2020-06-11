<template>
  <modal
    :visible.sync="visibleFlag"
    :title="'로컬 녹화 파일'"
    :showClose="true"
    :width="940"
    :height="708"
    :beforeClose="beforeClose"
  >
    <div class="record-list">
      <div class="record-list__paragraph">
        <p class="paragraph--text">
          ᛫ 완료된 로컬 녹화 파일은 사용하시는 PC의 시스템에 귀속되어 있습니다.
        </p>
        <p class="paragraph--text">
          ᛫ 공용 PC의 경우, 다른 계정에 의해서 녹화된 파일도 보일 수 있습니다.
        </p>
        <p class="paragraph--text">
          ᛫ 중요한 녹화 파일의 경우, [다운로드]를 하신 후 파일 리스트를 삭제하여
          주시기 바랍니다.
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
  name: 'ServiceLocalRecordList',
  data() {
    return {
      visibleFlag: false,
      headers: ['파일명', '녹화된 시간', '파일 용량', '녹화 계정'],
      columns: ['fileName', 'playTime', 'fileSize', 'accountName'],
      datas: [],
      tableTitle: '로컬 녹화 파일 리스트',
      selectedArray: [],
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
      console.log('download :: called')

      const uuids = []
      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          uuids.push(this.datas[index].uuid)
        }
      })

      let chunks = await IDBHelper.getMediaChunkArrays(uuids)
      if (chunks && chunks.length > 0) {
        if (chunks.length === 1) {
          RecordRTC.invokeSaveAsDialog(chunks[0].blob, chunks[0].fileName)
        } else {
          let zip = new JSZip()

          chunks.forEach(chunk => {
            zip.file(chunk.fileName, chunk.blob)
          })

          zip.generateAsync({ type: 'blob' }).then(function(content) {
            FileSaver.saveAs(content, 'recorded.zip')
          })
        }
      }
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
      console.log('너 업데이트 되는거임?', selectedArray)
      this.selectedArray = selectedArray
    },

    async getList() {
      return await IDBHelper.getAllDataArray()
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
        return hours + hText + minutes + mText + seconds + sText
      }

      const fileSizeRender = fileSize => {
        let fileSizeMB = (fileSize / 1024 / 1204).toFixed(1)

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
  updated() {},
  mounted() {
    console.log('mounted')
    this.$eventBus.$on('table:selectedarray', this.refreshSelectedArray)
  },
  async created() {
    await IDBHelper.initIDB()
  },
  beforeDestroy() {
    console.log('service local record beforeDestroy')
    this.$eventBus.$off('table:selectedarray')
  },
}
</script>

<style lang="scss">
.record-list {
  width: 100%;
  height: 100%;
  padding: 8px 22px 22px 22px;
}

.record-list__paragraph {
  margin: 0px 0px 44px 0px;
}

.paragraph--text {
  color: #b7b7b7;
  font-size: 14px;
}
.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 14px 0px;
}

.table__title {
  color: #dedede;
  font-weight: normal;
  font-size: 18px;
}

.table__tools {
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
