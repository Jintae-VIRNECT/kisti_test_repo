<template>
  <full-screen-modal
    :title="$t('workspace.workspace_add_and_update_member_group')"
    :visible="visible"
    @close="close"
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
  </full-screen-modal>
</template>

<script>
import FullScreenModal from 'FullScreenModal'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'
import InputRow from 'InputRow'
export default {
  components: { FullScreenModal, RoomInvite, InputRow },
  props: {
    visible: {
      type: Boolean,
      default: true,
    },
    beforeClose: {
      type: Function,
    },
    value: {
      type: String,
    },
    valid: {
      type: Boolean,
    },
    groupNameInvalidMessage: {
      type: String,
    },
    users: {
      type: Array,
    },
    selection: {
      type: Array,
    },
    loading: {
      type: Boolean,
    },
    maxSelect: {
      type: Number,
    },
  },
  data() {
    return {
      groupNameInput: '',
      groupNameInValid: null,
    }
  },
  watch: {
    visible(f) {
      console.error(f)
    },
    groupNameInput(newVal) {
      this.$emit('update:value', newVal)
    },
    groupNameInValid(newVal) {
      this.$emit('update:valid', newVal)
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    save() {
      this.$emit('save')
    },
    selectUser() {
      this.$emit('userSelect')
    },
    refreshUser() {
      this.$emit('inviteRefresh')
    },
  },
}
</script>

<style></style>
