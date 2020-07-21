<template>
  <div class="header-tools__calltime">
    <span>{{ callTime | timeFilter }}</span>
  </div>
</template>

<script>
export default {
  name: 'CallTime',
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
        const diff = this.$dayjs().unix() - this.callStartTime

        this.callTime = this.$dayjs.duration(diff, 'seconds').as('milliseconds')
      }, 1000)
    },
  },

  /* Lifecycles */
  mounted() {
    this.callStartTime = this.$dayjs().unix()
    this.timeRunner()
  },
  beforeDestroy() {
    this.callStartTime = null
    clearInterval(this.runnerID)
  },
}
</script>
