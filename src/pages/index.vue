<template>
  <div id="home">
    <div class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <div class="ribbon" v-html="$t('home.notice')" />
    <el-tabs v-model="activeTab" @tab-click="tabClick">
      <el-tab-pane
        v-for="(product, name) in products"
        :label="$t(`home.${name}`)"
        :name="name"
        :key="name"
      >
        <el-row v-for="category in product.categories" :key="category">
          <el-col :md="5" :span="24">
            <h4>{{ category }}</h4>
          </el-col>
          <el-col :md="19" :span="24">
            <Item
              v-for="app in product.list.filter(
                app => app.deviceType === category,
              )"
              :key="app.id"
              :app="app"
            />
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { filters } from '@/plugins/dayjs'
import storeIds from '@/models/storeIds'

export default {
  async asyncData({ route }) {
    return {
      redirectPath: route.path,
    }
  },
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
      trackDownloadUrl: 'http://track.virnect.com/download/download/',
    }
  },
  watch: {
    async activeTab(tab, oldTab) {
      if (tab === 'track') {
        window.open(this.trackDownloadUrl, '_blank')
        document.querySelector(`#tab-${oldTab}`).click()
        return
      }
      window.history.replaceState({}, null, tab)
      if (!this.products[tab].length) {
        this.products[tab] = await this.loadList(tab)
      }
    },
    async '$i18n.locale'() {
      this.products[this.activeTab] = await this.loadList(this.activeTab)
    },
  },
  filters,
  methods: {
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
    storeId(productName) {
      return app => {
        if (storeIds[productName])
          app.storeId = storeIds[productName][app.os][app.deviceType]
        return app
      }
    },
    async loadList(productName) {
      const data = await this.$api('APP_LIST', {
        route: { productName },
      })
      const categories = new Set()
      data.appInfoList.forEach(app => categories.add(app.deviceType))

      return {
        categories,
        list: data.appInfoList.map(this.storeId(productName)),
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
      }[this.redirectPath] || 'remote'
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
