<template>
  <modal
    :title="title"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :width="'64.2857rem'"
    :height="'50.4286rem'"
    customClass="modal-server-record-info"
  >
    <div class="file-list">
      <file-table
        @play="openPlayModal"
        :showToggleHeader="true"
        :showPlayButton="true"
        :headers="headers"
        :columns="columns"
        :datas="fileList"
        :renderOpts="getRenderOpts()"
        :emptyText="''"
      >
        <div class="table__header">
          <div class="table__title">
            {{ $t('file.server_record') }}
          </div>
          <div class="table__tools">
            <icon-button
              :text="$t('button.select_download')"
              :imgSrc="require('assets/image/ic_down_off.svg')"
              :active="hasSelect"
              :activeImgSrc="require('assets/image/ic_down_on.svg')"
              @click="download"
            ></icon-button>
            <icon-button
              v-if="deletable"
              :text="$t('button.select_delete')"
              :imgSrc="require('assets/image/ic_delete.svg')"
              :active="hasSelect"
              @click="deleteItems"
            ></icon-button>
          </div>
        </div>
      </file-table>
    </div>
  </modal>
</template>

<script>
import Modal from 'components/modules/Modal'
import FileTable from 'FileTable'
import IconButton from 'components/modules/IconButton'
// import { URLS } from 'configs/env.config'

// import FileSaver from 'file-saver'
import {
  getServerRecordFileUrl,
  deleteServerRecordFileItem,
} from 'api/http/file'

import confirmMixin from 'mixins/confirm'
import { convertProxyUrl } from 'utils/file'

export default {
  name: 'ServerRecordInfo',
  mixins: [confirmMixin],
  components: {
    FileTable,
    IconButton,
    Modal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: '',
    },
    fileList: {
      type: Array,
      default: () => [],
    },
    deletable: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: false,
      // toggleAllFlag: false,
      selectedArray: [],
      columns: [['filename'], ['duration'], ['size']],
    }
  },
  computed: {
    headers() {
      return [
        this.$t('file.name'),
        this.$t('file.record_time'),
        this.$t('file.size'),
      ]
    },
    hasSelect() {
      return this.selectedArray.some(select => select)
    },
  },
  watch: {
    async visible(flag) {
      this.visibleFlag = flag
    },
  },

  methods: {
    async openPlayModal(index) {
      const file = this.fileList[index]

      const url = await getServerRecordFileUrl({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        id: file.recordingId,
      })

      // this.$eventBus.$emit('open::player', url)
      this.$eventBus.$emit('open::player', convertProxyUrl(url))
    },
    async download() {
      const downloadFiles = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          downloadFiles.push(this.fileList[index])
        }
      })
      console.log(downloadFiles)
      for (const file of downloadFiles) {
        try {
          console.log(file)
          const url = await getServerRecordFileUrl({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          })

          const a = document.createElement('a')
          // a.href = url
          a.href = convertProxyUrl(url)
          a.setAttribute('type', 'application/octet-stream')
          a.setAttribute('download', file.filename)
          a.click()
        } catch (e) {
          console.error(e)
        }
      }
    },
    async deleteItems() {
      const deleteFiles = []
      const errorFiles = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          deleteFiles.push(this.fileList[index])
        }
      })

      for (const file of deleteFiles) {
        try {
          await deleteServerRecordFileItem({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          })
        } catch (e) {
          console.error(e)
          errorFiles.push(file.filename)
        }
      }
      if (errorFiles.length > 0) {
        this.confirmDefault(
          `${this.$t('confirm.file_not_found')}\n <p> ${errorFiles.join(
            '\n',
          )}</p>`,
        )
      }
      this.$eventBus.$emit('reload::list')
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },
    //for record render
    getRenderOpts() {
      const renderOpts = []

      //display h hour m min s sec
      const playTimeRender = playTime => {
        console.log('playTime:', playTime)
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
          column: 'duration',
          render: playTimeRender,
        },
        { column: 'size', render: fileSizeRender },
      )

      return renderOpts
    },
    beforeClose() {
      this.$emit('update:visible', false)
      this.$eventBus.$emit('close::record-list')
    },
  },

  mounted() {
    this.$eventBus.$on('table:selectedarray', this.refreshSelectedArray)
  },
  beforeDestroy() {
    // this.selectedArray = []
    this.$eventBus.$off('table:selectedarray')
  },
}
</script>

<style lang="scss">
.modal.modal-server-record-info {
  .modal--header {
    height: 68px;
    padding: 22px 0px 16px 27px;
    color: rgb(11, 31, 72);
    font-weight: normal;

    font-size: 16px;
    background-color: #f8f8fa;
  }

  .modal--body {
    padding: 40px 45px 45px 45px;
  }

  .modal--close {
    width: 14px;
    height: 14px;
  }
}

.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 1rem 0px;
}

.table__title {
  color: rgb(11, 31, 72);
  font-weight: normal;
  font-size: 22px;
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
