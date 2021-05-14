<template>
  <tab-view
    :title="$t('workspace.user_title')"
    :description="$t('workspace.user_description')"
    :placeholder="$t('workspace.search_member')"
    :emptyImage="require('assets/image/img_user_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :empty="list.length === 0"
    :listCount="list.length"
    :showDeleteButton="false"
    :showRefreshButton="true"
    :loading="loading"
    @refresh="getList"
    @search="doSearch"
  >
    <div class="grid-container">
      <member-card
        v-for="userinfo in list"
        :key="'user_' + userinfo.uuid"
        :name="userinfo.nickName"
        :imageUrl="userinfo.profile"
        :email="userinfo.email"
        :role="userinfo.role"
        :status="userinfo.accessType"
      >
      </member-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import MemberCard from 'MemberCard'
import { getMemberList } from 'api/http/member'

export default {
  name: 'WorkspaceUser',
  components: { TabView, MemberCard },
  data() {
    return {
      memberList: [],
      searchText: '',
      searchMemberList: [],
      loading: false,
    }
  },
  computed: {
    list() {
      if (this.searchText.length > 0) {
        return this.searchMemberList
      } else {
        return this.memberList
      }
    },
    emptyTitle() {
      if (this.memberList.length > 0) {
        return this.$t('workspace.search_empty')
      } else {
        return this.$t('workspace.user_empty')
      }
    },
    emptyDescription() {
      if (this.memberList.length > 0) {
        return ''
      } else {
        return this.$t('workspace.user_empty_description')
      }
    },
  },
  watch: {
    workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid) {
        this.getList()
      }
    },
    // 'list.length': 'scrollReset',
  },
  methods: {
    doSearch(text) {
      this.searchMemberList = this.memberList.filter(member => {
        if (member.email.toLowerCase().includes(text.toLowerCase())) {
          return true
        }
        if (member.nickName.toLowerCase().includes(text.toLowerCase())) {
          return true
        }
        return false
      })
      this.searchText = text
    },
    async getList() {
      try {
        const params = {
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        }
        this.loading = true
        const datas = await getMemberList(params)
        this.memberList = datas.memberList
        this.memberList.sort((A, B) => {
          if (A.role === 'MASTER') {
            return -1
          } else if (B.role === 'MASTER') {
            return 1
          } else if (A.role === 'MANAGER' && B.role !== 'MANAGER') {
            return -1
          } else {
            return 0
          }
        })
        this.loading = false
      } catch (err) {
        this.loading = false
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

<style lang="scss">
@import '~assets/style/vars';

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(14.286rem, 1fr));
  column-gap: 0.571rem;
  row-gap: 0.571rem;
}
</style>
