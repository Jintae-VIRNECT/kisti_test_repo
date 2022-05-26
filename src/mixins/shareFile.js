import { mapGetters } from 'vuex'
import { ROLE, DRAWING } from 'configs/remote.config'

import confirmMixin from 'mixins/confirm'

export default {
  mixins: [confirmMixin],
  computed: {
    ...mapGetters(['shareFile']),
  },
  methods: {
    $_participantChange(connectionId) {
      if (this.account.roleType !== ROLE.LEADER) return
      if (this.shareFile && this.shareFile.id) {
        //협업보드 활성화 상태에서 신규 참가자 진입 시 fileShare 이벤트 전송하는 부분
        const isNotModified =
          !this.shareFile.json || this.shareFile.json.length === 0

        if (isNotModified) {
          this.$_sendImage([connectionId])
          return
        }

        this.confirmDefault(this.$t('service.drawing_sync'), {
          action: () => {
            this.$_sendImage()
          },
        })
      }
    },
    $_sendImage(target = null) {
      const index = this.shareFile.pageNum ? this.shareFile.pageNum - 1 : 0 //pdf의 경우 pageNum이 0이상의 수로 존재, image의 경우 0으로 세팅되어 옴
      const name = this.shareFile.oriName || this.shareFile.name
      this.$call.sendDrawing(
        DRAWING.FILE_SHARE,
        {
          name,
          objectName: this.shareFile.objectName,
          contentType: this.shareFile.contentType,
          width: this.shareFile.width,
          height: this.shareFile.height,
          index,
        },
        target,
      )
    },
  },
}
