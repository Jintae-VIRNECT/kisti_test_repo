<template>
  <card
    class="groupcard"
    :menu="true"
    width="100%"
    height="33rem"
    popoverClass="group-menu"
  >
    <div class="groupcard-body">
      <span class="groupcard__leader" v-if="isLeader">Leader</span>
      <div class="groupcard-profile">
        <div class="profile__image">
          <profile
            :group="true"
            :image="room.profile"
            :thumbStyle="{ width: '5.143rem', height: '5.143rem' }"
          ></profile>
        </div>
        <!-- <img
          class="profile__image"
          :src="room.path"
          @error="onImageErrorGroup"
        /> -->
        <p class="profile__name">{{ title ? title : room.title }}</p>
        <p class="profile__leader">리더 : {{ leader.nickname }}</p>
      </div>
      <div class="groupcard-info">
        <div class="info__section">
          <p class="info__title">그룹 정보</p>
          <p class="info__description">
            <b>{{ `접속 멤버 &nbsp;&nbsp;${activeMemberList.length}` }}</b>
            {{ `/ ${room.memberList.length}` }}
          </p>
        </div>
        <div class="info__section">
          <p class="info__title">접속한 멤버</p>
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
            :users="activeMemberList"
          ></profile-list>
        </div>
      </div>
      <button class="groupcard-button btn small" @click="join">
        참가하기
      </button>
    </div>
    <ul slot="menuPopover" class="groupcard-popover">
      <li>
        <button class="group-pop__button" @click="openRoomInfo">
          상세 보기
        </button>
      </li>
      <li v-if="isLeader">
        <button class="group-pop__button" @click="$emit('remove')">
          협업 삭제
        </button>
      </li>
      <li v-else>
        <button class="group-pop__button" @click="$emit('leave')">
          협업 나가기
        </button>
      </li>
    </ul>
    <roominfo-modal
      :visible.sync="showRoomInfo"
      :sessionId="room.sessionId"
      :isLeader="isLeader"
      @updatedInfo="updateInfo"
    ></roominfo-modal>
  </card>
</template>

<script>
import Card from 'Card'
import Profile from 'Profile'
import ProfileList from 'ProfileList'
import RoominfoModal from '../modal/WorkspaceRoomInfo'
import { STATUS } from 'configs/status.config'
import { ROLE } from 'configs/remote.config'
import mixinToast from 'mixins/toast'

export default {
  name: 'RemoteCard',
  mixins: [mixinToast],
  components: {
    Card,
    Profile,
    ProfileList,
    RoominfoModal,
  },
  data() {
    return {
      showRoomInfo: false,
      title: '',
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
    leader() {
      if (
        !this.room ||
        !this.room.memberList ||
        this.room.memberList.length === 0
      )
        return {}
      const idx = this.room.memberList.findIndex(
        member => member.memberType === ROLE.EXPERT_LEADER,
      )
      if (idx < 0) return {}
      return this.room.memberList[idx]
    },
    isLeader() {
      if (this.leader.uuid === this.account.uuid) {
        return true
      }
      return false
    },
    activeMemberList() {
      let activeMembers = []
      for (let member of this.room.memberList) {
        if (member.memberStatus === STATUS.LOAD) {
          activeMembers.push(member)
        }
      }
      return activeMembers
    },
  },
  methods: {
    openRoomInfo() {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        this.showRoomInfo = !this.showRoomInfo
      })
    },
    updateInfo(info) {
      this.title = info.title
    },
    join() {
      this.$emit('join')
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
