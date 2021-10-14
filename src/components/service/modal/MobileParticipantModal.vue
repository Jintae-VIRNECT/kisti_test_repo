<template>
  <full-screen-modal
    class="service-mobile-participant-modal"
    :title="$t('workspace.info_remote_member')"
    :visible="visible"
    @close="close"
  >
    <participant-video
      v-for="participant of participants"
      :key="'participant_' + participant.id"
      :participant="participant"
      @selectMain="selectMain(participant)"
    ></participant-video>

    <!-- 참가자 초대 -->
    <article v-if="!openRoom && isLeader && !isMaxLength" key="append">
      <div
        class="participant-video append more"
        @click="showInviteModalWithEventBus"
      >
        <img src="~assets/image/call/ic_addppl.svg" />
        <p>{{ $t('service.participant_invite') }}</p>
      </div>
    </article>
    <!-- 게스트 멤버 초대 -->
    <article v-else-if="openRoom && !isGuest && !isMaxLength" key="append">
      <div class="participant-video append guest" @click="showGuestInviteModal">
        <img src="~assets/image/call/ic_guest_url.svg" />
        <p>{{ $t('service.guest_invite_url') }}</p>
      </div>
    </article>

    <!-- <invite-modal :visible.sync="invite"></invite-modal> -->
    <mobile-select-view
      :visible.sync="selectview"
      :isLeader="isLeader"
      @share="share"
      @normal="normal"
      @mute="mute"
      @kickout="kickout"
    ></mobile-select-view>
  </full-screen-modal>
</template>

<script>
import ParticipantVideo from '../participants/ParticipantVideo'
import FullScreenModal from '../../modules/FullScreenModal'
import MobileSelectView from './MobileSelectView.vue'
import participantListMixin from 'mixins/participantList'
// import InviteModal from '../modal/InviteModal'

export default {
  components: {
    FullScreenModal,
    ParticipantVideo,
    MobileSelectView,
    //InviteModal,
  },
  mixins: [participantListMixin],
  props: {
    visible: {
      type: Boolean,
      dafault: true,
    },
    beforeClose: {
      type: Function,
    },
  },
  data() {
    return {}
  },
  methods: {
    close() {
      this.beforeClose()
    },
    showInviteModalWithEventBus() {
      this.$eventBus.$emit('inviteModal:show', true)
    },
  },
}
</script>

<style lang="scss">
.service-mobile-participant-modal .fullscreen-modal--body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  height: auto;
  padding: 2rem;
  column-gap: 1.2rem;
  row-gap: 3.7rem;
}
</style>
