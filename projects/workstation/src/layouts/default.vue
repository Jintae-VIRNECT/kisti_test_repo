<template>
  <div>
    <the-sidebar :logo="sideLogo" :menus="sideMenus" />
    <div>
      <the-top-nav :logo="topLogo" :title="topTitle" :user="user" />
      <main class="main-container">
        <nuxt />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

import TheSidebar from '@/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'

import { sideLogo, sideMenus, topLogo, topTitle } from '@/models/layout'

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

<style lang="scss">
.main-container {
  padding: 40px;

  a:hover {
    color: blue;
  }
}
</style>
