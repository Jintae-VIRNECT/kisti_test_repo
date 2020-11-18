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

      <figcaption class="chart-legend">
        <chart-legend
          @click="toggle(dailyChart, 'my')"
          :text="$t('chart.my_collabo_list')"
          shape="round"
        ></chart-legend>
        <chart-legend
          @click="toggle(dailyChart, 'total')"
          :text="$t('chart.total_collabo_list')"
          shape="round"
          customClass="grey"
        ></chart-legend>
      </figcaption>
      <div class="chart-holder" :class="{ loading: loading }">
        <canvas id="chart-dayily" width="1250" height="250"></canvas>
      </div>
    </card>

    <div class="board-figures">
      <figure-board
        :header="$t('chart.daily_total_collabo_count')"
        :count="daily ? daily.total.count : 0"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_total_collabo_time')"
        :time="daily ? daily.total.time : 0"
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
          this.dailyChart.data.datasets[1].data = data.total.set
          this.dailyChart.update()
        }
      },
      deep: true,
    },
  },
  methods: {
    initChart() {
      this.$nextTick(() => {
        const ctx = document.getElementById('chart-dayily').getContext('2d')

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
                data: this.daily ? this.daily.total.set : [],
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
          options: this.getOptionDaily(custom),
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
