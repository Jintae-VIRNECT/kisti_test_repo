<template>
  <div id="home">
    <div class="ribbon">
      {{ $t('home.needPlan') }}
    </div>
    <div class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <el-tabs v-model="state.activeTab">
      <el-tab-pane
        v-for="(product, name) in state.products"
        :label="$t(`home.${name}`)"
        :name="name"
        :key="name"
      >
        <el-card v-for="app in product" :key="app.id">
          <h6 v-html="app.os" />
          <h5 v-html="app.device" />
          <img :src="app.imageUrl" />
          <span class="release">
            Release: {{ app.releaseTime | dateFormat }}
          </span>
          <span class="version">{{ app.version }}</span>
          <el-button type="primary" @click="download(app.appUrl)">
            {{ $t('home.installFileDownload') }}
          </el-button>
          <el-button
            type="text"
            @click="download(app.guideUrl)"
            :disabled="!app.guideUrl"
          >
            {{ $t('home.guideDownload') }}
          </el-button>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import fileDownload from 'js-file-download'
import { filters } from '@/plugins/dayjs'
import {
  defineComponent,
  reactive,
  watchEffect,
  onMounted,
  onUnmounted,
} from 'nuxt-composition-api'

export default defineComponent({
  filters,
  setup(props, context) {
    const state = useTabs(context.root)
    useStickyNav()

    /**
     * 다운로드
     */
    function download(url) {
      window.open(url)
    }
    function link(url) {
      window.open(url)
    }

    return { state, download, link }
  },
})

/**
 * 탭 변경
 */
function useTabs({ $route, $api }) {
  const state = reactive({
    activeTab: null,
    products: {
      remote: [],
      make: [],
      view: [],
    },
  })

  watchEffect(async () => {
    const { products, activeTab } = state
    if (activeTab && !products[activeTab].length) {
      const data = await $api('APP_LIST', {
        route: { productName: activeTab },
      })
      products[activeTab] = data.appInfoList
    }
  })

  onMounted(() => {
    state.activeTab =
      {
        '/remote': 'remote',
        '/make': 'make',
        '/view': 'view',
      }[$route.path] || 'remote'
  })

  return state
}

/**
 * stick nav
 */
function useStickyNav() {
  let snbTop = 0
  let didScroll = 0

  function hasScrolled() {
    const header = document.querySelector('#headerSection')
    const tab = document.querySelector('.el-tabs')
    if (window.pageYOffset > didScroll) {
      header.classList.add('sticky-pushup')
      tab.classList.add('sticky')
    } else {
      header.classList.remove('sticky-pushup')
      tab.classList.remove('sticky')
    }
    didScroll = window.pageYOffset
  }
  function snbNav() {
    const scrollY = window.pageYOffset
    snbTop = document.querySelector('.el-tabs').offsetTop
    if (scrollY > snbTop) {
      document.querySelector('#headerSection').classList.add('sticky-pushup')
      hasScrolled()
    } else {
      document.querySelector('#headerSection').classList.remove('sticky-pushup')
    }
  }

  // vue hooks
  onMounted(() => {
    window.addEventListener('scroll', snbNav)
  })
  onUnmounted(() => {
    window.removeEventListener('scroll', snbNav)
    window.removeEventListener('scroll', hasScrolled)
  })
}
</script>

<style lang="scss">
#home {
  color: $font-color-content;
  font-weight: 500;
  text-align: center;

  .ribbon {
    width: 100%;
    height: 50px;
    line-height: 50px;
    background: #e6f0ff;
  }
  .visual {
    height: 340px;
    padding: 93px;
    color: #fff;
    background: url('~assets/images/img-top.jpg') center;
    background-size: cover;

    h3 {
      font-size: 30px;
    }
    p {
      margin-top: 10px;
      font-size: 15px;
    }
  }
  .el-tabs {
    position: relative;
    margin: 0 auto;

    [role='tab'] {
      height: 64px;
      padding: 0 60px;
      font-size: 18px;
      line-height: 64px;

      &:nth-child(2) {
        padding-left: 0;
      }
      &:last-child {
        padding-right: 0;
      }
    }
  }
  .el-tabs__header {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    background: #fff;
    border-bottom: solid 1px #f3f6f9;
    .el-tabs__nav-wrap {
      width: 630px;
      margin: 0 auto;
    }
  }
  .el-tabs__nav-wrap::after {
    display: none;
  }
  .el-tabs.sticky .el-tabs__header {
    position: fixed;
    z-index: 1;
  }

  .el-tabs__content {
    width: 900px;
    min-height: 870px;
    margin: 0 auto;
    padding: 184px 0 160px;
  }
  .el-tabs__content .el-card {
    display: inline-block;
    width: 372px;
    margin: 0 18px 32px;
    box-shadow: none;
  }
  .el-card .el-card__body {
    padding: 36px 44px 30px;
    h6 {
      font-size: 18px;
    }
    h5 {
      font-size: 26px;
      span {
        color: $color-primary;
      }
    }
    img {
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
      margin: 5px auto;
    }
  }

  // transition
  .rise-enter {
    transform: translateY(10px);
    opacity: 0;
  }
  .rise-enter-to {
    transform: translateY(0);
    opacity: 1;
    transition: 0.5s 0.3s;
    &-2 {
      transition: 0.5s 0.5s;
    }
  }
}
</style>
