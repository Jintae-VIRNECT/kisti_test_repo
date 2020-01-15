import { mapActions } from 'vuex'

export default {
    data() {
			return {
			mx_label: null,
			mx_component: null
		}
	},
	watch: {
		'$route.query': 'mx_setComponent'
	},
	computed: {
		mx_menu() {
				return this.$route.params.menu
		},
		mx_tab() {
				return this.$route.params.tab
		},
		mx_type() {
            return this.$route.query.type
		},
		groups() {
			return  {
				group: [{
					type: 'list',
					label: this.$t('group.menu_management_group'),
					depth: 2,
					component: ()=>import('components/admin/groups/partials/AdminGroupList')
				},{
					type: 'user',
					label: null,
					depth: 3,
					component: ()=>import('components/admin/groups/partials/AdminGroupUser')
				},{
					type: 'add-user',
					label: this.$t('group.menu_add_member'),
					depth: 4,
					component: ()=>import('components/admin/groups/partials/AdminGroupAddMember')
				},{
					type: 'add',
					label: this.$t('group.menu_add_group'),
					depth: 4,
					component: ()=>import('components/admin/groups/partials/AdminGroupEdit')
				},{
					type: 'edit',
					label: this.$t('group.menu_edit_group'),
					depth: 3,
					component: ()=>import('components/admin/groups/partials/AdminGroupEdit')
				},{
					type: 'enable',
					label: this.$t('group.menu_enable_group'),
					depth: 4,
					component: ()=>import('components/admin/groups/partials/AdminGroupEnable')
				},{
					type: 'remove',
					label: this.$t('group.menu_remove_group'),
					depth: 4,
					component: ()=>import('components/admin/groups/partials/AdminGroupRemove')
				}],
				user: [{
					type: 'list',
					label: this.$t('group.menu_management_user'),
					depth: 2,
					component: ()=>import('components/admin/groups/partials/AdminUserList')
				},{
					type: 'edit',
					label: this.$t('group.menu_edit_user'),
					depth: 3,
					component: ()=>import('components/admin/groups/partials/AdminUserEdit')
				},{
				// 	type: 'add-user',
				// 	label: this.$t('group.menu_add_user'),
				// 	depth: 3,
				// 	component: ()=>import('components/admin/groups/partials/AdminAddUser')
				// },{
					type: 'invite-user',
					label: this.$t('group.menu_invite_user'),
					depth: 3,
					component: ()=>import('components/admin/groups/partials/AdminInviteUser')
				},{
					type: 'full-user',
					label: this.$t('group.menu_management_user'),
					depth: 2,
					component: ()=>import('components/admin/groups/partials/AdminFullLicense')
				},{
					type: 'remove',
					label: this.$t('group.menu_remove_user'),
					depth: 4,
					component: ()=>import('components/admin/groups/partials/AdminUserRemove')
				}]
			}
		},
		payment() {
			return {
				history: [{
					type: 'list',
					label: this.$t('payment.menu_list'),
					depth: 2,
					component: ()=>import('components/admin/payment/partials/PaymentList')
				},{
					type: 'contact',
					label: this.$t('payment.menu_payment_contact'),
					depth: 3,
					component: ()=>import('components/admin/payment/partials/ReContact')
				}]
			}
		},
		setting() {
			return {
				account: [{
					type: 'setting',
					label: this.$t('setting.menu_account'),
					depth: 2,
					component: ()=>import('components/admin/setting/partials/AdminSettingAccount')
				},{
					type: 'leave',
					label: this.$t('setting.menu_leave'),
					depth: 3,
					component: ()=>import('components/admin/setting/partials/AdminLeaveOut')
				}]
			}
		}
    },
    methods: {
		...mapActions(['setBreadcrumb']),
		mx_setLabel(label) {
			this.mx_label = label
		},
		mx_setComponent() {
			if(!this.mx_menu) {
				this.$router.push('/admin/groups/group')
				return
			}
			let page = this[this.mx_menu][this.mx_tab]
			let comp = this.mx_type
			
			let idx = page.findIndex(data => {
				return data.type === this.mx_type
			})
			if(idx < 0) {
				idx = 0
			}

			let array = []
			for(let i=0;i<page[idx].depth;i++) {
				array.push(null)
			}
			let labelValue = page[idx].label?page[idx].label:this.mx_label
			
			array[page[idx].depth - 1] = {
				click: () => {
					this.$eventBus.$emit('setComp', comp)
				},
				label: labelValue
			}
			if(!page[idx].label) {
				array[page[idx].depth - 1].pathLabel = this.mx_label.length > 5 ? this.mx_label.substr(0, 5) + '...' : this.mx_label
			}
			
			// this.$router.push({query: {type: comp}})
			this.setBreadcrumb(array)
			this.mx_component = page[idx].component
		}
	},
	mounted() {
		// this.mx_setComponent()
	}
}