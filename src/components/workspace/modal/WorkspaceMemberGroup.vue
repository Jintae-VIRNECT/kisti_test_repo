<template>
  <div>
    <modal
      :title="$t('workspace.workspace_add_and_update_member_group')"
      width="50.8571rem"
      height="60.8571rem"
      :showClose="true"
      :visible.sync="visiblePcFlag"
      :beforeClose="beforeClose"
      class="member-group-modal"
    >
      <div class="member-group">
        <input-row
          :title="$t('workspace.workspace_member_group_name')"
          :placeholder="$t('workspace.workspace_member_group_name')"
          :value.sync="groupNameInput"
          :valid.sync="groupNameInValid"
          validate="validName"
          :validMessage="groupNameInvalidMessage"
          type="text"
          :count="20"
          showCount
          countPosition="mid-right"
          titlePosition="bottom"
        ></input-row>

        <room-invite
          :users="users"
          :selection="selection"
          :total="users.length"
          :loading="loading"
          @userSelect="selectUser"
          @inviteRefresh="refreshUser"
        ></room-invite>
        <button
          class="btn save-group"
          :disabled="selection.length === 0 || groupNameInValid"
          @click="save"
        >
          {{ $t('button.confirm') }} {{ selection.length }}/{{ this.maxSelect }}
        </button>
      </div>
    </modal>
    <workspace-mobile-member-group
      :visible.sync="visibleMobileFlag"
      :beforeClose="beforeClose"
      :value.sync="groupNameInput"
      :users="users"
      :selection="selection"
      :loading="loading"
      :maxSelect="maxSelect"
      @userSelect="selectUser"
      @inviteRefresh="refreshUser"
      @save="save"
    ></workspace-mobile-member-group>
  </div>
</template>

<script>
import Modal from 'Modal'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'
import InputRow from 'InputRow'

import {
  createPrivateMemberGroup,
  updatePrivateMemberGroup,
} from 'api/http/member'

import toastMixin from 'mixins/toast'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'

import { maxParticipants } from 'utils/callOptions'

export default {
  name: 'WorkspaceMemberGroup',
  mixins: [toastMixin, responsiveModalVisibleMixin],
  components: {
    Modal,
    RoomInvite,
    InputRow,
    WorkspaceMobileMemberGroup: () => import('./WorkspaceMobileMemberGroup'),
  },
  props: {
    groupId: {
      type: String,
      default: null,
    },
    groupName: {
      type: String,
      default: '',
    },
    visible: {
      type: Boolean,
      default: false,
    },
    users: {
      tpye: Array,
      default: () => [],
    },
    total: {
      type: [Number, Boolean],
      default: false,
    },
    groupMembers: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      //visibleFlag: false,
      loading: false,
      selection: [],
      maxSelect: maxParticipants - 1,
      groupNameInput: '',
      groupNameInValid: true,
    }
  },
  computed: {
    totalNum() {
      if (this.total === false) {
        return this.users.length
      } else {
        return this.total
      }
    },
    groupNameInvalidMessage() {
      if (this.groupNameInput.length < 2) {
        return this.$t('workspace.workspace_member_group_name_valid1')
      } else {
        return this.$t('workspace.remote_name_valid2')
      }
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        if (this.groupId) {
          this.groupNameInput = this.groupName
          this.selection = this.groupMembers
        }
      } else {
        this.selection = []
        this.groupNameInput = ''
      }
      this.setVisiblePcOrMobileFlag(flag)
    },
    groupNameInput() {
      if (this.groupNameInput.length < 2) {
        this.groupNameInValid = true
      }
    },
    users() {
      this.loading = false
    },
    groupMembers() {
      this.loading = false
    },
  },

  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
    accessType(accessType) {
      if (accessType) return accessType.toLowerCase()
      return ''
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
    refreshUser() {
      this.loading = true
      if (this.groupId) {
        this.selection = this.groupMembers
      } else {
        this.selection = []
      }

      this.$emit('refresh', this.groupId)
    },
    async save() {
      try {
        const userIds = this.selection.map(member => member.uuid)
        userIds.push(this.account.uuid)
        if (this.groupId) {
          const result = await updatePrivateMemberGroup({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            groupId: this.groupId,
            groupName: this.groupNameInput,
            memberList: userIds,
          })
        } else {
          const result = await createPrivateMemberGroup({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            groupName: this.groupNameInput,
            memberList: userIds,
          })
        }

        //정상 저장시 모달창 close
        this.$emit('update:visible', false)
      } catch (error) {
        if (error.code === 4027) {
          this.toastDefault(
            this.$t('workspace.workspace_member_group_max_description'),
          )
        } else {
          this.toastError(this.$t('confirm.network_error'))
        }

        console.error(error)
      }
    },
  },
}
</script>

<style lang="scss">
.member-group-modal {
  height: 100%;
  .modal--inner {
    > .modal--body {
      padding: 2.3571rem 2.5714rem 2.1429rem 2.5714rem;
    }
  }
  .member-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    height: 100%;

    .createroom-user {
      flex: 1;
      width: 100%;
      margin: 0;
      margin-top: 3.7143rem;
    }

    .input-row {
      width: 100%;
    }
  }
  .save-group {
    width: 18.4286rem;
    height: 3.4286rem;
    margin-top: 2.2857rem;
    border-radius: 2px;
  }
}
</style>
