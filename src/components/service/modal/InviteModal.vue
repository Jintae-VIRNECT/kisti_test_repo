<template>
  <modal
    :title="$t('service.participant_invite_title')"
    width="50.857em"
    height="60.143em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="service-invite"
  >
    <div class="invite-modal__body">
      <div v-if="!nouser" class="invite-modal__selected">
        <p class="invite-modal__selected-title">
          {{
            $t('service.participant_invite_selected', {
              num: selection.length,
              max: maxSelect,
            })
          }}
        </p>
        <profile-list
          v-if="selection.length > 0"
          :users="selection"
          size="3.714em"
        ></profile-list>
        <p class="invite-modal__selected-empty" v-else>
          {{ $t('service.participant_invite_add_member') }}
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
          {{ $t('service.participant_invite_require') }}
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
import { getRoomInfo } from 'api/workspace'
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
          this.toastNotify(this.$t('service.participant_invite_max'))
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
      const roomInfo = await getRoomInfo({
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
      })
      this.users = res.memberInfoList.filter(
        member =>
          roomInfo.memberList.findIndex(part => part.uuid === member.uuid) < 0,
      )
      this.totalElements = res.pageMeta.totalElements - this.roomMember.length
      this.loading = false
      this.selection = []
    },
    async invite() {
      if (this.checkBeta()) return
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
        this.toastNotify(this.$t('service.participant_invite_success'))
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
