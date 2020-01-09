<template lang="pug">
	.top-nav
		router-link.top-nav__left(to="/")
			img.logo-img(src="~@/assets/image/logo-smic.jpg") 
			.divider
			label.workspace Workspace' name
		.top-nav__right
			img.profile-img(src="~@/assets/image/admin/ic-user.svg") 
			span.username {{getUser.username}}
			button.logout-btn(v-if="$store.getters.getIsLoggedIn === true" @click="userLogout") 로그아웃
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

<style lang="scss">
$top-nav-height: 60px;
.top-nav {
	&__left {
		height: $top-nav-height;
		padding: 17px 27px;
		& > * {
			vertical-align: middle;
		}
		.logo-img {
			height: 26px;
		}
		.divider {
			display: inline-block;
			width: 1px;
			height: 20px;
			background-color: #cdd1d6;
			margin: 0px 15px;
		}
		.workspace {
			display: inline-block;
			font-size: 16px;
			font-weight: 500;
			font-stretch: normal;
			font-style: normal;
			line-height: normal;
			letter-spacing: normal;
			color: #0d2a58;
		}
	}
	&__right {
		position: absolute;
		right: 0;
		top: 0;
		height: $top-nav-height;
		padding: 12px 30px;
		& > * {
			vertical-align: middle;
		}
		.profile-img {
			border-radius: 50%;
			width: 28px;
			height: 28px;
			margin-right: 8px;
		}
		.username {
			font-size: 12px;
			font-weight: 500;
			font-stretch: normal;
			font-style: normal;
			line-height: 1.5;
			letter-spacing: normal;
			color: #081730;
			margin-right: 30px;
		}
		.logout-btn {
			height: 36px;
			padding: 7px 20px;
			border-radius: 3px;
			background-color: #eaedf3;
		}
	}
}
</style>
