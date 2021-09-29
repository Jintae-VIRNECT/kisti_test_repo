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

export default {
  components: {
    FullScreenModal,
    ParticipantVideo,
    MobileSelectView,
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
  },
}
</script>

<style></style>
