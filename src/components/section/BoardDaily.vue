<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">{{
          $t('chart.daily_collabo_title')
        }}</span>
        <span class="board__header--description">
          {{ $t('chart.daily_collabo_description') }}
        </span>
        <datepicker
          class="board__header--datepicker"
          :pickerName="'daily'"
          :minimumView="'day'"
          :maximumView="'day'"
          :initValue="today"
        ></datepicker>
      </div>

      <div class="chart-legend">
        <chart-legend
          :text="$t('chart.my_collabo_list')"
          shape="round"
        ></chart-legend>
        <chart-legend
          :text="$t('chart.total_collabo_list')"
          shape="round"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder" :class="{ loading: loading }">
        <canvas id="chart-dayily" width="1250" height="250"></canvas>
      </div>
    </card>

    <div class="board-figures">
      <figure-board
        :header="$t('chart.daily_my_collabo_count')"
        :count="9999"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_total_collabo_time')"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_my_collabo_count')"
        :onlyMe="true"
        :count="daily ? daily.my.count : 0"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_my_collabo_time')"
        :onlyMe="true"
        :time="daily ? daily.my.time : 0"
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

import { hourLabels } from 'utils/chartDatas'
import chartMixin from 'mixins/chart'

export default {
  name: 'BoardDaily',
  mixins: [chartMixin],
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    Datepicker,
  },
  data() {
    return {
      //dummy datas
      totalColaboDatas: [],
      dailyChart: null,
      today: new Date(),
    }
  },
  props: {
    daily: {
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
    daily: {
      handler(data) {
        if (this.dailyChart) {
          this.dailyChart.data.datasets[0].data = data.my.set
          this.dailyChart.update()
        }
      },
      deep: true,
    },
    loading: {
      handler() {
        // if (this.loading === false) {
        //   console.log('init chart')
        //   setTimeout(() => {
        //     this.initChart()
        //   }, 1000)
        // } else {
        //   console.log('clearing')
        //   if (this.dailyChart) {
        //     this.dailyChart.destroy()
        //   }
        // }
      },
    },
  },
  methods: {
    getDummyDataTotal() {
      return [
        0,
        2,
        4,
        5,
        10,
        8,
        2,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        10,
        10,
        9,
        0,
        0,
        0,
        0,
        0,
      ]
    },
    initChart() {
      this.$nextTick(() => {
        const ctx = document.getElementById('chart-dayily').getContext('2d')

        this.totalColaboDatas = this.getDummyDataTotal()

        const custom = this.customTooltips(
          'chart-dayily',
          'chartjs-tooltip',
          'inner',
        )
        this.dailyChart = new Chart(ctx, {
          type: 'line',
          data: {
            labels: hourLabels,
            datasets: [
              {
                label: this.$t('chart.my_collabo_list'),
                data: this.daily ? this.daily.my.set : [],
                borderColor: '#0f75f5',
                borderWidth: 4,
                pointRadius: 0,
                pointBackgroundColor: '#ffffff',
                borderJoinStyle: 'bevel',
                lineTension: 0,
                fill: false,
                hoverBorderWidth: 4,
              },
              {
                label: this.$t('chart.total_collabo_list'),
                data: this.totalColaboDatas,
                borderColor: '#bbc8d9',
                borderWidth: 4,
                pointRadius: 0,
                pointBackgroundColor: '#ffffff',
                borderJoinStyle: 'bevel',
                lineTension: 0,
                fill: false,
                hoverBorderWidth: 4,
              },
            ],
          },
          options: {
            maintainAspectRatio: false,
            aspectRatio: 5,
            hover: {
              mode: 'index',
              intersect: false,
            },
            tooltips: {
              mode: 'index',
              intersect: false,
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
                  return this.$t('chart.collabo_count_by_time')
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
                  gridLines: {
                    display: false,
                  },
                },
              ],
              yAxes: [
                {
                  ticks: {
                    beginAtZero: true,
                    min: 0,
                    stepSize: 4,
                  },
                  gridLines: {
                    borderDash: [1, 2],
                  },
                },
              ],
            },
          },
        })
      })
    },
  },
  mounted() {
    //todo TotalData
    console.log('board daily mounted')
    this.initChart()
  },
}
</script>

<style lang="scss">
#chartjs-tooltip {
  position: absolute;
  width: 9.1429rem;
  height: 5.7143rem;
  // min-height: 40px;
  color: white;
  background: #3b3b3b;
  // border: 1px solid rgb(52, 65, 81);
  border-radius: 4px;
  -webkit-transform: translate(-50%, 0);
  transform: translate(-50%, 0);
  opacity: 1;
  -webkit-transition: all 0.1s ease;
  transition: all 0.1s ease;
  pointer-events: none;

  table {
    position: relative;
    width: 100%;

    thead {
      color: rgb(255, 255, 255);
      font-weight: 400;
      font-size: 1.0714rem;
    }

    &::after {
      position: absolute;
      top: 5.2143rem;
      right: 3.5714rem;
      z-index: 999;
      width: 0;
      height: 0;
      border-top: 9px solid #3b3b3b;
      border-right: 9px solid transparent;
      border-left: 9px solid transparent;
      content: '';
    }
  }

  .tooil-tip-legend {
    width: 0.7143rem;
    height: 0.7143rem;
    margin-right: 0.3571rem;
    margin-left: 0.3571rem;
  }
}

.chart-legend {
  display: flex;
  align-items: center;
  height: 4.8571rem;
  padding-left: 2.1429rem;

  & > .legend.round.grey {
    &::before {
      border: 4px solid #bbc8d9;
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
