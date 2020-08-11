<template>
  <tab-view
    :title="$t('workspace.user_title')"
    :description="$t('workspace.user_description')"
    :placeholder="$t('workspace.search_member')"
    :emptyImage="require('assets/image/img_user_empty.svg')"
    :emptyTitle="$t('workspace.user_empty')"
    :emptyDescription="$t('workspace.user_empty_description')"
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
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid) {
        this.getList()
      }
    },
  },
  methods: {
    async getList() {
      try {
        const params = {
          workspaceId: this.workspace.uuid,
        }
        this.loading = true
        const datas = await getMemberList(params)
        this.loading = false
        this.memberList = datas.memberInfoList.filter(
          member => member.uuid !== this.account.uuid,
        )
      } catch (err) {
        console.error(err)
      }
    },
  },

  mounted() {},

  /* Lifecycles */
  async created() {
    this.getList()
  },
  beforeDestroy() {
    this.$eventBus.$off('refresh')
  },
}
</script>
