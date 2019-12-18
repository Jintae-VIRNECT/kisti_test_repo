<template lang="pug">
	el-menu.el-menu-demo(:default-active="activeLink" mode="horizontal" :router="true")
		el-menu-item(index="/") Home
		el-menu-item
			el-button(type="text" v-if="$store.getters.getIsLoggedIn === false" @click="toggleUserModal") Sign in
			el-button(v-else @click="userLogout") Logout
		el-menu-item(index="/posts" ) Posts
		el-menu-item
			span {{getUser.email}}
		el-submenu(index="locale")
			template(slot="title") {{this.$i18n.messages[$i18n.locale].language_type}}
			el-menu-item(:index="null" @click="onSetLangauge('ko')") 한국어
			el-menu-item(:index="null" @click="onSetLangauge('en')") English
</template>

<script>
import { mapGetters } from 'vuex'
import EventBus from '@/utils/eventBus.js'

export default {
	data() {
		return {
			activeLink: null,
			i18nLanguageType: null,
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
		onSetLangauge(lang) {
			this.$i18n.locale = lang
			this.$store.commit('USER_SET_LOCALE', { locale: lang })
		},
		toggleUserModal() {
			EventBus.$emit('toggleUserModal', true)
		},
	},
	watch: {
		$route(to) {
			const paths = to.path.split('/')
			this.activeLink = paths.length > 2 ? '/' + paths[1] : to.path
		},
	},
}
</script>
