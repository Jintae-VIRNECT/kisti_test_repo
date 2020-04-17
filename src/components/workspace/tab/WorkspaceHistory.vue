<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="통화 기록 검색"
  >
    <workspace-history-list></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
export default {
  name: 'WorkspaceHistory',
  components: {
    TabView,
    WorkspaceHistoryList,
  },
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
