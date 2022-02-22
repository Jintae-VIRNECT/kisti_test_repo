<template>
  <popover
    placement="top-start"
    width="18.8rem"
    trigger="click"
    :popperClass="`popover-more${isCameraMenu ? ' camera-menu' : ''}`"
    :useTopMargin="-10"
    @visible="setVisible"
    :hide="onHide"
  >
    <button
      slot="reference"
      class="mobile-more-btn"
      :class="{ visible }"
    ></button>
    <div>
      <div class="popover-more__tools">
        <stream size="3.6rem" :tooltipActive="false"></stream>
        <mobile-self-flash-button></mobile-self-flash-button>
        <mic size="3.6rem" :tooltipActive="false"></mic>
        <mobile-setting-button v-if="isSettingVisible"></mobile-setting-button>
      </div>

      <div class="popover-more__back"></div>

      <div
        class="popover-more__menu-container"
        :class="{ swipe: isCameraMenu, 'not-swipe': !restrictedRoom }"
      >
        <more-menu-camera
          v-if="restrictedRoom"
          @backToMain="onBackToMain"
        ></more-menu-camera>
        <more-menu-main
          @selectMember="onSelectMember"
          @selectCameraControl="onSelectCameraControl"
        ></more-menu-main>
      </div>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
import Stream from '../../../header/tools/Stream.vue'
import Mic from '../../../header/tools/Mic'
import MobileSelfFlashButton from './MobileSelfFlashButton'
import MobileSettingButton from './MobileSettingButton'
import MoreMenuMain from '../MoreMenuMain'
import MoreMenuCamera from '../MoreMenuCamera'
import { mapGetters } from 'vuex'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'MobileMoreButton',
  components: {
    Popover,
    Stream,
    Mic,
    MobileSelfFlashButton,
    MobileSettingButton,
    MoreMenuMain,
    MoreMenuCamera,
  },
  data() {
    return {
      visible: false,
      isCameraMenu: false,
    }
  },
  computed: {
    ...mapGetters(['useTranslate', 'restrictedRoom']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isSettingVisible() {
      if (this.isSafari && !this.isLeader && !this.useTranslate) {
        return false
        //safari에서는 로컬녹화 설정이 지원되지 않음. 따라서 사파리 브라우저에서 리더가 아니고 번역기능을 사용하지 않으면 세팅창이 필요가 없으므로 visible false
      } else {
        return true
      }
    },
  },
  methods: {
    setVisible(visible) {
      this.visible = visible
    },
    onBackToMain() {
      this.isCameraMenu = false
    },
    onSelectCameraControl() {
      this.isCameraMenu = true
    },
    onHide() {
      setTimeout(() => {
        this.isCameraMenu = false
      }, 400)
    },
    onSelectMember() {
      this.$emit('selectMember')
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-more-btn {
  background: url(~assets/image/call/mdpi_icon_more_new.svg) center/2.4rem
    no-repeat;
  @include mobile-circle-btn($new_color_bg_button_primary);
  transition: transform 0.3s;

  &.visible {
    transform: rotate(45deg);
  }
}
</style>

<style lang="scss">
@import '~assets/style/mixin';

.popover-more {
  background-color: $new_color_popup_bg;
  transition: background-color 0.3s;

  &.camera-menu {
    background-color: transparent;
    box-shadow: unset;
    > .popover--body .popover-more__tools {
      opacity: 0;
    }
  }
}

.popover-more > .popover--body {
  padding: 0;
  .popover-more__tools {
    display: flex;
    padding: 1rem;
    background-color: $new_color_bg_popover;
    opacity: 1;
    transition: opacity 0.2s;

    .stream,
    .mic {
      opacity: 0.7;
    }

    button {
      margin-right: 0.8rem;
    }
  }
  .popover-more__menu-container {
    display: flex;
    transform: translateX(-188px);
    transition: transform 0.3s;

    &.not-swipe {
      transform: translateX(0px);
      // .popover-more__menus {
      //   padding-bottom: 4px;
      // }
    }

    &.swipe {
      min-height: 18.2rem;
      transform: translateX(0px);
    }

    .popover-more__menus {
      min-width: 18.8rem;
      color: $new_color_text_sub;
      @include fontLevel(75);

      li {
        display: flex;
        align-items: center;
        //height: 4rem;
        padding: 0.8rem 0;
        cursor: pointer;
      }
      li > img {
        margin-right: 0.8rem;
        //margin-bottom: 0.3rem;
      }
    }
  }
}
</style>
