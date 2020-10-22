<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">월별 협업</span>
        <span class="board__header--description">
          설정한 기간의 협업 내용을 보여줍니다.
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
        <chart-legend text="개인 협업 내역" shape="square"></chart-legend>
        <chart-legend
          text="전체 협업 내역"
          shape="square"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder">
        <canvas id="chart-month" width="1145" height="230"></canvas>
      </div>
    </card>
    <div class="board-figures">
      <figure-board
        header="전체 월별 협업 수"
        :count="48"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        header="전체 월별 협업 시간"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        header="나의 월별 협업 수"
        :onlyMe="true"
        :count="monthly ? monthly.my.count : 0"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        header="나의 월별 협업 시간"
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
            label: '개인 협업 내역',
            data: this.monthly ? this.monthly.my.set : [],
            backgroundColor: '#0f75f5',
            barThickness: 10,
          },
          {
            label: '전체 협업 내역',
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
          titleFontSize: '15',
          bodyFontSize: '14',
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: function() {
              return '일별 완료 협업'
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
        dayList.push(i + '일')
      }
      return dayList
    },
  },
}
</script>

<style lang="scss">
#chartjs-noarrow {
  position: absolute;
  width: 128px;
  height: 80px;
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

  table {
    position: relative;
    width: 100%;
  }

  .tooil-tip-legend {
    width: 10px;
    height: 10px;
    margin-right: 5px;
    margin-left: 5px;
    border-radius: 50%;
  }
}
.chart-legend {
  display: flex;
  align-items: center;
  height: 68px;
  padding-left: 30px;

  & > .legend.square.grey {
    &::before {
      background-color: #bbc8d9;
    }
  }
}
</style>
