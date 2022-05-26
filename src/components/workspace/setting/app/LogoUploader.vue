<template>
  <div class="logo-uploader">
    <div class="product">
      <h5>{{ $t(logo.title) }}</h5>
      <el-upload
        ref="logoUpload"
        action="#"
        accept=".png"
        :auto-upload="false"
        :on-change="onChange"
        :show-file-list="false"
      >
        {{ $t('workspace.onpremiseSetting.favicon.upload') }}
      </el-upload>
    </div>
    <div class="preview" :class="logo.type">
      <div class="area">
        <span class="editable">
          <img :src="logo.src || logo.defaultLogo" />
        </span>
      </div>
      <div class="tooltip">
        {{ $t('workspace.onpremiseSetting.logo.tooltip') }}
      </div>
      <p class="caution" v-html="$t(logo.caution)" />
    </div>
  </div>
</template>

<script>
export default {
  props: {
    logo: {
      type: Object,
      require: true,
    },
    clearFiles: {
      type: Boolean,
      require: true,
    },
  },
  data() {
    return {
      logoFile: null,
    }
  },
  watch: {
    clearFiles(val) {
      if (val) {
        this.$refs.logoUpload.clearFiles()
      }
    },
  },
  methods: {
    /**
     * 파일 용량 체크
     * @param {number} limitSize 제한할 MB
     * @param {File} file 이미지 파일
     * @returns 용량 초과면 true, 아니면 false 리턴
     */
    checkLimitImageSize(limitSize, fileSize) {
      if (typeof limitSize !== 'number') return true

      const imageLimitSize = limitSize * 1024 * 1024

      if (fileSize > imageLimitSize) return true

      return false
    },
    /**
     * png 파일이 아닌지 확인
     * @param {File} file
     * @returns png 파일이 아니면 리턴 true
     */
    isNotPngFile(file) {
      return file.type.includes('png') ? false : true
    },
    /**
     * 파일 에러 메시지 출력
     */
    printFileError() {
      this.$message.error({
        message: this.$t('workspace.appSetting.warning'),
        duration: 4000,
        showClose: true,
      })
    },
    onChange(file) {
      if (this.isNotPngFile(file.raw)) {
        this.printFileError()
        return false
      }
      if (this.checkLimitImageSize(3, file.size)) {
        this.printFileError()
        return false
      }

      const reader = new FileReader()
      this.logoFile = file.raw
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.$emit('logoSelected', this.logoFile, reader.result, this.logo.type)
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .logo-uploader {
  .preview.androidType1Logo {
    .area > .editable {
      width: 96px;
      height: 96px;
      top: 20px;
      left: 70px;
    }
    .tooltip {
      top: 0px;
      left: 70px;
    }
  }
  .preview.androidType2Logo {
    .area > .editable {
      width: 194px;
      height: 44px;
      top: 48px;
      left: 23px;
    }
    .tooltip {
      top: 28px;
      left: 23px;
    }
  }
  .preview.hololens2Logo {
    .area > .editable {
      width: 194px;
      height: 55px;
      top: 42px;
      left: 23px;
    }
    .tooltip {
      top: 22px;
      left: 23px;
    }
  }
  .caution {
    width: 224px;
    color: var(--color-static-gray-70);
    @include fontLevel(50);
    font-weight: 600;
    line-height: 18px;
    span {
      color: var(--color-blue-70);
    }
    blockquote {
      margin-block-start: unset;
      margin-block-end: unset;
      margin-inline-start: 8px;
      margin-inline-end: unset;
    }
  }
}
</style>
