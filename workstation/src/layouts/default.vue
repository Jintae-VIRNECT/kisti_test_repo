<template>
  <div>
    <header>
      <the-header :showSection="showSection" />
    </header>
    <div>
      <the-sidebar
        :logo="sideLogo"
        :menus="sideMenus"
        :bottomMenus="sideBottomMenus"
      />
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

import { sideLogo, sideMenus, sideBottomMenus } from '@/models/layout'

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
      sideLogo,
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
