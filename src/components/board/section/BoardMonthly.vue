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
      <figcaption class="chart-legend">
        <chart-legend
          @click="toggle(monthlyChart, 'my')"
          :text="$t('chart.my_collabo_list')"
          shape="circle"
        ></chart-legend>
        <chart-legend
          @click="toggle(monthlyChart, 'total')"
          :text="$t('chart.total_collabo_list')"
          shape="circle"
          customClass="grey"
        ></chart-legend>
      </figcaption>
      <div class="chart-holder" :class="{ loading: loading }">
        <canvas :id="chartId" width="1250" height="250"></canvas>
      </div>
    </card>
    <div class="board-figures">
      <figure-board
        :header="$t('chart.monthly_my_collabo_count')"
        :my="true"
        :count="monthly ? monthly.my.count : 0"
        :imgSrc="require('assets/image/figure/ic_chart_monthly_count.svg')"
        type="monthly"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_my_collabo_time')"
        :my="true"
        :time="monthly ? monthly.my.time : 0"
        :imgSrc="require('assets/image/figure/ic_chart_monthly_time.svg')"
        type="monthly"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_total_collabo_count')"
        :count="monthly ? monthly.total.count : 0"
        :imgSrc="
          require('assets/image/figure/ic_chart_monthly_total_count.svg')
        "
        type="monthly"
      ></figure-board>
      <figure-board
        :header="$t('chart.monthly_total_collabo_time')"
        :time="monthly ? monthly.total.time : 0"
        :imgSrc="require('assets/image/figure/ic_chart_monthly_total_time.svg')"
        type="monthly"
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
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    Datepicker,
  },
  props: {
    monthly: {
      type: Object, //my,total - count, time, set
      default: () => {},
      require: true,
    },
    loading: {
      type: Boolean,
    },
  },
  data() {
    return {
      monthlyChart: null,
      today: new Date(),
      chartId: 'chart-month',
    }
  },
  watch: {
    monthly: {
      handler(data) {
        this.updateChart(data)
      },
      deep: true,
    },
  },
  methods: {
    initCart() {
      const ctx = document.getElementById(this.chartId).getContext('2d')
      const chartData = this.initMonthlyChart(this.chartId)
      this.monthlyChart = new Chart(ctx, chartData)
    },
    updateChart(data) {
      if (this.monthlyChart) {
        this.monthlyChart.data.labels = this.getLabel()
        this.monthlyChart.data.datasets[0].data = data.my.set
        this.monthlyChart.data.datasets[1].data = data.total.set
        this.monthlyChart.update()
      }
    },
    getLabel() {
      const dayList = []
      for (let i = 1; i <= this.monthly.my.set.length; i++) {
        dayList.push(i + this.$t('chart.collabo_day'))
      }
      return dayList
    },
  },
  mounted() {
    this.initCart()
  },
}
</script>
