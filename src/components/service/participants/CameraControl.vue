<template>
  <article class="control-btn">
    <popover
      placement="top"
      width="auto"
      trigger="click"
      popperClass="camera-control"
      @visible="visible"
    >
      <button
        slot="reference"
        class="camera-control__btn"
        :class="{ active: showControl }"
      >
        {{ $t('service.camera_control') }}
      </button>
      <div class="camera-control__body">
        <span>{{ $t('service.camera_control') }}</span>
        <check
          :text="$t('service.camera_control_allow')"
          :value="flagAllowCameraControl"
          @click="toggleFlagAllowCameraControl"
        ></check>
        <button class="camera-control__body-btn" @click="setCameraStatue(true)">
          {{ $t('service.camera_control_every_on') }}
        </button>
        <button
          class="camera-control__body-btn"
          @click="setCameraStatue(false)"
        >
          {{ $t('service.camera_control_every_off') }}
        </button>
      </div>
    </popover>
  </article>
</template>

<script>
import toastMixin from 'mixins/toast'
import { CONTROL } from 'configs/remote.config'

import Popover from 'Popover'
import Check from 'Check'
import { mapMutations, mapGetters } from 'vuex'

export default {
  name: 'ParticipantVideo',
  mixins: [toastMixin],
  components: {
    Popover,
    Check,
  },
  data() {
    return {
      flagAllowCameraControl: false,
      showControl: false,
    }
  },
  watch: {
    flagAllowCameraControl(flag) {
      this.SET_ALLOW_CAMERA_CONTROL(flag)
    },
  },
  computed: {
    ...mapGetters(['allowCameraControlFlag']),
  },
  methods: {
    ...mapMutations(['SET_ALLOW_CAMERA_CONTROL']),
    setCameraStatue(flag) {
      this.$call.sendControl(CONTROL.VIDEO, flag)
    },
    visible(val) {
      this.showControl = val
    },
    toggleFlagAllowCameraControl() {
      this.$call.sendControlRestrict('video', this.flagAllowCameraControl)
      this.flagAllowCameraControl = !this.flagAllowCameraControl
    },
  },
  created() {
    this.flagAllowCameraControl = this.allowCameraControlFlag
  },
}
</script>
<style lang="scss">
@import '~assets/style/mixin';
.control-btn {
  > .popover--wrapper {
    position: relative;
    top: 50%;
    transform: translateY(-50%);
  }
}
.camera-control {
  min-width: 183px;
  background: #313135;
  border: solid 1px #3d4147;
  border-radius: 2px;
  transform: translateY(-11px);
  > .popover--body {
    padding: 16px;
  }
}
.camera-control__btn {
  position: relative;
  width: 38px;
  height: 38px;
  background: url('~assets/image/call/ic_camera_control.svg') center/24px
    no-repeat #3c3c3c;
  border: solid 1px #5c5c5c;
  border-radius: 2px;
  @include ir();
  &.active {
    background: url('~assets/image/call/ic_camera_control_active.svg')
      center/24px no-repeat #626368;
  }
}
.camera-control__body {
  display: flex;
  flex-direction: column;
  > span {
    font-size: 12px;
  }
  > .check {
    margin: 8px 0;
    background-color: #262a31;
    border-color: #494b50;
    .check-toggle {
      width: 20px;
      height: 20px;
      border-color: #494b50;
    }
    .check-text {
      font-size: 13px;
    }
    &.toggle {
      background-color: $color_primary;
      border-color: transparent;
      .check-toggle {
        border-color: transparent;
      }
    }
  }
}
.camera-control__body-btn {
  width: 100%;
  margin-top: 8px;
  padding: 6px;
  color: #fff;
  font-size: 13px;
  background-color: #626368;
  border-radius: 3px;
  &:hover {
    background-color: #808189;
  }
}
</style>
