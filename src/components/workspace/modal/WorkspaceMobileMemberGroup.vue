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
        type="text"
        :count="20"
        showCount
        :validate="validate"
        :valid.sync="groupNameInValid"
        required
      ></input-row>
      <p class="validate-message">
        {{ groupNameInValid ? groupNameInvalidMessage : ' ' }}
      </p>

      <room-invite
        :subGroups="subGroups"
        :selection="selection"
        :loading="loading"
        @userSelect="selectUser"
        @inviteRefresh="refreshUser"
      ></room-invite>
      <button
        class="btn save-group"
        :disabled="
          selection.length === 0 ||
            groupNameInput.length === 0 ||
            groupNameInValid
        "
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
    validate: {
      type: String,
      default: null,
    },
    groupNameInvalidMessage: {
      type: String,
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
    groupId: {
      type: String,
      default: null,
    },
    groupName: {
      type: String,
      default: '',
    },
    subGroups: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      groupNameInput: this.value,
      groupNameInValid: true,
    }
  },
  watch: {
    visible(flag) {
      if (flag) {
        if (this.groupId) {
          this.groupNameInput = this.groupName
        }
      } else {
        this.groupNameInput = ''
      }
    },
    groupNameInput(newVal) {
      this.$emit('update:value', newVal)
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    save() {
      this.$emit('save')
    },
    selectUser(user) {
      this.$emit('userSelect', user)
    },
    refreshUser() {
      this.$emit('inviteRefresh')
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/_mixin';

@include responsive-mobile {
  .member-group {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
  .member-group > .inputrow {
    padding: 2rem 1.6rem;
    padding-bottom: 0.4rem;
    @include fontLevel(100);

    .inputrow-title {
      margin-bottom: 1.1rem;
      color: $new_color_text_sub;
      &.valid.required {
        color: $new_color_text_sub;
        text-indent: initial;
        &::after {
          display: block;
        }
      }
    }

    .inputrow-input {
      background-color: $new_color_bg;
      @include fontLevel(100);
      border-color: $new_color_bg;
      &:hover {
        border-color: $new_color_bg;
      }
      &:active,
      &:focus {
        border: 1px solid $new_color_active_border;
      }
    }
    .inputrow-length {
      top: 20px;
      right: 16px;
      @include fontLevel(100);
      color: $new_color_text_sub;
    }
  }

  .member-group > .validate-message {
    height: 1.6rem;
    padding-left: 1.6rem;
    @include fontLevel(50);
    color: #ff757b;
  }

  .member-group > .createroom-user {
    height: 100%;
    margin-top: -0.9rem;
  }

  .member-group > .btn.save-group {
    min-height: 4.8rem;
    margin: 0 1.6rem 1.6rem;
    color: $new_color_text_main;
    background-color: $new_color_bg_button_primary;
    border-radius: 0.4rem;
    @include fontLevel(200);

    &:disabled {
      opacity: 0.4;
    }
  }
}
</style>
