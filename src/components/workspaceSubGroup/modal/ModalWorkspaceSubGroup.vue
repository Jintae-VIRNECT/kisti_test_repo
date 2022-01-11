<template>
  <modal
    :title="$t('subgroup.add_and_delete')"
    width="50.8571rem"
    height="60.8571rem"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    class="member-group-modal"
  >
    <div class="member-group">
      <input-row
        :title="$t('subgroup.group_name')"
        :placeholder="$t('subgroup.group_name')"
        :value.sync="groupNameInput"
        :valid.sync="groupNameInValid"
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
        :loading="modalLoading"
        @userSelect="selectUser"
        @inviteRefresh="refreshUser"
      ></member-list>
      <button class="btn save-group" :disabled="!canSave" @click="save">
        {{ $t('button.confirm') }} {{ '(' + selection.length + ')' }}
      </button>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import MemberList from '../partials/WorkspaceSubGroupMemberList'
import InputRow from 'InputRow'

import { createSubGroup, updateSubGroup } from 'api/http/subGroup'

import toastMixin from 'mixins/toast'
import errorMsgMixin from 'mixins/errorMsg'

import { maxParticipants } from 'utils/callOptions'

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
      type: Array,
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
    modalLoading: {
      type: Boolean,
      default: false,
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
      isSaving: false,
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
      const inputLength = this.groupNameInput.length

      if (inputLength === 0) {
        return this.$t('subgroup.error_enter_group_name')
      }

      if (inputLength < 2 && inputLength > 0) {
        return this.$t('subgroup.error_please_input_more_two_letter')
      }

      if (this.selection.length < 1) {
        return this.$t('subgroup.error_please_select_at_least_one_member')
      }

      return ''
    },
    canSave() {
      const hasSelection = this.selection.length > 0
      const validGroupNameLength = this.groupNameInput.length > 1

      return hasSelection && validGroupNameLength && !this.isSaving
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
      this.loading = false
    },
    async save() {
      this.isSaving = true
      try {
        const userIds = this.selection.map(member => member.uuid)
        if (this.groupId) {
          await this.updateSubGroup(userIds)
        } else {
          await this.createSubGroup(userIds)
        }
        //정상 저장시 모달창 close
        this.$emit('update:visible', false)
      } catch (err) {
        if (err.code === 4030) {
          this.toastError(
            this.$t('subgroup.error_there_are_already_configured_members'),
          )
        } else {
          console.error(err)
        }
      }
      this.isSaving = false
    },
    async createSubGroup(userIds) {
      await createSubGroup({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        groupName: this.groupNameInput,
        memberList: userIds,
      })
    },
    async updateSubGroup(userIds) {
      await updateSubGroup({
        groupId: this.groupId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        groupName: this.groupNameInput,
        memberList: userIds,
      })
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
      font-size: 1.0714rem;

      font-weight: 500;
      text-align: center;
      letter-spacing: 0px;
    }
  }
}
</style>
