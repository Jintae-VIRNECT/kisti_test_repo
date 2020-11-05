<template>
  <modal
    :visible.sync="visibleFlag"
    :title="$t('workspace.record_file')"
    :showClose="true"
    :width="'67.1429rem'"
    :height="'50.5714rem'"
    :beforeClose="beforeClose"
    :class="'record-list-modal'"
  >
    <div class="record-list">
      <div class="record-list__paragraph">
        <p
          class="paragraph--text"
          v-html="$t('workspace.record_description1')"
        ></p>
        <p
          class="paragraph--text"
          v-html="$t('workspace.record_description2')"
        ></p>
        <p
          class="paragraph--text"
          v-html="$t('workspace.record_description3')"
        ></p>
      </div>

      <div class="record-list__table">
        <remote-table
          :showToggleHeader="true"
          :headers="headers"
          :columns="columns"
          :datas="datas"
          :showTools="true"
          :renderOpts="getRenderOpts()"
          :emptyText="$t('workspace.record_nofile')"
        >
          <div class="table__header">
            <div class="table__title">
              {{ $t('workspace.record_file_title') }}
            </div>
            <div class="table__tools">
              <icon-button
                v-if="isHome && onpremise"
                :text="'선택 업로드'"
                :imgSrc="require('assets/image/ic_upload.svg')"
                :customClass="{
                  highlight: hasSelect,
                  'custom-local-record': true,
                }"
                @click="upload"
              ></icon-button>
              <icon-button
                :text="$t('workspace.record_download')"
                :imgSrc="require('assets/image/ic_download.svg')"
                :customClass="{
                  highlight: hasSelect,
                  'custom-local-record': true,
                }"
                @click="download"
              ></icon-button>
              <icon-button
                :text="$t('workspace.record_remove')"
                :imgSrc="require('assets/image/ic_delete.svg')"
                :customClass="{
                  highlight: hasSelect,
                  'custom-local-record': true,
                }"
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
import JSZip from 'jszip'
import FileSaver from 'file-saver'
import { RUNTIME, RUNTIME_ENV } from 'configs/env.config'

export default {
  name: 'LocalRecordList',
  data() {
    return {
      visibleFlag: false,
      columns: ['fileName', 'playTime', 'fileSize', 'roomName'],
      datas: [],
      selectedArray: [],
      zipName: 'vremote_rec.zip',
    }
  },
  computed: {
    headers() {
      return [
        this.$t('workspace.record_filename'),
        this.$t('workspace.record_time'),
        this.$t('workspace.record_size'),
        this.$t('workspace.record_remote'),
      ]
    },
    hasSelect() {
      return this.selectedArray.some(select => select)
    },
    isHome() {
      return this.$route.path === '/home'
    },
    onpremise() {
      return RUNTIME.ONPREMISE === RUNTIME_ENV ? true : false
    },
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
          FileSaver.saveAs(chunks[0].blob, chunks[0].fileName)
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
      const uuids = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          uuids.push(this.datas[index].uuid)
          this.datas[index] = null
        }
      })

      this.datas = this.datas.filter(obj => obj !== null)

      IDBHelper.deleteMediaChunk(uuids)
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },

    async upload() {
      const uuids = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          uuids.push(this.datas[index].uuid)
        }
      })

      if (uuids.length > 0) {
        this.$eventBus.$emit('fileupload:show', uuids)
      }
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

        let hText = this.$t('date.hour')
        let mText = this.$t('date.minute')
        let sText = this.$t('date.second')

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

        return `${hours}${hText} ${minutes}${mText} ${seconds}${sText}`
      }

      const fileSizeRender = size => {
        const mb = 1048576

        if (size >= mb) {
          size = size / 1024 / 1024
          return `${size.toFixed(1)}MB`
        } else {
          size = size / 1024
          return `${size.toFixed(1)}KB`
        }
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
    await IDBHelper.initIDB()
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
  border-bottom: 1px solid rgba(#808080, 0.27);
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
  &:before {
    content: '\00B7\00a0';
  }
}
.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 1rem 0px;
}

.table__title {
  color: #dedede;
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
