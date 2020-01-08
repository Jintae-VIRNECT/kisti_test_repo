<template lang="pug">
	.top-nav
		.logo
			b SMIC
			span Workstation
		.top-nav__right
			img.profile-img(src="") 
			el-button(v-if="$store.getters.getIsLoggedIn === true" @click="userLogout") 로그아웃
			span {{getUser.email}}
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
				.then(() => this.$router.push('/'))
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
