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
          <h6 v-html="app.deviceType" />
          <h5 v-html="app.deviceName" />
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

  function snbNav() {
    const isDesktop = window.innerWidth > 1200
    const scrollY = window.pageYOffset
    const header = document.querySelector('#headerSection')
    const tab = document.querySelector('.el-tabs')
    snbTop = isDesktop ? tab.offsetTop : tab.offsetTop - header.offsetHeight
    if (scrollY > snbTop) {
      tab.classList.add('sticky')
    } else {
      tab.classList.remove('sticky')
    }
  }

  // vue hooks
  onMounted(() => {
    window.addEventListener('scroll', snbNav)
  })
  onUnmounted(() => {
    window.removeEventListener('scroll', snbNav)
  })
}
</script>

<style lang="scss">
@import '@/assets/css/home.scss';
</style>
