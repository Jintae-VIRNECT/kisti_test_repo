<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <transition-group name="list" tag="div" class="participants__view">
        <!-- <div class="participants__view"></div> -->
        <participant-video
          v-for="participant of participants"
          :key="participant.id"
          :participant="participant"
        ></participant-video>
        <article v-if="showInvite" key="append">
          <div class="participant-video more" @click="more">
            <p>추가 초대하기</p>
          </div>
        </article>
      </transition-group>
    </vue2-scrollbar>
    <invite-modal :visible.sync="invite"></invite-modal>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { maxParticipants } from 'utils/callOptions'
import { ROLE } from 'configs/remote.config'

import ParticipantVideo from './ParticipantVideo'
import InviteModal from '../modal/ServiceInviteModal'
export default {
  name: 'ParticipantList',
  components: {
    ParticipantVideo,
    InviteModal,
  },
  data() {
    return {
      max: maxParticipants,
      invite: false,
    }
  },
  computed: {
    ...mapGetters(['participants', 'mainView']),
    showInvite() {
      if (
        this.account.roleType === ROLE.EXPERT_LEADER &&
        this.participants.length < this.max
      ) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    'participants.length': {
      deep: false,
      handler(newVal, oldVal) {
        if (newVal > oldVal) {
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(999999999)
            }
          })
        } else if (newVal < oldVal) {
          // let idx = this.participants.findIndex(
          //   session => session.uuid === this.mainView.uuid,
          // )
          // if (idx < 0) {
          //   this.setMainSession(this.sessions[0])
          // }
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(999999999)
            }
          })
        }
      },
    },
  },
  methods: {
    more() {
      this.invite = !this.invite
    },
  },

  /* Lifecycles */
  mounted() {},
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
