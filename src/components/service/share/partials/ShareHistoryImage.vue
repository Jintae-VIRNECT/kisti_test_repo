<template>
  <li class="sharing-image">
    <button
      class="sharing-image__item"
      :class="{
        active: shareFile.id === imgInfo.id,
        selected: selected,
      }"
      @dblclick="show"
      @click="select"
      @touchstart="touch"
      @touchend="touchEnd"
    >
      <img :id="'history_image_' + imgInfo.id" :src="imgInfo.img" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>
    <button class="sharing-image__remove" @click="deleteImage">
      {{ $t('service.share_delete') }}
    </button>
    <p class="sharing-image__name">{{ imgInfo.fileName }}</p>
  </li>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import confirmMixin from 'mixins/confirm'
import touchMixin from 'mixins/touch'
import { base64ToBlob } from 'utils/file'
import { drawingUpload } from 'api/http/drawing'
import { DRAWING } from 'configs/remote.config'
export default {
  name: 'ShareHistoryImage',
  mixins: [confirmMixin, touchMixin],
  components: {},
  data() {
    return {
      clicking: false,
      touched: null,
    }
  },
  props: {
    selected: {
      type: Boolean,
      default: false,
    },
    imgInfo: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters(['shareFile', 'roomInfo']),
  },
  methods: {
    ...mapActions(['removeHistory']),
    doEvent() {
      this.show()
    },
    oneClick() {
      this.select()
    },
    async show() {
      this.clicking = false
      if (this.shareFile.id === this.imgInfo.id) return
      const dataType = 'application/octet-stream'

      const file = await base64ToBlob(
        this.imgInfo.img,
        dataType,
        this.imgInfo.fileName,
      )

      const res = await drawingUpload({
        file: file,
        sessionId: this.roomInfo.sessionId,
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })
      this.$call.sendDrawing(DRAWING.ADDED, {
        deleted: false, //false
        expired: false, //false
        sessionId: res.sesssionId,
        name: res.name,
        objectName: res.objectName,
        contentType: res.contentType, // "image/jpeg", "image/bmp", "image/gif", "application/pdf",
        size: res.size,
        createdDate: res.createdDate,
        expirationDate: res.expirationDate,
        width: res.width, //pdf 는 0
        height: res.height,
      })
    },
    deleteImage() {
      if (this.shareFile.id === this.imgInfo.id) return
      this.confirmCancel(
        this.$t('service.share_delete_confirm'),
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
      if (this.selected) {
        this.$emit('unSelected', this.imgInfo.id)
      }
      this.removeHistory(this.imgInfo.id)
    },
    select() {
      if (!this.clicking) {
        this.clicking = true
        setTimeout(() => {
          if (!this.clicking) return
          this.clicking = false
          if (!this.selected) {
            this.$emit('selected', this.imgInfo.id)
          } else {
            this.$emit('unSelected', this.imgInfo.id)
          }
        }, 300)
      }
    },
  },
}
</script>
