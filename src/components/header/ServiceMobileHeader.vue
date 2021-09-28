<template>
  <div class="mobile-header-service">
    <div class="mobile-header-info">
      <h1>{{ roomInfo.title }}</h1>
      <h1>{{ `${min}:${sec}` }}</h1>
    </div>

    <div class="mobile-header-tools">
      <chat></chat>
      <button class="mobile-header-tools__leave" @click.once="leave"></button>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Chat from './tools/Chat'

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
    startTimer() {
      this.timer = setInterval(() => {
        this.time++
        const min = parseInt(this.time / 60)
        this.min = min < 10 ? '0' + min : min
        const sec = this.time % 60
        this.sec = sec < 10 ? '0' + sec : sec
      }, 1000)
    },
    leave() {
      try {
        this.$call.leave()
        this.$router.push({ name: 'workspace' })
      } catch (err) {
        this.$router.push({ name: 'workspace' })
      }
    },
  },
  mounted() {
    this.startTimer()
  },
  beforeDestroy() {
    clearInterval(this.timer)
    this.time = 0
    this.min = 0
    this.sec = 0
  },
}
</script>

<style></style>
