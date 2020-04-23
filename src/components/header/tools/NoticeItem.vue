<template>
  <div class="notice-item">
    <img class="notice-item__image" :class="section" :src="icon" />
    <div class="notice-item__body">
      <p class="notice-item__info" :class="section">{{ info }}</p>
      <p class="notice-item__description" v-html="description"></p>
      <p class="notice-item__date">{{ date }}</p>
      <div class="notice-item__buttons" v-if="btnType === 'license'">
        <button class="btn small">라이선스 구매</button>
      </div>
      <div class="notice-item__buttons" v-if="btnType === 'invite'">
        <button class="btn small">수락</button>
        <button class="btn small sub">거절</button>
      </div>
      <div class="notice-item__buttons" v-if="btnType === 'file'">
        <button class="btn small sub filelink">{{ filename }}</button>
      </div>
      <button class="notice-item__close">삭제</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NoticeItem',
  data() {
    return {}
  },
  props: {
    // 구분 status, detail, section, 분류 상태 종류 정보
    info: {
      type: String,
      default: '',
    },
    description: {
      type: String,
      default: '',
    },
    date: {
      type: String,
      default: '',
    },
    section: {
      type: String,
      validate(value) {
        return (
          ['message', 'invite', 'info', 'alert', 'license', 'file'].indexOf(
            value,
          ) >= 0
        )
      },
      default: 'message',
    },
    image: {
      type: String,
      default: '',
    },
    filename: {
      type: String,
      default: '',
    },
    filelink: {
      type: String,
      default: '',
    },
    btnType: {
      type: String,
      default: '',
    },
  },
  computed: {
    icon() {
      if (this.section === 'info' || this.section === 'license') {
        return require('assets/image/ic_system.svg')
      } else if (this.section === 'alert') {
        return require('assets/image/ic_notice.svg')
      } else {
        return this.image
      }
    },
  },
  methods: {
    notice() {},
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';
.notice-item {
  position: relative;
  display: flex;
  padding: 20px;
  &:before {
    position: absolute;
    top: 0;
    right: 0;
    width: calc(100% - 75px);
    height: 1px;
    background: rgba(#f3f6f9, 0.08);
    content: '';
  }
  &:first-child {
    &:before {
      height: 0;
    }
  }
}
.notice-item__close {
  position: absolute;
  top: 24px;
  right: 14px;
  width: 20px;
  height: 20px;
  background: url(~assets/image/ic_close.svg) 50% no-repeat;
  opacity: 0.5;
  @include ir();
  &:hover {
    opacity: 0.8;
  }
  &:active {
    opacity: 1;
  }
}

.notice-item__image {
  width: 36px;
  height: 36px;
  margin: 0 20px auto 0;
  @include image();
  &.info {
    background-color: $color_primary;
  }
  &.alert {
    background-color: #ffd059;
  }
}
.notice-item__body {
}
.notice-item__info {
  color: #f55560;
  font-size: 12px;
  &.message,
  &.invite,
  &.file {
    color: rgba(#fff, 0.5);
  }
}
.notice-item__description {
  color: #fff;
  font-size: 16px;
  > em {
    color: #6bb4f9;
  }
}
.notice-item__date {
  color: #98a0a6;
  font-size: 12px;
}
.notice-item__buttons {
  margin-top: 10px;
  > button {
    margin-right: 14px;
    &.filelink {
      position: relative;
      padding-left: 40px;
      color: #bfddff;
      background-color: rgba(#bfddff, 0.12);
      border: solid 1px rgba(#bfddff, 0.4);
      &:before {
        position: absolute;
        top: 5px;
        left: 10px;
        width: 22px;
        height: 22px;
        background: url(~assets/image/material_link.svg) 50%/22px no-repeat;
        content: '';
      }
    }
  }
}
</style>
