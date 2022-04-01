import { AR_3D_CONTROL_TYPE } from 'configs/remote.config'
import { mapGetters } from 'vuex'

const AXIS = {
  X: 'X',
  Y: 'Y',
  Z: 'Z',
}

const FIVE_CM = 5
const TEN_DEGREE = 10

export default {
  props: {
    //ar 공유자로부터 수신받는 zoomlevel
    controlZoomLevel: {
      type: Number,
      default: 100,
    },
  },
  data() {
    return {
      zoomValue: 100,
      controlPart: AR_3D_CONTROL_TYPE.MOVE,
      AR_3D_CONTROL_TYPE: Object.freeze(AR_3D_CONTROL_TYPE),
      AXIS: Object.freeze(AXIS),
    }
  },
  computed: {
    ...mapGetters(['mainView']),
  },
  watch: {
    controlZoomLevel: {
      immediate: true,
      handler(newVal) {
        //50 ~ 200 범위를 넘어가지 않도록 함
        if (newVal > 200) {
          this.zoomValue = 200
        } else if (newVal < 50) {
          this.zoomValue = 50
        } else {
          this.zoomValue = newVal
        }
      },
    },
    zoomValue(newVal) {
      this.sendZoomStatus(newVal)
    },
  },
  methods: {
    setControlPart(controlPart) {
      this.controlPart = controlPart
    },

    //줌 정보 전달
    sendZoomStatus(level) {
      this.$call.sendAr3dSharing(AR_3D_CONTROL_TYPE.ZOOM, { level }, [
        this.mainView.connectionId,
      ])
    },

    //direction : 부호. -1 or 1
    //axis : 축 X, Y, Z
    setControl(direction, axis) {
      //move인 경우 5cm 씩, rotate인 경우 10도 씩
      const value =
        this.controlPart === AR_3D_CONTROL_TYPE.MOVE ? FIVE_CM : TEN_DEGREE

      const param = {
        type: this.controlPart,
        posX: axis === AXIS.X ? direction * value : 0,
        posY: axis === AXIS.Y ? direction * value : 0,
        posZ: axis === AXIS.Z ? direction * value : 0,
      }

      this.$call.sendAr3dSharing(this.controlPart, param, [
        this.mainView.connectionId,
      ])
    },

    //제어 reset
    resetControl() {
      this.zoomValue = 100 //zoom value는 직접 초기화 해준다.

      this.$call.sendAr3dSharing(AR_3D_CONTROL_TYPE.RESET, {}, [
        this.mainView.connectionId,
      ])
    },

    transmitParentcontrolZoomLevel() {
      this.$emit('controlZoomLevel', this.zoomValue)
    },
  },
  beforeDestroy() {
    this.transmitParentcontrolZoomLevel()
  },
}
