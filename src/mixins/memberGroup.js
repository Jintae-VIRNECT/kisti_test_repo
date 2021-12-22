import { checkSpecialCharacters } from 'utils/regexp'
import { maxParticipants } from 'utils/callOptions'

export default {
  name: 'memberGroup',
  data() {
    return {
      maxSelect: maxParticipants - 1,
    }
  },
  computed: {
    canSave() {
      const validGroupNameLength = this.groupNameInput.length > 1
      const validGroupName = !checkSpecialCharacters(this.groupNameInput)
      const hasSelection = this.selection.length > 0

      return validGroupNameLength && validGroupName && hasSelection
    },
    groupNameInvalidMessage() {
      const inputLength = this.groupNameInput.length

      if (inputLength === 0) {
        return this.$t('workspace.workspace_member_group_name_valid1')
      }

      if (checkSpecialCharacters(this.groupNameInput)) {
        return this.$t('workspace.remote_name_valid2')
      }

      if (inputLength < 2 && inputLength > 0) {
        return this.$t('workspace.workspace_member_group_name_valid2')
      }

      if (this.selection.length === 0) {
        return this.$t('workspace.workspace_member_group_count_valid')
      }

      return ''
    },
  },
  watch: {
    canSave() {
      this.groupNameInValid = !this.canSave
    },
  },
}
