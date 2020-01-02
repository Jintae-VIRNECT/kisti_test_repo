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
						:key='menu.menu' 
						:class='{\
							current: true,\
							active: true,\
						}')
						button.sidebar--item__button
							img(:src='menu.image' :alt='menu.label')
							//- img(:src='"~@/assets/image/admin/ic-settings.svg"'  :alt='menu.label')
							span.sidebar--item__label {{ menu.label }}

</template>

<script>
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
		// documentClickHandler(event) {
		// 	if (event.target !== this.$el) {
		// 		this.openedMenu = false
		// 	}
		// },
	},
	// /* Lifecycles */
	// mounted() {
	// 	document.addEventListener('click', this.documentClickHandler)
	// },
	// beforeDestroy() {
	// 	document.removeEventListener('click', this.documentClickHandler)
	// },
}
</script>
