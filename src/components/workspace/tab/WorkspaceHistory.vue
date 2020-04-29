<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="협업, 멤버 검색"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="historyList.length"
    @refresh="refresh"
    @delete="deleteList"
  >
    <workspace-history-list :historyList="historyList"></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
import { getHistoryList, deleteAllHistory } from 'api/workspace/history'
export default {
  name: 'WorkspaceHistory',
  components: {
    TabView,
    WorkspaceHistoryList,
  },
  data() {
    return {
      historyList: [],
    }
  },
  watch: {},
  methods: {
    async refresh() {
      try {
        const datas = await getHistoryList()
        this.historyList = datas.data.romms
      } catch (err) {
        console.log(err)
      }
    },
    async deleteList() {
      try {
        await deleteAllHistory()
        this.historyList = []
      } catch (err) {
        console.log(err)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
  async created() {
    try {
      const datas = await getHistoryList()
      this.historyList = datas.data.romms
    } catch (err) {
      // 에러처리
      console.error(err)
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('delete')
    this.$eventBus.$off('refresh')
  },
}
</script>
