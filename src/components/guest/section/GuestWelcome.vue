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

export default {
  name: 'GuestWelcome',
  mixins: [roomMixin],
  components: {},
  data() {
    return {
      remainTime: 120,
      timerId: null,
    }
  },

  computed: {
    welcomeText() {
      return `<em>[${this.account.nickname}]</em> ${this.$t(
        'guest.guest_join_description_1',
      )}`
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
    async joinAsGuest() {
      if (this.remainTime <= 0) {
        //멤버 재할당
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
    timer() {
      this.timerId = setInterval(() => {
        this.remainTime--
      }, 1000)
    },
  },
  mounted() {
    this.timer()
  },
}
</script>

<style></style>
