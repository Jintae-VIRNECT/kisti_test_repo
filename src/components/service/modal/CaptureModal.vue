<template>
  <div class="capture">
    <div class="capture-header">
      <p class="capture-header__title">{{ $t('service.capture_message') }}</p>
      <button class="capture-header__close" @click="close">
        {{ $t('button.close') }}
      </button>
    </div>
    <div class="capture-body">
      <div class="capture-image" :class="{ shutter: showShutter }">
        <img :src="imageData" />
      </div>
      <div class="capture-tools">
        <button class="capture-tools_button" @click="recapture">
          <p>
            <img src="~assets/image/call/ic_recapture.svg" />
            {{ $t('service.capture_recapture') }}
          </p>
        </button>
        <div v-if="isMobileSize" class="divider"></div>
        <button class="capture-tools_button" @click="save">
          <p>
            <img src="~assets/image/call/ic_download.svg" />
            {{ $t('service.capture_image_save') }}
          </p>
        </button>
      </div>
      <button v-if="isLeader" class="capture-share" @click="share">
        <p>
          <img src="~assets/image/call/ic_share.svg" />
          {{ $t('service.capture_image_share') }}
        </p>
      </button>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import shutterMixin from 'mixins/shutter'
import confirmMixin from 'mixins/confirm'
import FileSaver from 'file-saver'

import { VIEW } from 'configs/view.config'
import { ERROR } from 'configs/error.config'

import { base64ToBlob } from 'utils/file'
import { remoteFileUpload } from 'api/http/drawing'
import { DRAWING, ROLE, FILE_TYPE } from 'configs/remote.config'
import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'

export default {
  name: 'CaptureModal',
  mixins: [shutterMixin, confirmMixin, toastMixin, errorMsgMixin],
  data() {
    return {
      status: false,
      imageData: '',
      doubleCheckFlag: false,
    }
  },
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    ...mapGetters(['view', 'roomInfo']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {
    file: {
      deep: true,
      handler(e) {
        if (e && e.id) {
          this.init()
        }
      },
    },
  },
  methods: {
    ...mapActions(['addHistory', 'setView', 'clearCapture']),
    init() {
      const fileReader = new FileReader()
      fileReader.onload = e => {
        this.imageData = e.target.result
      }
      fileReader.readAsDataURL(this.file.fileData)
    },
    recapture() {
      this.$eventBus.$emit('capture')
    },
    async save() {
      const dataType = 'application/octet-stream'
      const file = await base64ToBlob(
        this.imageData,
        dataType,
        this.file.fileName,
      )
      FileSaver.saveAs(file)
    },
    share() {
      if (this.imageData && this.imageData.length > 0) {
        if (this.view === VIEW.AR) {
          this.serviceConfirmTitle(
            this.$t('service.ar_exit'),
            this.$t('service.ar_exit_description'),
            {
              text: this.$t('button.exit'),
              action: () => {
                this.$call.sendArFeatureStop()
                this.shareCapture()
              },
            },
          )
        } else {
          this.shareCapture()
        }
      }
    },

    //한번만 false를 반환하고, 이후에는 true만 반환. 이미지 등록 후 모달창은 닫히기 때문에 초기화됨
    doubleCheck() {
      if (this.doubleCheckFlag) return this.doubleCheckFlag
      else {
        this.doubleCheckFlag = true
        return false
      }
    },

    async shareCapture() {
      if (this.doubleCheck()) return

      const result = await this.uploadImage()

      if (!result) return

      this.setView('drawing')
      this.$nextTick(() => {
        this.close()
      })
    },
    async uploadImage() {
      const dataType = 'image/jpg'
      const file = await base64ToBlob(
        this.imageData,
        dataType,
        this.file.fileName,
      )

      let res = null
      try {
        res = await remoteFileUpload({
          file: file,
          fileType: FILE_TYPE.SHARE,
          sessionId: this.roomInfo.sessionId,
          userId: this.account.uuid,
          workspaceId: this.workspace.uuid,
        })
        if (res.usedStoragePer >= 90) {
          this.toastError(this.$t('alarm.file_storage_about_to_limit'))
        } else {
          this.toastDefault(this.$t('alarm.file_uploaded'))
        }
      } catch (err) {
        if (
          err.code === ERROR.FILE_STORAGE_CAPACITY_FULL ||
          err.code === ERROR.FILE_EXTENSION_UNSUPPORT
        ) {
          this.showErrorToast(err.code)
        } else {
          if (err.code) {
            this.toastError(this.$t('confirm.network_error'))
          }
        }
        return false
      }

      //파일 업로드
      this.$call.sendDrawing(DRAWING.ADDED, {
        deleted: false, //false
        expired: false, //false
        sessionId: res.sesssionId,
        name: res.name,
        objectName: res.objectName,
        contentType: res.contentType, // "image/jpeg", "image/bmp", "image/gif", "application/pdf",
        size: res.size,
        createdDate: res.createdDate,
        expirationDate: res.expirationDate,
        width: res.width, //pdf 는 0
        height: res.height,
      })

      //파일 공유
      this.$call.sendDrawing(DRAWING.FILE_SHARE, {
        name: res.name,
        objectName: res.objectName,
        contentType: res.contentType,
        width: res.width,
        height: res.height,
        index: 0,
      })

      return true
    },
    close() {
      this.clearCapture()
    },
  },

  mounted() {
    this.init()
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.capture {
  position: absolute;
  right: 2.143rem;
  bottom: 2.143rem;
  z-index: 9;
  background-color: $color_darkgray_500;
  border: solid 1px $color_darkgray_400;
  border-radius: 4px;
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
.capture-image {
  position: relative;
  width: 25.714rem; //360px;
  height: 14.429rem; // 202px;
  background: #000;
  > img {
    position: relative;
    top: 50%;
    left: 50%;
    max-width: 100%;
    max-height: 100%;
    transform: translate(-50%, -50%);
  }

  &.shutter::after {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    background-color: #000;
    opacity: 0;
    animation: shutter 0.2s linear;
    content: '';
    user-select: none;

    @keyframes shutter {
      0% {
        opacity: 0;
      }
      40% {
        opacity: 0.52;
      }
      60% {
        opacity: 0.52;
      }
      100% {
        opacity: 0;
      }
    }
  }
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

@include responsive-mobile {
  .capture {
    bottom: 9.8rem;
    left: 50%;
    width: 91%;
    background-color: $new_color_popup_bg;
    border: none;
    border-radius: 0.6rem;
    transform: translateX(-50%);
  }
  .capture-header {
    display: flex;
    align-items: center;
    height: 4.9rem;
  }
  .capture-header__title {
    color: $new_color_text_main;
    @include fontLevel(100);
  }
  .capture-header__close {
    right: 1.6rem;
    width: 2rem;
    height: 2rem;
    background: url(~assets/image/call/mdpi_icon_close_new.svg) center no-repeat;
  }
  .capture-body {
    .capture-image {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 31.5vh;
      background-color: $new_color_sub_border;
      img {
        top: unset;
        left: unset;
        max-width: 100%;
        max-height: 100%;
        transform: unset;
      }
    }

    .capture-tools {
      .divider {
        height: 3rem;
        border-right: 1px solid $new_color_bg_button_sub;
      }
      button > p {
        align-items: center;
        @include fontLevel(100);
        color: $new_color_text_sub;
        img {
          width: 2.8rem;
          height: 2.8rem;
          margin-right: 0.8rem;
        }
      }
    }

    .capture-share {
      width: calc(100% - 3.2rem);
      margin: 1.4rem 1.6rem 1.6rem 1.6rem;
      color: $new_color_text_main;
      background-color: $new_color_bg_button_sub;
      border-radius: 4px;
      p {
        align-items: center;
        @include fontLevel(75);
        img {
          width: 2.8rem;
          height: 2.8rem;
          margin-right: 0.8rem;
        }
      }
    }
  }
}
</style>
