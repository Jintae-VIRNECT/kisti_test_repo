<template>
  <tab-view
    title="원격협업"
    placeholder="통화 기록 검색"
    emptyTitle="원격 협업 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :empty="true"
  ></tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
export default {
  name: 'WorkspaceRemote',
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
