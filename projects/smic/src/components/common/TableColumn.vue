<template lang="pug">
  div(v-if="true")
    span {{ prop }}
    
  //- 진행 상태 (컨텐츠)
  div(v-else-if="prop.test(/(status)/) && type === 'contents'")
    span.publish-boolean(:class="data[prop]") {{ data[prop] | publishBoolean }}
  //- 업로더 (컨텐츠)
  .auth-wrapper(v-else-if="prop.test(/(uploaderName)/) && type === 'contents'")
    .auth-img(:style="{'background-image': `url(${ data['uploaderProfile'] })`}")
  //- 일시
  div(v-else-if="prop.test(/(createdDate)/)")
    span {{data[prop] | dayJs_FilterDateTime}}

  //- 이름
  div(v-else-if="prop.test(/(contentName|subProcessName)/)")
    span {{ data[prop] }}
  //- 개수
  div(v-else-if="prop.test(/(sceneGroupTotal|numOfDetailProcess)/)")
    span.nums {{ data[prop] }}
  //- 진행률
  .process-percent(v-else-if="prop.test(/(processPercent)/)")
    el-progress(:percentage="data[prop]" :show-text="true")
  //- 일정
  .total-done(v-else-if="prop.test(/(schedule)/)")
    span {{ data['startAt'] | dayJs_FilterDateTime }} 
    span &nbsp;~ {{ data['endAt'] | dayJs_FilterDateTime }}
  //- 진행 상태
  div(v-else-if="prop.test(/(status)/)")
    button.btn.btn--status(
      size="mini" 
      :class="data[prop]"
      plain
    ) {{ data[prop] | statusFilterName }}
  //- 멤버
  div(v-else-if="prop.test(/(uploaderName|auths)/)")
    span {{ data[prop] | limitAuthsLength }}
  //- 이슈
  div(v-else-if="prop.test(/(issue)/)")
    .blub(:class="data[prop] ? 'on' : 'off'")
    span {{ data[prop] ? "있음" : "없음" }}
  //- 기타
  div(v-else)
    span {{ data[prop] }}
</template>

<script>
import filters from '@/mixins/filters'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [filters, dayjs],
  props: {
    type: String,
    prop: String,
    data: String,
  },
}
</script>
