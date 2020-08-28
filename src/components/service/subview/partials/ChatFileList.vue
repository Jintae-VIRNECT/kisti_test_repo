<template>
  <div class="chat-list">
    <vue2-scrollbar ref="chatFileListScrollbar">
      <div>
        <chat-file-item
          v-for="(fileItem, index) in fileList"
          :key="index"
          :id="fileItem.id"
          :fileName="fileItem.name"
          :ext="fileItem.ext"
          :checked="fileItem.checked"
          :validDate="fileItem.validDate"
          :fileSize="fileItem.fileSize"
          :isValid="fileItem.isValid"
        ></chat-file-item>
      </div>
    </vue2-scrollbar>
    <chat-file-down></chat-file-down>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ChatFileItem from './ChatFileItem'
import ChatFileDown from './ChatFileDownload'
import { getFileList } from 'api/workspace/call'
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
      this.fileList = res.fileInfoList
      this.$nextTick(() => {
        this.$refs['chatFileListScrollbar'].calculateSize()
      })
    },
  },

  mounted() {
    this.init()
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
