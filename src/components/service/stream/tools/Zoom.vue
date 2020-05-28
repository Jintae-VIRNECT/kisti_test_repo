<template>
  <div class="zoom">
    <tool-button
      :text="$t('상대방 영상 확대/축소')"
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
              :class="{ disable: zoomLevel >= maxLevel }"
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

import * as device from 'utils/deviceinfo'

export default {
  name: 'ToolZoomLevel',
  mixins: [toastMixin],
  components: {
    ToolButton,
  },
  data() {
    return {
      picker: false,
      zoomLevel: 1,
      maxLevel: 5,
      zoomStatus: 'default', // 'default': 초기값 / -1: 초기값 세팅 준비 / 0: 카메라 꺼짐 / 1: 카메라 켜짐 / 2: 카메라 없음 / 3: 권한없음
      toastTime: null,
    }
  },
  computed: {
    ...mapGetters({
      // zoomLevel: 'zoomLevel',
      // maxLevel: 'zoomMax',
      // zoomStatus: 'zoomStatus',
    }),
  },
  methods: {
    ...mapMutations(['deviceUpdate']),
    clickHandler() {
      const toPicker = this.picker

      // if (this.zoomStatus === device.CAMERA_NONE) {
      //   this.toastDefault(this.$t('service.call_device_control_no_camera'))
      //   return
      // }

      // if (this.zoomStatus === device.NO_PERMISSION) {
      //   this.toastDefault(this.$t('service.call_device_control_camera_deny'))
      //   return
      // }
      // this.$eventBus.$emit('control:close')
      this.picker = !toPicker
    },
    hidePicker() {
      this.picker = false
    },
    plus() {
      if (this.zoomLevel >= this.maxLevel) return

      let value = this.zoomLevel + 1
      if (value > this.maxLevel) {
        value = this.maxLevel
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
      if (this.zoomStatus === 'default') {
        this.deviceUpdate({
          zoomStatus: -1,
        })
      } else if (this.zoomStatus === -1) {
        this.toastDefault(this.$t('service.call_device_permission'))
        return
      }
      // this.$remoteSDK.message('cameraControl', { level: level })
    },
    zoomListener(message) {
      if ('status' in message) {
        // 응답
        if (parseInt(message.status) === device.CAMERA_ZOOMING) {
          this.toastNotice(this.$t('service.call_device_control_camera'))
          return
        }
        if (parseInt(message.status) === device.APP_IS_BACKGROUND) {
          this.toastDefault(this.$t('service.call_ar_background_zoom'))
        }
        // this.zoomLevel = parseFloat(message.level)
        // this.zoomStatus = parseInt(message.status)
        this.deviceUpdate({
          zoomLevel: parseFloat(message.level),
          zoomStatus: parseInt(message.status),
        })
        this.$nextTick(() => {
          if (this.zoomStatus === device.NO_PERMISSION) {
            this.toastNotice(this.$t('service.call_device_control_camera_deny'))
            this.hidePicker()
          }
        })
      } else {
        // 웹-웹 테스트용
        // this.$remoteSDK.message('cameraControl', {
        //   level: message.level,
        //   status: device.CAMERA_ON,
        // })
        // this.$remoteSDK.message('cameraControl', { level: this.zoomLevel, status: device.CAMERA_ZOOMING }) // zooming test
      }
    },
    cameraInfoListener(message) {
      if ('currentZoomLevel' in message) {
        const time = Date.now()
        if (!this.toastTime) {
          this.toastTime = time
        } else if (time - this.toastTime > 5000) {
          if (this.zoomLevel !== parseFloat(message.currentZoomLevel)) {
            this.toastDefault(this.$t('service.call_device_control_camera'))
          }
        }
        this.toastTime = time

        // this.zoomLevel = parseFloat(message.currentZoomLevel)
        // this.maxLevel = parseFloat(message.maxZoomLevel)
        this.deviceUpdate({
          zoomLevel: parseFloat(message.currentZoomLevel),
          zoomMax: parseFloat(message.maxZoomLevel),
        })
        // 디바이스 세팅 권한이 거절이면 초기 한번의 권한요청이 필요함
        if (
          parseInt(message.status) === device.NO_PERMISSION &&
          this.zoomStatus === 'default'
        ) {
          return
        }
        // 권한이 거절된 카메라는 상태를 바꿀 수 없음. zoom 레벨만 표출
        // 카메라 제어중일 때는 상태를 바꾸지 않음. zoom 레벨만 표출
        if (
          this.zoomStatus !== device.NO_PERMISSION &&
          parseInt(message.status) !== device.CAMERA_ZOOMING
        ) {
          // this.zoomStatus = parseInt(message.status)
          this.deviceUpdate({
            zoomStatus: parseInt(message.status),
          })
        }
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
