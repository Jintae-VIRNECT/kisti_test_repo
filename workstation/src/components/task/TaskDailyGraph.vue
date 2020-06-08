<template>
  <div id="task-daily-graph">
    <div class="select-month">
      <i class="el-icon-arrow-left" @click="prevMonth" />
      <el-date-picker v-model="date" type="month" />
      <i class="el-icon-arrow-right" @click="nextMonth" />
    </div>
    <div id="task-daily-graph-chart" />
  </div>
</template>

<script>
import dayjs from '@/plugins/dayjs'
import taskService from '@/services/task'

// ssr error
let bb = null
if (process.client) {
  bb = require('billboard.js').bb
}

export default {
  data() {
    return {
      date: null,
    }
  },
  watch: {
    date(val) {
      this.initProcessGraph(dayjs(val))
    },
  },
  methods: {
    prevMonth() {
      this.date = dayjs(this.date).subtract(1, 'month')
    },
    nextMonth() {
      this.date = dayjs(this.date).add(1, 'month')
    },
    async initProcessGraph(date) {
      const arrayData = await taskService.getTaskDailyRateAtMonth(
        date.format('YYYY-MM'),
      )
      const daysInMonth = date.daysInMonth()
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
            title: val => `${date.month() + 1} / ${val + 1}`,
            name: () => this.$t('task.list.rate'),
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
        bindto: '#task-daily-graph-chart',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
    },
  },
  mounted() {
    this.date = new Date()
  },
}
</script>

<style lang="scss">
#task-daily-graph .select-month {
  margin-top: 32px;
  text-align: center;

  & > i {
    display: inline-block;
    color: #6d798b;
    font-weight: bold;
    font-size: 16px;
    vertical-align: middle;
    cursor: pointer;
  }
  .el-date-editor {
    display: inline-block;
    width: 88px;
    vertical-align: middle;
    .el-input__inner {
      padding: 0;
      color: #0d2a58;
      font-weight: 500;
      font-size: 16px;
      line-height: 26px;
      text-align: center;
      border: none;
    }
    .el-input__icon {
      display: none;
    }
  }
}
#__nuxt #task-daily-graph-chart {
  min-height: 400px;
  margin: 0 20px 20px;

  .bb-tooltip {
    th,
    td {
      padding: 2px 5px;
    }
  }
}
</style>
