<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">일일 협업</span>
        <span class="board__header--description">
          설정한 날짜의 일일 협업 내용을 보여줍니다.
        </span>
        <datepicker
          class="board__header--datepicker"
          :pickerName="'daily'"
          :minimumView="'day'"
          :maximumView="'day'"
        ></datepicker>
      </div>
      <div class="chart-legend">
        <chart-legend text="개인 협업 내역" shape="round"></chart-legend>
        <chart-legend
          text="전체 협업 내역"
          shape="round"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder">
        <canvas id="chart-dayily" width="1145" height="230"></canvas></div
    ></card>
    <div class="board-figures">
      <figure-board
        header="전체 일별 협업 수"
        :count="9999"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        header="전체 일별 협업 시간"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        header="나의 일별 협업 수"
        :onlyMe="true"
        :count="dailyData ? dailyData.count : 0"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        header="나의 일별 협업 시간"
        :onlyMe="true"
        :time="dailyData ? dailyData.time : 0"
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
      privateCollaboDatas: [],
      totalColaboDatas: [],

      dailyChart: null,
    }
  },
  props: {
    dailyData: {
      type: Object, //count, time, set
      default: () => {
        return {}
      },
      require: true,
    },
  },
  watch: {
    dailyData: {
      handler(data) {
        if (this.dailyChart) {
          this.dailyChart.data.datasets[0].data = data.set
          this.dailyChart.update()
        }
      },
    },
    deep: true,
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
  },
  mounted() {
    //todo TotalData

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
              label: '개인 협업 내역',
              data: this.dailyData ? this.dailyData.set : [],
              borderColor: '#0f75f5',
              borderWidth: 4,
              pointRadius: 0,
              pointBackgroundColor: '#fff',
              borderJoinStyle: 'bevel',
              lineTension: 0,
              fill: false,
            },
            {
              label: '전체 협업 내역',
              data: this.totalColaboDatas,
              borderColor: '#bbc8d9',
              borderWidth: 4,
              pointRadius: 0,
              pointBackgroundColor: '#fff',
              borderJoinStyle: 'bevel',
              lineTension: 0,
              fill: false,
            },
          ],
        },
        options: {
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
            titleFontSize: '15',
            bodyFontSize: '14',
            displayColors: false,
            backgroundColor: '#516277',
            bodyFontStyle: 'bold',
            callbacks: {
              title: function() {
                return '시간별 완료 협업'
              },
              label: function(tooltipItem) {
                return Number(tooltipItem.yLabel) + '건'
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
}
</script>

<style lang="scss">
#chartjs-tooltip {
  position: absolute;
  width: 128px;
  height: 80px;
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
      font-size: 15px;
    }

    &::after {
      position: absolute;
      top: 73px;
      right: 50px;
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
    width: 10px;
    height: 10px;
    margin-right: 5px;
    margin-left: 5px;
  }
}

.chart-legend {
  display: flex;
  align-items: center;
  height: 68px;
  padding-left: 30px;

  & > .legend.round.grey {
    &::before {
      border: 4px solid #bbc8d9;
    }
  }
}
</style>
