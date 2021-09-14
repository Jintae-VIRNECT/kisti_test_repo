<template>
  <section class="spot-control-layout">
    <spot-camera-layer-mobile
      v-if="isMobileSize || isTabletSize"
      :cameraBlockList="cameraBlockList"
      :frontLeftImage="frontLeftImage"
      :frontRightImage="frontRightImage"
      :sideLeftImage="sideLeftImage"
      :sideRightImage="sideRightImage"
      :backImage="backImage"
    ></spot-camera-layer-mobile>

    <spot-camera-layer
      v-else
      :cameraBlockList="cameraBlockList"
      :frontLeftImage="frontLeftImage"
      :frontRightImage="frontRightImage"
      :sideLeftImage="sideLeftImage"
      :sideRightImage="sideRightImage"
      :backImage="backImage"
    ></spot-camera-layer>

    <main-control
      :estop="estop"
      :power="power"
      :estopBtn="estopBtn"
      :estopHighlight="estopHighlight"
      :motorBtn="motorBtn"
      :motorHighlight="motorHighlight"
    ></main-control>
    <battery-status v-if="battery" :battery="battery"></battery-status>
    <stand-sit-control :sitStandBtn="sitStandBtn"></stand-sit-control>
    <move-control :moveBtn="moveBtn"></move-control>
    <div v-if="isMobileSize" class="control-back-layer"></div>

    <spot-connect-modal
      :isReconnect="isReconnect"
      :visible.sync="visibleSpotConnectModal"
      @reconnectCancel="onReconnectCancel"
    ></spot-connect-modal>
  </section>
</template>

<script>
import SpotCameraLayer from './partials/SpotCameraLayer'
import SpotCameraLayerMobile from './partials/SpotCameraLayerMobile'

import MainControl from './partials/MainControl'
import StandSitControl from './partials/StandSitControl'
import MoveControl from './partials/MoveControl'
import BatteryStatus from './partials/BatteryStatus'
import SpotConnectModal from './modal/SpotConnectModal'

import confirmMixin from 'mixins/confirm'
import spotMixin from 'mixins/spot'

import { spotControlRouterGuard } from 'utils/validator'

export default {
  components: {
    SpotCameraLayer,
    SpotCameraLayerMobile,
    MoveControl,
    StandSitControl,
    MainControl,
    BatteryStatus,
    SpotConnectModal,
  },
  //spot 페이지 진입 라우터 가드
  // beforeRouteEnter(to, from, next) {
  //   spotControlRouterGuard(next)
  // },
  mixins: [confirmMixin, spotMixin],
  data() {
    return {
      isReconnect: false,
      visibleSpotConnectModal: true,
      cameraBlockList: {
        front: [
          {
            name: 'fl',
            title: 'Front Camera 01',
            ratio: 'potrait',
            video: this.frontLeft,
            option: {
              //clearRect: [0, 0, 480, 640],
              //translate: [240, 320],
              //rotate: (90 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
          {
            name: 'fr',
            title: 'Front Camera 02',
            ratio: 'potrait',
            video: null,
            option: {
              //clearRect: [0, 0, 480, 640],
              //translate: [240, 320],
              //rotate: (90 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
        ],
        side: [
          {
            name: 'sl',
            title: 'Left Camera',
            ratio: 'landscape',
            video: null,
            option: null,
          },
          {
            name: 'sr',
            title: 'Right Camera',
            ratio: 'landscape',
            video: null,
            option: {
              upsideDown: true,
              //clearRect: [0, 0, 640, 480],
              //translate: [320, 240],
              //rotate: (180 * Math.PI) / 180,
              //drawImage: [-320, -240],
            },
          },
        ],
        back: [
          {
            name: 'b',
            title: 'Back Camera',
            ratio: 'landscape',
            video: null,
            option: null,
          },
        ],
      },
    }
  },
  watch: {
    frontLeftImage(val) {
      this.cameraBlockList['front'][0].video = val
    },
    frontRightImage(val) {
      this.cameraBlockList['front'][1].video = val
    },
    sideLeftImage(val) {
      this.cameraBlockList['side'][0].video = val
    },
    sideRightImage(val) {
      this.cameraBlockList['side'][1].video = val
    },
    backImage(val) {
      this.cameraBlockList['back'][0].video = val
    },
  },
  methods: {
    onReconnectCancel() {
      this.visibleSpotConnectModal = false
      close()
    },
  },
}
</script>

<style lang="scss" src="assets/style/spot.scss"></style>
