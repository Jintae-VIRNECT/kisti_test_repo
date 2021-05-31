<template>
  <el-card class="item--old">
    <h6 v-html="app.deviceType" />
    <h5 v-html="app.deviceName" />
    <img :src="app.imageUrl" />
    <span class="release"> Release: {{ app.releaseTime | dateFormat }} </span>
    <span class="version">{{ app.version }}</span>
    <el-button type="primary" @click="link('app', app)">
      {{ downloadText(app) }}
    </el-button>
    <el-button
      type="text"
      @click="link('guide', app)"
      :disabled="!app.guideUrl"
    >
      {{ $t('home.guide') }}
    </el-button>
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
      if (/play\.google\.com/.test(app.appUrl)) str = 'Google Play'
      if (/apps\.apple\.com/.test(app.appUrl)) str = 'App Store'
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
  },
}
</script>

<style lang="scss">
.el-tabs__content .item--old.el-card {
  display: inline-block;
  width: 372px;
  min-height: 364px;
  margin: 0 18px 32px;
  vertical-align: top;
  box-shadow: none;

  @media screen and (max-width: $tablet) {
    max-width: 372px;
    margin: 18px 18px 0 18px;
  }
  @media screen and (max-width: $short) {
    width: calc(100% - 36px);
    max-width: none;
  }

  .el-card__body {
    padding: 36px 44px 20px;
    h6 {
      font-size: 18px;
    }
    h5 {
      font-size: 26px;
      overflow-wrap: break-word;
      span {
        color: $color-primary;
      }
    }
    img {
      width: 248px;
      margin: 10px auto 16px;
    }
    & > span {
      display: block;
    }
    .release {
      color: #8e95a1;
      font-weight: normal;
    }
    .version {
      margin-bottom: 23px;
      color: #3f465a;
      font-size: 18px;
    }
    .el-button {
      display: block;
      height: 36px;
      margin: 8px auto;
      padding: 10px 20px;
    }
  }
}
</style>
