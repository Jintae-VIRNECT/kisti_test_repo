<template>
  <div class="chat-list">
    <vue2-scrollbar ref="chatFileListScrollbar">
      <div>
        <chat-file-item
          v-for="(fileItem, index) in chatFileList"
          :key="index"
          :id="fileItem.id"
          :fileName="fileItem.fileName"
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
export default {
  name: 'ChatFileList',
  components: {
    ChatFileItem,
    ChatFileDown,
  },
  data() {
    return {
      //test datas
      chatFileList: [
        {
          id: 'dd',
          fileName: '20200311_설계도면.txt',
          ext: 'txt',
          checked: true,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'ee',
          fileName: '20200412_투입공정 3D.pdf',
          ext: 'pdf',
          checked: true,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'ff',
          fileName: '20200220_배출공정 스캔데이터.jpg',
          ext: 'jpg',
          checked: false,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'gg',
          fileName: '20200311_협업공정 소음.mp3',
          ext: 'mp3',
          checked: false,
          validDate: '2020. 05. 14.',
          fileSize: '1MB',
          isValid: true,
        },
        {
          id: 'hh',
          fileName: '20200311_협업공정 영상.mp4',
          ext: 'video',
          checked: false,
          validDate: '2020. 03. 14.',
          fileSize: '1MB',
          isValid: false,
        },
        {
          id: 'ii',
          fileName: '가나다라마바사아자차카타파하긴텍스트',
          ext: 'video',
          checked: false,
          validDate: '2020. 03. 14.',
          fileSize: '1MB',
          isValid: false,
        },
        {
          id: 'jj',
          fileName: '20200311_설계도면.txt',
          ext: 'txt',
          checked: true,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'kk',
          fileName: '20200412_투입공정 3D.pdf',
          ext: 'pdf',
          checked: true,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'll',
          fileName: '20200220_배출공정 스캔데이터.jpg',
          ext: 'jpg',
          checked: false,
          validDate: '2020. 05. 14.',
          fileSize: '10MB',
          isValid: true,
        },
        {
          id: 'mm',
          fileName: '20200311_협업공정 소음.mp3',
          ext: 'mp3',
          checked: false,
          validDate: '2020. 05. 14.',
          fileSize: '1MB',
          isValid: true,
        },
        {
          id: 'nn',
          fileName: '20200311_협업공정 영상.mp4',
          ext: 'video',
          checked: false,
          validDate: '2020. 03. 14.',
          fileSize: '1MB',
          isValid: false,
        },
        {
          id: 'oo',
          fileName: '가나다라마바사아자차카타파하긴텍스트',
          ext: 'video',
          checked: false,
          validDate: '2020. 03. 14.',
          fileSize: '1MB',
          isValid: false,
        },
      ],
    }
  },
  computed: {
    ...mapGetters(['view']),
  },
  watch: {
    // chatFileList: {
    //   handler() {
    //     this.$nextTick(() => {
    //       if (this.$refs['chatFileListScrollbar']) {
    //         this.$refs['chatFileListScrollbar'].scrollToY(
    //           Number.MAX_SAFE_INTEGER,
    //         )
    //       }
    //     })
    //   },
    //   deep: true,
    // },
  },

  methods: {},

  mounted() {
    this.chatFileList.push({
      id: 'cc',
      fileName: '1가나다라마바사아자차카타파하긴텍스트',
      ext: 'video',
      checked: false,
      validDate: '2020. 03. 14.',
      fileSize: '1MB',
      isValid: false,
    })
    this.chatFileList.push({
      id: 'bb',
      fileName: '2가나다라마바사아자차카타파하긴텍스트',
      ext: 'video',
      checked: false,
      validDate: '2020. 03. 14.',
      fileSize: '1MB',
      isValid: false,
    })
    this.chatFileList.push({
      id: 'aa',
      fileName: '3가나다라마바사아자차카타파하긴텍스트',
      ext: 'video',
      checked: false,
      validDate: '2020. 03. 14.',
      fileSize: '1MB',
      isValid: false,
    })
  },
}
</script>

<style></style>
