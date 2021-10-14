<template>
  <div :class="{ visible }" class="mobile-select-view-container">
    <section class="mobile-select-view__menu">
      <ul class="select-view-menu-list">
        <li class="select-view-user-name">
          {{ participant ? participant.nickname : '' }}
        </li>
        <li
          v-for="menuItem in menus"
          :key="menuItem.name"
          class="select-view-menu-item"
          :class="{
            disable: !menuItem.visible,
          }"
          @click="menuItem.fn"
        >
          <img class="icon" :src="menuItem.icon" />
          <span> {{ menuItem.title }}</span>
        </li>
      </ul>
      <div class="button-container">
        <button @click="close">{{ $t('button.close') }}</button>
      </div>
    </section>
  </div>
</template>

<script>
import confirmMixin from 'mixins/confirm'

const MENU = {
  SHARE_VIEW: 'SHARE_VIEW',
  MAIN_VIEW: 'MAIN_VIEW',
  MUTE: 'MUTE',
  KICK_OUT: 'KICK_OUT',
}

export default {
  mixins: [confirmMixin],

  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.participant = newVal
        this.initMenus()
      }
    },
  },
  data() {
    return {
      participant: null,
      isMute: false,
      menus: [],
    }
  },
  computed: {
    muteVisible() {
      return this.participant.id !== this.account.uuid
    },
    kickoutVisible() {
      return this.isLeader && this.participant.id !== this.account.uuid
    },
  },
  methods: {
    //사용자에 따라 메뉴 초기화
    initMenus() {
      this.menus = [
        //사용자가 리더인 경우에만 표시
        {
          name: MENU.SHARE_VIEW,
          icon: require('assets/image/call/mdpi_icon_sharing_new.svg'),
          title: this.$t('button.stream_sharing'),
          visible: this.isLeader,
          fn: this.shareView,
        },

        {
          name: MENU.MAIN_VIEW,
          icon: require('assets/image/call/icon_main_view_new.svg'),
          title: this.$t('button.stream_normal'),
          visible: true,
          fn: this.selectMainView,
        },
        {
          name: MENU.MUTE,
          icon: this.isMuted
            ? require('assets/image/call/icon_mute_off_new.svg')
            : require('assets/image/call/icon_mute_new.svg'),
          title: this.isMuted
            ? this.$t('service.participant_mute_cancel')
            : this.$t('service.participant_mute'),
          visible: this.muteVisible,
          fn: this.toggleMute,
        },
        //사용자가 리더이고, 본인이 아닌 경우만 표시
        {
          name: MENU.KICK_OUT,
          icon: require('assets/image/call/mdpi_icon_kickout_new.svg'),
          title: this.$t('button.kickout'),
          visible: this.kickoutVisible,
          fn: this.kickout,
        },
      ]
    },
    shareView() {
      this.$emit('share')
      this.close()
    },
    selectMainView() {
      this.$emit('normal')
      this.close()
    },
    toggleMute() {
      this.isMuted = !this.participant.mute
      this.$emit('mute', this.participant)
      this.close()
    },
    kickout() {
      this.serviceConfirmCancel(
        this.$t('service.participant_kick_confirm', {
          name: this.participant.nickname,
        }),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.$emit('kickout', this.participant.id)
          },
        },
        {
          text: this.$t('button.cancel'),
        },
      )
      this.close()
    },
    close() {
      this.$emit('update:visible', !this.visible)
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-select-view-container {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 20;
  width: 100%;
  height: 100%;
  background-color: rgba(#121314, 0.6);
  opacity: 0;
  transition: all 0.4s;
  pointer-events: none;

  .mobile-select-view__menu {
    position: absolute;
    right: 0;
    bottom: -100%;
    left: 0;
    padding: 0 1.6rem;
    border-radius: 6px;
    transition: bottom 0.4s;
  }

  .mobile-select-view__menu ul {
    background-color: $new_color_bg_popover;
    border-radius: 6px;

    li.select-view-user-name {
      @include fontLevel(200);
    }

    li.select-view-menu-item {
      cursor: pointer;
      @include fontLevel(100);
      &:hover {
        background-color: $new_color_popup_bg;
      }

      &.disable {
        display: none;
      }
    }

    li {
      display: flex;
      align-items: center;
      height: 5.6rem;
      padding: 1.6rem;
      color: $new_color_text_main;

      img {
        width: 2.8rem;
        height: 2.8rem;
        margin-right: 0.8rem;
      }
    }
  }

  .button-container {
    padding: 1.6rem 0;
    > button {
      width: 100%;
      height: 4.8rem;
      color: $new_color_text_main;
      background-color: $new_color_popup_bg;
      border-radius: 6px;
      @include fontLevel(200);
    }
  }
}

.mobile-select-view-container.visible {
  opacity: 1;
  pointer-events: unset;
  .mobile-select-view__menu {
    bottom: 0;
  }
}
</style>
