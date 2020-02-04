<template lang="pug">
  .card
    .card__header
      .card__header--left
        span.title 공정 진행 그래프
        span.sub-title 시간별 세부공정 진행 상태별 보고 수
      .card__header--right
        .text-right
          router-link.more-link(type="text" to="/process") 더보기
    .card__body
      el-tabs(v-model='activeTab' @tab-click="onClickToggleTab" )
        el-tab-pane(
          v-for="(status, index) in tabs.processStatus" 
          :key="index" 
          :label="status.label" 
          :name="status.name"
        )
    .vn-date-time-select-wrapper
      .vn-date-time-select-wrapper--left
        i.el-icon-arrow-left.arrow-left(@click="onChangeDateArrow(-1)")
        .date-label {{form.date | filterDayName}}
        i.el-icon-arrow-right.arrow-right(@click="onChangeDateArrow(1)")
      .vn-date-time-select-wrapper--right
        .vn-date-range-select
          el-date-picker(
            :clearable='false'
            v-model='form.date' 
            type='date'
            @change="checkMinMaxTime"
            :placeholder='String(form.date)')
        .vn-time-range-select
          el-time-select(
            :clearable='false'
            :placeholder='form.startTime'
            v-model='form.startTime' 
            :picker-options="{\
              start: '01:00',\
              step: '01:00',\
              end: form.endTime,minTime: '00:00'\
            }"
            @change="checkMinMaxTime"
            popper-class="vn-time-range-select--dropdown"
          )
          .time-divider ~
          el-time-select(
            :clearable='false'
            :placeholder='form.endTime' 
            v-model='form.endTime' 
            :picker-options="{\
              start: form.startTime,\
              step: '01:00',\
              end: '24:00',\
              minTime: form.startTime == '24:00' ? '23:59' : form.startTime\
            }"
            @change="checkMinMaxTime"
            popper-class="vn-time-range-select--dropdown"
          )
    .bar-chart-wrapper
      #bar-chart
</template>
<style lang="scss">
$el-date-height: 36px;
$el-date-width: 80px;
#bar-chart {
  height: 210px;
}
.vn-time-range-select--dropdown {
  width: $el-date-width !important;
}
.vn-date-time-select-wrapper {
  padding: 20px 30px 0px 15px;
  &--left,
  &--right {
    width: 49.5%;
    display: inline-block;
    & > i {
      padding: 3px;
      cursor: pointer;
      vertical-align: middle;
      font-weight: 600;
    }
    & > * {
      display: inline-block;
      vertical-align: middle;
    }
  }
  &--left {
    .date-label {
      font-size: 16px;
      color: #0d2a58;
      margin: 0px 15px;
      text-align: center;
    }
  }
  &--right {
    text-align: right;
    .vn-date-range-select {
      margin-right: 10px;
      display: inline-block !important;
    }
    .time-divider {
      display: inline-block;
      margin: 0px 5px;
    }
    .vn-time-range-select {
      display: inline-block !important;
    }
    input {
      height: $el-date-height !important;
      padding-right: 0px !important;
      display: inline-block !important;
    }
    .el-date-editor {
      width: $el-date-width !important;
      i {
        line-height: $el-date-height !important;
      }
    }
    .el-date-editor--date {
      width: 110px !important;
    }
    .vn-time-range-select {
      display: inline-block;
    }
  }
}
</style>
<script>
import bb from 'billboard.js'
import { processStatus } from '@/models/process'
import dayjs from 'dayjs'

function getRandomArbitrary() {
  return Math.floor(Math.random() * (40 - 0) + 0)
}
function jsonData() {
  return [
    { time: '1', value: getRandomArbitrary() },
    { time: '2', value: getRandomArbitrary() },
    { time: '3', value: getRandomArbitrary() },
    { time: '4', value: getRandomArbitrary() },
    { time: '5', value: getRandomArbitrary() },
    { time: '6', value: getRandomArbitrary() },
    { time: '7', value: getRandomArbitrary() },
    { time: '8', value: getRandomArbitrary() },
    { time: '9', value: getRandomArbitrary() },
    { time: '10', value: getRandomArbitrary() },
    { time: '11', value: getRandomArbitrary() },
    { time: '12', value: getRandomArbitrary() },
    { time: '13', value: getRandomArbitrary() },
    { time: '14', value: getRandomArbitrary() },
    { time: '15', value: getRandomArbitrary() },
    { time: '16', value: getRandomArbitrary() },
    { time: '17', value: getRandomArbitrary() },
    { time: '18', value: getRandomArbitrary() },
    { time: '19', value: getRandomArbitrary() },
    { time: '20', value: getRandomArbitrary() },
    { time: '21', value: getRandomArbitrary() },
    { time: '22', value: getRandomArbitrary() },
    { time: '23', value: getRandomArbitrary() },
    { time: '24', value: getRandomArbitrary() },
  ]
}
export default {
  data() {
    return {
      tabs: {
        processStatus,
      },
      activeTab: processStatus[0].name,
      form: {
        date: dayjs().toString(),
        startTime: '01:00',
        endTime: '24:00',
      },
      barChart: null,
    }
  },
  mounted() {
    this.initProcessGraph(jsonData())
  },
  methods: {
    initProcessGraph(json) {
      const startTime = Number(this.form.startTime.split(':')[0])
      const endTime = Number(this.form.endTime.split(':')[0])
      this.barChart = bb.generate({
        data: {
          json,
          color(color, d) {
            if (d.x >= startTime - 1 && d.x < endTime)
              return 'rgba(24, 106, 226)'
            else return 'rgba(24, 106, 226, 0.3)'
          },
          keys: {
            x: 'time',
            value: ['value'],
          },
          type: 'bar',
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
            title: d => {
              return `${d}:00 ~ ${d}:59`
            },
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
        bindto: '#bar-chart',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
    },
    checkMinMaxTime() {
      const startTime = Number(this.form.startTime.split(':')[0])
      const endTime = Number(this.form.endTime.split(':')[0])
      if (startTime > endTime) this.form.endTime = this.form.startTime
      this.initProcessGraph(jsonData())
    },
    onClickToggleTab({ label }) {
      this.activeTab = label
      this.initProcessGraph(jsonData())
    },
    onChangeDateArrow(val) {
      if (val === 1)
        this.form.date = dayjs(this.form.date)
          .add(1, 'day')
          .toString()
      else
        this.form.date = dayjs(this.form.date)
          .subtract(1, 'day')
          .toString()
      this.onChangeDate()
    },
    onChangeDate() {
      this.initProcessGraph(jsonData())
    },
  },
  filters: {
    filterDayName(value) {
      const today = dayjs().format('YYYY-MM-DD')
      const inputDay = dayjs(value).format('YYYY-MM-DD')
      const diffDay = dayjs(inputDay).diff(dayjs(today), 'day')
      if (diffDay === 0) return '오늘'
      else if (diffDay === -1) return '어제'
      else if (diffDay > 0) return `${diffDay}일 후`
      else if (diffDay < -1) return `${Math.abs(diffDay)}일 전`
    },
  },
}
</script>
