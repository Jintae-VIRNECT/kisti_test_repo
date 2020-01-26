<template lang="pug">
	.card
		.card__header
			.card__header--left
				slot(name="header-left")
			.card__header--right
				slot(name="header-right")
		.card__body
			slot(name="tabs")
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
						div(v-if="prop == 'index'") 
							span {{scope.$index + 1}}
						div(v-if="prop === 'contentPublish'")
							span {{publishBoolean(tableData[scope.$index][prop])}}
						.process-percent(v-else-if="prop === 'processPercent'")
							el-progress(:percentage="tableData[scope.$index][prop]" :show-text="true")
						div(v-else-if="prop === 'numOfDetailProcess'")
							span.nums {{tableData[scope.$index][prop]}}
						//- 이슈 타입
						.content-name(v-else-if="dataType === 'contents' && prop === 'name'")
							img.prefix-img(src="~@/assets/image/ic-content.svg")
							span {{tableData[scope.$index][prop]}}
							
						div(v-else-if="prop === 'issue'")
							.blub(:class="tableData[scope.$index][prop] ? 'on' : 'off'")
							span {{tableData[scope.$index][prop] ? "있음" : "없음"}}
						div(v-else-if="prop === 'type'")
							span.issue-type {{tableData[scope.$index][prop]}}
						//- auths String.substring(0,12) + '...'
						div(v-else-if="prop === 'auths'")
							span {{tableData[scope.$index][prop] | limitAuthsLength}}
						//- schedule = (startAt ~ endAt)
						.total-done(v-else-if="prop === 'schedule'")
							span {{tableData[scope.$index]['startAt']}} 
							span &nbsp;~ {{tableData[scope.$index]['endAt']}}
						//- 체결 수
						.total-done(v-else-if="prop === 'totalDone'")
							span.count {{tableData[scope.$index]['count']}} 
							span &nbsp;/ {{tableData[scope.$index]['total']}}
						//- 보고일시
						div(v-else-if="prop === 'reportedAt'")
							span.count {{tableData[scope.$index][prop] | filterDateTime}} 
						div(v-else-if="prop === 'status'")
							button.btn.btn--status(
								size="mini" 
								:class="tableData[scope.$index][prop]" 
								plain
							) {{ statusFilterName(tableData[scope.$index][prop]) }}
						.auth-wrapper(v-else-if="prop === 'auth'")
							.auth-img(:style="{'background-image': `url(${tableData[scope.$index]['profileImg']})`}")
							span {{tableData[scope.$index][prop]}}
						div(v-else)
							span {{ tableData[scope.$index][prop] }}
					//- template(v-else) 
					//- 	span {{ tableData[scope.$index][prop] }}
				el-table-column(v-if="controlCol" :width="50" class-name="tool-col")
					template(slot-scope='scope')
						process-control-dropdown(:processId="tableData[scope.$index].processId")
		el-pagination.inline-table-pagination(
			v-if='setPagination'
			:hide-on-single-page='false' 
			:page-size="pageSize" 
			:pager-count="tableOption ? tableOption.pagerCount : 5"
			:total='tableData.length' 
			layout='prev, jumper, next'
			:current-page='currentPage'
			@prev-click='currentPage -= 1'
			@next-click='currentPage += 1'
		)
</template>
<script>
import ProcessControlDropdown from '@/components/process/ProcessControlDropdown'

// utils
import dayjs from '@/utils/dayjs'

export default {
  mixins: [dayjs],
  components: {
    ProcessControlDropdown,
  },
  props: {
    tableData: Array,
    setPagination: Boolean,
    dataType: String,
    colSetting: Array,
    controlCol: Boolean,
    cellClick: Function,
    tableOption: {
      title: String,
      // colSetting: Array,
      pagerCount: 5,
    },
  },
  data() {
    return {
      dataKeys: null,
      currentPage: 1,
      pageSize: this.$props.tableOption ? this.$props.tableOption.pageSize : 5,
    }
  },
  methods: {
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
    publishBoolean(value) {
      if (value === true) return 'ON'
      else return 'OFF'
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
  filters: {
    limitAuthsLength(array) {
      let answer = ''
      let sumOfStrings = 0
      const divider = ', '
      for (let i = 0; i < array.length; i++) {
        answer += array[i]
        sumOfStrings += array[i].length + divider.length
        if (sumOfStrings > 13) return answer + '...'
        if (array.length - 1 === i) break
        answer += divider
      }
      return answer
    },
  },
}
</script>
