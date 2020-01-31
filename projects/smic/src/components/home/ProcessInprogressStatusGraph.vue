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
    .bar-chart-wrapper
      el-date-picker(v-model='form.data' type='date' placeholder='Pick a day')
      el-time-select(
        :clearable='false'
        :placeholder='form.startTime'
        v-model='form.startTime' 
        :picker-options="{ start: form.startTime, step: '01:00', end: form.endTime }"
        @change="checkMinMaxTime"
      )
      el-time-select(
        :clearable='false'
        :placeholder='form.endTime' 
        v-model='form.endTime' 
        :picker-options="{start: form.startTime,step: '01:00',end: form.endTime,minTime: form.startTime == '24:00' ? '23:59' : form.startTime}"
        @change="checkMinMaxTime"
      )
      #bar-chart
</template>
<style lang="scss" scoped>
#bar-chart {
  height: 210px;
}
</style>
<script>
import bb from 'billboard.js'
import { processStatus } from '@/models/process'

function getRandomArbitrary() {
  return Math.random() * (40 - 0) + 0
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
        data: null,
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
    checkMinMaxTime() {
      const startTime = Number(this.form.startTime.split(':')[0])
      const endTime = Number(this.form.endTime.split(':')[0])
      if (startTime > endTime) this.form.endTime = this.form.startTime
      this.initProcessGraph(jsonData())
    },
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
                return index, val
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
    onClickToggleTab({ label }) {
      this.activeTab = label
      this.initProcessGraph(jsonData())
    },
  },
}
</script>
