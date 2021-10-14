<template>
  <full-screen-modal
    class="service-mobile-inviteroom-modal"
    :title="$t('service.invite_title')"
    :visible="visible"
    @close="close"
  >
    <section class="mobile-inviteroom__selected" v-if="userList.length > 0">
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
        v-if="userList.length > 0 && selectedListVisible"
        :users="userList"
        :remove="true"
        :showNickname="true"
        :showStatus="true"
        size="4.3rem"
        @remove="selectUser"
        @kickout="kickout"
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
    currentUser: {
      type: Array,
    },
  },
  data() {
    return {
      selectedListVisible: false,
    }
  },
  watch: {
    userList: {
      handler(newVal, oldVal) {
        if (newVal.length !== oldVal.length) this.selectedListVisible = true
      },
    },
  },
  computed: {
    userList() {
      return this.currentUser.concat(this.selection)
    },
  },
  methods: {
    invite() {
      this.$emit('invite')
    },
    toggleSelectedListVisible() {
      if (!this.selectedListVisible && this.userList.length === 0) return
      this.selectedListVisible = !this.selectedListVisible
      this.$eventBus.$emit('scrollHeightReset')
    },
    kickout({ participant, idx }) {
      this.$emit('kickout', { participant, idx })
    },
  },
}
</script>

<style></style>
