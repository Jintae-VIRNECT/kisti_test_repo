<template>
  <li class="sharing-image">
    <button
      class="sharing-image__item"
      :class="{
        active:
          shareFile.objectName === fileInfo.objectName &&
          pageInfo.pageNum === shareFile.pageNum,
        disable:
          !isLeader ||
          (shareFile.objectName === fileInfo.objectName &&
            pageInfo.pageNum === shareFile.pageNum),
      }"
      @dblclick="shareImage"
      @touchstart="touch"
      @touchend="touchEnd"
    >
      <img :src="imageData" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>
    <p class="sharing-image__name">{{ fileData.name }}</p>
  </li>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
import touchMixin from 'mixins/touch'
import { ROLE, DRAWING } from 'configs/remote.config'
export default {
  name: 'SharingImage',
  mixins: [confirmMixin, toastMixin, touchMixin],
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
    pageInfo: {
      type: Object,
    },
    pdfPage: {
      type: Number,
      default: -1,
    },
  },
  computed: {
    ...mapGetters(['shareFile']),
    fileData() {
      if (this.pageInfo && this.pageInfo.filedata) {
        return this.pageInfo.filedata
      } else {
        return {}
      }
    },
    isLeader() {
      if (this.account.roleType === ROLE.LEADER) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    ...mapActions(['addHistory']),
    init() {
      const fileReader = new FileReader()
      fileReader.onload = async e => {
        let imgUrl = e.target.result
        if (this.fileData.size > 1024 * 1024 * 5) {
          imgUrl = await this.resizing(imgUrl)
        }
        this.imageData = imgUrl
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
      let fileName = this.fileData.name
      if (this.fileInfo.name) {
        const idx = this.pdfName.lastIndexOf('.')
        fileName = `${this.fileInfo.name.slice(0, idx)} [${this.pdfPage}].png`
      }
      return {
        id: imgId,
        fileName: fileName,
        oriName:
          this.fileInfo.name && this.fileInfo.name.length > 0
            ? this.fileInfo.name
            : this.fileData.name,
        img: this.imageData,
        // fileData: this.fileData,
      }
    },
    doEvent() {
      this.shareImage()
    },
    shareImage() {
      this.$call.sendDrawing(DRAWING.FILE_SHARE, {
        name: this.fileInfo.name,
        objectName: this.fileInfo.objectName,
        contentType: this.fileInfo.contentType,
        width: this.pageInfo.width,
        height: this.pageInfo.height,
        index: this.pdfPage - 1,
      })
    },
  },

  /* Lifecycles */
  mounted() {
    this.init()
  },
}
</script>
