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
        v-if="isLeader"
      >
        {{ $t('button.kickout') }}
      </button>
    </div>
  </wide-card>
</template>

<script>
import WideCard from 'WideCard'
import Profile from 'Profile'
import { DEVICE } from 'configs/device.config'
import { STATUS } from 'configs/status.config'
import responsiveCardMixin from 'mixins/responsiveCard'

export default {
  name: 'UserInfo',
  components: {
    WideCard,
    Profile,
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
