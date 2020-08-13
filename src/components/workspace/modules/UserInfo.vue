<template>
  <wide-card
    :key="user.uuid"
    :customClass="['remoteinfo-usercard', { offline: isOffline }]"
  >
    <div class="roominfo-userinfo">
      <profile
        class="profile-short"
        :image="user.profile"
        :mainText="user.nickname"
        :subText="user.email"
        :role="user.memberType"
        :thumbStyle="{ width: '3em', height: '3em' }"
      ></profile>

      <img class="userinfo__image" :src="deviceImg" />
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
export default {
  name: 'UserInfo',
  components: {
    WideCard,
    Profile,
  },
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
  data() {
    return {}
  },
  computed: {
    deviceImg() {
      switch (this.user.deviceType) {
        case DEVICE.WEB:
          return require('assets/image/ic_monitor.svg')
        case DEVICE.MOBILE:
          return require('assets/image/ic_mobile.svg')
        case DEVICE.GLASSES:
        case DEVICE.HOLOLENS1:
        case DEVICE.HOLOLENS2:
          return require('assets/image/ic_hololens.svg')
      }
      return ''
    },
    isOffline() {
      if (this.user.memberStatus === STATUS.LOAD) {
        return false
      } else {
        return true
      }
    },
    canKick() {
      if (this.isLeader && this.account.uuid === this.user.uuid) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
