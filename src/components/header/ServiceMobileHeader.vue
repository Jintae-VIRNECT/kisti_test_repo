<template>
  <div class="mobile-header-service">
    <div class="mobile-header-info">
      <h1>{{ roomInfo.title }}</h1>
      <h1>{{ `${min}:${sec}` }}</h1>
    </div>

    <div class="mobile-header-tools">
      <chat :tooltipActive="false"></chat>
      <button class="mobile-header-tools__leave" @click="leave"></button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Chat from './tools/Chat'
import { ROLE } from 'configs/remote.config'

export default {
  components: {
    Chat,
  },
  data() {
    return {
      timer: null,
      time: 0,
      min: '00',
      sec: '00',
    }
  },
  computed: {
    ...mapGetters(['roomInfo']),
  },
  methods: {
    //협업 소요시간을 표기하기 위한 타이머 시작
    startTimer() {
      this.timer = setInterval(() => {
        this.time++

        //분
        const min = parseInt(this.time / 60)
        this.min = min < 10 ? '0' + min : min

        //초
        const sec = this.time % 60
        this.sec = sec < 10 ? '0' + sec : sec
      }, 1000)
    },
    //pc인 경우 HeaderServiceTools에서 처리된다(동일함수)
    // this.$call.leave() 는 ServiceLayout.vue의 beforeRouteLeave에서 처리된다
    leave() {
      try {
        // this.$call.leave()

        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace', params: 'leave' })
        }
      } catch (err) {
        if (this.account.roleType === ROLE.GUEST) {
          window.history.back()
        } else {
          this.$router.push({ name: 'workspace' })
        }
      }
    },
  },
  mounted() {
    this.startTimer()
    this.$eventBus.$on('call:logout', this.leave)
  },
  beforeDestroy() {
    clearInterval(this.timer)
    this.time = 0
    this.min = 0
    this.sec = 0
    this.$eventBus.$off('call:logout', this.leave)
  },
}
</script>

<style></style>
