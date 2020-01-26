<template lang="pug">
	div
		h1.admin-body__header 이슈관리
		.page-nav
			.search-wrapper.text-right
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
		inline-table(:setMainHeader="true")
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
import PageTabNav from '@/components/common/PageTabNav.vue'
import PageHeader from '@/components/common/PageHeader.vue'

/// data
import issueData from '@/data/issue'

// model
import tableColSettings from '@/models/issue'

// mixin
import contentList from '@/mixins/contentList'

export default {
  components: {
    InlineTable,
    ContentControlDropdown,
    PageTabNav,
    PageHeader,
  },
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
