<template lang="pug">
	el-card
		.card
			slot(name="header")
				.card__header
					span {{tableOption.title}}
					router-link.more(v-if="tableOption.moreHref" style="float: right; padding: 3px 0" type="text" :to="tableOption.moreHref") 더보기
		.card__body
			el-table.inline-table(
				:data='tableData' 
				style='width: 100%'
				:show-header="tableOption.showHeader || true"
				@cell-click="onClickCell"
			)
				el-table-column(
					v-for="{label, width, prop} in tableOption.colSetting" 
					:key="prop" 
					:prop="prop" 
					:label="label" 
					:width="width || ''"
				) 
					template(slot-scope='scope')
						div(v-if="prop == 'index'") 
							span {{scope.$index}}
						div(v-if="['contentPublish', 'processRegister'].includes(prop)") 
							button.el-button--mini(
								size="mini" 
								:class="publishFilterClass(prop, tableData[scope.$index][prop])" 
								:plain='tableData[scope.$index][prop] ? false : true'
							) {{ publishFilterName(prop, tableData[scope.$index][prop]) }}
						div(v-else-if="prop === 'status'")
							button.btn.btn--status(
								size="mini" 
								:class="tableData[scope.$index][prop]" 
								plain
							) {{ statusFilterName(tableData[scope.$index][prop]) }}
						div(v-else)
							span {{ tableData[scope.$index][prop] }}
					//- template(v-else) 
					//- 	span {{ tableData[scope.$index][prop] }}
				el-table-column(v-if="tableOption.colOptions")
					template(slot='header') 설정
					template(slot-scope='scope')
						el-dropdown(trigger="click")
							span.el-dropdown-link
								i.el-icon-arrow-down.el-icon--right
							el-dropdown-menu(slot='dropdown')
								el-dropdown-item(icon='el-icon-plus') Action 1
								el-dropdown-item(icon='el-icon-circle-plus') Action 2
								el-dropdown-item(icon='el-icon-circle-plus-outline') Action 3
								el-dropdown-item(icon='el-icon-check') Action 4
								el-dropdown-item(icon='el-icon-circle-check') Action 5
			el-pagination.inline-table-pagination(
				:hide-on-single-page='false' 
				:page-size="pageSize" 
				:pager-count="tableOption.pagerCount"
				:total='tableData.length' 
				layout='prev, jumper, next'
				:current-page='currentPage'
				@prev-click='currentPage -= 1'
				@next-click='currentPage += 1'
			)
</template>
<script>
export default {
	props: {
		tableData: Array,
		tableOption: {
			title: String,
			moreHref: String,
			colSetting: Array,
			colOptions: Boolean,
			showHeader: Boolean,
			pagerCount: 5,
		},
	},
	data() {
		return {
			dataKeys: null,
			currentPage: 1,
			pageSize: this.$props.tableOption.pageSize || 5,
		}
	},
	methods: {
		// statusFilterClass(value) {
		// 	if (value == 'complete') return 'success'
		// 	else if (value == 'progress') return 'primary'
		// 	else if (value == 'idle') return 'warning'
		// 	else if (value == 'imcomplete') return 'danger'
		// },
		statusFilterName(value) {
			if (value == 'complete') return '완료'
			else if (value == 'progress') return '진행'
			else if (value == 'idle') return '미진행'
			else if (value == 'imcomplete') return '미흡'
		},
		publishFilterClass(type, value) {
			let suffix = ''
			if (value === false) suffix = 'plain'

			return `${type}-btn ${suffix}`
		},
		publishFilterName(type, value) {
			let className, suffix
			if (type === 'contentPublish') className = '배포'
			else if (type === 'processRegister') className = '등록'

			if (value === true) suffix = '중'
			else if (value === false) suffix = '대기'

			return className + suffix
		},
		onClickCell(row) {
			const { rowIdName, subdomain } = this.$props.tableOption
			if (!rowIdName) return false
			this.$router.push(`${subdomain}/${row[rowIdName]}`)
		},
	},
}
</script>

<style lang="scss">
.inline-table-pagination .el-pagination__jump {
	margin-left: 0px !important;
}
.inline-table {
	.el-table__row {
		cursor: pointer;
	}
	.contentPublish-btn {
		background-color: #3f88c6 !important;
		color: white;
		border-radius: 15px;
		width: 80px;
		&.plain {
			background-color: white !important;
			color: #3f88c6 !important;
			border: solid 1px #3f88c6 !important;
		}
	}
	.processRegister-btn {
		background-color: #ff216f !important;
		color: white;
		border-radius: 15px;
		width: 80px;
		&.plain {
			background-color: white !important;
			color: #ff216f !important;
			border: solid 1px #ff216f !important;
		}
	}
}
</style>
