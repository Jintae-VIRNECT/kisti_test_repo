<template>
  <div id="home">
    <div id="subVisualSection" class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <div class="ribbon" v-html="$t('home.notice')" v-if="!$isOnpremise" />
    <client-only>
      <snb :products="products" :activeTab="activeTab" @tabClick="tabClick" />
    </client-only>
    <div class="download-contents-wrapper">
      <article class="download-contents" v-if="isLoading">
        <VirnectSkeleton style="width: 120px; height: 30px" />
        <section class="item">
          <div class="info-wrapper">
            <VirnectSkeleton style="width: 100px; height: 20px" />
            <VirnectSkeleton style="width: 240px; height: 40px" />
            <VirnectSkeleton
              style="width: 60px; height: 20px; margin-top: 28px"
            />
            <VirnectSkeleton style="width: 140px; height: 16px" />
          </div>
          <div class="button-wrapper">
            <VirnectSkeleton style="width: 100%; height: 37px" />
            <VirnectSkeleton style="width: 100%; height: 37px" />
            <VirnectSkeleton
              style="
                width: 100px;
                height: 30px;
                margin-left: auto;
                margin-top: 28px;
              "
            />
          </div>
        </section>
      </article>
      <template v-else>
        <Item
          v-for="(category, prod) of products[activeTab]"
          :key="prod"
          :app="category"
          :activeTab="activeTab"
        />
      </template>

      <figure v-if="isEmpty && !isLoading">
        <img src="~assets/images/No_file@2x.png" />
        <figcaption>
          <p class="desc">설치 파일이 등록되지 않았습니다.</p>
          <p class="noti">관리자에게 문의해 주세요</p>
        </figcaption>
      </figure>
    </div>
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
      isLoading: true,
      activeTab: null,
      isEmpty: false,
      products: {
        remote: [],
        make: [],
        view: [],
        track: [],
      },
      // TODO: 추후 Track 버전을 서버에서 전달받아 URL에 넣어주기.
      trackDownloadUrl: 'http://track.virnect.com/1.2.0/download/download/',
      guide: {
        remote: {
          Realwear: {
            url: `https://file.virnect.com/Guide/remote_realwear_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
          Mobile: {
            url: `https://file.virnect.com/Guide/remote_mobile_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
          Linkflow: {
            url: `https://file.virnect.com/Guide/remote_linkflow_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
          Hololens: {
            url: `https://file.virnect.com/Guide/remote_hololens2_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
        },
        make: {
          PC: {
            url: `https://file.virnect.com/Guide/make_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
        },
        view: {
          Realwear: {
            url: `https://file.virnect.com/Guide/view_realwear_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
          Mobile: {
            url: `https://file.virnect.com/Guide/view_mobile_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
          Hololens: {
            url: `https://file.virnect.com/Guide/view_hololens2_user_guide_EN.pdf?t=${new Date().getTime()}`,
          },
        },
      },
    }
  },
  watch: {
    async activeTab(tab, oldTab) {
      if (tab === 'track') {
        window.open(this.trackDownloadUrl, '_blank')
        document.querySelector(`#tab-${oldTab}`).click()
        this.activeTab = oldTab
        return
      }
      window.history.replaceState({}, null, tab)

      if (!this.products[tab].length) {
        this.products[tab] = await this.loadList(tab)

        /**
         * @todo Hotfix 건으로 임시로 추가 2022.06.24
         * 국문과 영문에 따라 다운로드 링크가 서버에 다르게 요청 필요
         * 서버도 관련 API 작업이 필요함
         */
        if (this.$i18n.locale === 'en') {
          this.updateEnglishGuideUrl()
        }

        this.isEmpty = this.products[tab].length === 0 ? true : false
      }
    },
    async '$i18n.locale'(locale) {
      this.products[this.activeTab] = await this.loadList(this.activeTab)

      if (locale === 'en') {
        this.updateEnglishGuideUrl()
      }
    },
  },
  computed: {
    isMobile() {
      return this.$store.getters['mobile/isMobile']
    },
  },
  filters,
  methods: {
    updateEnglishGuideUrl() {
      this.products[this.activeTab] = this.products[this.activeTab].map(
        product => {
          product.guideUrl = this.guide[this.activeTab][product.deviceType].url
          return product
        },
      )
    },
    tabClick(product) {
      this.activeTab = product
    },
    storeId(productName) {
      return app => {
        if (storeIds[productName])
          app.storeId = storeIds[productName][app.os][app.deviceType]
        return app
      }
    },
    async loadList(productName) {
      const { appInfoList } = await this.$api('APP_LIST', {
        route: { productName },
      })
      this.isEmpty = this.products[this.activeTab].length === 0 ? true : false

      if (!this.$isOnpremise) {
        return appInfoList.map(this.storeId(productName))
      } else return appInfoList
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

    if (this.$isOnpremise) {
      this.activeTab =
        {
          '/remote': 'remote',
          '/make': 'make',
          '/view': 'view',
        }[this.redirectPath] || 'remote'

      this.products = {
        remote: [],
        make: [],
        view: [],
      }
    }
    setTimeout(() => {
      this.isLoading = false
    }, 400)
  },
}
</script>

<style lang="scss">
@import '@/assets/css/home.scss';
</style>
