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
        <!-- <datepicker
          class="board__header--datepicker"
          :pickerName="'daily'"
          :minimumView="'day'"
          :maximumView="'day'"
          :initValue="today"
        ></datepicker> -->
        <v-date-picker
          class="chart-picker"
          v-model="date"
          :masks="masks"
          :popover="{ visibility: 'click' }"
          @popoverWillShow="toggleCalendarBtn"
          @popoverWillHide="toggleCalendarBtn"
        >
          <template v-slot="{ inputValue, inputEvents, togglePopover }">
            <div class="collabo-search-bar__date">
              <input
                class="collabo-search-bar__date--input"
                :value="inputValue"
                v-on="inputEvents"
                readonly
              />
              <button
                class="collabo-search-bar__date--button"
                @click="togglePopover({ placement: 'auto-start' })"
              >
                <img
                  v-if="!calendarBtn"
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
        <canvas id="chart-dayily" width="1250" height="250"></canvas>
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
// import Datepicker from 'Datepicker'
import Chart from 'chart.js'
import ChartLegend from 'Legend'
import FigureBoard from 'FigureBoard'

import { hourLabels } from 'utils/chartDatas'
import chartMixin from 'mixins/chart'

import { mapActions } from 'vuex'
export default {
  name: 'BoardDaily',
  mixins: [chartMixin],
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    // Datepicker,
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
      today: new Date(),
      date: new Date(),
      masks: {
        input: 'YYYY-MM-DD',
        title: 'YYYY-MM',
      },
      calendarBtn: false,
    }
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
    toggleCalendarBtn() {
      this.calendarBtn = !this.calendarBtn
    },
  },
  mounted() {
    this.initChart()
  },
}
</script>
