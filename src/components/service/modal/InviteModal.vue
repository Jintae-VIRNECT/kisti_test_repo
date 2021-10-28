<template>
  <div>
    <modal
      :title="$t('service.invite_title')"
      width="50.857em"
      height="60.143em"
      :showClose="true"
      :visible.sync="visiblePcFlag"
      :beforeClose="beforeClose"
      customClass="service-invite"
    >
      <div class="invite-modal__body">
        <div class="invite-modal__current">
          <div class="invite-modal__current-title">
            <p>
              {{ $t('service.invite_unconnected_list') }}
            </p>
            <tooltip
              customClass="tooltip-guide"
              :content="$t('service.invite_unconnected_remove')"
              placement="right"
              effect="blue"
              :guide="true"
            >
              <img
                slot="body"
                class="setting__tooltip--icon"
                src="~assets/image/ic_tool_tip.svg"
              />
            </tooltip>
          </div>

          <div class="invite-modal__current-list">
            <figure
              class="invite-modal__current-user"
              v-for="(user, idx) of currentUser"
              :key="user.uuid"
            >
              <tooltip :content="user.nickname || user.nickName">
                <div class="invite-modal__profile" slot="body">
                  <profile
                    :image="user ? user.profile : 'default'"
                    :thumbStyle="{ width: '3.714em', height: '3.714em' }"
                  ></profile>
                  <button
                    class="invite-modal__current-kickout"
                    @click="kickoutConfirm({ participant: user, idx })"
                  >
                    {{ $t('button.kickout') }}
                  </button>
                </div>
                <span>{{ user.nickname || user.nickName }}</span>
              </tooltip>
            </figure>
          </div>
        </div>
        <room-invite
          :users="users"
          :selection="selection"
          :total="users.length"
          :loading="loading"
          @userSelect="selectUser"
          @inviteRefresh="init"
        ></room-invite>
      </div>
      <div slot="footer" class="invite-modal__footer">
        <p
          class="invite-modal__selected-title"
          v-html="
            $t('service.invite_selected', {
              num: selection.length,
              max: maxSelect,
            })
          "
        ></p>
        <profile-list :users="selection" size="2.143em"></profile-list>
        <button class="btn" :disabled="selection.length === 0" @click="invite">
          {{ $t('service.invite_require') }}
        </button>
      </div>
    </modal>
    <mobile-invite-modal
      :visible.sync="visibleMobileFlag"
      :maxSelect="maxSelect"
      :roomInfo="roomInfo"
      :users="users"
      :currentUser="currentUserWithFlag"
      :selection="selection"
      :beforeClose="beforeClose"
      :loading="loading"
      @userSelect="selectUser"
      @inviteRefresh="init"
      @invite="invite"
      @kickout="kickoutConfirm"
    ></mobile-invite-modal>
  </div>
</template>

<script>
import Modal from 'Modal'
import Profile from 'Profile'
import ProfileList from 'ProfileList'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'
import Tooltip from 'Tooltip'
import MobileInviteModal from './MobileInviteModal.vue'

import { mapGetters, mapActions } from 'vuex'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import { inviteRoom, kickoutMember, invitableList } from 'api/http/member'
import { getRoomInfo as roomInfo } from 'api/http/room'
import { memberSort } from 'utils/sort'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'

export default {
  name: 'InviteModal',
  mixins: [toastMixin, confirmMixin, responsiveModalVisibleMixin],
  components: {
    Modal,
    Profile,
    ProfileList,
    RoomInvite,
    Tooltip,
    MobileInviteModal,
  },
  data() {
    return {
      selection: [],
      users: [],
      loading: false,
      currentUser: [],
      currentLength: 0,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters(['roomInfo']),
    maxSelect() {
      return this.roomInfo.maxUserCount - this.currentLength
    },
    currentUserWithFlag() {
      return this.currentUser.map(user => {
        user.currentInvited = true
        return user
      })
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.init()
      } else {
        this.selection = []
        this.users = []
        this.loading = false
      }
      this.setVisiblePcOrMobileFlag(flag)
    },
  },
  methods: {
    ...mapActions(['addMember', 'removeMember']),
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    kickoutConfirm({ participant, idx }) {
      console.log(participant, idx)

      this.serviceConfirmCancel(
        this.$t('service.participant_kick_confirm', {
          name: participant.nickName,
        }),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.$emit('kickout')
            this.kickout(participant.uuid, idx)
          },
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    async kickout(participantId, idx) {
      const params = {
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        leaderId: this.account.uuid,
        participantId,
      }
      const res = await kickoutMember(params)
      if (res.result === true) {
        this.toastNotify(this.$t('confirm.access_removed'))

        this.currentUser.slice(idx, 1)[0]
        this.init()
        this.removeMember(participantId)
      }
      this.init()
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify(this.$t('service.invite_max'))
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    init() {
      this.getInviteList()
      this.getRoomInfo()
    },
    async getInviteList() {
      this.loading = true
      const res = await invitableList({
        workspaceId: this.workspace.uuid,
        sessionId: this.roomInfo.sessionId,
        userId: this.account.uuid,
      })
      this.users = res.memberList.sort(memberSort)
      this.loading = false
      this.selection = []
    },
    async getRoomInfo() {
      const res = await roomInfo({
        workspaceId: this.workspace.uuid,
        sessionId: this.roomInfo.sessionId,
      })
      this.currentLength = res.memberList.length
      const unloadUsers = res.memberList.filter(
        member => member.memberStatus !== 'LOAD',
      )
      this.currentUser = unloadUsers
    },
    async invite() {
      const participantIds = []
      for (let select of this.selection) {
        participantIds.push(select.uuid)
      }
      const params = {
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        leaderId: this.account.uuid,
        participantIds,
      }
      const res = await inviteRoom(params)
      if (res.result === true) {
        this.addMember(this.selection)

        this.toastNotify(this.$t('service.invite_success'))

        //PC환경에서만 협업 초대 후 창이 닫히도록 처리한다.
        if (!this.isMobileSize) {
          this.$nextTick(() => {
            this.$emit('update:visible', false)
          })
        }
      }
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/service/service-invite-modal.scss"></style>
