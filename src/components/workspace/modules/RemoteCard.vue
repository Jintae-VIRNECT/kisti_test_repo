<template>
  <card
    class="groupcard"
    :menu="true"
    width="100%"
    height="35rem"
    popoverClass="group-menu"
  >
    <div class="groupcard-body">
      <span class="groupcard__leader" v-if="leader">Leader</span>
      <div class="groupcard-profile">
        <div class="profile__image">
          <profile
            :group="true"
            :image="room.orgPath"
            :thumbStyle="{ width: '5.143rem', height: '5.143rem' }"
          ></profile>
        </div>
        <!-- <img
          class="profile__image"
          :src="room.path"
          @error="onImageErrorGroup"
        /> -->
        <p class="profile__name">{{ room.title }}</p>
        <p class="profile__description">
          {{ room.description }}
        </p>
        <p class="profile__leader">리더 : {{ room.leaderNickName }}</p>
      </div>
      <div class="groupcard-info">
        <div class="info__section">
          <p class="info__title">그룹 정보</p>
          <p class="info__description">
            {{ `총 멤버: ${room.participantsCount}명` }}
          </p>
        </div>
        <div class="info__section">
          <p class="info__title">접속한 회원</p>
          <profile-list
            :customStyle="{
              width: '2rem',
              height: '2rem',
              'font-size': '12px',
              'margin-left': '0.286rem',
              'line-height': '2rem',
            }"
            size="2rem"
            :max="5"
            :users="room.participants"
          ></profile-list>
        </div>
      </div>
      <button class="groupcard-button btn small" @click="$emit('join', room)">
        참가하기
      </button>
    </div>
    <ul slot="menuPopover" class="groupcard-popover">
      <li>
        <button class="group-pop__button" @click="openRoomInfo">
          상세 보기
        </button>
      </li>
      <li v-if="leader">
        <button class="group-pop__button" @click="$emit('remove', room.roomId)">
          협업 삭제
        </button>
      </li>
      <li v-else>
        <button class="group-pop__button" @click="$emit('leave', room.roomId)">
          협업 나가기
        </button>
      </li>
    </ul>
    <roominfo-modal
      :visible.sync="showRoomInfo"
      :roomId="room.roomId"
      :leader="leader"
    ></roominfo-modal>
  </card>
</template>

<script>
import Card from 'Card'
import Profile from 'Profile'
import ProfileList from './ProfileList'
import RoominfoModal from '../modal/WorkspaceRoomInfo'

export default {
  name: 'RemoteCard',
  components: {
    Card,
    Profile,
    ProfileList,
    RoominfoModal,
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },
  props: {
    room: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    accountId() {
      return this.account.userId
    },
    leader() {
      if (this.account.userId === this.room.leaderId) {
        return true
      } else {
        return false
      }
    },
  },
  methods: {
    openRoomInfo() {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        this.showRoomInfo = !this.showRoomInfo
      })
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-remotecard.scss"
></style>

<style lang="scss">
@import '~assets/style/vars';

.popover.group-menu {
  width: 8.571em;
  min-width: 8.571em;
  background-color: $color_bg_sub;
  border: solid 1px #3a3a3d;
  border-radius: 6px;
  > .popover--body {
    padding: 0;
  }
}
.groupcard-popover {
  padding: 0.286em 0;
  .group-pop__button {
    width: 100%;
    padding: 0.929em 1.429em;
    color: #fff;
    text-align: left;
    &:focus {
      background-color: rgba($color_bg_item, 0.15);
    }
    &:hover {
      background-color: rgba($color_bg_item, 0.3);
    }
    &:active {
      background-color: rgba($color_bg_item, 0.5);
    }
  }
}
</style>
