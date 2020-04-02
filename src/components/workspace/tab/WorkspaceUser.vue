<template>
  <tab-view
    title="원격 협업 가능한 멤버"
    description="협업 가능한 회원을 선택하고 문자 보내기 또는 1:1통화를 해보세요."
    placeholder="통화 기록 검색"
  ></tab-view>
</template>

<script>
import TabView from '../partials/workspaceTabView'
export default {
  name: 'WorkspaceUser',
  components: { TabView },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
