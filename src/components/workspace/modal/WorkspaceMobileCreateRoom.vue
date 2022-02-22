<template>
  <full-screen-modal
    class="workspace-mobile-createroom"
    :title="$t('workspace.create_remote')"
    :visible="visible"
    @close="close"
  >
    <section class="mobile-createroom__selected">
      <div class="selected-header">
        <h1>{{ $t('workspace.create_remote_selected') }}</h1>
        <p class="selected-status">
          {{ `${selection.length}/${maxSelect}` }}
        </p>
        <button
          class="user-list-toggle-btn"
          :class="{
            off: !selectedListVisible,
          }"
          @click="toggleSelectedListVisible"
        ></button>
      </div>
      <profile-list
        v-if="selection.length > 0 && selectedListVisible"
        :users="selection"
        :remove="true"
        :showNickname="true"
        :showStatus="true"
        size="4.3rem"
        @remove="selectUser"
      ></profile-list>
    </section>

    <create-room-invite
      :users="users"
      :subGroups="subGroups"
      :selection="selection"
      @userSelect="selectUser"
      @inviteRefresh="inviteRefresh"
      :loading="loading"
      :groupList="groupList"
      :showMemberGroupSelect="showMemberGroupSelect"
    ></create-room-invite>

    <button
      class="btn mobile-createroom__button"
      :class="{ disabled: btnDisabled, 'btn-loading': btnLoading }"
      @click="startRemoteMobile"
    >
      {{ $t('button.start') }}
    </button>
  </full-screen-modal>
</template>

<script>
import FullScreenModal from '../../modules/FullScreenModal'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'
import ProfileList from 'ProfileList'
import confirmMixin from 'mixins/confirm'
import { maxParticipants } from 'utils/callOptions'

export default {
  components: {
    FullScreenModal,
    CreateRoomInvite,
    ProfileList,
  },
  mixins: [confirmMixin],
  data() {
    return {
      selectedListVisible: false,
      maxSelect: maxParticipants - 1,
    }
  },
  props: {
    visible: {
      type: Boolean,
      dafault: true,
    },
    btnLoading: {
      type: Boolean,
    },
    beforeClose: {
      type: Function,
    },
    users: {
      type: Array,
    },
    selection: {
      type: Array,
    },
    roomInfo: {
      type: Object,
    },
    loading: {
      type: Boolean,
    },
    groupList: {
      type: Array,
      default: () => [],
    },
    subGroups: {
      type: Array,
      default: () => [],
    },
    showMemberGroupSelect: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    btnDisabled() {
      return this.selection.length < 1
    },
    shortName() {
      if (this.account.nickname.length > 10) {
        return this.account.nickname.substr(0, 10)
      } else {
        return this.account.nickname
      }
    },
  },
  watch: {
    selection(cur, prev) {
      if (cur.length > 0 && prev.length === 0) {
        this.selectedListVisible = true
      }
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    selectUser(user) {
      if (this.selection.length >= 0) this.selectedListVisible = true
      this.$emit('userSelect', user)
    },
    inviteRefresh() {
      this.$emit('inviteRefresh')
    },
    startRemoteMobile() {
      if (this.btnDisabled) {
        if (this.selection.length < 1) {
          this.confirmDefault(this.$t('workspace.create_remote_selected_empty'))
        } else if (this.title.length < 2) {
          this.confirmDefault(this.$t('workspace.remote_name_valid1'))
        } else {
          this.confirmDefault(this.$t('workspace.remote_name_valid2'))
        }
        return
      }

      this.$emit('startRemote', {
        title: `${this.shortName}'s Room`,
        description: '',
        imageFile: null,
        imageUrl: '',
        open: false,
      })
    },
    toggleSelectedListVisible() {
      if (!this.selectedListVisible && this.selection.length === 0) return
      this.selectedListVisible = !this.selectedListVisible
      this.$eventBus.$emit('scrollHeightReset')
    },
  },
}
</script>
