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
        :selection="selection"
        :nouser="users.length === 0"
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
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'

import { inviteParticipantsList } from 'api/workspace/room'
import toastMixin from 'mixins/toast'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [toastMixin],
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
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.reset()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(
        select => user.userId === select.userId,
      )
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify('선택 가능 멤버를 초과했습니다.')
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async inviteRefresh() {
      const inviteList = await inviteParticipantsList()
      this.users = inviteList.participants
      this.selection = []
    },
  },

  /* Lifecycles */
  async created() {
    const inviteList = await inviteParticipantsList()
    this.users = inviteList.participants
  },
  mounted() {},
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
