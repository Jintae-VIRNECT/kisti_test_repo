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
      options: this.getOptionMonthly(custom),
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

<style lang="scss"></style>
