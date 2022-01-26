<template>
  <full-screen-modal
    class="service-mobile-inviteroom-modal"
    :title="$t('service.invite_title')"
    :visible="visible"
    @close="close"
  >
    <section class="mobile-inviteroom__selected" v-if="currentUser.length > 0">
      <div class="selected-header">
        <h1>{{ $t('service.invite_unconnected_list') }}</h1>
        <button
          class="user-list-toggle-btn"
          :class="{
            off: !selectedListVisible,
          }"
          @click="toggleSelectedListVisible"
        ></button>
      </div>
      <profile-list
        v-if="currentUser.length > 0 && selectedListVisible"
        :users="currentUser"
        :remove="true"
        :showNickname="true"
        :showStatus="true"
        size="4.3rem"
        @remove="selectUser"
        @kickout="kickout"
      ></profile-list>
    </section>

    <create-room-invite
      :maxSelect="maxSelect"
      :users="users"
      :subGroups="subGroups"
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
  name: 'MobileInviteModal',
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
      default: () => [],
    },
    subGroups: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      selectedListVisible: false,
    }
  },
  watch: {
    currentUser: {
      handler(newVal, oldVal) {
        if (newVal.length !== oldVal.length) this.selectedListVisible = true
      },
    },
  },
  methods: {
    invite() {
      this.$emit('invite')
    },
    toggleSelectedListVisible() {
      if (!this.selectedListVisible && this.currentUser.length === 0) return
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
