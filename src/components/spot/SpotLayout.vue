<template>
  <section class="spot-control-layout">
    <spot-camera-layer
      :frontLeftImage="frontLeftImage"
      :frontRightImage="frontRightImage"
      :sideLeftImage="sideLeftImage"
      :sideRightImage="sideRightImage"
      :backImage="backImage"
    ></spot-camera-layer>

    <!--카메라 전체화면 -->
    <!-- <spot-camera-full></spot-camera-full> -->

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

    <spot-connect-modal
      :isReconnect="isReconnect"
      :visible.sync="visibleSpotConnectModal"
      @reconnectCancel="onReconnectCancel"
    ></spot-connect-modal>
  </section>
</template>

<script>
import SpotCameraLayer from './partials/SpotCameraLayer'
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
    MoveControl,
    StandSitControl,
    MainControl,
    BatteryStatus,
    SpotConnectModal,
  },
  beforeRouteEnter(to, from, next) {
    spotControlRouterGuard(next)
  },
  mixins: [confirmMixin, spotMixin],
  data() {
    return {
      isReconnect: false,
      visibleSpotConnectModal: true,
    }
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
