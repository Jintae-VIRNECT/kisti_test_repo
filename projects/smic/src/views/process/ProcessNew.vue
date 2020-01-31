<template lang="pug">
	.process-new
		page-tab-nav
			template(slot='page-nav--right')
				.search-wrapper.text-right
					el-input.tool.search(placeholder='콘텐츠 이름, 등록 멤버 이름' v-model='searchInput' @change="onChangeSearch(searchInput, filter.value, sort.value)")
						el-button(slot='append' icon='el-icon-search')
					span 필터 : 
					el-select(v-model='filter.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
						el-option(v-for='item in filter.options' :key='item.value' :label='item.label' :value='item.value')
					span 정렬 : 
					el-select(v-model='sort.value' placeholder='Select' @change="onChangeSearch(searchInput, filter.value, sort.value)")
						el-option(v-for='item in sort.options' :key='item.value' :label='item.label' :value='item.value')
            
		inline-table(:setSubHeader="true")
			template(slot="header--secondary")
				router-link.title(to="/contents")
					i.el-icon-back
					| 공정으로 등록할 콘텐츠 선택
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
							.content-name(v-if="prop === 'name'")
								img.prefix-img(src="~@/assets/image/ic-content.svg")
								span {{tableData[scope.$index][prop]}}
							div(v-else-if="prop === 'contentPublish'")
								span.publish-boolean(:class="tableData[scope.$index][prop]") {{tableData[scope.$index][prop] | publishBoolean}}
							.auth-wrapper(v-else-if="prop === 'auth'")
								.auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
								span {{tableData[scope.$index][prop]}}
							div(v-else-if="prop === 'uploadedAt'")
								span {{tableData[scope.$index][prop] | dayJs_FilterDateTime}}
							div(v-else)
								span {{ tableData[scope.$index][prop]}}
		process-new-modal(
			:target='target'
			:toggleProcessModal="toggleProcessModal"
			@handleCancel="handleCancel")
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
import ProcessNewModal from '@/components/process/ProcessNewModal.vue'

// model
import { tableColSettings } from '@/models/home'
import { processStatus } from '@/models/process'
import { sortOptions } from '@/models/index'

// mixin
import contentList from '@/mixins/contentList'
import dayjs from '@/utils/dayjs'

export default {
  mixins: [contentList, dayjs],
  components: {
    ProgressCard,
    InlineTable,
    PageTabNav,
    PageBreadCrumb,
    ProcessNewModal,
  },
  data() {
    return {
      tableData: this.$store.getters.currentUploadedContent,
      search: null,
      colSetting: tableColSettings.contents,
      searchInput: null,
      filter: {
        options: [
          {
            value: null,
            label: '전체',
          },
          ...processStatus,
        ],
        value: null,
      },
      sort: {
        options: sortOptions,
        value: null,
      },
      toggleProcessModal: false,
      target: null,
    }
  },
  methods: {
    async onChangeSearch(searchInput, filterValue, sortValue) {
      let tmpTableData = this.$store.getters.currentUploadedContent
      tmpTableData = await this.onChangeSearchText(tmpTableData, searchInput)
      tmpTableData = await this.onChangeFilter(tmpTableData, filterValue)
      tmpTableData = await this.onChangeSort(tmpTableData, sortValue)
      this.tableData = tmpTableData
    },
    onChangeSearchText(tableData, searchInput) {
      return tableData.filter(row => {
        return (
          row.processName.includes(searchInput) ||
          row.auths.some(a => a.includes(searchInput))
        )
      })
    },
    onChangeFilter(tableData, filterValue) {
      if (!filterValue) return tableData
      return tableData.filter(row => row.status === filterValue)
    },
    onChangeSort(tableData, sortValue) {
      if (!sortValue) return tableData
      if (sortValue === 'alphabetDesc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? 1 : -1,
        )
      else if (sortValue === 'alphabetAsc')
        return tableData.sort((a, b) =>
          a.processName - b.processName ? -1 : 1,
        )
      else if (sortValue === 'createdAtDesc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? 1 : -1))
      else if (sortValue === 'createdAtAsc')
        return tableData.sort((a, b) => (a.createdAt - b.createdAt ? -1 : 1))
    },
    onClickCell(row) {
      this.toggleProcessModal = true
      this.target = row
    },
    handleCancel() {
      this.toggleProcessModal = false
    },
  },
}
</script>
