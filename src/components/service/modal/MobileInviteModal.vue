<template>
  <full-screen-modal
    class="service-mobile-inviteroom-modal"
    :title="$t('service.invite_title')"
    :visible="visible"
    @close="close"
  >
    <section class="mobile-inviteroom__selected" v-if="selection.length > 0">
      <div class="selected-header">
        <h1>{{ $t('workspace.create_remote_selected') }}</h1>
        <p class="selected-status">
          {{ `${selection.length}/${maxSelect}` }}
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
      class="btn mobile-inviteroom__button"
      :disabled="selection.length === 0"
      @click="invite"
    >
      {{ $t('service.invite_require') }}
    </button>
  </full-screen-modal>
</template>

<script>
import FullScreenModal from 'FullScreenModal'
import inviteCreateMixin from 'mixins/inviteCreate'
import CreateRoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'
import ProfileList from 'ProfileList'

export default {
  components: {
    FullScreenModal,
    ProfileList,
    CreateRoomInvite,
  },
  mixins: [inviteCreateMixin],
  props: {
    maxSelect: {
      type: Number,
    },
  },
  methods: {
    invite() {
      this.$emit('invite')
    },
  },
}
</script>

<style></style>
