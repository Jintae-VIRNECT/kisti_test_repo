<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <div class="participants__view">
        <participant-video
          v-for="participant of participants"
          :key="participant.id"
          :participant="participant"
        ></participant-video>
        <article v-if="participants.length < max">
          <div class="participant-video more" @click="more">
            <p>추가 초대하기</p>
          </div>
        </article>
      </div>
    </vue2-scrollbar>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import ParticipantVideo from './ParticipantVideo'
import { maxParticipants } from 'utils/callOptions'
export default {
  name: 'ParticipantList',
  components: {
    ParticipantVideo,
  },
  data() {
    return {
      max: maxParticipants,
      // participants: [
      //   {
      //     uuid: 'main',
      //     email: 'wooka@virnect.com',
      //     name: 'wooka',
      //     nickname: 'wuwuwuwu',
      //     device: 'web',
      //     role: 'MASTER',
      //     path: 'default',
      //     status: 'good',
      //     mic: true,
      //     audio: true,
      //   },
      //   {
      //     uuid: 2,
      //     email: 'wooka@virnect.com',
      //     name: 'wooka',
      //     nickname: 'wuwuwuwu',
      //     device: 'web',
      //     role: 'MASTER',
      //     path: 'default',
      //     status: 'normal',
      //     mic: true,
      //     audio: true,
      //     mute: true,
      //   },
      //   {
      //     uuid: 3,
      //     email: 'wooka@virnect.com',
      //     name: 'wooka',
      //     nickname: 'wuwuwuwuwuwuwuwuwuwuwuwuwuwuwuwu',
      //     device: 'web',
      //     role: 'MASTER',
      //     path: 'https://virnect.com/favicon.ico',
      //     status: 'bad',
      //     mic: false,
      //     audio: false,
      //     mute: true,
      //   },
      // ],
    }
  },
  computed: {
    ...mapGetters(['participants', 'mainView']),
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
    // ...mapActions(['setMainSession']),
    more() {
      console.log('추가 초대하기')
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
