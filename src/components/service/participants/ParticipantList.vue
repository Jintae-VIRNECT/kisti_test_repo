<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <transition-group name="list" tag="div" class="participants__view">
        <camera-control
          v-if="restrictedRoom && isLeader"
          key="controlBtn"
        ></camera-control>
        <!-- <div class="participants__view"></div> -->
        <participant-video
          v-for="participant of participants"
          :key="'participant_' + participant.id"
          :participant="participant"
          @selectMain="selectMain(participant)"
          @kickout="kickout(participant.id)"
        ></participant-video>
        <article v-if="!openRoom && isLeader && !isMaxLength" key="append">
          <div class="participant-video append more" @click="more">
            <p>{{ $t('service.participant_invite') }}</p>
          </div>
        </article>
        <article v-else-if="openRoom && isLeader && !isMaxLength" key="append">
          <div
            class="participant-video append guest"
            @click="showGuestInviteModal"
          >
            <p>{{ $t('service.guest_invite_url') }}</p>
          </div>
        </article>
      </transition-group>
    </vue2-scrollbar>
    <invite-modal :visible.sync="invite"></invite-modal>
    <select-view
      :visible.sync="selectview"
      @share="share"
      @normal="normal"
    ></select-view>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { SIGNAL, VIDEO, ROLE } from 'configs/remote.config'
import { kickoutMember } from 'api/http/member'
import { maxParticipants } from 'utils/callOptions'

import ParticipantVideo from './ParticipantVideo'
import InviteModal from '../modal/InviteModal'
import SelectView from '../modal/SelectView'
import CameraControl from './CameraControl'
export default {
  name: 'ParticipantList',
  components: {
    ParticipantVideo,
    InviteModal,
    SelectView,
    CameraControl,
  },
  data() {
    return {
      selectview: false,
      invite: false,
      force: null,
    }
  },
  computed: {
    ...mapGetters([
      'participants',
      'mainView',
      'viewForce',
      'roomInfo',
      'openRoom',
      'allowCameraControl',
      'restrictedRoom',
    ]),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isMaxLength() {
      return this.participants.length === maxParticipants
    },
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
    ...mapActions(['setMainView', 'addChat', 'removeMember']),
    selectMain(participant) {
      if (this.restrictedRoom) {
        this.$call.sendVideo(participant.id, true)
        return
      }
      this.selectview = {
        id: participant.id,
        nickname: participant.nickname,
      }
    },
    normal() {
      this.changeMainView(this.selectview, false)
    },
    share() {
      this.changeMainView(this.selectview, true)
    },
    changeMainView(select, force) {
      this.selectview = false
      if (
        this.account.roleType === ROLE.LEADER &&
        select.id === this.mainView.id &&
        this.viewForce === force
      )
        return
      this.$call.sendVideo(select.id, force)
      this.setMainView({ id: select.id, force })
    },
    more() {
      this.invite = !this.invite
    },
    showGuestInviteModal() {
      this.$eventBus.$emit('guestInvite:show', true)
    },
    async kickout(participantId) {
      const params = {
        sessionId: this.roomInfo.sessionId,
        workspaceId: this.workspace.uuid,
        leaderId: this.account.uuid,
        participantId: participantId,
      }
      const rtn = await kickoutMember(params)
      if (rtn.result === true) {
        this.removeMember(participantId)
      }
      // this.$call.disconnect(this.participant.connectionId)
    },
    signalVideo(event) {
      const data = JSON.parse(event.data)
      if (data.type === VIDEO.SHARE) {
        const participant = this.participants.find(user => user.id === data.id)
        this.addChat({
          name: participant.nickname,
          status: this.isLeader ? 'sharing-start-leader' : 'sharing-start',
          type: 'system',
        })
        this.force = data.id
      } else if (data.type === VIDEO.NORMAL) {
        if (this.force) {
          const participant = this.participants.find(
            user => user.id === this.force,
          )
          if (participant) {
            this.addChat({
              name: participant.nickname,
              status: this.isLeader ? 'sharing-stop-leader' : 'sharing-stop',
              type: 'system',
            })
          }
        }
        this.force = null
      }
    },
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.VIDEO, this.signalVideo)
  },
  created() {
    this.$eventBus.$on(SIGNAL.VIDEO, this.signalVideo)
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
