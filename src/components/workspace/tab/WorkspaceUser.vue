<template>
  <tab-view
    :title="$t('workspace.user_title')"
    :description="$t('workspace.user_description')"
    :placeholder="$t('workspace.search_member')"
    :emptyImage="require('assets/image/img_user_empty.svg')"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :empty="list.length === 0"
    :listCount="memberList.length"
    :showDeleteButton="false"
    :showRefreshButton="true"
    :loading="loading"
    @refresh="getList"
  >
    <div class="grid-container">
      <member-card
        v-for="userinfo in list"
        :key="'user_' + userinfo.uuid"
        :name="userinfo.nickName"
        :imageUrl="userinfo.profile"
        :email="userinfo.email"
        :role="userinfo.role"
      >
      </member-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import MemberCard from 'MemberCard'
import { getMemberList } from 'api/http/member'
import searchMixin from 'mixins/filter'

export default {
  name: 'WorkspaceUser',
  mixins: [searchMixin],
  components: { TabView, MemberCard },
  data() {
    return {
      memberList: [],
      loading: false,
    }
  },
  computed: {
    list() {
      return this.getFilter(this.memberList, ['email', 'nickName'])
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
    'list.length': 'scrollReset',
  },
  methods: {
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
