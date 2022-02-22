<!--모델 증강 위치 선택 레이어-->
<template>
  <div @click="doPointing($event)"></div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import { ACTION } from 'configs/view.config'
import { AR_3D_CONTENT_SHARE, ROLE } from 'configs/remote.config'
import { normalizedPosX, normalizedPosY } from 'utils/normalize'
import { remoteFileDownload } from 'api/http/drawing'
import toastMixin from 'mixins/toast'

export default {
  name: 'Ar3d',
  mixins: [toastMixin],
  props: {
    videoSize: {
      type: Object,
    },
  },
  computed: {
    ...mapGetters([
      'mainView',
      'viewAction',
      'share3dContent',
      'ar3dShareStatus',
      'is3dPositionPicking',
    ]),
    isLeader() {
      if (this.account.roleType === ROLE.LEADER) return true
      return false
    },
  },
  methods: {
    ...mapMutations(['SET_IS_3D_POSITION_PICKING']),
    async doPointing(event) {
      if (this.viewAction !== ACTION.AR_3D) return false //3d 공유 모드가 아닌 경우
      if (!this.isLeader) return false //리더가 아닌 경우
      if (!this.share3dContent.objectName) return false //현재 선택한 3d 오브젝트가 없는 경우
      if (this.share3dContent.objectName && !this.is3dPositionPicking) {
        //3d 공유가 이미 출력 중인 경우
        this.toastDefault(this.$t('service.ar_3d_exist'))
        return false
      }

      let posX = normalizedPosX(event.offsetX, this.videoSize.width)
      let posY = normalizedPosY(event.offsetY, this.videoSize.height)
      if (posX > 1) posX = 1
      if (posY > 1) posY = 1

      const fileUrl = await this.getDownloadUrl()

      const params = {
        posX,
        posY,
        fileUrl,
        fileType: 'gltf',
      }

      //시그널 전송
      this.$call.sendAr3dSharing(
        AR_3D_CONTENT_SHARE.CONTENT_AUGUMENTED,
        params,
        [this.mainView.connectionId],
      )
      //position picking 여부 false로 전환
      this.SET_IS_3D_POSITION_PICKING(false)

      //clear tool clearable하게 업데이트
      this.$eventBus.$emit(`tool:clear`, true)
    },

    async getDownloadUrl() {
      const userId = this.account.uuid
      const { sessionId, workspaceId, objectName } = this.share3dContent
      const res = await remoteFileDownload({
        sessionId,
        workspaceId,
        objectName,
        userId,
      })
      return res.url
    },
  },
}
</script>

<style></style>
