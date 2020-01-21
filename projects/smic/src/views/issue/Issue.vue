<template lang="pug">
	div
		inline-table(:setHeader="true")
			template(slot="header-left")
				span.title 최근 등록된 공정 콘텐츠
			template(slot="header-right")
				.text-right
					router-link.more-link(type="text" to="/contents") 더보기
			template(slot="body")
				el-table.inline-table(
					:data='tableData' 
					style='width: 100%'
					@cell-click="onClickCell")
					el-table-column(
						v-for="{label, width, prop} in colSetting" 
						:key="prop" 
						:prop="prop" 
						:label="label" 
						:width="width || ''") 
						template(slot-scope='scope')
							//- 이슈 타입
							.content-name(v-if="prop === 'name'")
								img.prefix-img(src="~@/assets/image/ic-content.svg")
								span {{tableData[scope.$index][prop]}}
							div(v-else-if="prop === 'contentPublish'")
								span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
							.auth-wrapper(v-else-if="prop === 'auth'")
								.auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
								span {{tableData[scope.$index][prop]}}
							div(v-else)
								span {{ tableData[scope.$index][prop]}}
</template>
<script>
import InlineTable from '@/components/common/InlineTable'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'

/// data
import issueData from '@/data/issue'

// model
import tableColSettings from '@/models/issue'

// mixin
import contentList from '@/mixins/contentList'

export default {
	components: { InlineTable, ContentControlDropdown },
	mixins: [contentList],
	data() {
		return {
			tableData: issueData,
			search: null,
			colSetting: tableColSettings,
		}
	},
	methods: {
		onClickCell() {},
	},
}
</script>
