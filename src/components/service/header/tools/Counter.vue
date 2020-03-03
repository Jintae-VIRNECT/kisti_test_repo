<template>
  <div class="header-tools__time">
    <!-- <span>{{ callTime | moment('mm:ss') }}</span> -->
    <span>{{ callTime }}</span>
  </div>
</template>

<script>
export default {
  name: 'Counter',
  data() {
    return {
      runnerID: null,
      callStartTime: null,
      callTime: null,
    }
  },
  methods: {
    timeRunner() {
      clearInterval(this.runnerID)
      this.runnerID = setInterval(() => {
        const diff = this.$moment().unix() - this.callStartTime

        const time = this.$moment.utc(
          this.$moment.duration(diff, 'seconds').as('milliseconds'),
        )
        if (time > 60 * 60 * 1000) {
          this.callTime = this.$moment(time).format('hh:mm:ss')
        } else {
          this.callTime = this.$moment(time).format('mm:ss')
        }
      }, 1000)
    },
  },

  /* Lifecycles */
  mounted() {
    this.callStartTime = this.currentTime = this.$moment().unix()
    this.timeRunner()
  },
  beforeDestroy() {
    this.callStartTime = this.currentTime = null
    clearInterval(this.runnerID)
  },
}
</script>
