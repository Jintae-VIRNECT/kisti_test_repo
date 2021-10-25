<template>
  <modal
    :title="$t('workspace.workspace_add_and_update_member_group')"
    width="50.8571rem"
    height="60.8571rem"
    :showClose="true"
    :visible.sync="visibleFlag"
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

      <member-list
        :users="users"
        :selection="selection"
        :total="users.length"
        :loading="loading"
        @userSelect="selectUser"
        @inviteRefresh="refreshUser"
      ></member-list>
      <button
        class="btn save-group"
        :disabled="selection.length === 0 || groupNameInValid"
        @click="save"
      >
        {{ $t('button.confirm') }} {{ '(' + selection.length }}/{{
          this.maxSelect + ')'
        }}
      </button>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import MemberList from '../partials/WorkspaceSubGroupMemberList'
import InputRow from 'InputRow'

// import {
//   createPrivateMemberGroup,
//   updatePrivateMemberGroup,
// } from 'api/http/member'

import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'

import { maxParticipants } from 'utils/callOptions'

// import { ERROR } from 'configs/error.config'

export default {
  name: 'ModalWorkspaceSubGroup',
  mixins: [toastMixin, errorMsgMixin],
  components: {
    Modal,
    MemberList,
    InputRow,
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
      visibleFlag: false,
      loading: false,
      selection: [],
      maxSelect: maxParticipants,
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
      this.visibleFlag = flag
      if (flag) {
        if (this.groupId) {
          this.groupNameInput = this.groupName
          this.selection = this.groupMembers
        }
      } else {
        this.selection = []
        this.groupNameInput = ''
      }
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
      //   try {
      //     const userIds = this.selection.map(member => member.uuid)
      //     userIds.push(this.account.uuid)
      //     if (this.groupId) {
      //       await this.updatePrivateMemberGroup(userIds)
      //     } else {
      //       await this.createPrivateMemberGroup(userIds)
      //     }
      //     //정상 저장시 모달창 close
      //     this.$emit('update:visible', false)
      //   } catch (err) {
      //     if (err.code === ERROR.WORKSPACE_MEMBER_GROUP_MAX_OVER) {
      //       this.showErrorToast(err.code)
      //     } else {
      //       if (err.code) {
      //         this.toastError(this.$t('confirm.network_error'))
      //       }
      //     }
      //     console.error(err)
      //   }
      // },
      // async updatePrivateMemberGroup(userIds) {
      //   await updatePrivateMemberGroup({
      //     workspaceId: this.workspace.uuid,
      //     userId: this.account.uuid,
      //     groupId: this.groupId,
      //     groupName: this.groupNameInput,
      //     memberList: userIds,
      //   })
      // },
      // async createPrivateMemberGroup(userIds) {
      //   await createPrivateMemberGroup({
      //     workspaceId: this.workspace.uuid,
      //     userId: this.account.uuid,
      //     groupName: this.groupNameInput,
      //     memberList: userIds,
      //   })
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
      background: rgb(248, 248, 250);
      border-radius: 0.714em;
    }
  }
  .member-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    height: 100%;

    .sub-group-member {
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
    margin-top: 1.2857rem;
    border-radius: 2px;

    &:disabled {
      background-color: #0f75f5;
      cursor: default;

      color: rgba(255, 255, 255, 0.4);
      font-size: 15px;

      font-weight: 500;
      text-align: center;
      letter-spacing: 0px;
    }
  }
}
</style>
