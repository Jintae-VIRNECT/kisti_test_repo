<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <transition-group name="list" tag="div" class="participants__view">
        <camera-control
          v-if="restrictedRoom && isLeader"
          key="controlBtn"
        ></camera-control>
        <participant-video
          v-for="participant of participants"
          :key="'participant_' + participant.id"
          :participant="participant"
          @selectMain="selectMain(participant)"
          @kickout="kickout(participant.id)"
          @mute="mute"
        ></participant-video>
        <article v-if="!openRoom && isLeader && !isMaxLength" key="append">
          <div class="participant-video append more" @click="openInviteModal">
            <img src="~assets/image/call/ic_addppl.svg" />
            <p>{{ $t('service.participant_invite') }}</p>
          </div>
        </article>
        <article v-else-if="openRoom && !isGuest && !isMaxLength" key="append">
          <div
            class="participant-video append guest"
            @click="showGuestInviteModal"
          >
            <img src="~assets/image/call/ic_guest_url.svg" />
            <p>{{ $t('service.guest_invite_url') }}</p>
          </div>
        </article>
      </transition-group>
    </vue2-scrollbar>
    <select-view
      :visible.sync="selectview"
      @share="share"
      @normal="normal"
    ></select-view>
  </div>
</template>

<script>
import ParticipantVideo from './ParticipantVideo'
import SelectView from '../modal/SelectView'
import CameraControl from './CameraControl'
import participantListMixin from 'mixins/participantList'

export default {
  name: 'ParticipantList',
  mixins: [participantListMixin],
  components: {
    ParticipantVideo,
    SelectView,
    CameraControl,
  },
  watch: {
    'participants.length': {
      deep: false,
      handler(newVal, oldVal) {
        if (newVal > oldVal) {
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(
                Number.MAX_SAFE_INTEGER,
              )
            }
          })
        } else if (newVal < oldVal) {
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(
                Number.MAX_SAFE_INTEGER,
              )
            }
          })
        }
      },
    },
  },
  methods: {
    openInviteModal() {
      this.$emit('openInviteModal', true)
    },
  },
}
</script>
<style>
.list-enter-active,
.list-leave-active {
  transition: all 1s;
}
.list-enter,
.list-leave-to {
  /* transform: translateX(11.428rem); */
  opacity: 0;
}
</style>
