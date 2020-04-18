<template>
  <div class="widecard" :style="{ height: height + 'px' }" :class="customClass">
    <div class="card-item"><slot></slot></div>

    <div class="card-item"><slot name="column1"></slot></div>
    <div class="card-item"><slot name="column2"></slot></div>
    <div class="card-item"><slot name="column3"></slot></div>

    <div class="card-item widecard-tools">
      <slot name="column4"></slot>
      <popover
        v-if="menu"
        trigger="click"
        placement="bottom-start"
        popperClass="custom-popover"
      >
        <button slot="reference" class="widecard-tools__menu-button"></button>
        <ul class="groupcard-popover">
          <li>
            <button class="group-pop__button" @click="openRoomInfo">
              상세 보기
            </button>
          </li>
          <li>
            <button class="group-pop__button" @click="deleteItem">
              목록삭제
            </button>
          </li>
        </ul>
      </popover>
    </div>
    <roominfo-modal :visible.sync="showRoomInfo" :room="room"></roominfo-modal>
  </div>
</template>

<script>
import Popover from 'Popover'
//import RoominfoModal from '../../workspace/modal/WorkspaceRoomInfo'
import RoominfoModal from '../workspace/modal/WorkspaceRoomInfo'
export default {
  name: 'WideCardExtend',
  components: {
    Popover,
    RoominfoModal,
  },
  props: {
    height: {
      type: Number,
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
  computed: {},
  methods: {
    openRoomInfo() {
      this.showRoomInfo = !this.showRoomInfo
    },
    deleteItem() {},
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.widecard {
  display: flex;
  flex-basis: 0px;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-bottom: 8px;
  padding: 22px 30px;
  background-color: #29292c;
  border-radius: 2px;

  &:hover {
    background-color: #313135;
  }
  .card-item {
    flex-basis: 0;
    flex-grow: 1;
    flex-shrink: 1;
  }
  .card-item:first-of-type {
    flex-grow: 0.8;
  }
  .card-item:last-of-type {
    flex-grow: 0.8;
  }
}

.widecard-tools {
  display: flex;
  align-items: center;
  justify-content: center;
}
.widecard-tools__menu-button {
  width: 28px;
  height: 28px;
  margin-left: 26px;
  background: url(~assets/image/ic-more-horiz-light.svg) 50% no-repeat;
  &:hover {
    background: url(~assets/image/back/mdpi_icn_more.svg) 50% no-repeat;
  }
}

.popover.custom-popover {
  background-color: #242427;
  border: solid 1px #3a3a3d;
  > .popover--body {
    width: 120px;
    padding: 0px;
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
// .menu {
//   opacity: 70%;

//   .menu--item {
//     color: rgb(255, 255, 255);
//     font-size: 14px;
//     font-family: NotoSansCJKkr-Regular;
//     font-weight: normal;
//     margin-top: 13px;
//     margin-right: 42px;
//     margin-bottom: 13px;
//     margin-left: 20px;
//   }
// }
</style>
