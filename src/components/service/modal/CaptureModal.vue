<template>
  <div class="capture">
    <div class="capture-header">
      <p class="capture-header__title">영상이 캡쳐되었습니다.</p>
      <button class="capture-header__close" @click="$emit('close')">
        닫기
      </button>
    </div>
    <div class="capture-body">
      <img class="capture-image" :src="imgUrl" />
      <div class="capture-tools">
        <button class="capture-tools_button">
          <p>
            <img
              src="~assets/image/call/ic_recapture.svg"
              @click="$emit('recapture')"
            />
            다시 찍기
          </p>
        </button>
        <button class="capture-tools_button">
          <p><img src="~assets/image/call/ic_download.svg" /> 이미지 저장</p>
        </button>
      </div>
      <button class="capture-share">
        <p><img src="~assets/image/call/ic_share.svg" /> 캡쳐 이미지 공유</p>
      </button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'CaptureModal',
  props: {
    imgUrl: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      status: false,
    }
  },
  computed: {
    ...mapGetters(['mainView']),
    disabled() {
      if (!(this.mainView && this.mainView.id)) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {},
  methods: {
    capture() {
      if (this.disabled) return
      this.status = !this.status
    },
    close() {
      this.status = false
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.capture {
  position: absolute;
  right: 30px;
  bottom: 30px;
  width: 362px;
  background-color: #313135;
  border: solid 1px #3e3e41;
  border-radius: 2px;
}
.capture-header {
  position: relative;
}
.capture-header__title {
  padding: 14px 16px;
  font-weight: 500;
}
.capture-header__close {
  position: absolute;
  top: 50%;
  right: 14px;
  width: 20px;
  height: 20px;
  background: url(~assets/image/call/ic-close-w.svg) center no-repeat;
  transform: translateY(-50%);
  @include ir();
}
.capture-body {
}
.capture-image {
  width: 360px;
  height: 202px;
  background-color: #c7deff;
}
.capture-tools {
  display: flex;
  margin: 18px 14px;
}
.capture-tools_button {
  position: relative;
  display: flex;
  flex: 1;
  background: transparent;
  & + & {
    &:before {
      position: absolute;
      top: 0;
      left: 0;
      width: 1px;
      height: 100%;
      background-color: #464646;
      content: '';
    }
  }

  > p {
    display: flex;
    margin: auto;
    color: #fff;
    font-size: 13px;
    line-height: 28px;
    text-align: center;
    > img {
      width: 28px;
      height: 28px;
    }
  }
}
.capture-share {
  display: flex;
  width: calc(100% - 24px);
  margin: 12px;
  text-align: center;
  background-color: #626268;
  border-radius: 2px;

  > p {
    display: flex;
    margin: 6px auto;
    color: #fff;
    font-size: 13px;
    line-height: 28px;
    line-height: 28px;
    > img {
      width: 28px;
      height: 28px;
    }
  }
}
</style>
