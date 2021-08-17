<template>
  <div class="workspace-mobile-createroom" v-if="visibleFlag">
    <header class="mobile-createroom__header">
      <h1>{{ $t('workspace.create_remote') }}</h1>
      <button class="header-close-btn" @click="close"></button>
    </header>

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
  </div>
</template>

<script>
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'
import ProfileList from 'ProfileList'
import confirmMixin from 'mixins/confirm'

export default {
  components: {
    CreateRoomInvite,
    ProfileList,
  },
  mixins: [confirmMixin],
  data() {
    return {
      visibleFlag: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      dafault: false,
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
  watch: {
    visible(flag) {
      this.visibleFlag = flag
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
        imageFile: this.roomInfo.description,
        imageUrl: this.roomInfo.profile,
        open: false,
      })
    },
  },
}
</script>
