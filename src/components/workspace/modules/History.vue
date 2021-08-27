<template>
  <div
    class="widecard"
    :style="{ height: height }"
    :class="{ ...customClass, open: isOpenRoom }"
  >
    <div class="card-item">
      <profile
        :image="history.profile"
        :imageAlt="'profileImg'"
        :mainText="history.title"
        :subText="
          $t('workspace.history_member_length', {
            length: history.memberList.length,
          })
        "
        :thumbStyle="thumbStyle"
        :group="true"
      ></profile>
    </div>

    <div class="card-item info-mobile">
      <p class="info-mobile__title" :class="{ open: isOpenRoom }">
        {{ history.title }}
      </p>
      <div class="info-mobile__subtext">
        <span class="subtext__member">{{
          $t('workspace.history_member_length', {
            length: history.memberList.length,
          })
        }}</span>
        <span class="subtext__date">
          {{ date }}
        </span>
      </div>
    </div>

    <div class="card-item hide-tablet">
      <div class="label label--noraml">
        {{ $t('workspace.history_call_time', { time: time }) }}
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
        <span class="text">{{
          `${$t('common.leader')} : ${leader.nickName ? leader.nickName : ''}`
        }}</span>
      </div>
    </div>

    <div class="widecard-tools">
      <button class="btn small" @click="$emit('createRoom')">
        {{ $t('button.restart') }}
      </button>
      <popover
        trigger="click"
        :placement="placement"
        popperClass="custom-popover"
        width="auto"
        :scrollHide="true"
      >
        <button slot="reference" class="widecard-tools__menu-button"></button>
        <ul class="groupcard-popover">
          <li>
            <button class="group-pop__button" @click="$emit('openRoomInfo')">
              {{ $t('button.show_detail') }}
            </button>
          </li>
          <li>
            <button class="group-pop__button" @click="$emit('deleteHistory')">
              {{ $t('button.remove_list') }}
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
import { ROOM_STATUS } from 'configs/status.config'
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
    thumbStyle: {
      type: Object,
      default: () => {
        return { width: '3em', height: '3em' }
      },
    },
    placement: {
      type: String,
      default: 'bottom-start',
    },
  },
  data() {
    return {
      showRoomInfo: false,
    }
  },
  computed: {
    isOpenRoom() {
      return (
        this.history &&
        this.history.sessionType &&
        this.history.sessionType === ROOM_STATUS.OPEN
      )
    },
    date() {
      return this.$dayjs(this.history.activeDate + '+00:00')
        .local()
        .calendar(null, {
          sameDay: 'A h:mm',
          lastDay: this.$t('date.lastday'),
          nextDay: this.$t('date.nextday'),
          // lastWeek: '[지난] dddd',
          lastWeek: 'YYYY.MM.DD',
          sameElse: 'YYYY.MM.DD',
        })
    },
    time() {
      if (this.history.durationSec > 60 * 60) {
        return this.$dayjs(this.history.durationSec * 1000)
          .utc()
          .format(this.$t('date.timeHour'))
      } else {
        return this.$dayjs(this.history.durationSec * 1000)
          .utc()
          .format(this.$t('date.time'))
      }
    },
    leader() {
      const idx = this.history.memberList.findIndex(member => {
        return member.memberType === 'LEADER'
      })
      if (idx < 0) {
        return {}
      }
      return this.history.memberList[idx]
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
  &.open {
    background-color: #203531;
    border-color: #0a4338;
    &:hover {
      background-color: #1d4e44;
    }
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

@include responsive-mobile {
  .widecard {
    margin-bottom: 0.4rem;
    padding-left: 0;
    background-color: transparent;
    &.open {
      background-color: transparent;
    }
    &:hover {
      background: transparent;
      &.open {
        background-color: transparent;
      }
    }
    .card-item {
      display: none;
      flex: none;
      .profile {
        margin-right: 0.5rem;
      }
      .profile .profile--text {
        display: none;
      }
    }

    .card-item.info-mobile {
      display: block;
      .info-mobile__title {
        @include fontLevel(100);
        color: $new_color_text_main;
        &.open {
          color: #4cda9f;
        }
      }
      .info-mobile__subtext {
        margin-top: 0.2rem;
        @include fontLevel(75);
        .subtext__member {
          color: $new_color_text_sub;
        }
        .subtext__date {
          margin-left: 0.8rem;
          color: $new_color_text_sub_description;
        }
      }
    }
  }
  .widecard-tools {
    position: absolute;
    right: 0;
    > button {
      display: none;
    }
    .widecard-tools__menu-button {
      @include mobile-popover-btn(2.8rem);
    }
  }
  .custom-popover {
    @include responsive-popover;
  }
  // .popover.custom-popover {
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
