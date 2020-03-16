<template lang="pug">
  div
    .vn-date-time-select-wrapper
      .vn-date-time-select-wrapper--left
        i.el-icon-arrow-left.arrow-left(@click="onChangeDateArrow(-1)")
        .date-label {{date | dateFilter}}
        i.el-icon-arrow-right.arrow-right(@click="onChangeDateArrow(1)")
    #process-graph-day-by-day(ref='processGraphDayByDay')
</template>
<style lang="scss" scoped>
#process-graph-day-by-day {
  height: 400px;
}
</style>
<script>
import { mapGetters } from 'vuex'
import bb from 'billboard.js'
import dayjs from 'dayjs'

export default {
  data() {
    return {
      lineChart: null,
      date: dayjs().set('day', 1),
    }
  },
  computed: {
    ...mapGetters(['processDailyTotal']),
  },
  filters: {
    dateFilter(val) {
      return dayjs(val).format('YYYY-MM')
    },
  },
  methods: {
    initProcessGraph(arrayData) {
      const daysInMonth = dayjs(this.date).daysInMonth()

      const xAxisTicks = []
      const rateData = []
      for (let i = 1; i <= daysInMonth; i++) {
        xAxisTicks.push(i)
        rateData.push(null)
      }

      arrayData.forEach(data => {
        const day = data.onDay.replace(/.*-/, '') * 1 - 1
        rateData[day] = data.totalRate
      })

      this.lineChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...xAxisTicks],
            ['value', ...rateData],
          ],
          type: 'area-spline',
          color() {
            return '#186AE2'
          },
        },
        axis: {
          x: {
            type: 'category',
            tick: {
              show: false,
              text: {
                show: true,
              },
              format: function(index, val) {
                return val
              },
            },
          },
          y: {
            tick: {
              show: false,
              text: {
                show: true,
              },
            },
            min: 0,
            max: 100,
            default: [0, 100],
            padding: {
              top: 0,
              bottom: 0,
            },
          },
        },
        legend: {
          show: false,
        },
        tooltip: {
          format: {
            title: val => `${this.date.month() + 1} / ${val}`,
            name: () => '진행률',
            value: val => `${val}%`,
          },
        },
        grid: {
          y: {
            show: true,
          },
        },
        bar: {
          width: {
            max: 12,
          },
        },
        size: {
          height: 400,
        },
        padding: {
          left: 40,
          right: 30,
          top: 20,
          bottom: 20,
        },
        bindto: '#process-graph-day-by-day',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
    },
    onChangeDateArrow(val) {
      if (val === 1) this.date = dayjs(this.date).add(1, 'month')
      else this.date = dayjs(this.date).subtract(1, 'month')
      this.onChangeDate()
    },
    async onChangeDate() {
      const month = this.date.format('YYYY-MM')
      await this.$store.dispatch('getProcessDailyTotal', month)
      this.initProcessGraph(this.processDailyTotal)
    },
  },
  mounted() {
    this.onChangeDate()
  },
}
</script>
