<template lang="pug">
	nav.sidebar
		.sidebar-inner(@click.stop='')
			h1.sidebar--logo
				a.popover--button(href='/')
					img(src='~@/assets/image/lnb-logo-smart-factory.png' alt='Remote')
			.sidebar--tools
				ul.sidebar--mode
					//- li.sidebar--item(
					//- 	v-for='menu of menus' 
					//- 	:key='menu.path' 
					//- 	:class='{ current: isCurrentMenu, active: isActiveMenu(menu.path)}'
					//- )
					li.sidebar--item(
						v-for='menu of menus' 
						:key='menu.path' 
						:class='{ current: isCurrentMenu(menu.path), active: isActiveMenu(menu)}'
					)
						el-tooltip.item(effect='dark' :content='menu.label' placement='right')
							router-link(:to="menu.path")
								button.sidebar--item__button(@mouseover="currentMenuPath = menu.path" @mouseout="currentMenuPath = null")
									img(:src='menu.image' :alt='menu.label')
								//- span.sidebar--item__label {{ menu.label }}

						//- nav.submenu(v-if="(menu.subMenus && menu.path === currentMenuPath)" @mouseleave="resetSubmenu")
							.submenu--logo
								h1.submenu--logo__image
									img(src='~assets/image/admin/ic-logo-rectangle.svg')
							ul.submenu-inner
								li.submenu--item.title
									span 테스트
								li.submenu--item(@click="resetSubmenu" v-for='subMenu of menu.subMenus' :key='subMenu.path' :class="{'active' : isActiveSubMenu(subMenu.path)}")
									router-link(:to="subMenu.path")
										el-button {{ subMenu.label }}

</template>

<style lang="scss">
.sidebar--mode .sidebar--item {
  .sidebar--item__button {
    width: 40px;
    height: 40px;
    opacity: 0.75;
  }
  &.current .sidebar--item__button {
    background: none;
    img {
      opacity: 1;
    }
  }
  &.active .sidebar--item__button {
    background-color: rgba(0, 0, 0, 0);
    background: url('~@/assets/image/ic-bg.svg') no-repeat;
    img {
      opacity: 1;
    }
  }
}
</style>

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
    isActiveMenu(menu) {
      if (menu.pathAlias) return menu.pathAlias.includes(this.activeMenuPath)
      return menu.path == this.activeMenuPath
    },
    isActiveSubMenu(path) {
      return path == this.currentSubMenuPath
    },
    resetSubmenu() {
      this.currentMenuPath = ''
    },
  },
  created() {
    const paths = this.$route.path.split('/')
    this.activeMenuPath = '/' + paths[1]
  },
}
</script>

<style lang="scss">
.sidebar__logo {
  width: 44px;
  height: 44px;
  background-image: linear-gradient(332deg, #225bac 90%, #276ac7 6%);
}
// .sidebar--item:hover {
// 	background-color: #424955;
// 	opacity: 1;
// }
</style>
