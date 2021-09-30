<template>
  <full-screen-modal
    class="workspace-mobile-createroom"
    :title="$t('workspace.create_remote')"
    :visible="visible"
    @close="close"
  >
    <section class="mobile-createroom__selected" v-if="selection.length > 0">
      <div class="selected-header">
        <h1>{{ $t('workspace.create_remote_selected') }}</h1>
        <p class="selected-status">
          {{ `${onlineMemeberOfSelection}/${selection.length}` }}
        </p>
        <button></button>
      </div>
      <profile-list
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
      :selection="selection"
      @userSelect="selectUser"
      @inviteRefresh="inviteRefresh"
      :loading="loading"
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

export default {
  components: {
    FullScreenModal,
    CreateRoomInvite,
    ProfileList,
  },
  mixins: [confirmMixin],
  data() {
    return {}
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
    onlineMemeberOfSelection() {
      return this.selection.filter(user => user.accessType === 'LOGIN').length
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    selectUser(user) {
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
  },
}
</script>
