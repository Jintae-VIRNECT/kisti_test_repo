<template>
  <div class="capture">
    <div class="capture-header">
      <p class="capture-header__title">영상이 캡쳐되었습니다.</p>
      <button class="capture-header__close" @click="close">
        닫기
      </button>
    </div>
    <div class="capture-body">
      <img class="capture-image" :src="imgUrl" />
      <div class="capture-tools">
        <button class="capture-tools_button" @click="recapture">
          <p>
            <img src="~assets/image/call/ic_recapture.svg" />
            다시 찍기
          </p>
        </button>
        <button class="capture-tools_button" @click="save">
          <p><img src="~assets/image/call/ic_download.svg" /> 이미지 저장</p>
        </button>
      </div>
      <button class="capture-share" @click="share">
        <p><img src="~assets/image/call/ic_share.svg" /> 캡쳐 이미지 공유</p>
      </button>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
  name: 'CaptureModal',
  data() {
    return {
      status: false,
    }
  },
  props: {
    imgData: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    ...mapGetters(['mainView', 'resolutions']),
    imgId() {
      if (this.imgData && this.imgData.id > 0) {
        return this.imgData.id
      } else {
        return 0
      }
    },
    imgUrl() {
      if (this.imgData && this.imgData.id > 0) {
        return this.imgData.img
      } else {
        return ''
      }
    },
    resolution() {
      const idx = this.resolutions.findIndex(
        data => data.connectionId === this.mainView.connectionId,
      )
      if (
        idx < 0 ||
        this.resolutions[idx].width * this.resolutions[idx].height === 0
      ) {
        return {
          width: 0,
          height: 0,
        }
      }
      return this.resolutions[idx]
    },
  },
  methods: {
    ...mapActions(['addHistory', 'setView', 'clearCapture']),
    recapture() {
      this.$eventBus.$emit('capture')
    },
    save() {},
    share() {
      this.addHistory(this.imgData)
      this.setView('drawing')
      this.$nextTick(() => {
        this.close()
      })
    },
    close() {
      this.clearCapture()
    },
    getPosition() {},
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {
    console.log(this.imgData)
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.capture {
  position: absolute;
  right: 2.143rem;
  bottom: 2.143rem;
  background-color: $color_darkgray_500;
  border: solid 1px $color_darkgray_400;
  border-radius: 2px;
}
.capture-header {
  position: relative;
}
.capture-header__title {
  padding: 1rem 1.143rem;
  font-weight: 500;
}
.capture-header__close {
  position: absolute;
  top: 50%;
  right: 1rem;
  width: 1.429rem;
  height: 1.429rem;
  background: url(~assets/image/call/ic-close-w.svg) center no-repeat;
  transform: translateY(-50%);
  @include ir();
}
.capture-body {
}
.capture-image {
  width: 25.714rem; //360px;
  height: 14.429rem; // 202px;
}
.capture-tools {
  display: flex;
  margin: 1.286rem 1rem;
}
.capture-tools_button {
  position: relative;
  display: flex;
  flex: 1;
  background: transparent;
  opacity: 0.8;
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
  &:hover,
  &:active {
    opacity: 1;
  }

  > p {
    display: flex;
    margin: auto;
    color: #fff;
    font-size: 0.929rem;
    line-height: 2rem;
    text-align: center;
    > img {
      width: 2rem;
      height: 2rem;
      margin-right: 0.571rem;
      transform: translateY(-1px);
    }
  }
}
.capture-share {
  display: flex;
  width: calc(100% - 1.714rem);
  margin: 0.857rem;
  text-align: center;
  background-color: rgba(#626268, 0.4);
  border-radius: 2px;

  > p {
    display: flex;
    margin: 0.429rem auto;
    color: $color_text;
    font-size: 0.929rem;
    line-height: 2rem;
    line-height: 2rem;
    opacity: 0.8;
    > img {
      width: 2rem;
      height: 2rem;
    }
  }
  &:hover,
  &:active {
    > p {
      opacity: 1;
    }
  }
}
</style>
