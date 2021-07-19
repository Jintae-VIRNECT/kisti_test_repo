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
        :status="accessType(userinfo.accessType)"
        :showMasterMenu="isMaster && isOnpremise"
        @forceLogout="forceLogout(userinfo)"
      >
      </member-card>
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
import MemberCard from 'MemberCard'
import { getMemberList } from 'api/http/member'
import { WORKSPACE_ROLE } from 'configs/status.config'
import confirmMixin from 'mixins/confirm'
import { forceLogout } from 'api/http/message'

export default {
  name: 'WorkspaceUser',
  mixins: [confirmMixin],
  components: { TabView, MemberCard },
  data() {
    return {
      memberList: [],
      /*{
        deviceType: "UNKNOWN"
        email: "test2@test.com"
        memberStatus: "UNLOAD"
        memberType: "UNKNOWN"
        name: "홍길동2"
        nickName: "왕밤빵2"
        profile: "default"
        role: "MEMBER"
        uuid: "4b260e69bd6fa9a583c9bbe40f5aceb3"
        status?
      },...
      */
      searchText: '',
      searchMemberList: [],
      loading: false,
    }
  },
  computed: {
    isMaster() {
      return this.workspace.role === WORKSPACE_ROLE.MASTER
    },
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
    //멤버목록이 갱신되면, 검색 결과 목록도 업데이트 (검색 결과 목록 상태인 경우)
    memberList: {
      deep: true,
      handler() {
        if (this.searchMemberList.length) this.doSearch(this.searchText)
      },
    },
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
    accessType(accessType) {
      if (accessType) return accessType.toLowerCase()
      return ''
    },
    //(마스터인 경우) 협업 멤버 강제 로그아웃 시키기
    async forceLogout(targetUserinfo) {
      this.debug('selected user', targetUserinfo)

      //멤버의 상태가 실시간이 아니므로, 멤버 목록을 갱신한 후 강제 로그아웃 처리
      await this.getList()

      const { uuid } = targetUserinfo
      const nickNameTag = `<span style="color:#6bb4f9">${targetUserinfo.nickName}</span> `

      //갱신한 목록에서 해당 멤버 검색
      const latestTargetUserInfo = this.memberList.find(
        member => member.uuid === uuid,
      )

      //갱신한 목록에서 검색한 해당 멤버의 현재 접속 상태를 확인
      if (latestTargetUserInfo) {
        //강제 로그아웃 불가 메시지
        let text = `${nickNameTag} ${this.$t(
          'workspace.confirm_force_logout_unavailable',
        )}`

        let action = () => {}

        //멤버 상태가 '로그인'인 경우
        if (latestTargetUserInfo.accessType === 'LOGIN') {
          //강제 로그인 메시지 API 호출
          const params = {
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            targetUserIds: new Array(latestTargetUserInfo.uuid),
          }

          try {
            await forceLogout(params) //강제 로그아웃 실행

            //강제 로그아웃 실행 확인 메시지
            text = `${nickNameTag} ${this.$t(
              'workspace.confirm_force_logout_complete',
            )}`
          } catch (e) {
            console.error(e)
            return
          }

          //강제 로그아웃 처리 후 멤버 목록 갱신
          action = async () => await this.getList()
        }

        //로그인, 협업중인 상태의 멤버에게만 팝업 알림이 뜨도록 한다.
        if (latestTargetUserInfo.accessType !== 'LOGOUT')
          this.confirmDefault(text, { action })
      }
      //갱신한 멤버 목록에 해당 유저가 없는 경우(예외상황)
      else return
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
