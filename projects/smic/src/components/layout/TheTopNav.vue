<template lang="pug">
	.top-nav
		.top-nav__left
			router-link(to="/")
				img.logo-img(src="~@/assets/image/logo-smart-factory.png") 
			.divider
			label.workspace {{getWorkspaceName}}
		.top-nav__right(v-if="$store.getters.getIsLoggedIn === true")
			img.profile-img(:src="getUser.profile") 
			span.username {{getUser.name}}
			button.logout-btn(@click="userLogout") 로그아웃
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
    ...mapGetters(['getUser', 'getLocale', 'getWorkspaceName']),
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
