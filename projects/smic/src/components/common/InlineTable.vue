<template lang="pug">
	el-card
		slot(name="header")
			span {{tableOption.title}}
			router-link(v-if="tableOption.moreHref" style="float: right; padding: 3px 0" type="text" :to="tableOption.moreHref") 더보기
			
		el-table(:data='tableData' style='width: 100%')
			el-table-column(v-for="{label, width, prop} in tableOption.colSetting" :prop="prop" :label="label" :width="width || ''") 
				template(slot-scope='scope')
					div(v-if="prop === 'status'")
						el-button(size="mini" :type="filterClass(tableData[scope.$index][prop])" plain) {{filterName(tableData[scope.$index][prop])}}
					div(v-else)
						span {{tableData[scope.$index][prop]}}
				//- template(v-else) 
				//- 	span {{tableData[scope.$index][prop]}}
			el-table-column(v-if="tableOption.colSlot")
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

</template>
<script>
export default {
	props: {
		tableData: Array,
		tableOption: {
			title: String,
			moreHref: String,
			colSetting: Array,
			colSlot: Array,
		},
	},
	data() {
		return {
			dataKeys: null,
		}
	},
	methods: {
		filterClass(value) {
			if (value == 'done') return 'success'
			else if (value == 'progress') return 'primary'
			else if (value == 'pending') return 'warning'
			else if (value == 'inadequate') return 'danger'
		},
		filterName(value) {
			if (value == 'done') return '완료'
			else if (value == 'progress') return '진행'
			else if (value == 'pending') return '지연'
			else if (value == 'inadequate') return '미흡'
		},
	},
}
</script>
