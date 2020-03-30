<template>
  <div>
    <header>
      <the-header />
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
  components: {
    TheHeader,
    TheSidebar,
  },
  data() {
    return {
      sideMenus,
    }
  },
}
</script>

<style lang="scss">
@import '@/assets/css/global.scss';
</style>
