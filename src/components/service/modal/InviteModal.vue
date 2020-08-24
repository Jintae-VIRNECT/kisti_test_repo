<template>
  <modal
    :title="$t('service.invite_title')"
    width="50.857em"
    height="60.143em"
    :showClose="true"
    :visible.sync="visibleFlag"
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
            v-for="user of currentUser"
            :key="user.uuid"
          >
            <tooltip :content="user.nickname || user.nickName">
              <div class="invite-modal__profile" slot="body">
                <profile
                  :image="user.profile"
                  :thumbStyle="{ width: '3.714em', height: '3.714em' }"
                ></profile>
                <button class="invite-modal__current-kickout" @click="kickout">
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
        :total="totalElements"
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
</template>

<script>
import Modal from 'Modal'
import Profile from 'Profile'
import ProfileList from 'ProfileList'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'
import Tooltip from 'Tooltip'

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
    Profile,
    ProfileList,
    RoomInvite,
    Tooltip,
  },
  data() {
    return {
      currentUser: [],
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
    kickout() {
      console.log('kickout user')
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          // TODO: MESSAGE
          this.toastNotify(this.$t('service.invite_max'))
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
      this.currentUser = roomInfo.memberList.filter(user => {
        return user.memberStatus !== 'LOAD'
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
      if (res === true) {
        this.addMember(this.selection)
        // TODO: MESSAGE
        this.toastNotify(this.$t('service.invite_success'))
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
