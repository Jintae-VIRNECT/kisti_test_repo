<template lang="pug">
  .page-nav
    .page-nav--left
      el-tabs.page-tab-nav(v-model='activeTab' @tab-click="pathToTab") 
        el-tab-pane(
          v-for="(m, index) in model" 
          :key="index" 
          :label="m.title" 
          :name="m.to"
      )
    .page-nav--right
      slot(name="page-nav--right")
</template>
<script>
const model = [
	{ title: '멤버', to: '/members' },
	{ title: '콘텐츠', to: '/contents' },
	{ title: '공정', to: '/process' },
]
export default {
	data() {
		return {
			model,
			activeTab: null,
		}
	},
	methods: {
		pathToTab({ name }) {
			this.$router.push(name)
		},
	},
	mounted() {
		const paths = this.$route.path.split('/')
		this.activeTab = '/' + paths[1]
	},
	watch: {
		$route(to) {
			const paths = to.path.split('/')
			this.activeTab = '/' + paths[1]
		},
	},
}
</script>
