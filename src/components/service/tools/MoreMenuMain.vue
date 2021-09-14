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
      menus: [
        {
          name: MENU.CAMERA_CONTROL,
          icon: require('assets/image/call/mdpi_icon_camera_control_new.svg'),
          title: this.$t('service.camera_control'),
          subMenuIcon: true,
          visible: true,
        },
        {
          name: MENU.SERVER_RECORD,
          icon: require('assets/image/call/mdpi_icon_server_rec_new.svg'),
          title: this.$t('service.record_server'),
          visible: true,
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
          visible: false,
        },
        {
          name: MENU.SPOT_CONTROL,
          icon: require('assets/image/call/mdpi_icon_spot_new.svg'),
          title: this.$t('service.spot_control'),
          subMenuIcon: true,
          visible: false,
        },
      ],
    }
  },
  watch: {
    isOnpremise: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.menus.forEach(menu => {
            if (menu.name === MENU.LOCATION) menu.visible = newVal
            else if (menu.name === MENU.SPOT_CONTROL && SPOT_CONTROL_ACTIVE)
              menu.visible = newVal
          })
        }
      },
    },
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
    ...mapGetters(['mainView', 'useRecording']),
    hasMainView() {
      return this.mainView && this.mainView.id
    },
  },
  methods: {
    onSelect(menu) {
      switch (menu) {
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
