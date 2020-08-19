<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <transition-group name="list" tag="div" class="participants__view">
        <!-- <div class="participants__view"></div> -->
        <participant-video
          v-for="participant of participants"
          :key="participant.id"
          :participant="participant"
          @selectMain="selectMain(participant)"
        ></participant-video>
        <article v-if="showInvite" key="append">
          <div class="participant-video more" @click="more">
            <p>{{ $t('service.participant_invite') }}</p>
          </div>
        </article>
      </transition-group>
    </vue2-scrollbar>
    <invite-modal :visible.sync="invite" :maxSelect="max"></invite-modal>
    <select-view
      :visible.sync="selectview"
      @share="share"
      @normal="normal"
    ></select-view>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { maxParticipants } from 'utils/callOptions'
import { ROLE } from 'configs/remote.config'

import ParticipantVideo from './ParticipantVideo'
import InviteModal from '../modal/InviteModal'
import SelectView from '../modal/SelectView'
export default {
  name: 'ParticipantList',
  components: {
    ParticipantVideo,
    InviteModal,
    SelectView,
  },
  data() {
    return {
      selectview: false,
      invite: false,
    }
  },
  computed: {
    ...mapGetters(['participants', 'roomMember', 'mainView', 'viewForce']),
    showInvite() {
      if (this.account.roleType === ROLE.LEADER) {
        return true
      } else {
        return false
      }
    },
    max() {
      return maxParticipants - this.roomMember.length
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
          // let idx = this.participants.findIndex(
          //   session => session.uuid === this.mainView.uuid,
          // )
          // if (idx < 0) {
          //   this.setMainSession(this.sessions[0])
          // }
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
    ...mapActions(['setMainView', 'addChat']),
    selectMain(participant) {
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
      if (this.account.roleType === ROLE.LEADER) {
        if (select.id === this.mainView.id && this.viewForce === force) return
        if (force) {
          this.addChat({
            name: select.nickname,
            status: 'sharing-start',
            type: 'system',
          })
        } else {
          if (this.viewForce === true) {
            this.addChat({
              name: this.mainView.nickname,
              status: 'sharing-stop',
              type: 'system',
            })
          }
        }
      }
      if (this.viewForce === true) {
        this.$call.mainview(select.id, force)
      }
      this.setMainView({ id: select.id, force })
    },
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
