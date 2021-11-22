<template>
  <div class="share-body__file">
    <vue2-scrollbar ref="upload-list-scroll">
      <ol class="upload-list" ref="upload-list">
        <li>
          <button class="upload-list__button" @click="addFileClick()">
            {{ $t('service.file_add') }}
          </button>
          <input
            type="file"
            ref="uploadFile"
            style="display:none;"
            @change="fileChangeHandler"
          />
        </li>
        <template v-for="file of sharingList">
          <sharing-3d-object
            :key="'sharing_3d_' + file.objectName"
            :shared="isShareStart"
            :fileInfo="file"
            @3dFileListUpdate="on3dFileListUpdated"
            @get3dFileList="getFileList"
          ></sharing-3d-object>
        </template>
        <!-- 업로드 로딩 역할 -->
        <sharing-file-spinner
          v-if="uploadingFileName"
          :fileName="uploadingFileName"
        ></sharing-file-spinner>
      </ol>
    </vue2-scrollbar>
  </div>
</template>

<script>
import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'
import shareFileUploadMixin from 'mixins/shareFileUpload'
import sharing3dObject from './Sharing3dObject'
import SharingFileSpinner from '../../share/partials/SharingFileSpinner'

import {
  SIGNAL,
  AR_3D_CONTENT_SHARE,
  AR_3D_FILE_SHARE_STATUS,
  FILE_TYPE,
} from 'configs/remote.config'
import { mapGetters, mapMutations } from 'vuex'
import { remoteFileList } from 'api/http/drawing'

export default {
  mixins: [toastMixin, errorMsgMixin, shareFileUploadMixin],
  components: {
    sharing3dObject,
    SharingFileSpinner,
  },
  data() {
    return {
      sharingList: [],
      uploadingFileName: '',
      isShareStart: false, //모델 공유 중 표시 결정 여부 (로딩 시작 부터 true로 set)
    }
  },
  computed: {
    ...mapGetters([
      'share3dContent',
      'roomInfo',
      'is3dPositionPicking',
      'ar3dShareStatus',
    ]),
  },
  watch: {
    ar3dShareStatus: {
      immediate: true,
      handler(newVal) {
        if (
          newVal === AR_3D_FILE_SHARE_STATUS.START ||
          newVal === AR_3D_FILE_SHARE_STATUS.COMPLETE
        ) {
          this.isShareStart = true
        } else if (
          newVal === AR_3D_FILE_SHARE_STATUS.CANCEL ||
          newVal === AR_3D_FILE_SHARE_STATUS.ERROR
        ) {
          this.isShareStart = false
          this.SHOW_3D_CONTENT({})
          //출력 취소 문구 출력
          this.toastDefault(this.$t('service.ar_3d_load_cancel'))
        } else if (newVal === '') {
          this.isShareStart = false
        }
      },
    },
  },
  methods: {
    ...mapMutations(['SHOW_3D_CONTENT', 'SET_AR_3D_SHARE_STATUS']),
    fileChangeHandler(event) {
      const file = event.target.files[0]
      this.loadFile(file, () => this.getFileList(), FILE_TYPE.OBJECT)
    },

    addFileClick() {
      this.$refs['uploadFile'].click()
    },

    async getFileList() {
      //3D 파일 목록 조회 API 호출
      const res = await remoteFileList({
        fileType: FILE_TYPE.OBJECT,
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.sharingList = res.fileInfoList
    },

    on3dFileListUpdated(fileList) {
      this.sharingList = fileList
    },

    //3D 파일 공유 : 랜더링하는 참가자로부터 상태 수신 : start, cancel, complete, error
    setFileShareStatus(event) {
      if (!event.data) return
      const { status, type } = JSON.parse(event.data)
      if (type !== AR_3D_CONTENT_SHARE.FILE_SHARE) return

      this.SET_AR_3D_SHARE_STATUS(status)
      /*
      AR_3D_FILE_SHARE_STATUS.START:
        -선택했던/공유했던 3D 파일 활성화
        -로딩화면 표출
        -툴, 탭 비활성화
      AR_3D_FILE_SHARE_STATUS.CANCEL:
        -선택했던/공유했던 3D 파일 비활성화
        -툴, 탭 활성화
      AR_3D_FILE_SHARE_STATUS.COMPLETE:
        -로딩화면 제거
        -툴, 탭 활성화
      AR_3D_FILE_SHARE_STATUS.ERROR:
        -선택했던/공유했던 3D 파일 비활성화
        -툴, 탭 활성화
      */
    },
  },
  created() {
    this.getFileList() //초기 파일 목록 조회
    this.$eventBus.$on(SIGNAL.AR_3D, this.setFileShareStatus)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.AR_3D, this.setFileShareStatus)
  },
}
</script>

<style></style>
