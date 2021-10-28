<template>
  <section class="guest-welcome">
    <div class="guest-welcome__body">
      <p
        v-if="!isExpired"
        class="guest-welcome__nickname"
        v-html="welcomeText"
      ></p>
      <div
        class="guest-welcome__name"
        v-html="
          isExpired
            ? $t('guest.guest_time_expired_description')
            : $t('guest.guest_join_description_2')
        "
      ></div>
      <button class="btn join" @click="joinAsGuest" v-html="joinText"></button>
    </div>
  </section>
</template>
<script>
import { getGuestRoomInfo } from 'api/http/guest'
import roomMixin from 'mixins/room'
import { ROLE } from 'configs/remote.config'
import { ERROR } from 'configs/error.config'
import { URLS } from 'configs/env.config'

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
    isExpired() {
      if (this.remainTime <= 0) return true
      else return false
    },
  },
  watch: {
    remainTime() {
      if (this.remainTime <= 0) {
        clearInterval(this.timerId)
      }
    },
    joinText: {
      immediate: true,
      handler(newVal) {
        this.$emit('joinText', newVal)
      },
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

      try {
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
      } catch (err) {
        if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
          this.confirmDefault(this.$t('workspace.remote_already_removed'), {
            action: () => {
              location.href = `${URLS['console']}/?continue=${location.href}`
            },
          })
        } else {
          console.error(err)
        }
      }
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
