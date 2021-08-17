<template>
  <div class="workspace-createroom-wrapper">
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
          :selection="selection"
          @userSelect="selectUser"
          @inviteRefresh="inviteRefresh"
          @selectedgroupid="getGroupMembers"
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
      :selection="selection"
      :beforeClose="beforeClose"
      :loading="loading"
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

import { getMemberGroupList, getMemberGroupItem } from 'api/http/member'

import { maxParticipants } from 'utils/callOptions'

import createRoomMixin from 'mixins/createRoom'

import { mapGetters } from 'vuex'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [createRoomMixin],
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
      default: false,
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

      roomInfo: {},
      maxSelect: maxParticipants - 1,
      visiblePcFlag: false,
      visibleMobileFlag: false,
    }
  },
  computed: {
    ...mapGetters(['targetCompany', 'restrictedMode', 'useScreenStrict']),
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
    async getGroups() {
      try {
        const groups = await getMemberGroupList({
          workspaceId: this.workspace.uuid,
          userId: this.account.uuid,
        })

        this.groupList = groups.groupInfoResponseList
        this.groupList.map(group => {
          group.memberCount = `${group.remoteGroupMemberInfoResponseList
            .length - 1}/${this.maxSelect}`
        })
        this.groupList.unshift({
          groupName: '선택 안함',
          groupId: 'NONE',
        })
      } catch (err) {
        console.error(err)
        this.toastError(this.$t('confirm.network_error'))
      }
    },
    async getGroupMembers(groupId) {
      console.log(groupId)
      try {
        if (groupId === 'NONE') {
          this.selection = []
          return
        }

        const group = await getMemberGroupItem({
          workspaceId: this.workspace.uuid,
          groupId: groupId,
        })

        this.selection = group.remoteGroupMemberInfoResponseList.filter(
          member => {
            return member.uuid !== this.account.uuid
          },
        )
      } catch (err) {
        console.error(err)
      }
    },
  },

  /* Lifecycles */
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
