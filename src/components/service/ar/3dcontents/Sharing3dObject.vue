<template>
  <li class="sharing-image threed">
    <button
      class="sharing-image__item"
      :class="{
        active: isSharing,
        selected:
          share3dContent.objectName === fileInfo.objectName &&
          is3dPositionPicking,
      }"
      @dblclick="select3dObject"
      @touchstart="touch"
      @touchend="touchEnd"
    >
      <img src="~assets/image/call/mdpi_img_3d_file.svg" />
      <div class="sharing-image__item-active">
        <p>{{ $t('service.share_current') }}</p>
      </div>
    </button>
    <p class="sharing-image__name">{{ fileInfo.name }}</p>
    <button
      v-if="(isMobileSize && isRemovable) || isRemovable"
      class="sharing-image__remove"
      @click="deleteObj"
    >
      {{ $t('service.file_remove') }}
    </button>
  </li>
</template>

<script>
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import touchMixin from 'mixins/touch'
import { mapGetters, mapMutations } from 'vuex'
import { ROLE, AR_3D_FILE_SHARE_STATUS, FILE_TYPE } from 'configs/remote.config'
import { remoteFileList, remoteFileRemove } from 'api/http/drawing'

export default {
  name: 'Sharing3dObject',
  mixins: [toastMixin, confirmMixin, touchMixin],
  props: {
    fileInfo: {
      type: Object,
    },
    shared: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters([
      'share3dContent',
      'ar3dShareStatus',
      'is3dPositionPicking',
      'roomInfo',
    ]),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isSharing() {
      return (
        this.share3dContent.objectName === this.fileInfo.objectName &&
        this.shared &&
        !this.is3dPositionPicking
      )
    },
    isRemovable() {
      return (
        this.isLeader &&
        !this.isSharing &&
        this.share3dContent.objectName !== this.fileInfo.objectName
      )
    },
  },
  methods: {
    ...mapMutations(['SHOW_3D_CONTENT', 'SET_IS_3D_POSITION_PICKING']),
    doEvent() {
      this.select3dObject()
    },
    async select3dObject() {
      //기존 공유 중인 3D 오브젝트가 있는지 체크
      if (
        this.share3dContent.objectName &&
        this.ar3dShareStatus === AR_3D_FILE_SHARE_STATUS.COMPLETE
      ) {
        this.toastDefault(this.$t('service.ar_3d_exist'))
        return false
      }

      //컨버팅 완료 상태인 파일인 경우
      if (this.fileInfo.fileConvertStatus === 'CONVERTED') {
        this.start3dObjectPositionPick()
        return true
      }

      //컨버팅 완료 여부 조회를 위해 목록 재조회 필요
      const res = await remoteFileList({
        fileType: FILE_TYPE.OBJECT,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })

      //조회해 온 목록으로 부모 목록 업데이트
      this.$emit('3dFileListUpdate', res.fileInfoList)

      const target = res.fileInfoList.find(
        item => item.objectName === this.fileInfo.objectName,
      )

      //해당 3D 오브젝트가 컨버팅 완료되었는지 체크
      if (target.fileConvertStatus === 'CONVERTING') {
        this.toastDefault(this.$t('service.ar_3d_converting'))
        return false
      }

      //파일 컨버팅 실패 시
      if (target.fileConvertStatus === 'CONVERTING_FAIL') {
        this.toastDefault(this.$t('service.ar_3d_converting_fail'))
        this.remove() //해당 파일 삭제, 삭제 후 : 목록 재조회
        return false
      }

      this.start3dObjectPositionPick()
    },

    //컨버팅 완료된 모델을 선택하여 다음 단계 진행
    start3dObjectPositionPick() {
      this.toastDefault(this.$t('service.ar_3d_position_pick'))
      this.SHOW_3D_CONTENT(this.fileInfo)
      this.SET_IS_3D_POSITION_PICKING(true)
      //여기서는 선택한 3D 파일만 VUEX에 저장한다.
      //화면상에서 위치를 선택하는 시점에 시그널 전송을 한다.
    },

    deleteObj() {
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
    //업로드한 3d 파일 제거
    async remove() {
      try {
        //삭제 API 호출
        await remoteFileRemove({
          workspaceId: this.workspace.uuid,
          sessionId: this.roomInfo.sessionId,
          leaderUserId: this.account.uuid,
          objectName: this.fileInfo.objectName,
        })

        //삭제 후 부모에서 목록 재조회
        this.$emit('get3dFileList')
      } catch (e) {
        console.error(e)
      }
    },
  },
}
</script>

<style></style>
