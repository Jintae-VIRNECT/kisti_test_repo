<template>
  <card
    class="groupcard"
    :class="{ open: isOpenRoom }"
    :menu="true"
    width="100%"
    :height="height"
    :placement="placement"
    popoverClass="group-menu"
  >
    <div class="groupcard-body">
      <span class="groupcard__leader" v-if="isLeader">Leader</span>
      <div class="groupcard-profile">
        <div class="profile__image">
          <profile
            :group="true"
            :image="room.profile"
            :thumbStyle="thumbStyle"
          ></profile>
        </div>
        <!-- <img
          class="profile__image"
          :src="room.path"
          @error="onImageErrorGroup"
        /> -->
        <p class="profile__name">{{ title ? title : room.title }}</p>
        <p class="profile__leader">
          {{ `${$t('common.leader')} : ${leader.nickName}` }}
        </p>
      </div>
      <div class="groupcard-info">
        <div class="info__section">
          <p class="info__title">{{ $t('workspace.remote_group_info') }}</p>
          <p class="info__description">
            <b>{{
              `${$t('workspace.remote_connected_member_num')} &nbsp;&nbsp;${
                activeMemberList.length
              }`
            }}</b>
            {{ `/ ${room.maxUserCount}` }}
          </p>
        </div>
        <div class="info__section">
          <p class="info__title">
            {{ $t('workspace.remote_connected_member') }}
          </p>
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
      <div class="groupcard-info-mobile">
        <p class="room-title">{{ title ? title : room.title }}</p>
        <div>
          <span class="room-leader">{{
            `${$t('common.leader')} : ${leader.nickName}`
          }}</span>
          <span class="room-member">{{
            `${activeMemberList.length}/ ${room.maxUserCount}`
          }}</span>
        </div>
      </div>
      <button class="groupcard-button btn small" @click="join">
        {{ $t('button.invite') }}
      </button>
    </div>
    <ul slot="menuPopover" class="groupcard-popover">
      <li>
        <button class="group-pop__button" @click="openRoomInfo">
          {{ $t('button.show_detail') }}
        </button>
      </li>
      <li v-if="isLeader">
        <button class="group-pop__button" @click="$emit('remove')">
          {{ $t('button.remove_room') }}
        </button>
      </li>
      <li v-else-if="!isOpenRoom">
        <button class="group-pop__button" @click="$emit('leave')">
          {{ $t('button.leave_room') }}
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
import { STATUS, ROOM_STATUS } from 'configs/status.config'
import { ROLE } from 'configs/remote.config'
import mixinToast from 'mixins/toast'
import thumbStyle from 'mixins/thumbStyle'

const defaultThumbStyle = { width: '5.143rem', height: '5.143rem' }
const mobileThumbStyle = { width: '4.2rem', height: '4.2rem' }
const defaultPlacement = 'bottom-start'
const mobilePlacement = 'left-start'
const defaultCardHeight = '33rem'
const mobileCardHeight = '8.4rem'

export default {
  name: 'RemoteCard',
  mixins: [mixinToast, thumbStyle],
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
      thumbStyle: defaultThumbStyle,
      placement: defaultPlacement,
      height: defaultCardHeight,
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
  watch: {
    showRoomInfo(val) {
      if (val === false) {
        this.$emit('init')
      }
    },
  },
  computed: {
    isOpenRoom() {
      return (
        this.room &&
        this.room.sessionType &&
        this.room.sessionType === ROOM_STATUS.OPEN
      )
    },
    leader() {
      if (
        !this.room ||
        !this.room.memberList ||
        this.room.memberList.length === 0
      )
        return {}
      const idx = this.room.memberList.findIndex(
        member => member.memberType === ROLE.LEADER,
      )
      if (idx < 0) return {}
      return this.room.memberList[idx]
    },
    isLeader() {
      return this.leader.uuid === this.account.uuid
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
    updateInfo() {
      this.$emit('init')
    },
    join() {
      this.$emit('join', {
        ...this.room,
        leaderId: this.leader.uuid,
        open: this.isOpenRoom,
      })
    },
    setResponsiveDefault() {
      this.placement = defaultPlacement
      this.thumbStyle = defaultThumbStyle
      this.height = defaultCardHeight
    },
    setResponsiveMobile() {
      this.placement = mobilePlacement
      this.thumbStyle = mobileThumbStyle
      this.height = mobileCardHeight
    },
  },
  /* Lifecycles */
  mounted() {
    this.responsiveFn = this.callAndGetMobileResponsiveFunction(
      this.setResponsiveMobile,
      this.setResponsiveDefault,
    )
    this.addEventListenerScreenResize(this.responsiveFn)
  },
  beforeDestroy() {
    this.removeEventListenerScreenResize(this.responsiveFn)
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
@import '~assets/style/mixin';

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

@include responsive-mobile {
  .group-menu {
    @include responsive-popover;
  }
  // .popover.group-menu {
  //   background-color: #606a77; //@추가 color
  //   border: none; //@추가 color
  // }
  // .groupcard-popover {
  //   .group-pop__button {
  //     @include fontLevel(100);
  //     width: 12.1rem;
  //     height: 4rem;
  //     padding: 0;
  //     padding-left: 2rem;
  //   }
  // }
}
</style>
