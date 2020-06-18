<template>
  <div class="zoom">
    <tool-button
      :text="'상대방 영상 확대/축소'"
      :active="picker"
      :src="require('assets/image/ic_zoom.svg')"
      :activeSrc="require('assets/image/ic_zoom.svg')"
      @click.stop="clickHandler()"
    ></tool-button>

    <transition name="tool">
      <div class="picker--container" v-if="picker" @click.stop>
        <div class="picker--container__range">
          <div class="picker--container__body">
            <button
              class="minus"
              :class="{ disable: zoomLevel <= 1 }"
              @click="minus"
            >
              -
            </button>
            <!-- <span class="percent">{{ 'x' + parseFloat(zoomLevel).toFixed(2) }}</span> -->
            <span class="percent">{{ parseInt(zoomLevel * 100) + '%' }}</span>
            <button
              class="plus"
              :class="{ disable: zoomLevel >= zoomMax }"
              @click="plus"
            >
              +
            </button>
          </div>
          <button class="reset" @click="reset">Reset</button>
        </div>
        <div class="picker--container__division"></div>
        <button class="picker--container__close" @click="hidePicker">
          {{ $t('service.call_zoom_close') }}
        </button>
      </div>
    </transition>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from 'vuex'
import toastMixin from 'mixins/toast'
import ToolButton from 'ToolButton'

import { CAMERA } from 'configs/device.config'

export default {
  name: 'ToolZoomLevel',
  mixins: [toastMixin],
  components: {
    ToolButton,
  },
  data() {
    return {
      picker: false,
      // zoomLevel: 1,
      // zoomMax: 5,
      // cameraStatus: 'default', // 'default': 초기값 / -1: 초기값 세팅 준비 / 0: 카메라 꺼짐 / 1: 카메라 켜짐 / 2: 카메라 없음 / 3: 권한없음
      toastTime: null,
    }
  },
  computed: {
    ...mapGetters(['deviceInfo', 'mainView']),
    zoomLevel() {
      if (this.mainView && this.mainView.id) {
        return this.deviceInfo.zoomLevel
      }
      return 1
    },
    zoomMax() {
      if (this.mainView && this.mainView.id) {
        return this.deviceInfo.zoomMax
      }
      return 1
    },
    cameraStatus() {
      if (this.mainView && this.mainView.id) {
        return this.deviceInfo.camera
      }
      return -1
    },
  },
  watch: {
    cameraStatus(level) {
      this.cameraListener(level)
    },
  },
  methods: {
    ...mapMutations(['deviceUpdate']),
    clickHandler() {
      const toPicker = this.picker

      if (this.cameraStatus === CAMERA.CAMERA_NONE) {
        this.toastDefault('카메라가 없는 기기입니다.')
        return
      }

      if (this.cameraStatus === CAMERA.NO_PERMISSION) {
        this.toastDefault('상대방이 카메라 제어 허가 요청을 거절하였습니다.')
        return
      }
      this.$eventBus.$emit('control:close')
      this.picker = !toPicker
    },
    hidePicker() {
      this.picker = false
    },
    plus() {
      if (this.zoomLevel >= this.zoomMax) return

      let value = this.zoomLevel + 1
      if (value > this.zoomMax) {
        value = this.zoomMax
      }
      this.changeZoomLevel(value)
    },
    minus() {
      if (this.zoomLevel <= 1) return

      let value = this.zoomLevel - 1
      if (value < 1) {
        value = 1
      }
      this.changeZoomLevel(value)
    },
    reset() {
      if (this.zoomLevel === 1) return

      this.changeZoomLevel(1)
    },
    changeZoomLevel(level) {
      if (this.cameraStatus === -1) {
        this.toastDefault('상대방 기기 제어 권한을 요청 중입니다.')
        return
      }
      this.$call.camera({ level: level })
    },
    cameraListener(status) {
      // 응답
      if (parseInt(status) === CAMERA.CAMERA_ZOOMING) {
        this.toastNotice('상대방이 영상을 확대/축소하고 있습니다.')
        return
      }
      if (parseInt(status) === CAMERA.APP_IS_BACKGROUND) {
        this.toastDefault(
          '상대방 앱이 비활성화 상태입니다. 확대/축소 기능을 사용할 수 없습니다.',
        )
      }
      if (parseInt(status) === CAMERA.NO_PERMISSION) {
        this.toastNotice('상대방이 카메라 제어 허가 요청을 거절하였습니다.')
        this.hidePicker()
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$eventBus.$on('control:close', this.hidePicker)
    window.addEventListener('click', this.hidePicker)
  },
  beforeDestroy() {
    this.$eventBus.$off('control:close', this.hidePicker)
    window.removeEventListener('click', this.hidePicker)
  },
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/_mixin';
.zoom {
  position: relative;
}
.picker--container {
  top: auto;
  bottom: 100%;
  display: flex;
  border: solid 1px rgba(#fff, 0.13);
}
.picker--container__range {
  display: flex;
  > .reset {
    margin: 12px 18px 12px 0;
    padding: 7px 10px;
    color: #f5f8ff;
    font-size: 12px;
    line-height: 12px;
    background-color: transparent;
    border: solid 1px #d3d3d3;
    border-radius: 3px;
  }
}

.picker--container__body {
  display: flex;
  padding: 16px 18px;

  > .minus,
  > .plus {
    @include ir();
    width: 20px;
    height: 20px;
    margin: auto 0;
    &.disable {
      opacity: 0.4;
      pointer-events: none;
    }
  }

  > .minus {
    background: url('~assets/image/ic_subtract.png') 50%/20px no-repeat;
  }
  > .plus {
    background: url('~assets/image/ic_addition.png') 50%/20px no-repeat;
  }

  > .percent {
    margin: auto 16px;
    color: #fafafa;
    font-size: 13px;
    line-height: 13px;
  }
}

.picker--container__division {
  width: 1px;
  height: 26px;
  margin: auto 0;
  background-color: rgba(#fff, 0.13);
}

.picker--container__close {
  @include ir();
  width: 40px;
  height: 40px;
  margin: auto 7px;
  background: url('~assets/image/ic-close-w.svg') 50% no-repeat;
}

.tool-enter-active {
  transition: 0.3s transform ease;
}
.tool-enter {
  transform: translate(-50%, 10px);
}
</style>
