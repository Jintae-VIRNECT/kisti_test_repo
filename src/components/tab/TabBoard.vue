<template>
  <section class="tab-board">
    <board-daily :daily="daily" :loading="dayLoading"> </board-daily>
    <board-monthly :monthly="monthly" :loading="monthLoading"> </board-monthly>
  </section>
</template>

<script>
import BoardDaily from 'components/board/section/BoardDaily'
import BoardMonthly from 'components/board/section/BoardMonthly'
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

      day: null,
      month: null,

      dayLoading: false,
      monthLoading: false,
    }
  },
  computed: {
    ...mapGetters(['calendars']),
  },
  watch: {
    async workspace() {
      this.load()
    },
    calendars: {
      handler() {
        if (this.workspace.uuid && this.account.uuid) {
          this.load()
        }
      },
      deep: true,
    },
  },
  methods: {
    async initDaily(refresh) {
      const index = this.calendars.findIndex(cal => cal.name === 'daily')

      if (index < 0) return null
      if (this.calendars[index].status) return null

      if (refresh) {
        await this.getDailyData()
      } else {
        if (this.day !== this.calendars[index].date) {
          this.day = this.calendars[index].date
          await this.getDailyData()
        }
      }
    },
    async getDailyData() {
      this.dayLoading = true
      console.log('this.dayLoading', this.dayLoading)
      const result = await getDailyData({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        date: this.day ? this.day : new Date(),
      })
      this.daily = result

      this.dayLoading = false
      console.log('this.dayLoading', this.dayLoading)
    },
    async initMonthly(refresh) {
      const index = this.calendars.findIndex(cal => cal.name === 'monthly')

      if (index < 0) return null
      if (this.calendars[index].status) return null

      if (refresh) {
        await this.getMonthlyData()
      } else {
        if (this.month !== this.calendars[index].date) {
          this.month = this.calendars[index].date
          await this.getMonthlyData()
        }
      }
    },
    async getMonthlyData() {
      this.monthLoading = true
      const result = await getMonthlyData({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        date: this.month ? this.month : new Date(),
      })
      this.monthly = result
      this.monthLoading = false
    },
    load() {
      this.initDaily()
      this.initMonthly()
    },
    async refresh() {
      const refresh = true
      this.initDaily(refresh)
      this.initMonthly(refresh)
    },
  },

  async mounted() {
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
