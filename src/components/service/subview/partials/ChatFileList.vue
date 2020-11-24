<template>
  <div class="chat-list">
    <vue2-scrollbar ref="chatFileListScrollbar" v-if="fileList.length > 0">
      <div>
        <chat-file-item
          v-for="(file, idx) in fileList"
          :key="idx"
          :checked="file.checked"
          :fileInfo="file"
          @toggle="selectFile(file, idx)"
        ></chat-file-item>
      </div>
    </vue2-scrollbar>
    <div v-else class="chat-list__empty">
      <div class="chat-list__empty-inner">
        <img src="~assets/image/img_nofile.svg" />
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
        // {
        //   id: 'ee',
        //   name: '20200412_투입공정 3D.pdf',
        //   ext: 'pdf',
        //   checked: true,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
        // {
        //   id: 'ff',
        //   name: '20200220_배출공정 스캔데이터.jpg',
        //   ext: 'jpg',
        //   checked: false,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
        // {
        //   id: 'gg',
        //   name: '20200311_협업공정 소음.mp3',
        //   ext: 'mp3',
        //   checked: false,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '1MB',
        //   isValid: true,
        // },
        // {
        //   id: 'hh',
        //   name: '20200311_협업공정 영상.mp4',
        //   ext: 'video',
        //   checked: false,
        //   validDate: '2020. 03. 14.',
        //   fileSize: '1MB',
        //   isValid: false,
        // },
        // {
        //   id: 'ii',
        //   name: '가나다라마바사아자차카타파하긴텍스트',
        //   ext: 'video',
        //   checked: false,
        //   validDate: '2020. 03. 14.',
        //   fileSize: '1MB',
        //   isValid: false,
        // },
        // {
        //   id: 'jj',
        //   name: '20200311_설계도면.txt',
        //   ext: 'txt',
        //   checked: true,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
        // {
        //   id: 'kk',
        //   name: '20200412_투입공정 3D.pdf',
        //   ext: 'pdf',
        //   checked: true,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
        // {
        //   id: 'll',
        //   name: '20200220_배출공정 스캔데이터.jpg',
        //   ext: 'jpg',
        //   checked: false,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '10MB',
        //   isValid: true,
        // },
        // {
        //   id: 'mm',
        //   name: '20200311_협업공정 소음.mp3',
        //   ext: 'mp3',
        //   checked: false,
        //   validDate: '2020. 05. 14.',
        //   fileSize: '1MB',
        //   isValid: true,
        // },
        // {
        //   id: 'nn',
        //   name: '20200311_협업공정 영상.mp4',
        //   ext: 'video',
        //   checked: false,
        //   validDate: '2020. 03. 14.',
        //   fileSize: '1MB',
        //   isValid: false,
        // },
        // {
        //   id: 'oo',
        //   name: '가나다라마바사아자차카타파하긴텍스트',
        //   ext: 'video',
        //   checked: false,
        //   validDate: '2020. 03. 14.',
        //   fileSize: '1MB',
        //   isValid: false,
        // },
      ],
      selectedList: [],
    }
  },
  computed: {
    ...mapGetters(['view', 'roomInfo']),
  },

  methods: {
    async init() {
      const res = await getFileList({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      this.fileList = res.fileInfoList.map(fileInfo => {
        fileInfo['checked'] = false
        return fileInfo
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
