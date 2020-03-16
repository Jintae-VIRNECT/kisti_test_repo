<template>
  <div>
    <the-sidebar :logo="sideLogo" :menus="sideMenus" :bottomMenus="sideMenus" />
    <div>
      <the-top-nav :logo="topLogo" :title="topTitle" :user="user" />
      <main>
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'

import { sideLogo, sideMenus, topLogo, topTitle } from '@/data/layout'

export default {
  components: {
    TheSidebar,
    TheTopNav,
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
