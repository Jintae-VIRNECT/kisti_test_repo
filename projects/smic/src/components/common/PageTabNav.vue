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
      el-input.tool.search(placeholder='이름 또는 이메일 검색' v-model='search' )
        el-button(slot='append' icon='el-icon-search')
      span 필터 : 
      el-dropdown.tool.filter
        el-button(type='primary')
          | 전체
          i.el-icon-arrow-down.el-icon--right
        el-dropdown-menu(slot='dropdown')
          el-dropdown-item Action 1
          el-dropdown-item Action 2
          el-dropdown-item Action 3
          el-dropdown-item Action 4
          el-dropdown-item Action 5
      span 정렬 : 
      el-dropdown.tool.order
        el-button(type='primary')
          | ㄱ-ㅎ순
          i.el-icon-arrow-down.el-icon--right
        el-dropdown-menu(slot='dropdown')
          el-dropdown-item Action 1
          el-dropdown-item Action 2
          el-dropdown-item Action 3
          el-dropdown-item Action 4
          el-dropdown-item Action 5


      

</template>
<style lang="scss">
.page-nav {
	&--left {
		display: inline-block;
	}
	&--right {
		float: right;
		margin-top: 7px;
		.search {
			width: 196px;
			margin-right: 24px;
			.el-input__inner {
				width: 140px;
				float: right;
				margin-right: 0px;
			}
		}
		.filter {
			margin-right: 8px;
		}
		.el-input-group__append {
			padding: 7px 8px;
			background-color: rgba(233, 237, 244, 0.5);
			border: none;
			color: #7a869a;
			button {
				padding: 7px 8px;
			}
			i {
				color: black;
			}
		}
		.el-input__inner {
			margin-right: 12px;
			padding: 7px 8px;
			background-color: rgba(233, 237, 244, 0.5);
			box-shadow: none;
			border: none;
			.tool i {
				color: black;
			}
		}
		.tool {
			vertical-align: middle;
			i {
				color: black;
			}
			&.order button,
			&.filter button {
				padding: 7px 8px;
				background-color: rgba(233, 237, 244, 0.5);
				border: none;
				color: #7a869a;
			}
		}
		& .el-input__inner,
		& button {
			height: 34px;
		}
	}
}
</style>
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
			search: null,
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
