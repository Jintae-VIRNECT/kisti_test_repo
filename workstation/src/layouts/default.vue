<template>
  <div>
    <the-sidebar
      :logo="logo"
      :menus="sideMenus"
      :bottomMenus="sideBottomMenus"
    />
    <div>
      <header>
        <the-header />
      </header>
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideMenus, sideBottomMenus } from '@/models/layout'

export default {
  components: {
    TheSidebar,
    TheHeader,
  },
  data() {
    return {
      logo: {
        image: '',
        path: '/',
      },
      sideMenus,
      sideBottomMenus,
    }
  },
  computed: {
    ...mapGetters({ getUser: 'user/getUser' }),
    user() {
      return {
        name: this.getUser.name,
        image: this.getUser.profile,
      }
    },
  },
}
</script>

<style lang="scss">
.header-section {
  left: $the-sidebar-width !important;
  width: calc(100% - #{$the-sidebar-width}) !important;
}
</style>
