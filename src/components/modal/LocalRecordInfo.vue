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
            {{ $t('file.local_record') }}
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

import confirmMixin from 'mixins/confirm'
import tableMixin from 'mixins/table'
import { getLocalRecordFileUrl } from 'api/http/file' //삭제 api 추가 필요함.
import { downloadByURL, proxyUrl } from 'utils/file'

export default {
  name: 'LocalRecordInfo',
  mixins: [confirmMixin, tableMixin],
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
      columns: [
        ['name'],
        ['durationSec'],
        ['size'],
        ['fileUserInfo', 'nickname'],
      ],
    }
  },
  computed: {
    headers() {
      return [
        this.$t('file.name'),
        this.$t('file.record_time'),
        this.$t('file.size'),
        this.$t('file.record_member'),
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
    async openPlayModal(index) {
      const file = this.fileList[index]

      const data = await getLocalRecordFileUrl({
        objectName: file.objectName,
        sessionId: file.sessionId,
        userId: this.account.uuid,
        workspaceId: file.workspaceId,
      })

      this.$eventBus.$emit('open::player', proxyUrl(data.url))
    },
    async download() {
      const downloadFiles = []

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          downloadFiles.push(this.fileList[index])
        }
      })

      for (const file of downloadFiles) {
        try {
          const data = await getLocalRecordFileUrl({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })
          console.log('download::getLocalRecordFileUrl', data)
          downloadByURL(data)
          console.log(data)
        } catch (e) {
          console.error(e)
        }
      }
    },
    async deleteItems() {
      // const deleteFiles = []
      // const errorFiles = []
      // this.selectedArray.forEach((selected, index) => {
      //   if (selected) {
      //     deleteFiles.push(this.fileList[index])
      //   }
      // })
      // for (const file of deleteFiles) {
      //   console.log(file)
      //   try {
      //     throw 'delete file'
      //     // await deleteRecordFile({
      //     //   id: recordingId,
      //     // })
      //   } catch (e) {
      //     console.error(e)
      //     errorFiles.push(file.name)
      //     this.confirmDefault(
      //       `${this.$t('confirm.file_not_found')}\n <p> ${file.name}</p>`,
      //     )
      //   }
      // }
      // if (errorFiles.length > 0) {
      //   this.confirmDefault(
      //     `${this.$t('confirm.file_not_found')}\n <p> ${errorFiles.join(
      //       '\n',
      //     )}</p>`,
      //   )
      // }
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },
    //for record render
    getRenderOpts() {
      const renderOpts = []
      renderOpts.push(
        {
          column: 'durationSec',
          render: this.playTimeRender,
        },
        { column: 'size', render: this.fileSizeRender },
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
