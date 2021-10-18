<template>
  <figure class="notice-item">
    <div class="notice-item__image">
      <img
        v-if="icon && icon.length > 0"
        :class="type"
        :src="icon"
        :alt="info"
        @error="onImageError"
      />
    </div>
    <!-- <img class="notice-item__image" :class="type" :src="icon" /> -->
    <figcaption class="notice-item__body">
      <p class="notice-item__info" :class="type" v-html="info"></p>
      <p class="notice-item__description" v-html="description"></p>
      <p class="notice-item__date">{{ date | noticeDate }}</p>
      <div class="notice-item__buttons" v-if="type === 'license'">
        <button class="btn small">{{ $t('button.license_purchase') }}</button>
      </div>
      <div class="notice-item__buttons" v-if="type === 'invite'">
        <template v-if="accept === 'none'">
          <button class="btn small" @click="$emit('accept')">
            {{ $t('button.accept') }}
          </button>
          <button class="btn small sub" @click="$emit('refuse')">
            {{ $t('button.refuse') }}
          </button>
        </template>
        <button
          v-else-if="accept === 'refuse'"
          class="btn small disable refuse"
          disabled
        >
          {{ $t('alarm.invite_refuse') }}
        </button>
        <button v-else class="btn small disabled refuse" disabled>
          {{ $t('alarm.invite_accept') }}
        </button>
      </div>
      <div class="notice-item__buttons" v-if="type === 'file'">
        <button class="btn small sub filelink">{{ filename }}</button>
      </div>
      <button class="notice-item__close" @click="$emit('remove')">
        {{ $t('button.remove') }}
      </button>
    </figcaption>
  </figure>
</template>

<script>
import dayjs from 'dayjs'
import { proxyUrl } from 'utils/file'
export default {
  name: 'NoticeItem',
  data() {
    return {}
  },
  props: {
    // 구분 status, detail, section, 분류 상태 종류 정보
    accept: {
      type: [Boolean, String],
      default: false,
    },
    info: {
      type: String,
      default: '',
    },
    description: {
      type: String,
      default: '',
    },
    date: {
      type: Date,
      default: () => {
        return new Date()
      },
    },
    type: {
      type: String,
      validate(value) {
        return (
          ['message', 'invite', 'info', 'fail', 'license', 'file'].indexOf(
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
  },
  filters: {
    noticeDate(date) {
      return dayjs(date).format('YYYY.MM.DD A h:mm')
    },
  },
  computed: {
    icon() {
      if (
        this.type === 'info_user' ||
        this.type === 'info' ||
        this.type === 'license'
      ) {
        return require('assets/image/header/ic_system.svg')
      } else if (this.type === 'fail') {
        return require('assets/image/header/ic_notice.svg')
      } else {
        return proxyUrl(this.image)
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
  padding: 1.429rem;
  &:before {
    position: absolute;
    top: 0;
    right: 0;
    width: calc(100% - 5.357em);
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
  top: 1.714rem;
  right: 1rem;
  width: 1.429rem;
  height: 1.429rem;
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
  flex: 0 0 auto;
  width: 2.571rem;
  height: 2.571rem;
  margin: 0 1.429em auto 0;
  @include image();
  > .info,
  > .info_user,
  > .license {
    background-color: $color_primary;
  }
  > .fail {
    background-color: $color_yellow;
  }
}
.notice-item__body {
  flex: 1 1 auto;
  max-width: calc(100% - 4.286em);
}
.notice-item__info {
  color: $color_text_red;
  font-size: 0.857rem;
  &.info_user {
    color: $color_text;
  }
  &.message,
  &.invite,
  &.file {
    color: rgba($color_text, 0.5);
  }
  > em {
    color: $color_primary_500;
  }
}
.notice-item__description {
  overflow: hidden;
  color: $color_text;
  font-size: 1.143rem;
  white-space: nowrap;
  text-overflow: ellipsis;
  > em {
    color: $color_primary_500;
  }
}
.notice-item__date {
  color: #98a0a6;
  font-size: 0.857rem;
}
.notice-item__buttons {
  margin-top: 0.714rem;
  > button {
    margin-right: 1rem;
    &.refuse {
      width: 90%;
      color: #bfddff;
      font-weight: normal;
      background-color: rgba(#bfddff, 0.15);
      border: none;
      pointer-events: none;
    }
    &.filelink {
      position: relative;
      padding-left: 2.857rem;
      color: $color_link;
      background-color: rgba($color_link, 0.12);
      border: solid 1px rgba($color_link, 0.4);
      &:before {
        position: absolute;
        top: 0.357rem;
        left: 0.714rem;
        width: 1.571rem;
        height: 1.571rem;
        background: url(~assets/image/material_link.svg) 50%/22px no-repeat;
        content: '';
      }
    }
  }
}

@include responsive-mobile {
  .notice-item::before {
    width: 100%;
    height: 1.5px;
    background-color: $new_color_line_border;
  }
  .notice-item__close {
    top: 1rem;
    right: 0.4rem;
    opacity: 1;
  }
  .notice-item__image {
    margin-right: 1rem;
  }
  .notice-item__info {
    @include fontLevel(50);
    color: $new_color_text_sub;
    > em {
      color: $new_color_text_blue;
    }
  }
  .notice-item__description {
    color: $new_color_text_main;
    @include fontLevel(100);
  }
  //날짜
  .notice-item__date {
    margin-top: 0.4rem;
    color: $new_color_text_sub_description;
    @include fontLevel(50);
  }
  //수락/거절 버튼
  .notice-item__buttons {
    display: flex;
    margin-top: 1.2rem;
    > .btn.small {
      width: 8rem;
      min-width: 8rem;
      height: 3.6rem;
      padding: 0;
      color: $new_color_text_main;
      background-color: $new_color_bg_button_primary;
      border-radius: 0.4rem;
      @include fontLevel(100);
      &.btn.sub {
        background-color: $new_color_bg_button_sub;
      }
    }
  }
}
</style>
