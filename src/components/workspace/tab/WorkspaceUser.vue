<template>
  <tab-view
    title="협업 가능 멤버"
    description="협업 가능한 회원을 선택하고 메세지를 보내보세요."
    placeholder="멤버 검색"
    :listCount="0"
    :showDeleteButton="false"
    :showRefreshButton="true"
    :memberList="memberList"
    ><workspace-user-list></workspace-user-list>
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
    }
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  async created() {
    const memberList = await getMemberList()
    this.memberList = memberList.data.participants
    console.log('this.memberList', this.memberList)
  },
}
</script>
