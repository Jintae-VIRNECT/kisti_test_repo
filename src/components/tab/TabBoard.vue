<template>
  <section class="tab-board">
    <board-daily :daily="daily"> </board-daily>
    <board-monthly :monthly="monthly"> </board-monthly>
  </section>
</template>

<script>
import BoardDaily from 'components/section/BoardDaily'
import BoardMonthly from 'components/section/BoardMonthly'
import { getDailyData, getMonthlyData } from 'utils/chartDatas'
import { mapGetters } from 'vuex'
export default {
  name: 'TabBoard',
  components: {
    BoardDaily,
    BoardMonthly,
  },
  data() {
    return {
      daily: null,
      monthly: null,
      now: new Date(),
    }
  },
  computed: {
    ...mapGetters(['calendars']),
  },
  watch: {
    async workspace() {
      await this.getDailyData()
      await this.getMonthlyData()
    },
    calendars: {
      handler() {
        if (this.workspace.uuid && this.account.uuid) {
          this.initDaily()
          this.initMonthly()
        }
      },
      deep: true,
    },
  },
  methods: {
    async initDaily() {
      const index = this.calendars.findIndex(cal => cal.name === 'daily')
      if (index < 0) return null
      if (this.calendars[index].status) return null

      await this.getDailyData(this.calendars[index].date)
    },
    async getDailyData(day) {
      const result = await getDailyData({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        date: day ? day : new Date(),
      })
      this.daily = result
    },
    async initMonthly() {
      const index = this.calendars.findIndex(cal => cal.name === 'monthly')
      if (index < 0) return null
      if (this.calendars[index].status) return null

      await this.getMonthlyData(this.calendars[index].date)
    },
    async getMonthlyData(month) {
      const result = await getMonthlyData({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        date: month ? month : new Date(),
      })
      this.monthly = result
    },
    async refresh() {
      this.initDaily()
      this.initMonthly()
    },
  },

  async mounted() {
    console.log('mounted')
    this.$eventBus.$on('refresh:chart', this.refresh)
  },
  beforeDestroy() {
    this.$eventBus.$off('refresh:chart')
  },
}
</script>

<style lang="scss">
.tab-board {
  margin-top: 2px;
}
</style>
