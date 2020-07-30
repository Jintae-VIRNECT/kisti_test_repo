<template>
  <modal
    title="협업 참가 요청"
    width="712px"
    height="842px"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="service-invite"
  >
    <div class="invite-modal__body">
      <div v-if="!nouser" class="invite-modal__selected">
        <p class="invite-modal__selected-title">
          {{ `선택한 멤버 (${selection.length}/${maxSelect})` }}
        </p>
        <profile-list
          v-if="selection.length > 0"
          :users="selection"
          size="3.714em"
        ></profile-list>
        <p class="invite-modal__selected-empty" v-else>
          멤버를 추가해 주세요.
        </p>
      </div>
      <room-invite
        :users="users"
        :selection="selection"
        :total="totalElements"
        :loading="loading"
        @userSelect="selectUser"
        @inviteRefresh="init"
      ></room-invite>
      <div class="invite-modal__footer">
        <button class="btn" :disabled="selection.length === 0" @click="invite">
          협업 요청
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileList from 'ProfileList'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'

import { mapGetters, mapActions } from 'vuex'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import { inviteRoom, getMember } from 'api/service'
export default {
  name: 'InviteModal',
  mixins: [toastMixin, confirmMixin],
  components: {
    Modal,
    ProfileList,
    RoomInvite,
  },
  data() {
    return {
      selection: [],
      nouser: false,
      visibleFlag: false,
      users: [],
      totalElements: 0,
      loading: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    maxSelect: {
      type: Number,
      default: 0,
    },
  },
  computed: {
    ...mapGetters(['roomInfo', 'roomMember']),
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.init()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    ...mapActions(['addMember']),
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
          // TODO: MESSAGE
          this.toastNotify('선택가능 멤버 명수를 초과했습니다.')
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async init() {
      this.loading = true
      const res = await getMember({
        size: 100,
        workspaceId: this.workspace.uuid,
      })
      this.users = res.memberInfoList.filter(
        member =>
          this.roomMember.findIndex(part => part.uuid === member.uuid) < 0,
      )
      this.totalElements = res.pageMeta.totalElements - this.roomMember.length
      this.loading = false
      this.selection = []
    },
    async invite() {
      const participants = []
      for (let select of this.selection) {
        participants.push({
          id: select.uuid,
          email: select.email,
        })
      }
      const params = {
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        leaderId: this.account.uuid,
        participants,
      }
      const res = await inviteRoom(params)
      if (res === true) {
        this.addMember(this.selection)
        // TODO: MESSAGE
        this.toastNotify('협업 참가를 요청했습니다.')
        this.$nextTick(() => {
          this.visibleFlag = false
        })
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/service/service-invite-modal.scss"></style>
