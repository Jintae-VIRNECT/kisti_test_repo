<template>
  <section class="tab-board">
    <board-daily :dailyData="myDaily"> </board-daily>
    <board-monthly :myData="myMonthly"> </board-monthly>
  </section>
</template>

<script>
import BoardDaily from 'components/section/BoardDaily'
import BoardMonthly from 'components/section/BoardMonthly'
import { getMyHistoryData } from 'utils/chartDatas'
import { mapGetters } from 'vuex'
export default {
  name: 'TabBoard',
  components: {
    BoardDaily,
    BoardMonthly,
  },
  data() {
    return {
      myDaily: null,
      myMonthly: null,
    }
  },
  computed: {
    ...mapGetters(['calendars']),
    dayCalendar() {
      const index = this.calendars.findIndex(cal => cal.name === 'daily')
      if (index < 0) return {}
      return this.calendars[index]
    },
  },
  watch: {
    async workspace(val) {
      console.log('workspace changed::', val)
      await this.getMyData()
    },
    calendars: {
      handler(cal) {
        console.log(cal[0])
        this.initMy()
        this.initTotal()
      },
      deep: true,
    },
  },
  methods: {
    async initMy() {
      const index = this.calendars.findIndex(cal => cal.name === 'daily')
      if (index < 0) return null
      await this.getMyData(this.calendars[index].date)
    },
    async initTotal() {
      console.log('initTotal')
    },
    async getMyData(day) {
      const result = await getMyHistoryData({
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        date: day ? day : new Date(),
      })
      this.myDaily = result.daily
      this.myMonthly = result.monthly
    },
  },

  async mounted() {
    console.log('mounted')
    // this.myData.daily = {}
    // this.myData.monthly = {}
  },
}
</script>

<style lang="scss">
.tab-board {
  margin-top: 2px;
}
</style>
