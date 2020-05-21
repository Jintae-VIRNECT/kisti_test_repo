<template>
  <div class="participants" id="video-list">
    <vue2-scrollbar ref="sessionListScrollbar" :reverseAxios="true">
      <div class="participants__view">
        <participant-video
          v-for="session of sessions"
          :key="session.nodeId"
          :session="session"
        ></participant-video>
        <article>
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
export default {
  name: 'ParticipantList',
  components: {
    ParticipantVideo,
  },
  data() {
    return {
      sessions: [
        {
          uuid: null,
          stream: null,
          nickName: '이름',
          sessionName: '세션이름',
          path: 'default',
          status: 'good',
        },
        {
          uuid: 2,
          stream: null,
          nickName: '프로필사진',
          sessionName: 'haha',
          path:
            'https://virnect.com/images/pages/landing/logo-main-bi-remote.png',
          status: 'normal',
        },
        {
          uuid: 3,
          stream: null,
          nickName: '프로필사진2',
          sessionName: 'haha2',
          path:
            'https://virnect.com/images/pages/landing/logo-main-bi-remote.png',
          status: 'bad',
        },
      ],
    }
  },
  computed: {
    ...mapGetters(['mainSession']),
  },
  watch: {
    'sessions.length': {
      deep: false,
      handler(newVal, oldVal) {
        if (newVal > oldVal) {
          this.$nextTick(() => {
            if (this.$refs['sessionListScrollbar']) {
              this.$refs['sessionListScrollbar'].scrollToX(999999999)
            }
          })
        } else if (newVal < oldVal) {
          let idx = this.sessions.findIndex(
            session => session.nodeId === this.mainSession.nodeId,
          )
          if (idx < 0) {
            this.setMainSession(this.sessions[0])
          }
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
    ...mapActions(['setMainSession']),
    more() {
      console.log('추가 초대하기')
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
