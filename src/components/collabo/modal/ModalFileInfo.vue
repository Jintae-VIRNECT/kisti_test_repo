<template>
  <modal
    :title="title"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :width="width"
    :height="height"
    customClass="file-info"
  >
    <div class="file-list">
      <file-table
        @play="openPlayModal"
        :showToggleHeader="showToggleHeader"
        :showPlayButton="showPlayButton"
        :headers="headers"
        :columns="columns"
        :datas="fileList"
        :renderOpts="renderOpts"
        :emptyText="''"
        :type="type"
      >
        <div class="table__header">
          <div class="table__title">
            {{ tableTitle }}
          </div>
          <div class="table__tools">
            <icon-button
              :text="$t('button.select_download')"
              :imgSrc="require('assets/image/ic_down_off.svg')"
              :active="hasSelect"
              :activeImgSrc="require('assets/image/ic_down_on.svg')"
              @click="download(type, { selectedArray, fileList })"
            ></icon-button>
            <icon-button
              v-if="deletable"
              :text="$t('button.select_delete')"
              :imgSrc="require('assets/image/ic_delete.svg')"
              :active="hasSelect"
              @click="deleteFile(type, { selectedArray, fileList })"
            ></icon-button>
          </div>
        </div>
      </file-table>
    </div>
  </modal>
</template>

<script>
import Modal from 'components/modules/Modal'
import IconButton from 'components/modules/IconButton'
import FileTable from 'FileTable'
import { proxyUrl } from 'utils/file'

import { getLocalRecordFileUrl, getServerRecordFileUrl } from 'api/http/file'

export default {
  name: 'ModalFileInfo',

  components: {
    FileTable,
    IconButton,
    Modal,
  },
  props: {
    type: {
      type: String,
    },
    visible: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: '',
    },
    tableTitle: {
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
    columns: {
      type: Array,
    },
    download: {
      type: Function,
    },
    deleteFile: {
      type: Function,
    },
    renderOpts: {
      type: Array,
    },
    headers: {
      type: Array,
    },
    height: {
      type: String,
    },
    width: {
      type: String,
    },
    showToggleHeader: {
      type: Boolean,
    },
    showPlayButton: {
      type: Boolean,
    },
  },
  data() {
    return {
      visibleFlag: false,
      selectedArray: [],
    }
  },
  computed: {
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
    beforeClose() {
      this.$emit('update:visible', false)
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },
    async openPlayModal(index) {
      let params = {}
      let data = null
      let url = ''

      const file = this.fileList[index]

      switch (this.type) {
        case 'local':
          params = {
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          }
          data = await getLocalRecordFileUrl(params)
          url = data.url
          break
        case 'server':
          params = {
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          }
          url = await getServerRecordFileUrl(params)
      }

      this.$eventBus.$emit('open::player', proxyUrl(url))
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
.modal.file-info {
  .modal--header {
    height: 4.8571rem;
    padding: 1.5714rem 0rem 1.1429rem 1.9286rem;
    color: #0b1f48;
    font-weight: normal;

    font-size: 1.1429rem;
    background-color: #f8f8fa;
  }

  .modal--body {
    padding: 2.8571rem 3.2143rem 3.2143rem 3.2143rem;
  }

  .modal--close {
    width: 1rem;
    height: 1rem;
  }
}

.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 1rem 0px;
}

.table__title {
  color: #0b1f48;
  font-weight: normal;
  font-size: 1.5714rem;
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
