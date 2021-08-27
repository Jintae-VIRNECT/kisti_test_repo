<template>
  <section class="guest-welcome">
    <div class="guest-welcome__body">
      <p class="guest-welcome__nickname" v-html="welcomeText"></p>
      <div class="guest-welcome__name">
        {{ $t('guest.guest_join_description_2') }}
      </div>
      <button class="btn join" @click="join" v-html="joinText"></button>
    </div>
  </section>
</template>
<script>
import { joinOpenRoomAsGuest } from 'api/http/guest'
import roomMixin from 'mixins/room'
export default {
  name: 'GuestWelcome',
  mixins: [roomMixin],
  components: {},
  data() {
    return {
      seatMemberName: '테스트 guest member user',
      remainTime: 120,
      timerId: null,
    }
  },
  props: {
    type: String,
  },
  computed: {
    welcomeText() {
      return `<em>[${this.seatMemberName}]</em> ${this.$t(
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
    async join() {
      // const room = await joinOpenRoomAsGuest({
      //   joinRoomRequest: '',
      //   workspaceId: '',
      //   userId: '',
      // })
      //@TODO : 통화 화면으로 이동하기
      //leader 정보 find
      // const res = await this.join({
      // ...room,
      // leaderId: user ? user.uuid : null,
      // })
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
