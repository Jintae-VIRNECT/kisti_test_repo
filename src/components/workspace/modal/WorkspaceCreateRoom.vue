<template>
  <div>
    <modal
      :title="$t('workspace.create_remote')"
      width="78.429em"
      :showClose="true"
      :visible.sync="visiblePcFlag"
      :beforeClose="beforeClose"
      customClass="createroom-modal"
    >
      <div class="createroom">
        <create-room-info
          :key="'roominfo_' + sessionId"
          :roomInfo="roomInfo"
          :selection="selection"
          :btnLoading="clicked"
          @startRemote="startRemote"
        ></create-room-info>
        <create-room-invite
          :users="users"
          :subGroups="subGroups"
          :selection="selection"
          @userSelect="selectUser"
          @inviteRefresh="inviteRefresh"
          :loading="loading"
          :showMemberGroupSelect="true"
          :groupList="groupList"
        ></create-room-invite>
      </div>
    </modal>
    <workspace-mobile-create-room
      :visible.sync="visibleMobileFlag"
      :btnLoading="clicked"
      :roomInfo="roomInfo"
      :users="users"
      :subGroups="subGroups"
      :selection="selection"
      :beforeClose="beforeClose"
      :loading="loading"
      :showMemberGroupSelect="true"
      :groupList="groupList"
      @userSelect="selectUser"
      @inviteRefresh="inviteRefresh"
      @startRemote="startRemote"
    ></workspace-mobile-create-room>
  </div>
</template>

<script>
import Modal from 'Modal'
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'
import createRoomMixin from 'mixins/createRoom'
import roomMixin from 'mixins/room'
import { maxParticipants } from 'utils/callOptions'
import { getMemberGroupList, getMemberGroupItem } from 'api/http/member'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [createRoomMixin, roomMixin],
  components: {
    Modal,
    CreateRoomInfo,
    CreateRoomInvite,
    WorkspaceMobileCreateRoom: () => import('./WorkspaceMobileCreateRoom'),
  },
  props: {
    sessionId: {
      type: String,
      default: '',
    },
    visible: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      clicked: false,
      selection: [],
      selectHistory: [],
      users: [],
      loading: false,

      groupList: [],
      subGroups: [],

      roomInfo: {},
      maxSelect: maxParticipants - 1,
      visiblePcFlag: false,
      visibleMobileFlag: false,

      totalUserCount: 0,
    }
  },

  watch: {
    async visible(flag) {
      if (flag) {
        this.selection = []
        this.selectHistory = []
        if (this.sessionId && this.sessionId.length > 0) {
          await this.getInfo()
        }
        this.inviteRefresh()
        this.getGroups()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    reset() {
      this.selection = []
      this.selectHistory = []
    },
    /**
     * 즐겨찾기 목록 호출
     */
    async getGroups() {
      try {
        const groups = await getMemberGroupList({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })

        this.groupList = groups.favoriteGroupResponses

        //멤버 숫자 표기
        this.groupList.map(group => {
          group.memberCount = `${group.favoriteGroupMemberResponses.length}/${this.maxSelect}`
        })

        //'선택 없음' 항목 추가
        this.groupList.unshift({
          groupName: this.$t(
            'workspace.workspace_member_group_member_no_selection',
          ),
          groupId: 'NONE',
        })
      } catch (err) {
        console.error(err)

        if (err.code) {
          this.toastError(this.$t('confirm.network_error'))
        }
      }
    },

    /**
     * 즐겨찾기 그룹 상세 조회
     * @param {String} groupId 조회할 즐겨찾기 그룹 id
     */
    async getGroupMembers(groupId) {
      try {
        if (groupId === 'NONE') {
          this.selection = []
          return
        }

        const group = await getMemberGroupItem({
          workspaceId: this.workspace.uuid,
          groupId: groupId,
          userId: this.account.uuid,
        })

        //자기 자신은 제외하여 표출
        this.selection = group.favoriteGroupMemberResponses.filter(member => {
          return member.uuid !== this.account.uuid
        })
      } catch (err) {
        console.error(err)

        if (err.code) {
          this.toastError(this.$t('confirm.network_error'))
        }
      }
    },
  },

  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('update::selectedgroupid', this.getGroupMembers)
  },
  beforeDestroy() {
    this.$eventBus.$off('update::selectedgroupid', this.getGroupMembers)
  },
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
