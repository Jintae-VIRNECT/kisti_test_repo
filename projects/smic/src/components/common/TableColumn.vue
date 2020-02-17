<template lang="pug">
  div(v-if="false")
    span Error
  
  //- 진행 상태 (컨텐츠)
  div(v-else-if="/^(status)$/.test(prop) && type === 'contents'")
    span.publish-boolean(:class="data[prop]") {{ data[prop] | publishBoolean }}
  //- 업로더
  .auth-wrapper(v-else-if="/^(auth|uploaderName)$/.test(prop)")
    .auth-img(:style="{'background-image': `url(${ data['uploaderProfile'] })`}")
    span {{ data[prop] }}
  //- 일시
  div(v-else-if="/^(reportedAt|createdDate)$/.test(prop)")
    span {{data[prop] | dayJs_FilterDateTime}}
  //- 컨텐츠이름
  .content-name(v-else-if="/^(contentName)$/.test(prop)")
    img.prefix-img(src="~@/assets/image/ic-content.svg")
    span {{ data[prop] }}
  //- 개수
  div(v-else-if="/^(sceneGroupTotal|numOfDetailProcess)$/.test(prop)")
    span.nums {{ data[prop] }}
  //- 진행률
  .process-percent(v-else-if="/^(processPercent)$/.test(prop)")
    el-progress(:percentage="data[prop]" :show-text="true")
  //- 일정
  .total-done(v-else-if="/^(schedule)$/.test(prop)")
    span {{ data['startAt'] | dayJs_FilterDateTime }} 
    span &nbsp;~ {{ data['endAt'] | dayJs_FilterDateTime }}
  //- 진행 상태
  div(v-else-if="/^(status)$/.test(prop)")
    button.btn.btn--status(
      size="mini" 
      :class="data[prop]"
      plain
    ) {{ data[prop] | statusFilterName }}
  //- 멤버들
  div(v-else-if="/^(auths)$/.test(prop)")
    span {{ data[prop] | limitAuthsLength }}
  //- 이슈
  div(v-else-if="/^(issue)$/.test(prop)")
    .blub(:class="data[prop] ? 'on' : 'off'")
    span {{ data[prop] ? "있음" : "없음" }}
  //- 이슈 타입
  div(v-else-if="/^(type)$/.test(prop)")
    span.issue-type {{ data[prop] }}
  //- 체결 수
  .total-done(v-else-if="/^(totalDone)$/.test(prop)")
    span.count {{ data['count'] }} 
    span &nbsp;/ {{ data['total'] }}
  //- 기타
  div(v-else)
    span {{ data[prop] }}
</template>

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
}
</script>
