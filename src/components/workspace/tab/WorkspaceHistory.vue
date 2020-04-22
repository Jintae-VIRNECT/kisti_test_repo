<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="통화 기록 검색"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="historyListLength"
  >
    <workspace-history-list></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
import { getHistoryList, deleteAllHistory } from 'api/workspace/history'
import { mapGetters } from 'vuex'
export default {
  name: 'WorkspaceHistory',
  components: {
    TabView,
    WorkspaceHistoryList,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['historyListLength']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {
    //이 패턴은 옳은 패턴인가?...
    const _this = this

    this.$eventBus.$on('historyList:delete', async function(payload) {
      try {
        const result = await deleteAllHistory()
        _this.$store.dispatch('deleteAllHistoryList', '')
      } catch (err) {
        console.log(err)
      }
    })
    this.$eventBus.$on('historyList:refresh', async function(payload) {
      try {
        const datas = await getHistoryList()
        _this.$store.dispatch('setHistoryList', datas.data.romms)
      } catch (err) {
        console.log(err)
      }
    })
  },
  async created() {
    try {
      const datas = await getHistoryList()
      this.$store.dispatch('setHistoryList', datas.data.romms)
    } catch (err) {
      // 에러처리
      console.error(err)
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('historyList:delete')
    this.$eventBus.$off('historyList:refresh')
  },
}
</script>
