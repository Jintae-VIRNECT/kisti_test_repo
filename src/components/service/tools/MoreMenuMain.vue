<template>
  <div class="popover-more__menus main">
    <ul>
      <li
        v-for="menu in menus"
        :key="menu.name"
        :class="{
          'sub-menu-icon': menu.subMenuIcon,
          disabled: menu.disabled,
          hidden: !menu.visible,
        }"
        @click="onSelect(menu)"
      >
        <img :src="menu.icon" alt="" />
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
      isSpotControlActive: SPOT_CONTROL_ACTIVE, //spot control 활성화 여부 (from config서버)
    }
  },
  watch: {
    hasMainView: {
      immediate: true,
      handler(newVal) {
        this.menus[1].disabled = !newVal
      },
    },
    useRecording: {
      immediate: true,
      handler(newVal) {
        this.menus[1].visible = newVal
      },
    },
  },
  computed: {
    ...mapGetters(['mainView', 'useRecording', 'restrictedRoom']),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
    menus() {
      return [
        {
          name: MENU.CAMERA_CONTROL,
          icon: require('assets/image/call/mdpi_icon_camera_control_new.svg'),
          title: this.$t('service.camera_control'),
          subMenuIcon: true,
          visible: this.restrictedRoom,
        },
        {
          name: MENU.SERVER_RECORD,
          icon: require('assets/image/call/mdpi_icon_server_rec_new.svg'),
          title: this.$t('service.record_server'),
          visible: true, //@TODO 확인
        },
        {
          name: MENU.MEMBER,
          icon: require('assets/image/call/mdpi_icon_member_new.svg'),
          title: this.$t('workspace.info_remote_member'),
          visible: true,
        },
        {
          name: MENU.LOCATION,
          icon: require('assets/image/call/mdpi_icon_location_new.svg'),
          title: this.$t('service.map_information'),
          visible: this.isOnpremise, //@TODO 확인
        },
        {
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
        case MENU.MEMBER:
          this.$emit('selectMember')
          break
        case MENU.CAMERA_CONTROL:
          this.$emit('selectCameraControl')
          break
        case MENU.SPOT_CONTROL:
          window.open('/spot-control')
          break
      }
    },
  },
}
</script>

<style lang="scss">
.popover-more__menus {
  padding: 0.4rem 0.8rem 1.2rem 1.6rem;
}

li.disabled {
  opacity: 0.4;
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
