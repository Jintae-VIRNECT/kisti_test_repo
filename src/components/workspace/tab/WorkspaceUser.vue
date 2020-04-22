<template>
  <tab-view
    title="협업 가능 멤버"
    description="협업 가능한 회원을 선택하고 메세지를 보내보세요."
    placeholder="멤버 검색"
    :listCount="memberListLength"
    :showDeleteButton="false"
    :showRefreshButton="true"
    ><workspace-user-list></workspace-user-list>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import WorkspaceUserList from '../section/WorkspaceUserList'
import { getMemberList } from 'api/workspace/member'
import { mapGetters } from 'vuex'

export default {
  name: 'WorkspaceUser',
  components: { TabView, WorkspaceUserList },
  data() {
    return {
      memberList: [],
    }
  },
  computed: {
    ...mapGetters(['memberListLength']),
  },
  watch: {},
  methods: {},

  mounted() {
    const _this = this

    this.$eventBus.$on('memberList:refresh', async function(payload) {
      try {
        const datas = await getMemberList()
        _this.$store.dispatch('setHistoryList', datas.data.participants)
      } catch (err) {
        console.log(err)
      }
    })
  },

  /* Lifecycles */
  async created() {
    try {
      const datas = await getMemberList()
      this.$store.dispatch('setMemberList', datas.data.participants)
    } catch (err) {
      console.log(err)
    }
  },
  beforeDestroy() {
    this.$eventBus.$off('memberList:refresh')
  },
}
</script>
