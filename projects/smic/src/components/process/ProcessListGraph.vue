<template lang="pug">
  div
    .vn-date-time-select-wrapper
      .vn-date-time-select-wrapper--left
        i.el-icon-arrow-left.arrow-left(@click="onChangeDateArrow(-1)")
        .date-label {{date | dateFilter}}
        i.el-icon-arrow-right.arrow-right(@click="onChangeDateArrow(1)")
    #process-graph-day-by-day
</template>
<script>
import bb from 'billboard.js'
import dayjs from 'dayjs'

export default {
  data() {
    return {
      currentDay: dayjs().format('YYYY-MM-DD'),
      xAxisTicks: [],
      lineChart: null,
      graphData: [19, 21, 24, 36, 39, 41],
      date: dayjs(),
    }
  },
  mounted() {
    const daysInMonth = dayjs().daysInMonth()
    for (let i = 1; i <= daysInMonth; i++) {
      this.xAxisTicks.push(i)
    }

    const data = this.xAxisTicks.map(x =>
      this.graphData[x] === undefined ? null : this.graphData[x],
    )

    this.lineChart = bb.generate({
      data: {
        x: 'x',
        columns: [
          ['x', ...this.xAxisTicks],
          ['value', ...data],
        ],
        type: 'area-spline',
        color() {
          return '#186AE2'
        },
      },
      axis: {
        min: {
          y: 0,
        },
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
        },
      },
      legend: {
        show: false,
      },
      tooltip: {
        format: {
          name: () => '건수',
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
  methods: {
    onChangeDateArrow(val) {
      if (val === 1)
        this.date = dayjs(this.date)
          .add(1, 'month')
          .toString()
      else
        this.date = dayjs(this.date)
          .subtract(1, 'month')
          .toString()
      this.onChangeDate()
    },
  },
  filters: {
    dateFilter(val) {
      return dayjs(val).format('YYYY-MM-DD')
    },
  },
}
</script>
