<template>
  <article class="download-contents">
    <h3>{{ app.deviceType }}</h3>
    <section class="item">
      <div class="info-wrapper">
        <p class="os">{{ app.os }}</p>
        <p class="title" v-html="app.deviceName"></p>
        <p class="version">{{ app.version }}</p>
        <p class="release">Released: {{ app.releaseTime | dateFormat }}</p>
      </div>
      <div class="button-wrapper">
        <VirnectButton
          v-if="app.storeId"
          :label="downloadText(app.os)"
          type="primary"
          block
          @onClick="store(app)"
        />
        <VirnectButton
          v-if="app.appUrl"
          :label="$t('home.download')"
          :type="app.storeId ? null : 'primary'"
          block
          @onClick="link('app', app)"
        />
        <VirnectButton
          class="guide-button"
          :label="$t('home.guide')"
          :disabled="!app.guideUrl"
          clear
          @onClick="link('guide', app)"
        />
      </div>
    </section>
  </article>
</template>

<script>
import { filters } from '@/plugins/dayjs'

export default {
  props: {
    app: Object,
    activeTab: String,
  },
  data() {
    return {
      msHololensShortUrl: {
        remote: 'virnect-remote-2',
        view: 'virnect-view-hololens',
      },
    }
  },
  filters: {
    ...filters,
  },
  methods: {
    downloadText(os) {
      if (os === 'Android') return 'Google Play'
      if (os === 'iOS') return 'App Store'
      if (os === 'Windows(UWP)') return 'Microsoft Store'
      return this.$t('home.download')
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
        popup.location = `https://www.microsoft.com/ko-kr/p/${
          this.msHololensShortUrl[this.activeTab]
        }/${app.storeId}`
    },
  },
}
</script>
