<template>
  <div class="widecard" :style="{ height: height }" :class="customClass">
    <div class="card-item">
      <profile
        :image="history.profile"
        :imageAlt="'profileImg'"
        :mainText="history.title"
        :subText="`참석자 ${history.memberList.length}명`"
        :thumbStyle="{ width: '3em', height: '3em' }"
        :group="true"
      ></profile>
    </div>

    <div class="card-item hide-tablet">
      <div class="label label--noraml">
        {{ `총 이용시간: ${time}` }}
      </div>
    </div>
    <div class="card-item">
      <div class="label label--date">
        {{ date }}
      </div>
    </div>
    <div class="card-item">
      <div class="label label__icon">
        <img class="icon" :src="require('assets/image/ic_leader.svg')" />
        <span class="text">{{ `리더 : ${leader.nickname}` }}</span>
      </div>
    </div>

    <div class="widecard-tools">
      <button class="btn small" @click="$emit('createRoom')">
        재시작
      </button>
      <popover
        trigger="click"
        placement="bottom-start"
        popperClass="custom-popover"
      >
        <button slot="reference" class="widecard-tools__menu-button"></button>
        <ul class="groupcard-popover">
          <li>
            <button class="group-pop__button" @click="$emit('openRoomInfo')">
              상세 보기
            </button>
          </li>
          <li>
            <button
              class="group-pop__button"
              @click="$emit('showDeleteDialog')"
            >
              목록삭제
            </button>
          </li>
        </ul>
      </popover>
    </div>
  </div>
</template>

<script>
import Profile from 'Profile'
import Popover from 'Popover'
export default {
  name: 'History',
  components: {
    Profile,
    Popover,
  },
  props: {
    history: Object,
    height: {
      type: [Number, String],
      default: 86,
    },
    menu: {
      type: Boolean,
      default: false,
    },
    customClass: {
      type: Object,
      default: () => {
        return {}
      },
    },
    room: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },
  computed: {
    date() {
      // console.log(
      //   this.$dayjs(this.history.activeDate)
      //     .calendar(Date.now(), {
      //       sameDay: 'A h:mm',
      //       lastDay: '[어제]',
      //       nextDay: '[내일]',
      //       lastWeek: '[지난주] dddd',
      //       sameElse: 'YYYY.MM.DD',
      //     }),
      // )
      return this.$dayjs(this.history.activeDate + '+00:00')
        .local()
        .calendar(null, {
          sameDay: 'A h:mm',
          lastDay: '[어제] A',
          nextDay: '[내일] A',
          // lastWeek: '[지난] dddd',
          lastWeek: 'YYYY.MM.DD',
          sameElse: 'YYYY.MM.DD',
        })
    },
    time() {
      return this.$dayjs(this.history.durationSec * 1000).format('m분 s초')
    },
    leader() {
      return this.history.memberList.find(member => {
        return member.memberType === 'LEADER'
      })
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/_mixin';
.widecard {
  display: flex;
  // flex-basis: 0px;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-bottom: 0.571em;
  padding: 1.571em 2.143em;
  background-color: $color_darkgray_600;
  border-radius: 2px;
  transition: background-color 0.3s;

  &:hover {
    background-color: $color_darkgray_500;
  }
  .card-item {
    flex: 1 1 0;
    min-width: 0;
    // flex-basis: 0;
    // flex-grow: 1;
    // flex-shrink: 1;
    // @include tablet {
    //   flex-grow: 0.5;
    // }
    > .btn.small {
      padding: 0.5em 2.143em;
    }
  }
  .card-item:first-of-type {
    display: inherit;
    // flex-grow: 0.8;
    // min-width: 21.4286rem;
    padding-right: 0.7143rem;
  }
  .card-item:nth-of-type(2) {
    // flex-grow: 0.8;
    padding-right: 0.7143rem;
  }
  .card-item:nth-last-child(2) {
    // flex-grow: 0.7;
    padding-right: 0.7143rem;
  }
  .card-item:last-of-type {
    // flex-grow: 0.6;
  }
}

.widecard-tools {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>
