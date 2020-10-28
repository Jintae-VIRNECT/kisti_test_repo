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
        :showToggleHeader="true"
        :showPlayButton="false"
        :headers="headers"
        :columns="columns"
        :datas="fileList"
        :renderOpts="getRenderOpts()"
        :emptyText="'데이터가 없습니다.'"
      >
        <div class="table__header">
          <div class="table__title">
            첨부 파일
          </div>
          <div class="table__tools">
            <icon-button
              :text="'선택 다운로드'"
              :imgSrc="require('assets/image/ic_down_off.svg')"
              :active="hasSelect"
              :activeImgSrc="require('assets/image/ic_down_on.svg')"
              @click="download"
            ></icon-button>
            <icon-button
              :text="'선택 삭제'"
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

import FileSaver from 'file-saver'
import { downloadRecordFile } from 'api/remote/record'

import confirmMixin from 'mixins/confirm'

export default {
  name: 'AttachFileInfo',
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
  },
  data() {
    return {
      visibleFlag: false,
      // toggleAllFlag: false,
      selectedArray: [],
      columns: ['name', 'size', 'uploader', 'expirationDate'],
    }
  },
  computed: {
    headers() {
      return [
        '파일명',
        '파일 용량',
        '업로드 멤버',
        '다운로드 기간',
        // this.$t('workspace.record_filename'),
        // this.$t('workspace.record_time'),
        // this.$t('workspace.record_size'),
        // this.$t('workspace.record_remote'),
      ]
    },
    hasSelect() {
      return this.selectedArray.some(select => select)
    },
  },
  watch: {
    async visible(flag) {
      // this.initSelectedArray()
      this.visibleFlag = flag
    },
  },

  methods: {
    async download() {
      for (const file of this.selectedFiles) {
        try {
          const data = await downloadRecordFile({
            id: file.recordingId,
          })

          FileSaver.saveAs(
            new Blob([data], {
              type: data.type,
            }),
            file.filename,
          )
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
        console.log(file)
        try {
          throw 'delete file'
          // await deleteRecordFile({
          //   id: recordingId,
          // })
        } catch (e) {
          console.error(e)
          errorFiles.push(file.name)
        }
      }
      console.log(errorFiles)
      if (errorFiles.length > 0) {
        this.confirmDefault(
          `이미 삭제되었거나 존재하지 않은 파일입니다.\n <p> ${errorFiles.join(
            '\n',
          )}</p>`,
        )
      }
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },
    //for record render
    getRenderOpts() {
      const renderOpts = []

      const expirationDateRender = date => {
        return this.$dayjs(date).format('YYYY.MM.DD')
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
        { column: 'expirationDate', render: expirationDateRender },
        { column: 'size', render: fileSizeRender },
      )

      return renderOpts
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },

  mounted() {
    this.$eventBus.$on('table:selectedarray', this.refreshSelectedArray)
  },
  beforeDestroy() {
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
