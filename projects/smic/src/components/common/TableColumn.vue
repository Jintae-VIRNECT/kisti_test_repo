<template lang="pug">
  div(v-if="false")
    span Error
  
  //- 홈 화면
  //- 홈 화면 스마트 툴
  .total-done(v-else-if="/^(smartToolItems)$/.test(prop)")
    span.count {{ data['smartToolWorkedCount'] }} 
    span &nbsp;/ {{ data['smartToolBatchTotal'] }}

  //- 진행 상태 (컨텐츠)
  div(v-else-if="/^(status)$/.test(prop) && type === 'contents'")
    span.publish-boolean(:class="data[prop]") {{ data[prop] | publishBoolean }}
  //- 업로더
  .auth-wrapper(v-else-if="/^(auth|uploaderName)$/.test(prop)")
    .auth-img(:style="{'background-image': `url(${ data['uploaderProfile'] })`}")
    span {{ data[prop] }}
  //- 업로더 (uuid)
  .auth-wrapper(v-else-if="/^(workerUUID)$/.test(prop)")
    .auth-img(:style="{'background-image': `url(${ member.profile })`}")
    span {{ member.name }}
  //- 일시
  .total-done(v-else-if="/^(reportedAt|.*Date)$/.test(prop)")
    span {{data[prop] | dayJs_FilterDateTimeFormat}}
  //- 컨텐츠이름
  .content-name(v-else-if="/^(contentName)$/.test(prop)")
    img.prefix-img(src="~@/assets/image/ic-content.svg")
    span {{ data[prop] }}
  //- 개수
  div(v-else-if="/^.+(Total)$/.test(prop)")
    span.nums {{ data[prop] }}
  //- 진행률
  .process-percent(v-else-if="/^(.+Percent|.+Rate)$/.test(prop)")
    //- dummy data !!
    el-progress(:percentage="randomProgress()" :show-text="true")
    //- el-progress(:percentage="data[prop]" :show-text="true")
  //- 일정
  .total-done(v-else-if="/^(schedule)$/.test(prop)")
    span {{ data['startDate'] | dayJs_FilterDateTimeFormat }} 
    span &nbsp;~ {{ data['endDate'] | dayJs_FilterDateTimeFormat }}
  //- 진행 상태
  div(v-else-if="/^(status)$/.test(prop)")
    //- dummy data !!
    button.btn.btn--status(
      size="mini" 
      :class="randomStatus()"
      plain
    ) {{ randomStatus() | statusFilterName }}
    //- button.btn.btn--status(
    //-   size="mini" 
    //-   :class="data[prop]"
    //-   plain
    //- ) {{ data[prop] | statusFilterName }}
  //- 멤버들
  div(v-else-if="/^(auths)$/.test(prop)")
    //- dummy data !!
    span {{ ['작업자1', '작업자2', '작업자3', '작업자4'] | limitAuthsLength }}
    //- span {{ data[prop] | limitAuthsLength }}
  //- 이슈
  div(v-else-if="/^(issue)$/.test(prop) && typeof data[prop] !== 'object'")
    .blub(:class="data[prop] ? 'on' : 'off'")
    span {{ data[prop] ? "있음" : "없음" }}
  //- 이슈 타입
  div(v-else-if="/^(issueType)$/.test(prop)")
    span.issue-type {{ data['processId'] ? '작업 이슈' : '이슈' }}
  //- 체결 수
  .total-done(v-else-if="/^(totalDone)$/.test(prop)")
    span.count {{ data['count'] }} 
    span &nbsp;/ {{ data['total'] }}
  
  //- 작업
  //- 리포트 버튼
  div(v-else-if="/^(report)$/.test(prop)")
    el-button(v-if="data[prop]" v-on:click="buttonClick") 리포트보기
    span(v-else) ―
  //- 작업 이슈 버튼
  div(v-else-if="/^(issue)$/.test(prop) && typeof data[prop] !== 'string'")
    el-button(v-if="data[prop]" v-on:click="buttonClick") 작업 이슈 보기
    span(v-else) ―
  //- 스마트툴
  .total-done(v-else-if="/^(smartTool)$/.test(prop)")
    div(v-if="data[prop]")
      span Job ID no. {{ data.smartTool['smartToolJobId'] }}
      el-divider(direction="vertical")
      span.count {{ data.smartTool['smartToolWorkedCount'] }} 
      span &nbsp;/ {{ data.smartTool['smartToolBatchTotal'] }} &nbsp;
      el-button(v-on:click="buttonClick") 스마트툴 보기
    span(v-else) ―
    
  //- 디버깅용
  div(v-else-if="/^(name)$/.test(prop) && (data['id'] || data['subProcessId'])")
    span(v-if="data[prop]") {{ data[prop] }}
    span(v-else) ―
    span.debug (id: {{ data['id'] || data['subProcessId'] }})

  //- 기타
  div(v-else)
    span(v-if="data[prop]") {{ data[prop] }}
    span(v-else) ―
</template>

<style lang="scss">
span.debug {
  display: inline-block;
  margin-left: 8px;
  font-size: 0.5em;
  opacity: 0.5;
}
</style>

<script>
import filters from '@/mixins/filters'
import contentList from '@/mixins/contentList'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [filters, contentList, dayjs],
  props: {
    type: String,
    prop: String,
    data: Object,
  },
  methods: {
    buttonClick() {
      this.$emit('buttonClick', { prop: this.prop, data: this.data })
    },
    // dummy data !!
    random(min, max) {
      min = Math.ceil(min)
      max = Math.floor(max)
      return Math.floor(Math.random() * (max - min + 1)) + min
    },
    randomStatus() {
      return ['complete', 'progress', 'idle', 'imcomplete'][this.random(0, 2)]
    },
    randomProgress() {
      return this.random(0, 100)
    },
  },
  computed: {
    member() {
      const uuid = this.data[this.prop]
      const memberList = this.$store.getters.memberList
      return memberList.find(member => member.uuid === uuid) || {}
    },
  },
}
</script>
