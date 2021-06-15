import {
  spotServerConnect,
  activateSpotConnectResponse,
  spotRunningStateListener,
  spotCameraImageListenerFR,
  spotCameraImageListenerFL,
  spotCameraImageListenerL,
  spotCameraImageListenerR,
  spotCameraImageListenerB,
} from 'plugins/remote/spot/spotSocket'

import { ESTOP_STATE, MOTOR_POWER } from 'configs/spot.config.js'

export default {
  data() {
    return {
      battery: null,
      estop: null,
      power: null,

      estopBtn: true,
      estopHighlight: false,
      motorBtn: false,
      motorHighlight: false,
      sitStandBtn: false,
      moveBtn: false,

      flImageIntervalId: null,
      frontLeftImage: null,

      frImageIntervalId: null,
      frontRightImage: null,

      slImageIntervalId: null,
      sideLeftImage: null,

      srImageIntervalId: null,
      sideRightImage: null,

      bImageIntervalId: null,
      backImage: null,

      batteryWarnIntervalId: null,
    }
  },
  watch: {
    battery(val, oldVal) {
      if (val !== oldVal) {
        this.showBatteryWarning(val) //15%로 이하시 경고 팝업
      }
    },
  },
  methods: {
    async connectAndActivateListener() {
      try {
        await spotServerConnect(
          'REOMTE',
          this.onConnectionError,
          this.onSpotError,
        )
      } catch (e) {
        this.$router.push('/spot-error')
        return
      }

      activateSpotConnectResponse(this.onSpotConnectStatusResponse)
      spotRunningStateListener(this.spotRunningStateListener)
      spotCameraImageListenerFR(data => {
        if (this.flImageIntervalId) clearTimeout(this.flImageIntervalId)
        this.frontLeftImage = data
        this.flImageIntervalId = setTimeout(() => {
          this.frontLeftImage = null
        })
      }) //오른쪽 카메라가 왼쪽공간을 비추고 있으므로 바꿔줌
      spotCameraImageListenerFL(data => {
        if (this.frImageIntervalId) clearTimeout(this.frImageIntervalId)
        this.frontRightImage = data
        this.frImageIntervalId = setTimeout(() => {
          this.frontRigtImage = null
        })
      }) //왼쪽 카메라가 오른쪽공간을 비추고 있으므로 바꿔줌
      spotCameraImageListenerR(data => {
        if (this.srImageIntervalId) clearTimeout(this.srImageIntervalId)
        this.sideRightImage = data
        this.srImageIntervalId = setTimeout(() => {
          this.sideRightImage = null
        })
      }) //왼쪽 카메라가 오른쪽공간을 비추고 있으므로 바꿔줌
      spotCameraImageListenerL(data => {
        if (this.slImageIntervalId) clearTimeout(this.slImageIntervalId)
        this.sideLeftImage = data
        this.slImageIntervalId = setTimeout(() => {
          this.sideLeftImage = null
        })
      }) //왼쪽 카메라가 오른쪽공간을 비추고 있으므로 바꿔줌
      spotCameraImageListenerB(data => {
        if (this.bImageIntervalId) clearTimeout(this.bImageIntervalId)
        this.backImage = data
        this.bImageIntervalId = setTimeout(() => {
          this.backImage = null
        })
      }) //왼쪽 카메라가 오른쪽공간을 비추고 있으므로 바꿔줌
    },
    //spot error 발생 시
    onSpotError() {},

    //spot 연결 여부 응답 콜백
    onSpotConnectStatusResponse(data, spotInitConnected) {
      //spot 연결 완료 - 팝업 닫기
      if (data) this.visibleSpotConnectModal = false
      //spot 연결 중 - 팝업 열기
      else {
        if (this.visibleSpotConnectModal) return //이미 팝업 떠있다면 exit

        if (spotInitConnected) {
          this.isReconnect = true //연결 중/재연결 중 문구 표기 위한 플래그
        }
        this.visibleSpotConnectModal = true
      }
    },

    //spot 운전 정보 수신 콜백
    spotRunningStateListener(data) {
      const { battery, estop, power } = data

      this.battery = battery
      this.estop = estop
      this.power = power

      //(비상)정지된 상태
      if (estop === ESTOP_STATE.ESTOPPED) {
        this.estopBtn = true
        this.estopHighlight = false
        this.sitStandBtn = false
        this.motorBtn = false
      } else {
        //켜져있는 상태 - estop(비상정지) 버튼 비활성화, sit/stand 가능, 모터 활성화 상태 표시
        if (power === MOTOR_POWER.ON) {
          this.estopBtn = false
          this.sitStandBtn = true
          this.motorHighlight = true
        }
        //motor 꺼져있는 상태 - motor 킬 수 있어야하고, sit/stand 불가능, estop 불가능
        else if (power === MOTOR_POWER.OFF) {
          this.estopBtn = true
          this.sitStandBtn = false
          this.motorHighlight = false
        }

        this.motorBtn = true
        this.estopHighlight = true
      }
    },

    //spot 서버와 연결 상태가 불량인 경우 - 재시도/종료
    onConnectionError() {
      const retryAction = () => this.connectAndActivateListener()
      const cancelAction = () => close()

      this.confirmCancel(
        this.$t('service.spot_networ_error'),
        {
          text: this.$t('button.reconnect'),
          action: retryAction,
        },
        {
          text: this.$t('button.exit'),
          action: cancelAction,
        },
      )
    },

    showBatteryWarning(battery) {
      if (battery <= 15)
        this.confirmDefault(
          this.$t('service.spot_battery_warning', { battery }),
        )
    },
  },
  created() {
    this.connectAndActivateListener()
  },
}
