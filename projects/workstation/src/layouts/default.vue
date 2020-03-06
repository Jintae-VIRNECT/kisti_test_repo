<template>
  <div>
    <div v-if="getIsLoggedIn">
      <the-sidebar></the-sidebar>
      <div class="admin-container">
        <the-top-nav></the-top-nav>
        <main class="admin-body">
          <nuxt />
        </main>
      </div>
    </div>

    <div v-else>
      <nuxt />
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import TheSidebar from 'WC-Modules/vue/components/layout/TheSidebar.vue'
import TheTopNav from '@/components/layout/TheTopNav.vue'

export default {
  components: {
    TheSidebar,
    TheTopNav,
  },
  computed: {
    ...mapGetters({ getIsLoggedIn: 'user/getIsLoggedIn' }),
  },
}
</script>
