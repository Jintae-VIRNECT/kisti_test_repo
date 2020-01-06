<template lang="pug">
	el-menu.el-menu-demo(:default-active="activeLink" mode="horizontal" :router="true")
		el-menu-item(index="/") Home
		el-menu-item
			el-button(type="text" v-if="$store.getters.getIsLoggedIn === false") Sign in
			el-button(v-else @click="userLogout") Logout
		el-menu-item(index="/members" ) Members
		el-menu-item(index="/contents" ) Contents
		el-menu-item
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
