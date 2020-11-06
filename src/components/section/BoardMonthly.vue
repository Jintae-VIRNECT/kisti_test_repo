<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">{{
          $t('chart.monthly_collabo_title')
        }}</span>
        <span class="board__header--description">
          {{ $t('chart.monthly_collabo_description') }}
        </span>
        <datepicker
          class="board__header--datepicker"
          :pickerName="'monthly'"
          :minimumView="'month'"
          :maximumView="'month'"
          :format="'yyyy-MM'"
          :initValue="today"
        ></datepicker>
      </div>
      <div class="chart-legend">
        <chart-legend
          :text="$t('chart.my_collabo_list')"
          shape="square"
        ></chart-legend>
        <chart-legend
          :text="$t('chart.total_collabo_list')"
          shape="square"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder" :class="{ loading: loading }">
        <canvas id="chart-month" width="1250" height="250"></canvas>
      </div>
    </card>
    <div class="board-figures">
      <figure-board
        :header="$t('chart.monthly_total_collabo_count')"
        :count="48"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_my_collabo_time')"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_my_collabo_count')"
        :onlyMe="true"
        :count="monthly ? monthly.my.count : 0"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_my_collabo_time')"
        :onlyMe="true"
        :time="monthly ? monthly.my.time : 0"
        :imgSrc="require('assets/image/figure/ic_figure_date_time.svg')"
      ></figure-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import Datepicker from 'Datepicker'
import Chart from 'chart.js'
import ChartLegend from 'Legend'
import FigureBoard from 'FigureBoard'

import chartMixin from 'mixins/chart'

export default {
  name: 'BoardMonthly',
  mixins: [chartMixin],
  data() {
    return {
      monthlyChart: null,
      today: new Date(),
    }
  },
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    Datepicker,
  },
  props: {
    monthly: {
      type: Object, //my,total - count, time, set
      default: () => {
        return {}
      },
      require: true,
    },
    loading: {
      type: Boolean,
    },
  },

  watch: {
    monthly: {
      handler(data) {
        if (this.monthlyChart) {
          this.monthlyChart.data.datasets[0].data = data.my.set
          this.monthlyChart.update()
        }
      },
    },
    deep: true,
  },

  mounted() {
    console.log('board monthly mounted')
    const ctx = document.getElementById('chart-month').getContext('2d')

    this.setRoundedBar()
    const custom = this.customTooltips('chart-month', 'chartjs-noarrow')

    const chartData = {
      type: 'roundedBar',
      data: {
        labels: this.getMonth(),
        datasets: [
          {
            label: this.$t('chart.my_collabo_list'),
            data: this.monthly ? this.monthly.my.set : [],
            backgroundColor: '#0f75f5',
            barThickness: 10,
          },
          {
            label: this.$t('chart.total_collabo_list'),
            data: [
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
              12,
            ],
            backgroundColor: '#bbc8d9',
            barThickness: 10,
          },
        ],
      },
      options: {
        maintainAspectRatio: false,
        aspectRatio: 5,
        barRoundness: 1.2,
        hover: {
          mode: 'index',
        },
        tooltips: {
          mode: 'index',
          position: 'average',
          enabled: false,
          custom: custom,
          titleFontSize: '15rem',
          bodyFontSize: '14rem',
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: () => {
              return this.$t('chart.collabo_count_by_day')
            },
            label: tooltipItem => {
              return this.$t('chart.count', {
                count: Number(tooltipItem.yLabel),
              })
            },
          },
        },
        legend: {
          display: false,
        },
        scales: {
          xAxes: [
            {
              stacked: true,
              gridLines: {
                display: false,
              },
            },
          ],
          yAxes: [
            {
              stacked: true,
              gridLines: {
                borderDash: [1, 2],
              },
            },
          ],
        },
      },
    }

    this.monthlyChart = new Chart(ctx, chartData)
  },
  methods: {
    //temp
    getMonth() {
      const dayList = []
      for (let i = 1; i <= 31; i++) {
        dayList.push(i + this.$t('chart.collabo_day'))
      }
      return dayList
    },
  },
}
</script>

<style lang="scss">
#chartjs-noarrow {
  position: absolute;
  width: 9.1429rem;
  height: 6.4286rem;
  color: white;
  background: #3b3b3b;
  border: 1px solid rgb(52, 65, 81);
  border-radius: 4px;
  -webkit-transform: translate(-50%, 0);
  transform: translate(-50%, 0);
  opacity: 1;
  -webkit-transition: all 0.1s ease;
  transition: all 0.1s ease;
  pointer-events: none;

  .chartjs-tooltip-head {
    color: white;
    font-weight: 400;
    font-size: 1.0714rem;
    text-align: left;

    p {
      padding-left: 0.4286rem;
    }
  }

  table {
    position: relative;
    width: 100%;
  }

  .tooil-tip-legend {
    width: 0.7143rem;
    height: 0.7143rem;
    margin-right: 0.3571rem;
    margin-left: 0.3571rem;
    border-radius: 50%;
  }
}
.chart-legend {
  display: flex;
  align-items: center;
  height: 4.7143rem;
  padding-left: 2.1429rem;

  & > .legend.square.grey {
    &::before {
      background-color: #bbc8d9;
    }
  }
}

.chart-holder {
  color: transparent;
  &.loading {
    &:before {
      position: absolute;
      top: 0px;
      left: 0px;
      width: 100%;
      height: 100%;
      background-color: #ffffff;
      content: '';
    }

    &:after {
      position: absolute;
      top: 50%;
      left: 50%;
      width: 5.7143rem;
      height: 5.7143rem;
      background: center center/40px 40px no-repeat
        url(~assets/image/loading.gif);
      transform: translate(-50%, -50%);
      content: '';
    }
  }
}
</style>
