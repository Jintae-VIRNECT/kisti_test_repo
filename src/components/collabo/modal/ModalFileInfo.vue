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
            <span class="table__count" v-if="fileList.length > 0">
              {{ fileList.length }}
            </span>
          </div>

          <div class="table__tools">
            <icon-button
              :text="$t('button.select_download')"
              :select="hasSelect"
              :imgSrc="require('assets/image/ic_down_off.svg')"
              :activeImgSrc="require('assets/image/ic_down_on.svg')"
              :selectImgSrc="require('assets/image/ic_down_on.svg')"
              :colored="true"
              @click="download(type, { selectedArray, fileList })"
            ></icon-button>
            <icon-button
              v-if="deletable"
              :text="$t('button.select_delete')"
              :select="hasSelect"
              :imgSrc="require('assets/image/ic_delete.svg')"
              :activeImgSrc="require('assets/image/ic_delete_select.svg')"
              :selectImgSrc="require('assets/image/ic_delete_on.svg')"
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
