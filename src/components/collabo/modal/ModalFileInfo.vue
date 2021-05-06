<template>
  <modal
    :title="title"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :width="'75.2143rem'"
    :height="'50.4286rem'"
    customClass="file-info"
  >
    <div class="file-list">
      <file-table
        @play="openPlayModal"
        :showToggleHeader="showToggleHeader"
        :showPlayButton="showPlayButton"
        :headers="getHeader(type)"
        :columns="getColumns(type)"
        :datas="fileList"
        :renderOpts="getRenderer(type)"
        :emptyText="''"
        :type="type"
      >
        <div class="table__header">
          <div class="table__title">
            {{ tableTitle }}
            <span class="table__count" v-if="fileList.length > 0">
              {{ fileList.length }}
            </span>
            <!-- <span class="table__description" v-if="type === 'server'">{{
              $t('file.server_record_description')
            }}</span> -->
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

import fileInfoMixin from 'mixins/fileInfo'

import { getLocalRecordFileUrl, getServerRecordFileUrl } from 'api/http/file'

export default {
  name: 'ModalFileInfo',
  mixins: [fileInfoMixin],
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
