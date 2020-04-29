<template>
  <card
    class="groupcard"
    :menu="true"
    width="100%"
    height="35rem"
    popoverClass="group-menu"
  >
    <div class="groupcard-body">
      <span class="groupcard__leader" v-if="accountId === room.roomLeaderId"
        >Leader</span
      >
      <div class="groupcard-profile">
        <img
          class="profile__image"
          :src="room.path"
          @error="onImageErrorGroup"
        />
        <p class="profile__name">{{ room.title }}</p>
        <p class="profile__description">
          {{ room.description }}
        </p>
        <p class="profile__leader">리더 : {{ room.roomLeaderName }}</p>
      </div>
      <div class="groupcard-info">
        <div class="info__section">
          <p class="info__title">그룹 정보</p>
          <p class="info__description">
            {{ `총 멤버: ${room.maxParticipantCount}명` }}
          </p>
        </div>
        <div class="info__section">
          <p class="info__title">접속한 회원</p>
          <profile-list
            :size="28"
            :customStyle="{
              width: '28px',
              height: '28px',
              'font-size': '12px',
              'margin-left': '4px',
              'line-height': '25px',
              'border-width': '1px',
            }"
            :max="5"
            :users="participants"
          ></profile-list>
        </div>
      </div>
      <button class="groupcard-button btn small">참가하기</button>
    </div>
    <ul slot="menuPopover" class="groupcard-popover">
      <li>
        <button class="group-pop__button" @click="openRoomInfo">
          상세 보기
        </button>
      </li>
      <li v-if="accountId === room.roomLeaderId">
        <button class="group-pop__button" @click="remove(room.roomId)">
          협업 삭제
        </button>
      </li>
      <li v-else><button class="group-pop__button">협업 나가기</button></li>
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
import ProfileList from './ProfileList'
import RoominfoModal from '../modal/WorkspaceRoomInfo'

import { deleteRoom } from 'api/remote/room'

export default {
  name: 'RemoteCard',
  components: {
    Card,
    ProfileList,
    RoominfoModal,
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },
  props: {
    roomInfo: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    room() {
      return this.roomInfo.room
    },
    participants() {
      return this.roomInfo.participants
    },
    accountId() {
      return this.account.userId
    },
    leader() {
      if (this.account.userId === this.room.roomLeaderId) {
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
    async remove(roomId) {
      const rtn = await deleteRoom({ roomId: roomId })
      if (rtn) {
        this.$emit('refresh')
        this.$eventBus.$emit('popover:close')
      }
    },
  },

  /* Lifecycles */
  mounted() {
    // console.log(this.roomInfo)
  },
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
