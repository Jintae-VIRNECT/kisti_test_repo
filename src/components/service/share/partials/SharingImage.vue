<template>
  <li class="sharing-image">
    <button class="sharing-image__item" @dblclick="shareImage">
      <img :src="imageData" />
    </button>
    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button class="sharing-image__remove" @click="deleteImage">
      이미지 삭제
    </button>
  </li>
</template>

<script>
import { mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'SharingImage',
  mixins: [confirmMixin],
  components: {},
  data() {
    return {
      imageData: '',
    }
  },
  props: {
    fileInfo: {
      type: Object,
    },
    isHistory: {
      type: Boolean,
      default: false,
    },
    pdfName: {
      type: String,
      default: null,
    },
    pdfPage: {
      type: Number,
      default: -1,
    },
  },
  computed: {
    fileData() {
      if (this.fileInfo && this.fileInfo.filedata) {
        return this.fileInfo.filedata
      } else {
        return {}
      }
    },
  },
  methods: {
    ...mapActions(['addHistory', 'removeFile']),
    init() {
      const fileReader = new FileReader()
      fileReader.onload = e => {
        this.imageData = e.target.result
      }
      fileReader.readAsDataURL(this.fileData)
    },
    getHistoryObject() {
      // 모바일 수신부 타입: Int32
      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )
      return {
        id: imgId,
        fileName:
          this.pdfName && this.pdfName.length > 0
            ? `${this.pdfName} (${this.pdfPage})`
            : this.fileData.name,
        oriName:
          this.pdfName && this.pdfName.length > 0
            ? this.pdfName
            : this.fileData.name,
        img: this.imageData,
      }
    },
    shareImage() {
      if (this.imageData && this.imageData.length > 0) {
        const history = this.getHistoryObject()

        this.addHistory(history)
      } else {
        alert('이미지가 로드중')
      }
    },
    deleteImage() {
      this.confirmCancel(
        '정말로 삭제하시겠습니까?',
        {
          text: '확인',
          action: this.remove,
        },
        {
          text: '취소',
        },
      )
    },
    remove() {
      this.removeFile(this.fileInfo.id)
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
