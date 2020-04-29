<template>
  <div>
    <header>
      <the-header :showSection="showSection">
        <template slot="subTitle">{{ $t('menu.account') }}</template>
      </the-header>
    </header>
    <the-sidebar :menus="sideMenus" />
    <main>
      <nuxt />
    </main>
  </div>
</template>

<script>
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'
import TheSidebar from '@/components/TheSidebar'
import { sideMenus } from '@/models/layout'

export default {
  middleware: 'default',
  components: {
    TheHeader,
    TheSidebar,
  },
  data() {
    return {
      sideMenus,
      showSection: {
        login: false,
        lang: false,
        link: true,
        profile: true,
      },
    }
  },
}
</script>

<style lang="scss">
#headerSection .sub-title {
  font-size: 18px;
}
</style>
