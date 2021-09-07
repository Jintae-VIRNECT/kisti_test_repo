<template>
  <section class="guest-welcome">
    <div class="guest-welcome__body">
      <p class="guest-welcome__nickname" v-html="welcomeText"></p>
      <div class="guest-welcome__name">
        {{ $t('guest.guest_join_description_2') }}
      </div>
      <button class="btn join" @click="joinAsGuest" v-html="joinText"></button>
    </div>
  </section>
</template>
<script>
import { getGuestRoomInfo } from 'api/http/guest'
import roomMixin from 'mixins/room'
import { ROLE } from 'configs/remote.config'

const EXPIRE_TIMER = 120 //120초

export default {
  name: 'GuestWelcome',
  mixins: [roomMixin],
  components: {},
  data() {
    return {
      remainTime: EXPIRE_TIMER,
      timerId: null,
    }
  },

  computed: {
    welcomeText() {
      if (this.account.nickname) {
        return `<em>[${this.account.nickname}]</em> ${this.$t(
          'guest.guest_join_description_1',
        )}`
      } else {
        return ''
      }
    },
    joinText() {
      if (this.remainTime <= 0) {
        return this.$t('button.request_again')
      } else {
        return this.$t('button.join_sec', { time: this.remainTime })
      }
    },
  },
  watch: {
    remainTime() {
      if (this.remainTime <= 0) {
        clearInterval(this.timerId)
      }
    },
  },
  methods: {
    initGuestMember() {
      this.$eventBus.$emit('initGuestMember')
    },
    resetTimer() {
      this.remainTime = EXPIRE_TIMER
    },
    async joinAsGuest() {
      if (this.remainTime <= 0) {
        this.resetTimer()
        this.initGuestMember()
        this.startTimer()
        return
      }

      const sessionId = this.$route.query.sessionId

      const roomInfo = await getGuestRoomInfo({
        sessionId: sessionId,
        workspaceId: this.workspace.uuid,
      })

      const leader = roomInfo.memberList.find(member => {
        member.memberType === ROLE.LEADER
      })

      await this.join({
        ...roomInfo,
        leaderId: leader ? leader.uuid : null,
        isGuest: true,
      })
    },
    startTimer() {
      this.timerId = setInterval(() => {
        this.remainTime--
      }, 1000)
    },
  },
  mounted() {
    this.startTimer()
  },
}
</script>

<style></style>
