<template>
  <modal
    title="원격 협업 생성하기"
    width="78.429em"
    height="54.286em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="createroom-modal"
  >
    <div class="createroom">
      <create-room-info
        :roomInfo="roomInfo"
        :selection="selection"
        :nouser="users.length === 0"
        @startRemote="startRemote"
      ></create-room-info>
      <create-room-invite
        :users="users"
        :selection="selection"
        @userSelect="selectUser"
        @inviteRefresh="inviteRefresh"
      ></create-room-invite>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { mapActions } from 'vuex'
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'

import { getMemberList } from 'api/workspace/member'
import { createRoom, getRoomInfo } from 'api/workspace/room'
import { sendPush } from 'api/common/message'
import { ROLE } from 'configs/remote.config'
import { getHistorySingleItem } from 'api/workspace/history'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [toastMixin, confirmMixin],
  components: {
    Modal,
    CreateRoomInfo,
    CreateRoomInvite,
  },
  data() {
    return {
      selection: [],
      visibleFlag: false,
      users: [],
      maxSelect: 2,
      roomInfo: {},
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    roomId: {
      type: Number,
      default: 0,
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.inviteRefresh()
        if (this.roomId && this.roomId > 0) {
          this.getInfo()
        }
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear']),
    async getInfo() {
      try {
        this.roomInfo = await getHistorySingleItem({ roomId: this.roomId })
      } catch (err) {
        console.error(err)
      }
    },
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify('최대 2명까지 선택이 가능합니다.')
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async inviteRefresh() {
      const inviteList = await getMemberList({
        workspaceId: this.workspace.uuid,
      })
      this.users = inviteList.memberInfoList
      this.selection = []
    },
    async startRemote(roomInfo) {
      try {
        const selectedUser = []

        for (let select of this.selection) {
          selectedUser.push(select.uuid)
        }
        selectedUser.push(this.account.uuid)

        const createdRoom = await createRoom({
          file: roomInfo.imageFile,
          title: roomInfo.title,
          description: roomInfo.description,
          leaderId: this.account.uuid,
          participants: selectedUser,
          workspaceId: this.workspace.uuid,
        })

        const joinRtn = await this.$call.join(createdRoom, ROLE.EXPERT_LEADER)
        if (joinRtn) {
          this.$eventBus.$emit('popover:close')

          const params = {
            service: 'rm-message',
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            targetUserIds: selectedUser,
            event: 'invitation',
            contents: {
              roomId: createRoom.roomId,
              nickName: this.account.nickname,
              profile: this.account.profile,
            },
          }

          const rtn = await sendPush(params)
          console.log(rtn)

          const roomInfo = await getRoomInfo({
            roomId: createdRoom.roomId,
          })

          this.setRoomInfo(roomInfo)
          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear()
          console.error('>>>join room 실패')
        }
      } catch (err) {
        this.roomClear()
        console.log(err)
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
