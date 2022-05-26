<template>
  <div class="ar-view">
    <ar-video
      :canPointing="!leaderDrawing"
      :ar-area="!loadingFrame && !isArDeviceBackground"
    >
      <!-- 모델 증강 중에 표시되는 로딩 화면 -->
      <loading-3d v-if="loading3d"></loading-3d>
    </ar-video>
    <ar-canvas
      v-show="isDrawing"
      :file="shareArImage"
      @loading="loadingFrame = false"
    ></ar-canvas>
    <transition name="opacity">
      <!-- 3d 공유 -> ar 영역캡쳐(드로잉) 모드 전환시, ar영역 캡쳐 후 드로잉 화면 전환시 사용하는 로딩화면 -->
      <video-loading v-if="loadingFrame"></video-loading>
    </transition>
    <!-- ar 기기 백그라운드 전환 시 화면 -->
    <transition name="opacity">
      <video-stopped
        v-if="isArDeviceBackground"
        :cameraStatus="cameraStatus"
      ></video-stopped>
    </transition>

    <!-- 3d control btn -->
    <div v-if="!isMobileSize && isLeader" class="control-3d-btn-wrap">
      <div class="3d-control-btn-container">
        <tooltip
          v-if="is3dControlBtnVisible"
          :content="`${$t('service.3d_content_control')}`"
          customClass="control-3d-btn-tooltip"
          placement="left"
        >
          <button
            :disabled="!is3dControlBtnActive"
            slot="body"
            class="control-3d-btn"
            :class="{ on: controlActive }"
            @click="onClick3dControlBtn"
          ></button>
        </tooltip>
        <control-3d-pop-over
          v-if="isControlPopOverVisible"
          :controlZoomLevel="controlZoomLevel"
          @close="onControlPopOverClose"
          @controlZoomLevel="onReceiveControlZoomLevel"
        ></control-3d-pop-over>
      </div>
    </div>

    <!-- mobile 3d control ui -->
    <button
      v-if="isMobileSize && isControlPopOverVisible"
      class="control-3d-exit-btn"
      @click="onControlPopOverClose"
    >
      {{ $t('service.3d_content_control') }}
    </button>

    <control-3d
      v-if="isMobileSize && isControlPopOverVisible"
      :controlZoomLevel="controlZoomLevel"
      @controlZoomLevel="onReceiveControlZoomLevel"
    ></control-3d>
  </div>
</template>

<script>
import { mapGetters, mapActions, mapMutations } from 'vuex'
import { VIEW, ACTION } from 'configs/view.config'
import {
  AR_FEATURE,
  SIGNAL,
  AR_DRAWING,
  ROLE,
  AR_3D_CONTENT_SHARE,
  AR_3D_FILE_SHARE_STATUS,
  AR_3D_CONTROL_TYPE,
} from 'configs/remote.config'
import { CAMERA, DEVICE } from 'configs/device.config'
import { CAMERA_STATE } from 'configs/status.config'

import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import arEventQueueMixin from 'mixins/arEventQueue'
import ar3dControlMixin from 'mixins/ar3dControl'

import Loading3d from './3dcontents/Loading3d'
import ArVideo from './ArVideo'
import ArCanvas from './ardrawing/DrawingCanvas'
import VideoLoading from '../stream/partials/VideoLoading'
import VideoStopped from '../stream/partials/VideoStopped'
import Tooltip from 'Tooltip'
import Control3dPopOver from '../modal/Control3dPopOver'
import Control3d from './3dcontents/Control3d'

const MODE_CHANGE_DELAY = 3000

export default {
  name: 'ARView',
  mixins: [toastMixin, confirmMixin, arEventQueueMixin, ar3dControlMixin],
  components: {
    Loading3d,
    ArVideo,
    ArCanvas,
    VideoLoading,
    VideoStopped,
    Tooltip,
    Control3dPopOver,
    Control3d,
  },
  data() {
    return {
      chunk: [],
      loadingFrame: false,
      loading3d: false, //모델 랜더링 중 로딩화면
      leaderDrawing: false,
      controlZoomLevel: 100,
    }
  },
  computed: {
    ...mapGetters([
      'mainView',
      'view',
      'viewAction',
      'shareArImage',
      'ar3dShareStatus',
      'share3dContent',
      'is3dPositionPicking',
      'participants',
    ]),
    isDrawing() {
      if (!this.isLeader) {
        return false
      }
      if (this.viewAction === ACTION.AR_DRAWING) {
        return true
      } else {
        // this.$call.sendArDrawing(AR_DRAWING.END_DRAWING)
        return false
      }
    },
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isArDeviceBackground() {
      return (
        this.mainView.cameraStatus === CAMERA.APP_IS_BACKGROUND &&
        this.mainView.me !== true
      )
    },
    cameraStatus() {
      const hasMainView = this.mainView && this.mainView.id

      if (hasMainView) {
        const id = this.mainView.id
        let state = CAMERA_STATE.ON

        const isCameraOff = this.mainView.cameraStatus === CAMERA.CAMERA_OFF
        const isAppBackground =
          this.mainView.cameraStatus === CAMERA.APP_IS_BACKGROUND

        if (isCameraOff) {
          state = CAMERA_STATE.OFF
        } else if (isAppBackground) {
          state = CAMERA_STATE.BACKGROUND
        }

        return {
          state,
          id,
        }
      } else {
        return CAMERA_STATE.UNAVAILABLE
      }
    },
    is3dControlBtnVisible() {
      return this.viewAction === ACTION.AR_3D && this.isLeader
    },
    is3dControlBtnActive() {
      //3d 공유가 이미 출력 중인 경우
      return this.share3dContent.objectName && !this.is3dPositionPicking
    },
  },
  watch: {
    viewAction(newVal, beforeVal) {
      this.debug('ArView :: viewAction changed ::', beforeVal, '=>', newVal)

      const isNewViewAR = [
        ACTION.AR_POINTING,
        ACTION.AR_AREA,
        ACTION.AR_3D,
      ].includes(newVal)

      //종료 시그널 전송 부분
      //ar 드로잉 모드 종료
      if (beforeVal === ACTION.AR_DRAWING && isNewViewAR) {
        this.$call.sendArDrawing(AR_DRAWING.END_DRAWING)
      }
      //3d 공유 모드 종료 : ar 3d 컨텐츠 모드 해제 및 공유 중인 데이터/상태 초기화
      else if (beforeVal === ACTION.AR_3D) {
        //AR내에 모드 변경시에만 3d모드 종료 시그널을 보내고, AR기능 자체의 종료인 경우 보내지 않는다.
        if (this.view === VIEW.AR) {
          this.debug('ArView :: send stop 3d share mode signal')
          this.$call.sendAr3dSharing(AR_3D_CONTENT_SHARE.STOP_SHARE)
        }
        this.SHOW_3D_CONTENT({})
        this.SET_AR_3D_SHARE_STATUS('')
        this.SET_IS_3D_POSITION_PICKING(false)
        this.reset3dControlStatus()
      }

      //3d 공유 -> ar 드로잉으로 모드 전환 시
      if (beforeVal === ACTION.AR_3D && newVal === ACTION.AR_AREA) {
        this.loadingFrame = true
        setTimeout(() => {
          this.loadingFrame = false
        }, MODE_CHANGE_DELAY)
      }
    },
    //로딩화면 표출 여부 결정
    ar3dShareStatus(newVal) {
      if (newVal === AR_3D_FILE_SHARE_STATUS.START) this.loading3d = true
      else this.loading3d = false
    },

    isControlPopOverVisible(newVal) {
      //컨트롤 창 닫을 때, 제어 끝내는 신호 전송
      if (!newVal) {
        const isControlling = false
        this.$call.sendAr3dSharing(
          AR_3D_CONTROL_TYPE.STATUS,
          { isControlling },
          [this.mainView.connectionId],
        )
      }
    },

    share3dContent(currentContent) {
      const isSharingStopped = Object.keys(currentContent).length === 0

      if (isSharingStopped) {
        this.SET_IS_CONTROL_POP_OVER_VISIBLE(false)
        this.SET_CONTROL_ACTIVE(false)
      }
    },
  },
  methods: {
    ...mapMutations([
      'SHOW_3D_CONTENT',
      'SET_AR_3D_SHARE_STATUS',
      'SET_IS_3D_POSITION_PICKING',
      'updateParticipant',
      'SET_CONTROL_ACTIVE',
      'SET_IS_CONTROL_POP_OVER_VISIBLE',
    ]),
    ...mapActions(['showArImage', 'setAction', 'addChat', 'setView']),
    receiveSignal(receive) {
      const data = JSON.parse(receive.data)

      if (data.from === this.account.uuid) return

      if (this.account.roleType !== ROLE.LEADER) {
        if (data.type === AR_DRAWING.START_DRAWING) {
          this.leaderDrawing = true
          this.$eventBus.$emit('leaderDrawing', true)
        } else if (data.type === AR_DRAWING.END_DRAWING) {
          this.leaderDrawing = false
          this.$eventBus.$emit('leaderDrawing', false)
        }
      }
      if (this.account.roleType !== ROLE.LEADER) {
        return false
      }

      // frameResponse 수신
      if (
        ![
          AR_DRAWING.FIRST_FRAME,
          AR_DRAWING.FRAME,
          AR_DRAWING.LAST_FRAME,
        ].includes(data.type)
      )
        return

      if (!this.loadingFrame) this.loadingFrame = true

      if (data.type === AR_DRAWING.FIRST_FRAME) {
        this.chunk = []
      }
      this.chunk.push(data.chunk)

      if (data.type === AR_DRAWING.LAST_FRAME) {
        this.encodeImage(Date.now())
      }
    },
    encodeImage(imgId) {
      let imgUrl = ''
      for (let part of this.chunk) {
        imgUrl += part
      }
      this.chunk = []
      imgUrl = 'data:image/png;base64,' + imgUrl
      const imageInfo = {
        id: imgId,
        img: imgUrl,
      }

      this.showArImage(imageInfo)
    },

    // 타 참가자 : 3d 모델 공유 모드 시작/종료
    receiveSignal3d(event) {
      const fromUserId = event.from.connectionId
      const data = JSON.parse(event.data)
      const { type } = data

      if (!type) return false

      this.startShare(event)
      if (type === AR_3D_CONTENT_SHARE.STOP_SHARE) {
        if (this.viewAction !== ACTION.AR_3D) return false
        this.setAction(ACTION.AR_POINTING) //기본 포인팅 모드로 전환
      } else {
        this.on3dControlSignalHandler(data, fromUserId)
      }
    },

    checkArFeature(received) {
      let data
      let receive

      //received event directly from signal listener
      if (!received.receive) {
        data = JSON.parse(received.data)
        receive = received
      }
      //received event from vuex
      else {
        data = received.data
        receive = received.receive
      }

      if (data.from === this.account.uuid) return
      if (this.account.roleType === ROLE.LEADER) {
        if (data.type === AR_FEATURE.FEATURE) {
          if ('hasArFeature' in data) {
            this.updateParticipant({
              connectionId: receive.from.connectionId,
              hasArFeature: data.hasArFeature,
            })
            if (data.hasArFeature === false) {
              this.addChat({
                status: 'ar-unsupport',
                type: 'system',
              })
            }
          }
        }
      } else {
        if (
          data.type === AR_FEATURE.START_AR_FEATURE &&
          this.view !== VIEW.AR
        ) {
          this.startAr()
        } else if (data.type === AR_FEATURE.STOP_AR_FEATURE) {
          this.toastDefault(this.$t('service.toast_ar_exit'))
          this.setView(VIEW.STREAM)
        }
      }
    },

    startAr(sendSignal = false) {
      this.debug('ArView :: startAr')

      this.confirmClose()

      this.toastDefault(
        this.$t('service.toast_ar_start', { name: this.mainView.nickname }),
      )

      this.addChat({
        status: 'ar-start',
        name: this.mainView.nickname,
        type: 'system',
      })

      if (sendSignal) {
        this.$call.sendArFeatureStart(this.mainView.id)
      }

      this.setView(VIEW.AR)
    },

    startShare(event) {
      let type

      if (!event.receive) type = JSON.parse(event.data).type
      else type = event.data.type

      if (
        type === AR_3D_CONTENT_SHARE.START_SHARE &&
        !this.isLeader &&
        this.viewAction !== ACTION.AR_3D
      ) {
        this.setAction(ACTION.AR_3D)
      }
    },

    receiveQueuedSignal(event) {
      this.startShare(event)
      this.checkArFeature(event)
    },

    //증강된 3D 콘텐츠 제거
    clear3dObject() {
      if (this.viewAction !== ACTION.AR_3D) return false

      //시그널 전송
      const targetUserId = this.mainView.id
      this.$call.sendAr3dSharing(AR_3D_CONTENT_SHARE.CLEAR_CONTENTS, {
        targetUserId,
      })

      //vuex 초기화
      this.SHOW_3D_CONTENT({})
      this.SET_AR_3D_SHARE_STATUS('')
    },

    //콘텐츠 제어 시그널 수신 처리
    on3dControlSignalHandler(data, fromUserId) {
      const { type } = data

      //콘텐츠 제어 시그널 타입인지 필터링
      if (!Object.values(AR_3D_CONTROL_TYPE).includes(type)) return

      const handler = {
        //요청 수신
        request: () => this.onControlRequestReceived(fromUserId),

        //요청 수락 or 요청 거절
        response: () => this.onControlResponseReceived(data),

        //요청 취소
        cancelRequest: () => this.onControlCancelRequestReceived(),

        //상태 수신
        status: () => this.onControlStatusReceived(data),

        //@TODO : AR공유자에서 rest 시그널 송신이 필요하다.
        reset: () => (this.controlZoomLevel = 100),
      }

      handler[type]()
    },

    //ar공유자 -> 리더 콘텐츠 제어 권한 요청
    onControlRequestReceived(fromUserId) {
      const fromUser = this.participants.find(
        pt => pt.connectionId === fromUserId,
      )

      if (!fromUser) return false

      const popupText = `${fromUser.nickname}${this.$t(
        'service.3d_content_control_permission_requested',
      )}`

      const confirm = {
        text: this.$t('button.accept'),
        action: () => {
          //기존 제어 창은 닫기
          this.SET_IS_CONTROL_POP_OVER_VISIBLE(false)
          this.SET_CONTROL_ACTIVE(false)

          //요청수락 시그널 전송
          const isAllowed = true
          this.$call.sendAr3dSharing(
            AR_3D_CONTROL_TYPE.RESPONSE,
            { isAllowed },
            [fromUserId],
          )
        },
      }

      const cancel = {
        text: this.$t('button.refuse'),
        action: () => {
          //요청거절 시그널 전송
          const isAllowed = false
          this.$call.sendAr3dSharing(
            AR_3D_CONTROL_TYPE.RESPONSE,
            { isAllowed },
            [fromUserId],
          )
        },
      }

      const option = {
        customClass: 'service-popup',
      }

      this.confirmCancel(popupText, confirm, cancel, option)
    },

    //제어 권한 요청 응답 수신시
    onControlResponseReceived(data) {
      this.confirmClose()

      //수락 시 제어 팝업 열기
      if (data.isAllowed) {
        this.SET_IS_CONTROL_POP_OVER_VISIBLE(true)
      }
      //거절 시 토스트 띄우기
      else {
        this.SET_CONTROL_ACTIVE(false)
        this.toastDefault(this.$t('service.3d_content_control_request_denied'))
      }
    },

    onControlPopOverClose() {
      this.SET_IS_CONTROL_POP_OVER_VISIBLE(false)
      this.SET_CONTROL_ACTIVE(false)
    },

    //사용자가 권한 요청 보낸 후 요청 취소를 수신한 경우
    onControlCancelRequestReceived() {
      this.confirmClose()
    },

    //현재 컨트롤 상태 수신
    onControlStatusReceived(data) {
      //ar 공유자가 제어 중인지 여부
      const isArDeviceControling =
        !this.controlActive && !this.isControlPopOverVisible

      if (data.currentZoomLevel & isArDeviceControling) {
        this.controlZoomLevel = data.currentZoomLevel
      }
    },

    //자식 component로부터 zoomValue 최종 값 전달 받는 용도
    onReceiveControlZoomLevel(zoomValue) {
      this.controlZoomLevel = Number(zoomValue)
    },

    /**
     * 웹-웹 테스트용!
     */
    doArCapture() {
      // const videoEl = this.$el.querySelector('.ar-video__stream')
      // const width = videoEl.offsetWidth
      // const height = videoEl.offsetHeight
      // const tmpCanvas = document.createElement('canvas')
      // tmpCanvas.width = width
      // tmpCanvas.height = height
      // const tmpCtx = tmpCanvas.getContext('2d')
      // tmpCtx.drawImage(
      //   document.querySelector('.ar-video__stream'),
      //   0,
      //   0,
      //   width,
      //   height,
      // )
      // const imgUrl = tmpCanvas.toDataURL('image/png')
      // this.sendFrame(imgUrl, Date.now())
    },
    sendFrame(imgUrl, id) {
      // const params = {
      //   imgId: id,
      // }
      // const chunkSize = 1024 * 10
      // const chunk = []
      // const base64 = imgUrl.replace(/data:image\/.+;base64,/, '')
      // const chunkLength = Math.ceil(base64.length / chunkSize)
      // let start = 0
      // for (let i = 0; i < chunkLength; i++) {
      //   chunk.push(base64.substr(start, chunkSize))
      //   start += chunkSize
      // }
      // for (let i = 0; i < chunk.length; i++) {
      //   params.chunk = chunk[i]
      //   if (i === 0) {
      //     this.$call.sendArDrawing(AR_DRAWING.FIRST_FRAME, params)
      //   } else if (i === chunk.length - 1) {
      //     this.$call.sendArDrawing(AR_DRAWING.LAST_FRAME, params)
      //   } else {
      //     this.$call.sendArDrawing(AR_DRAWING.FRAME, params)
      //   }
      // }
    },
  },
  created() {
    this.$eventBus.$on(SIGNAL.AR_DRAWING, this.receiveSignal)
    this.$eventBus.$on(`control:${ACTION.AR_3D}:clear`, this.clear3dObject)

    this.$eventBus.$on(SIGNAL.AR_FEATURE, this.checkArFeature)
    this.$eventBus.$on(SIGNAL.AR_3D, this.receiveSignal3d)
    this.$eventBus.$on(SIGNAL.AR_FROM_VUEX, this.receiveQueuedSignal)
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.AR_DRAWING, this.receiveSignal)
    this.$eventBus.$off(`control:${ACTION.AR_3D}:clear`, this.clear3dObject)

    this.$eventBus.$off(SIGNAL.AR_FEATURE, this.checkArFeature)
    this.$eventBus.$off(SIGNAL.AR_3D, this.receiveSignal3d)
    this.$eventBus.$off(SIGNAL.AR_FROM_VUEX, this.receiveQueuedSignal)
    this.reset3dControlStatus()
  },
}
</script>
