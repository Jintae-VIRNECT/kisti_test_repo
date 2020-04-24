<template>
  <div>
    <header>
      <the-header :showSection="showSection" />
    </header>
    <div>
      <the-sidebar :menus="sideMenus" :bottomMenus="sideBottomMenus" />
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideMenus, sideBottomMenus } from '@/models/layout'

export default {
  middleware: 'default',
  components: {
    TheSidebar,
    TheHeader,
  },
  data() {
    return {
      showSection: {
        profile: true,
      },
      sideMenus,
      sideBottomMenus,
    }
  },
}
</script>

<style lang="scss">
:not(.no-sidebar) > .header-section {
  left: $the-sidebar-width !important;
  width: calc(100% - #{$the-sidebar-width}) !important;
}
</style>
