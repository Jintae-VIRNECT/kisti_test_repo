<template>
  <tab-view
    title="최근 통화 목록"
    description="최근 통화 목록은 30일 동안 보관됩니다."
    placeholder="협업, 멤버 검색"
    customClass="history"
    :emptyImage="require('assets/image/img_recent_empty.svg')"
    emptyTitle="최근 통화 목록이 없습니다."
    emptyDescription="원격 협업을 시작해보세요."
    :empty="historyList.length === 0"
    :showDeleteButton="true"
    :showRefreshButton="true"
    :deleteButtonText="'전체삭제'"
    :listCount="historyList.length"
    @refresh="refresh"
    @delete="showDeleteDialog"
  >
    <workspace-history-list :historyList="historyList"></workspace-history-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceHistoryList from '../section/WorkspaceHistoryList'
import { getHistoryList, deleteAllHistory } from 'api/workspace/history'

import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceHistory',
  mixins: [confirmMixin],
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
      this.getHistory()
    },
    showDeleteDialog() {
      this.confirmCancel(
        '모든 목록을 삭제하시겠습니까?',
        {
          text: '삭제하기',
          action: () => {
            this.deleteList()
          },
        },
        { text: '취소' },
      )
    },
    async deleteList() {
      try {
        await deleteAllHistory()
        this.historyList = []
      } catch (err) {
        console.log(err)
      }
    },
    async getHistory() {
      try {
        const param = {
          page: 0,
          paging: false,
          size: 100,
        }

        const datas = await getHistoryList(param)
        this.historyList = datas.rooms
      } catch (err) {
        // 에러처리
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
  async created() {
    this.getHistory()
  },

  beforeDestroy() {
    this.$eventBus.$off('delete')
    this.$eventBus.$off('refresh')
  },
}
</script>
