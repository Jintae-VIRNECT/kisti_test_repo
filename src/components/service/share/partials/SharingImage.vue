<template>
  <li class="sharing-image">
    <button class="sharing-image__item" @dblclick="shareImage">
      <img :src="imageData" />
    </button>
    <p class="sharing-image__name">{{ fileData.name }}</p>
    <button
      v-if="pdfPage < 0"
      class="sharing-image__remove"
      @click="deleteImage"
    >
      {{ $t('service.share_delete') }}
    </button>
  </li>
</template>

<script>
import { mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
export default {
  name: 'SharingImage',
  mixins: [confirmMixin, toastMixin],
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
  watch: {
    fileData() {
      this.init()
    },
  },
  methods: {
    ...mapActions(['addHistory', 'removeFile']),
    init() {
      const fileReader = new FileReader()
      fileReader.onload = async e => {
        let imgUrl = e.target.result
        if (this.fileInfo.filedata.size > 1024 * 1024 * 5) {
          imgUrl = await this.resizing(imgUrl)
        }
        this.imageData = imgUrl
      }
      fileReader.readAsDataURL(this.fileData)
    },
    resizing(imageUrl) {
      return new Promise(resolve => {
        const image = new Image()
        image.onload = () => {
          const canvas = document.createElement('canvas')
          const max_size = 1028 * 1028 * 5
          // 최대 기준을 1280으로 잡음.
          let width = image.width
          let height = image.height

          if (width > height) {
            // 가로가 길 경우
            if (width > max_size) {
              height *= max_size / width
              width = max_size
            }
          } else {
            // 세로가 길 경우
            if (height > max_size) {
              width *= max_size / height
              height = max_size
            }
          }
          canvas.width = width
          canvas.height = height
          canvas.getContext('2d').drawImage(image, 0, 0, width, height)
          const dataUrl = canvas.toDataURL('image/jpeg')
          resolve(dataUrl)
        }
        image.src = imageUrl
      })
    },
    getHistoryObject() {
      // 모바일 수신부 타입: Int32
      const imgId = parseInt(
        Date.now()
          .toString()
          .substr(-9),
      )
      let fileName = this.fileData.name
      if (this.pdfName) {
        const idx = this.pdfName.lastIndexOf('.')
        fileName = `${this.pdfName.slice(0, idx)} [${this.pdfPage}].png`
      }
      return {
        id: imgId,
        fileName: fileName,
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
        // TODO: MESSAGE
        this.toastNotify(this.$t('service.share_notready'))
      }
    },
    deleteImage() {
      this.confirmCancel(
        this.$t('service.share_delete_real'),
        {
          text: this.$t('button.confirm'),
          action: this.remove,
        },
        {
          text: this.$t('button.cancel'),
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
