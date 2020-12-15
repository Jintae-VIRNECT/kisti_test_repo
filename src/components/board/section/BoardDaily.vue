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
        <v-date-picker
          class="board__picker"
          v-model="date"
          :masks="masks"
          :popover="{ visibility: 'click', placement: 'auto-start' }"
          :locale="currentLanguage"
          @popoverWillShow="toggleCalendarBtn"
          @popoverWillHide="toggleCalendarBtn"
        >
          <template v-slot="{ inputValue, inputEvents, togglePopover }">
            <div class="board__picker--wrapper">
              <input
                class="board__picker--input"
                :class="{ active: calendarVisible }"
                :value="inputValue"
                v-on="inputEvents"
                readonly
              />
              <button
                class="board__picker--button"
                @click="togglePopover({ placement: 'auto-start' })"
              >
                <img
                  v-if="!calendarVisible"
                  src="~assets/image/calendar/ic_calendar_default.svg"
                  alt="calendar_hide"
                />
                <img
                  v-else
                  src="~assets/image/calendar/ic_calendar_active.svg"
                  alt="calendar_active"
                />
              </button>
            </div>
          </template>
        </v-date-picker>
      </div>

      <figcaption class="chart-legend">
        <chart-legend
          @click="toggle(dailyChart, 'my')"
          :text="$t('chart.my_collabo_list')"
          shape="double-circle"
        ></chart-legend>
        <chart-legend
          @click="toggle(dailyChart, 'total')"
          :text="$t('chart.total_collabo_list')"
          shape="double-circle"
          customClass="grey"
        ></chart-legend>
      </figcaption>
      <div class="chart-holder" :class="{ loading: loading }">
        <canvas :id="chartId" width="1250" height="250"></canvas>
      </div>
    </card>

    <div class="board-figures">
      <figure-board
        :header="$t('chart.daily_my_collabo_count')"
        :my="true"
        :count="daily ? daily.my.count : 0"
        :imgSrc="require('assets/image/figure/ic_chart_daily_count.svg')"
        type="daily"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_my_collabo_time')"
        :my="true"
        :time="daily ? daily.my.time : 0"
        :imgSrc="require('assets/image/figure/ic_chart_daily_time.svg')"
        type="daily"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_total_collabo_count')"
        :count="daily ? daily.total.count : 0"
        :imgSrc="require('assets/image/figure/ic_chart_daily_total_count.svg')"
        type="daily"
      ></figure-board>
      <figure-board
        :header="$t('chart.daily_total_collabo_time')"
        :time="daily ? daily.total.time : 0"
        :imgSrc="require('assets/image/figure/ic_chart_daily_total_time.svg')"
        type="daily"
      ></figure-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import Chart from 'chart.js'
import ChartLegend from 'Legend'
import FigureBoard from 'FigureBoard'

import chartMixin from 'mixins/chart'
import langMixin from 'mixins/language'
import calendarMixin from 'mixins/calendar'

import { mapActions } from 'vuex'
export default {
  name: 'BoardDaily',
  mixins: [chartMixin, langMixin, calendarMixin],
  components: {
    Card,
    ChartLegend,
    FigureBoard,
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
  data() {
    return {
      dailyChart: null,
      date: new Date(),
      calendarVisible: false,
      chartId: 'chart-dayily',
    }
  },
  watch: {
    daily: {
      handler(data) {
        this.updateChart(data)
      },
      deep: true,
    },
    date() {
      this.setCalendar({
        name: 'daily',
        date: this.date,
      })
    },
  },
  methods: {
    ...mapActions(['setCalendar']),
    initChart() {
      const ctx = document.getElementById(this.chartId).getContext('2d')
      const chartData = this.initDailyChart(this.chartId)
      this.dailyChart = new Chart(ctx, chartData)
    },
    updateChart(data) {
      if (this.dailyChart) {
        this.dailyChart.data.datasets[0].data = data.my.set
        this.dailyChart.data.datasets[1].data = data.total.set
        this.dailyChart.update()
      }
    },
    toggleCalendarBtn() {
      this.calendarVisible = !this.calendarVisible
    },
  },
  mounted() {
    this.initChart()

    this.setCalendar({
      name: 'daily',
      date: this.date,
    })
  },
}
</script>
