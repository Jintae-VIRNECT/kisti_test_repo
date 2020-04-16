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
import { mapGetters } from 'vuex'

import TheSidebar from '@/components/layout/TheSidebar'
import TheHeader from 'WC-Modules/vue/components/header/TheHeader'

import { sideLogo, sideMenus, topLogo, topTitle } from '@/models/layout'

export default {
  components: {
    TheSidebar,
    TheHeader,
  },
  data() {
    return {
      sideLogo,
      sideMenus,
      topLogo,
      topTitle,
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
.main-container {
  padding: 40px;

  a:hover {
    color: blue;
  }
}
</style>
