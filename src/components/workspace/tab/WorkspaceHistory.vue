<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="통화 기록 검색"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="listCount"
  >
    <workspace-history-list :historyList="historyList"></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
import { getHistoryList } from 'api/workspace/history'
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
  computed: {
    listCount() {
      return this.historyList.length
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
  async created() {
    try {
      const datas = await getHistoryList()
      this.historyList = datas.data.romms
      console.log(datas)

      //전체 방수
      console.log(datas.data.totalCount)

      //검색결과로 인한 방수? 아닐까..?
      console.log(datas.data.currentCount)
    } catch (err) {
      // 에러처리
      console.error(err)
    }
  },
}
</script>
