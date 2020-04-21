<template>
  <card
    class="groupcard"
    :menu="true"
    :width="290"
    :height="490"
    popoverClass="group-menu"
  >
    <div class="groupcard-body">
      <span class="groupcard__leader" v-if="accountId === reader.ids"
        >Leader</span
      >
      <div class="groupcard-profile">
        <img class="profile__image" :src="room.image" />
        <p class="profile__name">{{ room.name }}</p>
        <p class="profile__description">
          {{ room.description }}
        </p>
        <p class="profile__leader">리더 : {{ reader.name }}</p>
      </div>
      <div class="groupcard-info">
        <div class="info__section">
          <p class="info__title">그룹 정보</p>
          <p class="info__description">{{ `총 멤버: ${room.all}명` }}</p>
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
            :users="room.connectedMembers"
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
      <li v-if="accountId === reader.ids">
        <button class="group-pop__button">
          협업 삭제
        </button>
      </li>
      <li v-else><button class="group-pop__button">협업 나가기</button></li>
      <roominfo-modal
        :visible.sync="showRoomInfo"
        :room="room"
      ></roominfo-modal>
    </ul>
  </card>
</template>

<script>
import Card from 'Card'
import ProfileList from './ProfileList'
import RoominfoModal from '../modal/WorkspaceRoomInfo'

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
      accountId: 11,
      users: [
        {
          id: 1,
          image: require('assets/image/홍길동.png'),
          imageAlt: '버넥트 리모트 01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 2,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 02',
          mainText: '버넥트 리모트03',
          subText: 'example2@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 3,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 03',
          mainText: '버넥트 리모트03',
          subText: 'example3@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 4,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 5,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 03',
          mainText: '버넥트 리모트03',
          subText: 'example3@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 6,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 7,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 03',
          mainText: '버넥트 리모트03',
          subText: 'example3@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          id: 8,
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트 01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
      ],
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
    reader() {
      if (!this.room) return ''
      if (
        !this.room.readerId ||
        !this.room.connectedMembers ||
        this.room.connectedMembers.length === 0
      )
        return ''
      const idx = this.room.connectedMembers.findIndex(member => {
        return member.ids === this.room.readerId
      })
      if (idx < 0) return ''

      return this.room.connectedMembers[idx]
    },
  },
  methods: {
    openRoomInfo() {
      this.showRoomInfo = !this.showRoomInfo
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
.card.groupcard {
  width: 290px;
  max-width: 290px;
  height: 490px;
  background-color: #29292c;
  &:hover {
    background-color: #313135;
  }
}
.groupcard-body {
  position: relative;
  width: 100%;
  height: 100%;
}

.groupcard__leader {
  position: absolute;
  top: -10px;
  left: -10px;
  padding: 4px 12px;
  font-weight: 500;
  font-size: 12px;
  font-family: roboto;
  line-height: 12px;
  background-color: #18497c;
  border: solid 1px #0261c6;
  border-radius: 12px;
}
.groupcard-profile {
  padding-top: 20px;
  text-align: center;
}
.profile__image {
  width: 72px;
  height: 72px;
  margin-bottom: 5px;
  background-color: #fff;
  border-radius: 50%;
}
.profile__name {
  font-weight: 500;
  font-size: 18px;
}
.profile__description {
  display: -webkit-box;
  width: 228px;
  height: 3em;
  margin-bottom: 5px;
  overflow: hidden;
  color: rgba(#ddd, 0.76);
  font-size: 15px;
  font-size: 1em;
  line-height: 1.5;
  text-overflow: ellipsis;
  word-wrap: break-word;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
}
.profile__leader {
  position: relative;
  width: fit-content;
  margin: 14px auto;
  margin-bottom: 9px;
  color: #fafafa;
  &:before {
    position: absolute;
    top: 0;
    right: 105%;
    width: 20px;
    height: 20px;
    background: url(~assets/image/ic_leader.svg) 50%/20px no-repeat;
    content: '';
  }
}
.groupcard-info {
  display: inline-block;
}
.info__section {
  margin-top: 20px;
}
.info__title {
  margin-bottom: 5px;
  color: #d2d2d2;
  font-weight: 500;
}
.info__description {
  font-weight: 500;
}
.groupcard-button {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 10px 40px;
}
.groupcard.profilelist {
  > .profilelist-user {
    width: 28px;
    height: 28px;
    margin-left: 4px;
  }
  > .profilelist-user__expend {
    width: 28px;
    height: 28px;
    margin-left: 4px;
  }
}
</style>

<style lang="scss">
.popover.group-menu {
  width: 120px;
  min-width: 120px;
  background-color: #242427;
  border: solid 1px #3a3a3d;
  border-radius: 6px;
  > .popover--body {
    padding: 0;
  }
}
.groupcard-popover {
  padding: 4px 0;
}
.group-pop__button {
  width: 100%;
  padding: 13px 20px;
  color: #fff;
  text-align: left;
  &:focus {
    background-color: rgba(#4c5259, 0.15);
  }
  &:hover {
    background-color: rgba(#4c5259, 0.3);
  }
  &:active {
    background-color: rgba(#4c5259, 0.5);
  }
}
</style>
