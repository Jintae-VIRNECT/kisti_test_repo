<template lang="pug">
	nav.sidebar
		.sidebar-inner(@click.stop='')
			h1.sidebar--logo
				a.popover--button(href='/admin')
					img(src='~@/assets/image/admin/ic-remote-symbol.svg' alt='Remote')
			.sidebar--tools
				ul.sidebar--mode
					li.sidebar--item(v-for='menu of menus' :key='menu.menu' :class='{\
					current: menu.menu === mx_currentMenu,\
					active: menu.menu === openedMenu,\
					}')
						button.sidebar--item__button(@click='showSubMenu(menu.menu)')
							img(:src='menu.image' :alt='menu.label')
							span.sidebar--item__label {{ menu.label }}
						// <transition name="submenu">
						sub-menu(v-if='menu.menu === openedMenu' :menuname='menu.menu' :menulabel='menu.label' :menus='menu.children' v-on:close='showSubMenu(false)')
						// </transition>

</template>

<script>
// const img = require('@/assets/image/admin/ic-remote-symbol.svg')
export default {
	name: 'AdminSidebar',
	props: {
		menus: Array,
	},
	data() {
		return {
			openedMenu: false,
		}
	},
	watch: {
		mx_currentMenu: function() {
			this.openedMenu = false
		},
		mx_currentTab: function() {
			this.openedMenu = false
		},
	},
	methods: {
		showSubMenu(menu) {
			// document.dispatchEvent(new Event('click'))

			if (this.openedMenu === menu) {
				this.openedMenu = false
			} else {
				this.openedMenu = menu
			}
		},
		documentClickHandler(event) {
			if (event.target !== this.$el) {
				this.openedMenu = false
			}
		},
	},
	/* Lifecycles */
	mounted() {
		// console.log('img  : ', img)
		document.addEventListener('click', this.documentClickHandler)
	},
	beforeDestroy() {
		document.removeEventListener('click', this.documentClickHandler)
	},
}
</script>

<style>
/* Animation */
.submenu-enter-active {
	transition-property: opacity, transform;
	transition-duration: 0.2s;
	transition-timing-function: ease;
}
.submenu-enter {
	transform: translateX(-10%);
	opacity: 0;
}
.submenu-enter-to {
	transform: translateX(0);
	opacity: 1;
}

.submenu-leave-active {
	transition-delay: 0.2s;
}
.submenu-leave {
	opacity: 0;
}
.submenu-leave-to {
	opacity: 1;
}
</style>
