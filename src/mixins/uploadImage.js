// upload image mixin
import toastMixin from './toast'
export default {
  mixins: [toastMixin],
  data() {
    return {
      imageURL: '',
      imageFile: null,
    }
  },
  methods: {
    uploadImage(event) {
      this.validImage(event)
    },
    validImage(event) {
      const files = event.target.files

      this.imageFile = files[0]

      if (files.length > 0) {
        if (
          ['image/gif', 'image/jpeg', 'image/jpg', 'image/png'].indexOf(
            files[0].type,
          ) < 0
        ) {
          this.toastError(this.$t('service.file_type'))
          return
        }
        if (files[0].size > 2 * 1024 * 1024) {
          this.toastError(this.$t('service.file_maxsize'))
          return
        }
        const oReader = new FileReader()
        oReader.onload = e => {
          const imageData = e.target.result
          const oImg = new Image()

          oImg.onload = _event => {
            this.imageURL = imageData
            _event.target.remove()
            this.$refs['inputImage'].value = ''
          }
          oImg.onerror = () => {
            // 이미지 아닐 시 처리.
            this.toastError(this.$t('service.file_type'))
          }
          oImg.src = imageData
        }
        oReader.readAsDataURL(files[0])
      }
    },
    imageUpload() {
      this.$refs['inputImage'].click()
    },
    imageRemove() {
      this.imageURL = ''
      this.imageFile = null
    },
  },
}
