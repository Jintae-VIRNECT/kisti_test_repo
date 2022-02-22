<template>
  <div class="popover-more__menus camera-control">
    <button class="back-btn" @click="backToMain">
      <img src="~assets/image/call/mdpi_icon_arrow_left_new.svg" alt="" />
      <span>{{ $t('button.previous') }}</span>
    </button>
    <ul>
      <li class="popover-more__camera-allow" @click="setControlRestrict">
        <img src="~assets/image/call/mdpi_icon_camera_allow_new.svg" alt="" />
        {{
          allowCameraControlFlag
            ? $t('service.camera_control_limit')
            : $t('service.camera_control_allow')
        }}
      </li>
      <li class="popover-more__all-camera-on" @click="setCameraStatue(true)">
        <img src="~assets/image/call/mdpi_icon_camera_on_new.svg" alt="" />
        {{ $t('service.camera_control_every_on') }}
      </li>
      <li class="popover-more__all-camera-off" @click="setCameraStatue(false)">
        <img src="~assets/image/call/mdpi_icon_camera_off_new.svg" alt="" />
        {{ $t('service.camera_control_every_off') }}
      </li>
    </ul>
  </div>
</template>

<script>
import { CONTROL } from 'configs/remote.config'
import { mapMutations, mapGetters } from 'vuex'

export default {
  name: 'MoreMenuCamera',
  watch: {
    allowCameraControlFlag(flag) {
      this.SET_ALLOW_CAMERA_CONTROL(flag)
      this.$call.sendControlRestrict('video', !flag)
    },
  },
  computed: {
    ...mapGetters(['allowCameraControlFlag']),
  },
  methods: {
    ...mapMutations(['SET_ALLOW_CAMERA_CONTROL']),
    backToMain() {
      this.$emit('backToMain')
    },
    setControlRestrict() {
      const flag = !this.allowCameraControlFlag
      this.SET_ALLOW_CAMERA_CONTROL(flag)
    },
    setCameraStatue(flag) {
      this.$call.sendControl(CONTROL.VIDEO, flag)
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.popover-more__menus.camera-control {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 0;
  background: none;
  border: none;
  border-radius: 0;
  transform: unset;

  .back-btn {
    display: flex;
    align-items: center;
    width: fit-content;
    padding-right: 1.2143rem;
    height: 2.8rem;
    margin-bottom: 1rem;
    background-color: $new_color_popup_bg;
    border-radius: 1.4rem;
    img {
      margin-right: 0.2rem;
    }
    span {
      margin-top: 0.2rem;
    }
  }

  ul {
    padding: 1.2rem 2rem;
    background-color: $new_color_popup_bg;
    border-radius: 0.8rem;
  }
}
</style>
