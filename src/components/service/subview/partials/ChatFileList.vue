<template>
  <div class="chat-list">
    <vue2-scrollbar
      ref="chatFileListScrollbar"
      v-if="fileList.length > 0"
      :allowReset="false"
    >
      <div>
        <chat-file-item
          v-for="(file, idx) in fileList"
          :key="idx"
          :checked="file.checked"
          :file="file"
          @toggle="selectFile(file, idx)"
        ></chat-file-item>
      </div>
    </vue2-scrollbar>
    <div v-else class="chat-list__empty">
      <div class="chat-list__empty-inner">
        <img :src="noFileImgSrc" />
        <p>{{ $t('common.no_sharing_file') }}</p>
      </div>
    </div>
    <chat-file-down :fileList="selectedList" @clear="clear"></chat-file-down>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ChatFileItem from './ChatFileItem'
import ChatFileDown from './ChatFileDownload'
import { getFileList } from 'api/http/file'
export default {
  name: 'ChatFileList',
  components: {
    ChatFileItem,
    ChatFileDown,
  },
  data() {
    return {
      //test datas
      fileList: [
        // {
        //   id: 'dd',
        //   name: '20200311_설계도면.txt',
        //   ext: 'txt',
        //   checked: true,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
      ],
      selectedList: [],
    }
  },
  props: {
    show: {
      type: Boolean,
    },
  },
  computed: {
    ...mapGetters(['view', 'roomInfo']),
    noFileImgSrc() {
      if (this.isMobileSize) return require('assets/image/img_nofile_new.svg')
      else return require('assets/image/img_nofile.svg')
    },
  },
  watch: {
    show(val) {
      if (val) {
        this.init()
      }
    },
  },

  methods: {
    async init() {
      const res = await getFileList({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      res.fileInfoList.forEach(fileInfo => {
        const idx = this.fileList.findIndex(
          file => file.objectName === fileInfo.objectName,
        )
        if (idx === -1) {
          this.fileList.unshift(fileInfo)
        }
      })
      this.fileList.sort((a, b) => {
        return new Date(b.createdDate) - new Date(a.createdDate)
      })
      this.$nextTick(() => {
        if (this.$refs['chatFileListScrollbar']) {
          this.$refs['chatFileListScrollbar'].calculateSize()
        }
      })
    },
    selectFile(file, idx) {
      const checked = this.fileList[idx].checked
      if (checked) {
        const idx = this.selectedList.findIndex(
          selected => selected.path === file.path,
        )
        if (idx < 0) return
        this.selectedList.splice(idx, 1)
      } else {
        this.selectedList.push(file)
      }
      this.fileList[idx].checked = !checked
    },
    clear() {
      this.selectedList = []
      this.fileList.map(fileInfo => {
        fileInfo['checked'] = false
        return fileInfo
      })
    },
  },

  mounted() {
    this.init()
    if (this.$refs['chatFileListScrollbar']) {
      this.$refs['chatFileListScrollbar'].scrollToY(99999)
    }
    // this.chatFileList.push({
    //   id: 'cc',
    //   name: '1가나다라마바사아자차카타파하긴텍스트',
    //   ext: 'video',
    //   checked: false,
    //   validDate: '2020. 03. 14.',
    //   fileSize: '1MB',
    //   isValid: false,
    // })
    // this.chatFileList.push({
    //   id: 'bb',
    //   name: '2가나다라마바사아자차카타파하긴텍스트',
    //   ext: 'video',
    //   checked: false,
    //   validDate: '2020. 03. 14.',
    //   fileSize: '1MB',
    //   isValid: false,
    // })
    // this.chatFileList.push({
    //   id: 'aa',
    //   name: '3가나다라마바사아자차카타파하긴텍스트',
    //   ext: 'video',
    //   checked: false,
    //   validDate: '2020. 03. 14.',
    //   fileSize: '1MB',
    //   isValid: false,
    // })
  },
}
</script>

<style></style>
