<template>
  <div v-if="testdata.length > 0" class="list-wrapper">
    <wide-card-extend
      :menu="true"
      v-for="(item, index) in testdata"
      v-bind:key="index"
    >
      <profile
        :image="item.profileImg"
        :imageAlt="item.mainText"
        :mainText="item.mainText"
        :subText="item.subText"
      ></profile>

      <div slot="column1" class="label label--noraml">
        {{ item.entireTime }}
      </div>
      <div slot="column2" class="label label--date">
        {{ item.lastTime }}
      </div>
      <div slot="column3" class="label lable__icon">
        <img class="icon" :src="require('assets/image/back/mdpi_icon.svg')" />
        <span class="text">{{ item.leaderName }}</span>
      </div>
      <button slot="menuPopover"></button>
      <button class="btn" @click="createRoom" slot="column4">
        재시작
      </button>
    </wide-card-extend>
    <create-room-modal :visible.sync="visible"></create-room-modal>
  </div>
  <div v-else class="no-list">
    <div class="no-list__img"></div>
    <div class="no-list__title">협업 가능 멤버가 없습니다.</div>
    <div class="no-list__description">협업 멤버를 추가해주세요.</div>
  </div>
</template>

<script>
import Profile from 'Profile'
import WideCardExtend from 'WideCardExtend'

import sort from 'mixins/admin/adminSort'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
export default {
  name: 'WorkspaceHistoryList',
  mixins: [sort],
  components: { Profile, WideCardExtend, CreateRoomModal },
  data() {
    return {
      visible: false,
      testdata: [
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 01',
          subText: '참석자 0명',
          entireTime: '총 이용 시간: 08분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kim',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 02',
          subText: '참석자 2명',
          entireTime: '총 이용 시간: 10분 21초',
          lastTime: 'Today',
          leaderName: '리더 : Nari K',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 03',
          subText: '참석자 4명',
          entireTime: '총 이용 시간: 33분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kasdfs',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 01',
          subText: '참석자 0명',
          entireTime: '총 이용 시간: 08분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kim',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 02',
          subText: '참석자 2명',
          entireTime: '총 이용 시간: 10분 21초',
          lastTime: 'Today',
          leaderName: '리더 : Nari K',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 03',
          subText: '참석자 4명',
          entireTime: '총 이용 시간: 33분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kasdfs',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 01',
          subText: '참석자 0명',
          entireTime: '총 이용 시간: 08분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kim',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 02',
          subText: '참석자 2명',
          entireTime: '총 이용 시간: 10분 21초',
          lastTime: 'Today',
          leaderName: '리더 : Nari K',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 03',
          subText: '참석자 4명',
          entireTime: '총 이용 시간: 33분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kasdfs',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 01',
          subText: '참석자 0명',
          entireTime: '총 이용 시간: 08분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kim',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 02',
          subText: '참석자 2명',
          entireTime: '총 이용 시간: 10분 21초',
          lastTime: 'Today',
          leaderName: '리더 : Nari K',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 03',
          subText: '참석자 4명',
          entireTime: '총 이용 시간: 33분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kasdfs',
          leaderIcon: '',
        },
      ],
    }
  },
  computed: {
    list() {
      if (this.searchFilter === '') {
        return this.userList
      }

      const array = []
      for (const list of this.userList) {
        if (list.name.toLowerCase().match(this.searchFilter.toLowerCase())) {
          array.push(list)
        }
      }
      return array
    },
  },
  watch: {
    searchFilter() {},
  },
  methods: {
    createRoom() {
      this.visible = !this.visible
    },
  },
}
</script>

<style lang="scss">
.no-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  background-color: #29292c;

  .no-list__img {
    width: 191px;
    height: 233px;
    margin-bottom: 30px;
    background-image: url('~assets/image/mdpi_02.svg');
    background-repeat: no-repeat;
  }

  .no-list__title {
    color: #fafafa;
    font-weight: normal;
    font-size: 24px;
    font-family: NotoSansCJKkr-Regular;
    letter-spacing: 0px;
    text-align: center;
  }
  .no-list__description {
    color: #fafafa;
    font-weight: normal;
    font-size: 18px;
    font-family: NotoSansCJKkr-Regular;
    letter-spacing: 0px;
    text-align: center;
    opacity: 50%;
  }
}

.label {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.label--noraml {
  color: #d2d2d2;
  font-weight: normal;
  font-size: 15px;
  font-family: AppleSDGothicNeo-Regular;
  letter-spacing: 0px;
  text-align: center;
}

.label--date {
  color: #fafafa;
  font-weight: normal;
  font-size: 15px;
  font-family: Roboto-Regular;
  letter-spacing: 0px;
  text-align: center;
  opacity: 50%;
}

.lable__icon {
  display: flex;
  align-items: center;
  padding-left: 30px;
  opacity: 86%;
  .icon {
    width: 24px;
    height: 24px;
    margin-right: 5px;
  }

  .text {
    color: #fafafa;
    font-weight: normal;
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular;
    letter-spacing: 0px;
  }
}
</style>
