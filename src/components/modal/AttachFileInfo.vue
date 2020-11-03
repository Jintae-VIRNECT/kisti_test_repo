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
        :emptyText="''"
      >
        <div class="table__header">
          <div class="table__title">
            {{ $t('file.attach_file') }}
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

import { getFileDownloadUrl, deleteFileItem } from 'api/http/file'

import confirmMixin from 'mixins/confirm'
import { downloadByURL } from 'utils/file'

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
      columns: [['name'], ['size'], ['uploader'], ['expirationDate']],
    }
  },
  computed: {
    headers() {
      return [
        this.$t('file.name'),
        this.$t('file.size'),
        this.$t('file.upload_member'),
        this.$t('file.download_period'),
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
      const downloadFiles = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          downloadFiles.push(this.fileList[index])
        }
      })

      for (const file of downloadFiles) {
        try {
          const data = await getFileDownloadUrl({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })

          downloadByURL(data)
          console.log(data)
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
          const result = await deleteFileItem({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })
          console.log(result)
        } catch (e) {
          console.error(e)
          errorFiles.push(file.name)
        }
      }
      console.log(errorFiles)
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
