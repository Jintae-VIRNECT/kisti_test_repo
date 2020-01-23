<template lang="pug">
	div
		.card
			.card__header
				.card__header--left
					span.title 공정 콘텐츠 목록
				.card__header--right
					.inline-table__header--right
						span.prefix 업로드된 컨텐츠 
						span.value 102
						span.suffix &nbsp;projects
						.divider
						span.prefix 배포중인 컨텐츠 수 컨텐츠 
						span.value 22
						span.suffix &nbsp;projects
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
							div(v-else-if="prop === 'contentPublish'")
								span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
							.auth-wrapper(v-else-if="prop === 'auth'")
								.auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
								span {{tableData[scope.$index][prop]}}
							div(v-else)
								span {{ tableData[scope.$index][prop]}}
					el-table-column(:width="50" class-name="control-col")
						template(slot-scope='scope')
							content-control-dropdown(:contentPublish="tableData[scope.$index].contentPublish")
				
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
</style>

<script>
// UI component
import InlineTable from '@/components/common/InlineTable.vue'
import ContentControlDropdown from '@/components/contents/ContentControlDropdown'

/// data
import currentUploadedContent from '@/data/currentUploadedContent'

// model
import { tableColSettings } from '@/models/home'

// mixin
import contentList from '@/mixins/contentList'

export default {
  components: { InlineTable, ContentControlDropdown },
  mixins: [contentList],
  data() {
    return {
      tableData: currentUploadedContent,
      tableOption: {
        rowIdName: 'contentId',
        subdomain: '/contents',
      },
      search: null,
      colSetting: tableColSettings.contents,
    }
  },
  methods: {
    onClickCell(row, column) {
      if (column.className === 'control-col') return false
      const { rowIdName, subdomain } = this.tableOption
      if (!rowIdName) return false
      this.$router.push(`${subdomain}/${row[rowIdName]}`)
    },
  },
}
</script>
