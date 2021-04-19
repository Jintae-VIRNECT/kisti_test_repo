<template>
  <li class="sharing-image">
    <button
      class="sharing-image__item"
      :class="{
        active: shareFile.objectName === fileInfo.objectName,
        disable: !isLeader || shareFile.objectName === fileInfo.objectName,
      }"
      @dblclick="shareImage"
      @touchstart="touch"
      @touchend="touchEnd"
    >
      <img :src="fileInfo.thumbnailDownloadUrl" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>
    <p class="sharing-image__name">{{ fileInfo.name }}</p>
    <button
      v-if="shareFile.objectName !== fileInfo.objectName && isLeader"
      class="sharing-image__remove"
      @click="deleteImage"
    >
      {{ $t('service.share_delete') }}
    </button>
  </li>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
import touchMixin from 'mixins/touch'
import { ROLE, DRAWING } from 'configs/remote.config'
import { drawingRemove } from 'api/http/drawing'
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
  },
  computed: {
    ...mapGetters(['shareFile', 'roomInfo']),
    fileData() {
      if (this.fileInfo && this.fileInfo.filedata) {
        return this.fileInfo.filedata
      } else {
        return {}
      }
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    ...mapActions(['addHistory', 'removeFile']),
    doEvent() {
      this.shareImage()
    },
    shareImage() {
      this.$call.sendDrawing(DRAWING.FILE_SHARE, {
        name: this.fileInfo.name,
        objectName: this.fileInfo.objectName,
        contentType: this.fileInfo.contextType,
        width: this.fileInfo.width,
        height: this.fileInfo.height,
        index: 0,
      })
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
    async remove() {
      try {
        await drawingRemove({
          workspaceId: this.workspace.uuid,
          sessionId: this.roomInfo.sessionId,
          leaderUserId: this.account.uuid,
          objectName: this.fileInfo.objectName,
        })

        this.$call.sendDrawing(DRAWING.DELETED, {
          objectNames: [this.fileInfo.objectName],
        })
        // this.removeFile(this.fileInfo.id)
      } catch (err) {
        console.log(err)
      }
    },
  },
}
</script>
