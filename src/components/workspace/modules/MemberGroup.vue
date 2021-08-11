<template>
  <div class="widecard" :style="{ height: height }" :class="{ ...customClass }">
    <div class="card-item">
      <div class="label label--noraml">01</div>
    </div>
    <div class="card-item">
      {{ group.groupName }}
    </div>

    <div class="card-item">
      <span>그룹 멤버:</span>
      <profile-list
        :users="group.remoteGroupMemberInfoResponseList"
        size="2.143em"
      ></profile-list>
    </div>

    <div class="widecard-tools">
      <button>
        수정
      </button>
      <button>
        삭제
      </button>
    </div>
  </div>
</template>

<script>
import ProfileList from 'ProfileList'

export default {
  name: 'MemberGroup',
  components: {
    // Popover,
    ProfileList,
  },
  props: {
    group: Object,

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
  computed: {},
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
    flex-grow: 0.2;
    width: 0px;
    // min-width: 21.4286rem;
  }
  .card-item:nth-of-type(2) {
    // width: 500px;
    // flex-grow: 0.8;

    padding-right: 0.7143rem;
  }
  .card-item:nth-last-child(2) {
    display: flex;
    align-items: center;
    // justify-content: center;
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

  & > button {
    width: 102px;
    height: 38px;
    margin-left: 10px;
    background: rgb(89, 89, 96);
    border-radius: 2px;
    box-shadow: 0px 2px 4px 0px rgba(0, 0, 0, 0.16);
  }
}
</style>
