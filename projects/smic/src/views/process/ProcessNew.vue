<template lang="pug">
	.process-new
		page-tab-nav
			template(slot='page-nav--right')
				.search-wrapper
					el-input.tool.search(placeholder='공정 이름, 담당자 이름' v-model='search' )
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
		page-bread-crumb(title='공정')
		.card
			.card__header
				.card__header--left
					a.sub-title
						i.el-icon-back.before-icon
						| 공정으로 등록할 콘텐츠 선택
			.card__body
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
							.auth-wrapper(v-else-if="prop === 'auth'")
								.auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
								span {{tableData[scope.$index][prop]}}
							div(v-else-if="prop === 'contentPublish'")
								span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
							div(v-else-if="prop === 'uploadedAt'")
								span {{tableData[scope.$index][prop] | filterDateTime}}
							div(v-else)
								span {{ tableData[scope.$index][prop]}}
			//- el-pagination.inline-table-pagination(
			//- 	v-if='setPagination'
			//- 	:hide-on-single-page='false' 
			//- 	:page-size="pageSize" 
			//- 	:pager-count="tableOption ? tableOption.pagerCount : 5"
			//- 	:total='tableData.length' 
			//- 	layout='prev, jumper, next'
			//- 	:current-page='currentPage'
			//- 	@prev-click='currentPage -= 1'
			//- 	@next-click='currentPage += 1'
			//- )
</template>
<style lang="scss">
.inline-table__header--right {
  text-align: right;
  > * {
    vertical-align: middle;
  }
  .divider {
    width: 1px;
    height: 20px;
    opacity: 0.82;
    background-color: #cdd1d6;
    display: inline-block;
    margin: 0px 20px;
  }
  .prefix {
    font-size: 14px;
    font-weight: 500;
    line-height: 2;
    color: #0d2a58;
  }
  .value {
    font-size: 18px;
    font-weight: 500;
    line-height: 1.56;
    color: #0065e0;
  }
  .suffix {
    font-size: 12px;
    font-weight: 500;
    line-height: normal;
    color: #566173;
  }
}
.process-new {
  .card__header {
    padding: 10px 16px;
  }
  .sub-title {
    font-size: 14px;
    line-height: 2;
    color: #0d2a58;
    .before-icon {
      margin-right: 12px;
    }
  }
}
</style>

<script>
// UI component
import PageTabNav from '@/components/common/PageTabNav.vue'
import ProgressCard from '@/components/home/ProgressCard.vue'
import InlineTable from '@/components/common/InlineTable.vue'
import PageBreadCrumb from '@/components/common/PageBreadCrumb.vue'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'

// model
import { tableColSettings } from '@/models/home'

// mixin
import contentList from '@/mixins/contentList'
import dayjs from '@/utils/dayjs'

export default {
  mixins: [contentList, dayjs],
  components: { ProgressCard, InlineTable, PageTabNav, PageBreadCrumb },
  data() {
    return {
      tableData: currentUploadedContent,
      tableOption: {
        rowIdName: 'contentId',
        subdomain: '???',
      },
      search: null,
      colSetting: tableColSettings.contents,
    }
  },
  methods: {
    onClickCell() {},
  },
}
</script>
