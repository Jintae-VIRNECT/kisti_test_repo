<template>
  <el-card class="item">
    <el-row>
      <el-col :xs="24" :sm="13">
        <h6 v-html="app.os" />
        <h5 v-html="app.deviceName" />
        <p class="version">{{ app.version }}</p>
        <p class="release">Released: {{ app.releaseTime | dateFormat }}</p>
      </el-col>
      <el-col :xs="24" :sm="11">
        <el-button v-if="app.storeId" type="primary" @click="store(app)">
          {{ downloadText(app) }}
        </el-button>
        <el-button
          v-if="app.appUrl"
          :type="app.storeId ? 'simple' : 'primary'"
          @click="link('app', app)"
        >
          {{ $t('home.download') }}
        </el-button>
        <el-button
          type="text"
          @click="link('guide', app)"
          :disabled="!app.guideUrl"
        >
          {{ $t('home.guide') }}
        </el-button>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
import { filters } from '@/plugins/dayjs'

export default {
  props: {
    app: Object,
  },
  filters: {
    ...filters,
  },
  methods: {
    downloadText(app) {
      let str = this.$t('home.download')
      if (app.os === 'Android') str = 'Google Play'
      if (app.os === 'iOS') str = 'App Store'
      if (app.os === 'Windows(UWP)') str = 'Microsoft Store'
      return str
    },
    async download(type, app) {
      let uri, downloadUrl
      if (type === 'app') {
        uri = 'DOWNLOAD_APP'
        downloadUrl = app.appUrl
      }
      if (type === 'guide') {
        uri = 'DOWNLOAD_GUIDE'
        downloadUrl = app.guideUrl
      }
      try {
        await this.$api(uri, {
          route: { uuid: app.uuid },
        })
        return downloadUrl
      } catch (e) {
        this.$message.error({
          message: e,
          duration: 2000,
          showClose: true,
        })
      }
    },
    link(type, app) {
      const popup = window.open()
      this.download(type, app).then(url => {
        popup.location = url
      })
    },
    store(app) {
      const popup = window.open()
      if (app.os === 'Android')
        popup.location = `https://play.google.com/store/apps/details?id=${app.storeId}`
      if (app.os === 'iOS')
        popup.location = `https://apps.apple.com/kr/app/${app.storeId}`
      if (app.os === 'Windows(UWP)')
        // TODO: virnect remote도 고려하여 페이지 이동.
        popup.location = `https://www.microsoft.com/ko-kr/p/virnect-view-hololens/${app.storeId}`
    },
  },
}
</script>

<style lang="scss">
.el-tabs__content .item.el-card {
  margin-bottom: 24px;
  box-shadow: none;
  &:last-child {
    margin-bottom: 0;
  }

  .el-card__body {
    padding: 32px 32px 32px 40px;
  }

  .el-col:first-child {
    line-height: normal;
    text-align: left;

    h6 {
      font-size: 18px;
    }
    h5 {
      color: $color-primary;
      font-weight: bold;
      font-size: 26px;
    }
    .version {
      margin: 20px 0 8px;
      font-size: 20px;
    }
    .release {
      color: #8e95a1;
    }
  }
  .el-col:last-child {
    padding-left: 15px;
    text-align: right;

    button {
      display: block;
      width: 100%;
    }
    button + button {
      margin: 8px 0 0 0;
    }
    button.el-button--text {
      position: absolute;
      right: 0;
      bottom: 0;
      width: auto;
      height: 34px;
      padding: 7px 20px;
    }
  }

  @include responsive-to(max, 'medium') {
    .el-card__body {
      padding: 28px 12px 24px;
    }
    .el-col:first-child {
      margin-bottom: 29px;
      text-align: center;
      h6 {
        font-size: 16px;
      }
      h5 {
        font-size: 24px;
      }
    }
    .el-col:last-child {
      padding: 0 24px;
      button.el-button--text {
        position: inherit;
        width: 100%;
        margin-top: 16px;
      }
    }
  }
}
</style>
