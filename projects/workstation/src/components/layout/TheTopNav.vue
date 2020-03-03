<template>
  <div class="top-nav">
    <div class="top-nav__left">
      <router-link to="/"
        ><img
          class="logo-img"
          src="~assets/image/top-bar-logo-smart-factory.png"
      /></router-link>
      <div class="divider"></div>
      <label class="workspace">{{ getWorkspaceName }}</label>
    </div>
    <div class="top-nav__right" v-if="$store.getters.getIsLoggedIn === true">
      <img class="profile-img" :src="getUser.profile" /><span
        class="username"
        >{{ getUser.name }}</span
      ><button class="logout-btn" @click="userLogout">로그아웃</button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      activeLink: null,
    }
  },
  computed: {
    ...mapGetters(['user/*']),
  },
  mounted() {
    this.activeLink = this.$route.path
  },
  methods: {
    async userLogout() {
      try {
        await this.$store.dispatch('USER_LOGOUT', {
          user: this.$store.uid,
        })
        this.$router.go('/')
      } catch (e) {
        console.log('e : ', e)
      }
    },
  },
  watch: {
    $route(to) {
      const paths = to.path.split('/')
      this.activeLink = '/' + paths[1]
    },
  },
}
</script>
