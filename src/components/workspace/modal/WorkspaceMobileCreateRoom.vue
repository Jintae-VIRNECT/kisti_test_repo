<template>
  <div class="workspace-mobile-createroom" :class="{ visible }">
    <header>{{ $t('workspace.create_remote') }}</header>

    <button click="close-btn" @click="close">close</button>

    {{ $t('workspace.create_remote_selected') }}

    <profile-list v-if="selection.length > 0" :users="selection"></profile-list>

    <create-room-invite
      :users="users"
      :selection="selection"
      @userSelect="selectUser"
      @inviteRefresh="inviteRefresh"
      :loading="loading"
    ></create-room-invite>

    <button
      class="btn large createroom-info__button"
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
import createRoomMixin from 'mixins/createRoom'
import confirmMixin from 'mixins/confirm'

export default {
  components: {
    CreateRoomInvite,
    ProfileList,
  },
  mixins: [createRoomMixin, confirmMixin],
  data() {
    return {}
  },
  computed: {
    btnDisabled() {
      return this.selection.length < 1
    },
    btnLoading() {
      return this.clicked //mixin data
    },
    shortName() {
      if (this.account.nickname.length > 10) {
        return this.account.nickname.substr(0, 10)
      } else {
        return this.account.nickname
      }
    },
  },
  methods: {
    close() {
      this.beforeClose()
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

      //mixin
      this.startRemote({
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

<style lang="scss" scoped>
@import '~assets/style/vars';

.workspace-mobile-createroom {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 999;
  display: none;
  background-color: $new_color_bg_sub;
  &.visible {
    display: flex;
    flex-direction: column;
  }
  .createroom-user {
    height: 100%;
  }
}
</style>
