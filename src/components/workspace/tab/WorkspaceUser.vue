<template>
  <tab-view
    v-if="mode === 'user'"
    :title="$t('workspace.user_title')"
    :description="$t('workspace.user_description')"
    :placeholder="$t('workspace.search_member')"
    :emptyImage="emptyImage"
    :emptyTitle="emptyTitle"
    :emptyDescription="emptyDescription"
    :empty="list.length === 0"
    :listCount="list.length"
    :showDeleteButton="false"
    :showRefreshButton="true"
    :showManageGroupButton="true"
    :loading="loading"
    @refresh="getList"
    @search="doSearch"
    @showgroup="toggleMode"
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
  <tab-view
    v-else
    :title="$t('workspace.workspace_member_group')"
    :description="$t('workspace.workspace_member_group_max_description')"
    :emptyImage="require('assets/image/img_user_empty.svg')"
    :emptyTitle="$t('workspace.workspace_group_empty')"
    :empty="groupList.length === 0"
    :listCount="groupList.length"
    :showMemberButton="true"
    :showAddGroupButton="true"
    :loading="groupLoading"
    :showSubHeader="true"
    @showmember="toggleMode"
    @addgroup="showGroup"
  >
    <div class="list-wrapper">
      <member-group
        v-for="(group, index) in groupList"
        :key="'group_' + group.groupId"
        :index="index + 1"
        :group="group"
        :isOverflow="isOverflow"
        @deletegroup="deleteGroup"
        @updategroup="updategroup"
      ></member-group>
    </div>
    <template v-slot:modal>
      <member-group-modal
        :visible.sync="memberGroupModalFlag"
        :users="memberList"
        :groupMembers="groupMemberList"
        :groupId="selectedGroupId"
        :groupName="selectedGroupName"
        :total="groupMemberList.length"
        @refresh="refreshGroupMember"
      ></member-group-modal>
    </template>
  </tab-view>
</template>

<script>
import MemberGroup from 'MemberGroup'
import TabView from '../partials/WorkspaceTabView'
import MemberCard from 'MemberCard'

import MemberGroupModal from '../modal/WorkspaceMemberGroup'

import {
  getMemberList,
  getMemberGroupList,
  getMemberGroupItem,
  deletePrivateMemberGroup,
} from 'api/http/member'

import { WORKSPACE_ROLE } from 'configs/status.config'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
import { forceLogout } from 'api/http/message'
import responsiveEmptyImageMixin from 'mixins/responsiveEmptyImage'

const defaultEmptyImage = require('assets/image/img_user_empty.svg')
const mobileEmptyImage = require('assets/image/img_user_empty_new.svg')

import { memberSort } from 'utils/sort'

export default {
  name: 'WorkspaceUser',
  mixins: [confirmMixin, toastMixin, responsiveEmptyImageMixin],
  components: { TabView, MemberCard, MemberGroup, MemberGroupModal },
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
      groupLoading: false,

      memberGroupModalFlag: false,

      mode: 'user', //'user', 'group'

      selectedGroupId: null,
      selectedGroupName: '',

      groupList: [],
      groupMemberList: [],

      isOverflow: false,
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
    async workspace(val, oldVal) {
      if (val.uuid !== oldVal.uuid) {
        await this.getList()
        await this.getMemberGroups()
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

    async memberGroupModalFlag(flag) {
      await this.getMemberGroups()

      if (!flag) {
        this.groupMemberList = []
        this.selectedGroupId = null
        this.selectedGroupName = ''
      }
    },

    async mode(now) {
      if (now === 'group') {
        await this.getMemberGroups()
      }
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
    async getList(reason) {
      try {
        if (reason !== 'data_loading') {
          this.loading = true
        }

        const params = {
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        }

        const datas = await getMemberList(params)
        this.memberList = datas.memberList
        this.memberList.sort(memberSort)

        if (reason !== 'data_loading') {
          this.loading = false
        }
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
      const nickNameTag = this.isMobileSize
        ? `${targetUserinfo.nickName}\n`
        : `<span style="color:#6bb4f9">${targetUserinfo.nickName}</span> `

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

    toggleMode() {
      this.mode = this.mode === 'user' ? 'group' : 'user'
    },

    showGroup() {
      this.memberGroupModalFlag = true
    },
    async updategroup(groupId) {
      this.$eventBus.$emit('popover:close')

      const group = await getMemberGroupItem({
        workspaceId: this.workspace.uuid,
        groupId: groupId,
        userId: this.account.uuid,
      })

      //자기자신은 제외
      this.groupMemberList = group.favoriteGroupMemberResponses.filter(
        member => {
          return member.uuid !== this.account.uuid
        },
      )

      //그룹 수정 모달 출력
      this.selectedGroupId = groupId
      this.selectedGroupName = group.groupName
      this.memberGroupModalFlag = true
    },

    /**
     * 그룹 삭제
     * @param {String} 삭제할 그룹 id
     */
    async deleteGroup(groupId) {
      this.$eventBus.$emit('popover:close')

      try {
        await deletePrivateMemberGroup({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
          groupId: groupId,
        })
      } catch (err) {
        this.toastError(this.$t('confirm.network_error'))
      }

      await this.getMemberGroups()
    },
    async refreshGroupMember(groupId) {
      if (groupId) {
        //자기자신은 제외
        const group = await getMemberGroupItem({
          workspaceId: this.workspace.uuid,
          groupId: groupId,
          userId: this.account.uuid,
        })

        this.groupMemberList = group.favoriteGroupMemberResponses.filter(
          member => {
            return member.uuid !== this.account.uuid
          },
        )

        //그룹 수정 모달 출력
        this.selectedGroupId = groupId
        this.selectedGroupName = group.groupName
      } else {
        this.getList('data_loading')
      }
    },
    /**
     * 그룹 멤버 호출
     */
    async getMemberGroups() {
      this.groupLoading = true

      try {
        const groups = await getMemberGroupList({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })
        this.groupList = groups.favoriteGroupResponses
      } catch (err) {
        console.error(err)
        this.toastError(this.$t('confirm.network_error'))
      } finally {
        this.groupLoading = false
      }
    },

    checkIsOverflow() {
      if (
        matchMedia(
          'only screen and (min-width: 1025px) and (max-width: 1372px)',
        ).matches
      )
        this.isOverflow = true
      else if (matchMedia('screen and (max-width: 920px)').matches)
        this.isOverflow = true
      else this.isOverflow = false
    },
  },

  /* Lifecycles */
  async created() {
    this.setMobileDefaultEmptyImage(defaultEmptyImage, mobileEmptyImage)
    this.getList()
  },
  mounted() {
    this.checkIsOverflow()
    window.addEventListener('resize', this.checkIsOverflow)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkIsOverflow)
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(14.286rem, 1fr));
  column-gap: 0.571rem;
  row-gap: 0.571rem;
}

@include responsive-mobile {
  .grid-container {
    grid-template-columns: repeat(2, minmax(15.9rem, 1fr));
    -webkit-column-gap: 1rem;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    row-gap: 1rem;
  }
}
</style>
