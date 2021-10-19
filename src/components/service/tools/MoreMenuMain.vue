<template>
  <div class="popover-more__menus main">
    <ul>
      <li
        v-for="menu in menus"
        :key="menu.name"
        :class="{
          [menu.class]: true,
          'sub-menu-icon': menu.subMenuIcon,
          disabled: menu.name === MENU.SERVER_RECORD && !hasMainView,
          hidden: !menu.visible,
        }"
        @click="onSelect(menu)"
      >
        <img
          :src="
            menu.name === MENU.SERVER_RECORD && isRecording
              ? menu.activeIcon
              : menu.icon
          "
          alt=""
        />
        {{ menu.title }}
      </li>
    </ul>
  </div>
</template>

<script>
import { SPOT_CONTROL_ACTIVE } from 'configs/env.config'
import { mapGetters } from 'vuex'

const MENU = {
  CAMERA_CONTROL: 'CAMERA_CONTROL',
  SERVER_RECORD: 'SERVER_RECORD',
  MEMBER: 'MEMBER',
  LOCATION: 'LOCATION',
  SPOT_CONTROL: 'SPOT_CONTROL',
}

export default {
  data() {
    return {
      MENU: Object.freeze(MENU),
      isSpotControlActive: SPOT_CONTROL_ACTIVE, //spot control 활성화 여부 (from config서버)
    }
  },
  computed: {
    ...mapGetters([
      'mainView',
      'useRecording',
      'restrictedRoom',
      'serverRecordStatus',
    ]),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
    isWaiting() {
      return this.serverRecordStatus === 'WAIT'
    },
    isRecording() {
      const isNotStopped = this.serverRecordStatus !== 'STOP'
      const isNotDisabled = this.hasMainView
      const isNotPreparing = !this.isPreparing

      return isNotStopped && isNotDisabled && isNotPreparing
    },
    isPreparing() {
      return this.serverRecordStatus === 'PREPARE'
    },
    menus() {
      return [
        {
          class: 'camera-contol',
          name: MENU.CAMERA_CONTROL,
          icon: require('assets/image/call/mdpi_icon_camera_control_new.svg'),
          title: this.$t('service.camera_control'),
          subMenuIcon: true,
          visible: this.restrictedRoom,
        },
        {
          class: 'server-record',
          name: MENU.SERVER_RECORD,
          icon: require('assets/image/call/mdpi_icon_server_rec_new.svg'),
          activeIcon: require('assets/image/call/mdpi_icon_server_rec_ing_new.svg'),
          title: this.isRecording
            ? this.$t('service.record_server_end')
            : this.$t('service.record_server'),
          visible: this.useRecording,
        },
        {
          class: 'member',
          name: MENU.MEMBER,
          icon: require('assets/image/call/mdpi_icon_member_new.svg'),
          title: this.$t('workspace.info_remote_member'),
          visible: true,
        },
        {
          class: 'location',
          name: MENU.LOCATION,
          icon: require('assets/image/call/mdpi_icon_location_new.svg'),
          title: this.$t('service.map_information'),
          visible: this.isOnpremise,
        },
        {
          class: 'spot-control',
          name: MENU.SPOT_CONTROL,
          icon: require('assets/image/call/mdpi_icon_spot_new.svg'),
          title: this.$t('service.spot_control'),
          subMenuIcon: true,
          visible: this.isSpotControlActive && this.isOnpremise,
        },
      ]
    },
  },
  methods: {
    onSelect(menu) {
      switch (menu.name) {
        case MENU.CAMERA_CONTROL:
          this.$emit('selectCameraControl')
          break
        case MENU.SERVER_RECORD:
          this.recording()
          break
        case MENU.MEMBER:
          this.$eventBus.$emit('popover:close')
          this.$emit('selectMember')
          break
        case MENU.SPOT_CONTROL:
          this.$eventBus.$emit('popover:close')
          window.open('/spot-control')
          break
      }
    },
    recording() {
      if (!this.hasMainView) return false
      if (this.isWaiting) return false

      if (this.isRecording) {
        this.stop()
      } else {
        this.start()
      }
    },
    start() {
      this.$eventBus.$emit('serverRecord', 'WAIT')
    },
    stop() {
      this.$eventBus.$emit('serverRecord', 'STOP')
    },
  },
}
</script>

<style lang="scss">
.popover-more__menus {
  padding: 0.4rem 0.8rem 1.2rem 1.6rem;
}

.popover-more__menus.main li {
  opacity: 1;
  pointer-events: all;
  &.disabled {
    opacity: 0.4;
    pointer-events: none;
  }
}

.popover-more
  > .popover--body
  .popover-more__menu-container
  > .popover-more__menus
  > ul
  > li.hidden {
  display: none;
}

li.sub-menu-icon {
  position: relative;
  &::after {
    position: absolute;
    right: 8px;
    width: 2.2rem;
    height: 2.2rem;
    background: url(~assets/image/call/mdpi_icon_arrow_new.svg) center no-repeat;
    content: '';
  }
}
</style>
