<template lang="pug">
	nav.sidebar
		.sidebar-inner(@click.stop='')
			h1.sidebar--logo
				a.popover--button(href='/')
					img(src='~@/assets/image/admin/ic-remote-symbol.svg' alt='Remote')
			.sidebar--tools
				ul.sidebar--mode
					li.sidebar--item(
						v-for='menu of menus' 
						:key='menu.path' 
						:class='{ current: isCurrentMenu(menu.path), active: isActiveMenu(menu.path)}')
						button.sidebar--item__button(@mouseover="currentMenuPath = menu.path")
							img(:src='menu.image' :alt='menu.label')
							span.sidebar--item__label {{ menu.label }}
						nav.submenu(v-if="(menu.subMenus && menu.path === currentMenuPath)" @mouseleave="resetSubmenu")
							.submenu--logo
								h1.submenu--logo__image
									img(src='~assets/image/admin/ic-logo-rectangle.svg')
							ul.submenu-inner
								li.submenu--item.title
									span 테스트
								li.submenu--item(@click="resetSubmenu" v-for='subMenu of menu.subMenus' :key='subMenu.path' :class="{'active' : isActiveSubMenu(subMenu.path)}")
									router-link(:to="subMenu.path")
										el-button {{ menu.label }}

</template>

<script>
export default {
	props: {
		menus: Array,
	},
	data() {
		return {
			currentMenuPath: '',
			currentSubMenuPath: '',
			activeMenuPath: '',
			activeSubMenuPath: '',
		}
	},
	watch: {
		$route(to) {
			const paths = to.path.split('/')
			this.activeMenuPath = '/' + paths[1]
			this.currentSubMenuPath = to.path
		},
	},
	methods: {
		showSubMenu(menu) {
			if (this.openedMenu === menu) {
				this.openedMenu = false
			} else {
				this.openedMenu = menu
			}
		},
		isCurrentMenu(path) {
			return path == this.currentMenuPath
		},
		isActiveMenu(path) {
			console.log('path : ', path)
			console.log('this.activeMenuPath : ', this.activeMenuPath)
			console.log('path == this.activeMenuPath : ', path == this.activeMenuPath)
			return path == this.activeMenuPath
		},
		isActiveSubMenu(path) {
			return path == this.currentSubMenuPath
		},
		resetSubmenu() {
			this.currentMenuPath = ''
		},
	},
}
</script>
