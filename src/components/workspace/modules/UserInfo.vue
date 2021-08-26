<template>
  <wide-card
    :key="user.uuid"
    :customClass="['remoteinfo-usercard', { offline: !isOnline }]"
    :height="height"
  >
    <div class="roominfo-userinfo">
      <profile
        class="profile-short"
        :image="user.profile"
        :mainText="user.nickName"
        :subText="user.email"
        :role="user.memberType"
        :status="accessType(user.accessType)"
        :thumbStyle="thumbStyle"
        :isMe="isMe"
      ></profile>

      <img
        v-if="deviceImg && deviceImg.length > 0"
        class="userinfo__image"
        :src="deviceImg"
      />
      <button
        class="btn line userinfo__button"
        :class="{ me: account.uuid === user.uuid }"
        @click="$emit('kickout')"
        v-if="isLeader && !isMobile"
      >
        {{ $t('button.kickout') }}
      </button>
      <popover
        v-if="isLeader && isMobile && account.uuid !== user.uuid"
        trigger="click"
        popperClass="kickout-menu"
        placement="left-start"
        width="auto"
        scrollHide
      >
        <button slot="reference" class="kickout-popover-btn"></button>
        <ul class="groupcard-popover">
          <li>
            <button class="group-pop__button" @click="$emit('kickout')">
              {{ $t('button.kickout') }}
            </button>
          </li>
        </ul>
      </popover>
    </div>
  </wide-card>
</template>

<script>
import WideCard from 'WideCard'
import Profile from 'Profile'
import { DEVICE } from 'configs/device.config'
import { STATUS } from 'configs/status.config'
import responsiveCardMixin from 'mixins/responsiveCard'
import Popover from 'Popover'

export default {
  name: 'UserInfo',
  components: {
    WideCard,
    Profile,
    Popover,
  },
  mixins: [responsiveCardMixin],
  props: {
    user: {
      type: Object,
      default: () => {
        return {}
      },
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    deviceImg() {
      switch (this.user.deviceType) {
        case DEVICE.WEB:
          return require('assets/image/workspace/ic_monitor.svg')
        case DEVICE.MOBILE:
        case DEVICE.FITT360:
          return require('assets/image/workspace/ic_mobile.svg')
        case DEVICE.GLASSES:
        case DEVICE.HOLOLENS:
          return require('assets/image/workspace/ic_hololens.svg')
      }
      return ''
    },
    isOnline() {
      return this.user.memberStatus === STATUS.LOAD
    },
    canKick() {
      return this.isLeader && this.account.uuid === this.user.uuid
    },
    //본인인지 여부
    isMe() {
      return this.account.uuid === this.user.uuid
    },
  },
  methods: {
    accessType(accessType) {
      if (accessType) return accessType.toLowerCase()
      return ''
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
<style lang="scss">
@import '~assets/style/mixin';

@include responsive-mobile {
  .kickout-menu {
    min-width: 12.1rem;

    > .popover--body {
      padding: 0px;
    }

    @include responsive-popover;
  }
}
</style>
