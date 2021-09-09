<template>
  <div class="popover-more__menus main">
    <ul>
      <li
        v-for="menu in menus"
        :key="menu.name"
        :class="{ 'sub-menu-icon': menu.subMenuIcon }"
        @click="onSelect(menu)"
      >
        <img :src="menu.icon" alt="" />
        {{ menu.title }}
      </li>
    </ul>
  </div>
</template>

<script>
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
      menus: [
        {
          name: MENU.CAMERA_CONTROL,
          icon: require('assets/image/call/mdpi_icon_camera_control_new.svg'),
          title: this.$t('service.camera_control'),
          subMenuIcon: true,
        },
        {
          name: MENU.SERVER_RECORD,
          icon: require('assets/image/call/mdpi_icon_server_rec_new.svg'),
          title: this.$t('service.record_server'),
        },
        {
          name: MENU.MEMBER,
          icon: require('assets/image/call/mdpi_icon_member_new.svg'),
          title: this.$t('workspace.info_remote_member'),
        },
        {
          name: MENU.LOCATION,
          icon: require('assets/image/call/mdpi_icon_location_new.svg'),
          title: this.$t('service.map_information'),
        },
        {
          name: MENU.SPOT_CONTROL,
          icon: require('assets/image/call/mdpi_icon_spot_new.svg'),
          title: this.$t('service.spot_control'),
          subMenuIcon: true,
        },
      ],
    }
  },
  methods: {
    onSelect(menu) {
      if (menu.name === MENU.CAMERA_CONTROL) this.$emit('selectCameraControl')
    },
  },
}
</script>

<style lang="scss">
.popover-more__menus {
  padding: 0.4rem 0.8rem 1.2rem 1.6rem;
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
