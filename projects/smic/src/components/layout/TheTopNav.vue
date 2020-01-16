<template lang="pug">
	.top-nav
		.top-nav__left
			router-link(to="/")
				img.logo-img(src="~@/assets/image/logo-smart-factory.png") 
			.divider
			label.workspace Workspace' name
		.top-nav__right(v-if="$store.getters.getIsLoggedIn === true")
			img.profile-img(src="~@/assets/image/admin/ic-user.svg") 
			span.username {{getUser.username}}
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
		...mapGetters(['getUser', 'getLocale']),
	},
	mounted() {
		this.activeLink = this.$route.path
	},
	methods: {
		userLogout() {
			this.$store
				.dispatch('USER_LOGOUT', { user: this.$store.uid })
				.then(() => this.$router.go('/'))
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
