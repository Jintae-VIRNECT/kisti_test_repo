<template>
  <div id="home">
    <div class="ribbon">
      {{ $t('home.needPlan') }}
    </div>
    <div class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <el-tabs v-model="activeTab" @tab-click="tabClick">
      <el-tab-pane
        v-for="(product, name) in products"
        :label="$t(`home.${name}`)"
        :name="name"
        :key="name"
      >
        <el-card v-for="app in product" :key="app.id">
          <h6 v-html="app.deviceType" />
          <h5 v-html="app.deviceName" />
          <img :src="app.imageUrl" />
          <span class="release">
            Release: {{ app.releaseTime | dateFormat }}
          </span>
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
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { filters } from '@/plugins/dayjs'
export default {
  data() {
    return {
      activeTab: null,
      isMobile: false,
      snbTop: 0,
      didScroll: 0,
      products: {
        remote: [],
        make: [],
        view: [],
        track: [],
      },
    }
  },
  watch: {
    async activeTab(tab) {
      window.history.replaceState({}, null, tab)
      if (this.products[tab].length) return false
      const data = await this.$api('APP_LIST', {
        route: { productName: tab },
      })
      this.products[tab] = data.appInfoList
    },
    async '$i18n.locale'() {
      const data = await this.$api('APP_LIST', {
        route: { productName: this.activeTab },
      })
      this.products[this.activeTab] = data.appInfoList
    },
  },
  filters,
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
    snbNav() {
      const scrollY = window.pageYOffset
      const tab = document.querySelector('.el-tabs')
      this.snbTop = tab.offsetTop
      this.isMobile = document.body.clientWidth < 1200
      if (this.isMobile) {
        this.snbTop -= 64
      }
      if (scrollY > this.snbTop) {
        tab.classList.add('sticky')
      } else {
        tab.classList.remove('sticky')
      }
    },
    tabClick() {
      if (window.pageYOffset > this.snbTop) {
        window.scrollTo(0, this.snbTop)
      }
    },
  },
  mounted() {
    this.activeTab =
      {
        '/remote': 'remote',
        '/make': 'make',
        '/view': 'view',
        '/track': 'track',
      }[this.$route.path] || 'remote'
    window.addEventListener('scroll', this.snbNav)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.snbNav)
  },
}
</script>

<style lang="scss">
@import '@/assets/css/home.scss';
</style>
