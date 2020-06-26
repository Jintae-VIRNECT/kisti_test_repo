<template>
  <tab-view
    title="협업 가능 멤버"
    description="협업 가능한 회원을 선택하고 메세지를 보내보세요."
    placeholder="멤버 검색"
    :emptyImage="require('assets/image/img_user_empty.svg')"
    emptyTitle="협업 가능 멤버가 없습니다."
    emptyDescription="협업 멤버를 추가해주세요."
    :empty="memberList.length === 0"
    :listCount="memberList.length"
    :showDeleteButton="false"
    :showRefreshButton="true"
    :loading="loading"
    @refresh="getList"
    ><workspace-user-list :memberList="memberList"></workspace-user-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceUserList from '../section/WorkspaceUserList'
import { getMemberList } from 'api/workspace/member'

export default {
  name: 'WorkspaceUser',
  components: { TabView, WorkspaceUserList },
  data() {
    return {
      memberList: [],
      loading: false,
    }
  },
  computed: {},
  watch: {},
  methods: {
    async getList() {
      try {
        const params = {
          workspaceId: this.workspace.uuid,
        }
        this.loading = true
        const datas = await getMemberList(params)
        this.loading = false
        this.memberList = datas.memberInfoList
      } catch (err) {
        console.log(err)
      }
    },
  },

  mounted() {},

  /* Lifecycles */
  async created() {
    this.getList()
    // try {
    //   const datas = await getMemberList()
    //   this.memberList = datas.data.participants
    // } catch (err) {
    //   console.log(err)
    // }
  },
  beforeDestroy() {
    this.$eventBus.$off('refresh')
  },
}
</script>
