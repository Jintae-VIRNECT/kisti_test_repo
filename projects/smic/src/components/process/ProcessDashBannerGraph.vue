<template lang="pug">
  .box-wrapper
    .box
      hr.divider
      .main-title 전체 공정 정보
      .total-process-progress
        //- .main-percent 88%
        //- el-progress(:percentage="88" :show-text="false")
        .text-item
          span.key 전체 공정
          span.value 14
        .text-item
          span.key 시작 대기 공정
          span.value 14
        .text-item
          span.key 시작된 공정
          span.value 14
        .text-item
          span.key 마감된 공정
          span.value 14
          
    .box
      #process-dash-banner-graph
</template>

<script>
import bb from 'billboard.js'
import { processStatus } from '@/models/process'
import dayjs from 'dayjs'

import filters from '@/mixins/filters'

function getRandomArbitrary() {
  return Math.floor(Math.random() * (40 - 0) + 0)
}
function jsonData() {
  return [
    { processStatus: 'idle', status: 'yet', value: getRandomArbitrary() },
    { processStatus: 'start', status: 'yet', value: getRandomArbitrary() },
    { processStatus: 'start', status: 'start', value: getRandomArbitrary() },
    { processStatus: 'start', status: 'default', value: getRandomArbitrary() },
    { processStatus: 'start', status: 'end', value: getRandomArbitrary() },
    { processStatus: 'end', status: 'yet', value: getRandomArbitrary() },
    { processStatus: 'end', status: 'end', value: getRandomArbitrary() },
    { processStatus: 'end', status: 'default', value: getRandomArbitrary() },
  ]
}
export default {
  props: {
    tableData: Array,
  },
  mixins: [filters],
  data() {
    return {
      tabs: {
        processStatus,
      },
      graphData: [],
      activeTab: processStatus[0].name,
      form: {
        date: dayjs().toString(),
        startTime: '01:00',
        endTime: '24:00',
      },
      barChart: null,
      cursorData: null,
    }
  },
  mounted() {
    this.initProcessGraph(jsonData())
    document
      .querySelector('#process-dash-banner-graph')
      .addEventListener('click', () => {
        this.$router.push(`/process?filter=${this.cursorData}`)
      })
  },
  methods: {
    initProcessGraph(json) {
      const xAxisTicks = [
        '대기',
        '미진행',
        '진행',
        '미흡',
        '완료',
        '미완수',
        '완수',
        '결함',
      ]
      const self = this
      this.graphData = json.map(j => j.value)
      this.barChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...xAxisTicks],
            ['value', ...this.graphData],
          ],
          color(color, d) {
            const label = xAxisTicks[d.index]
            return self.$options.filters.processStatusColorFilter(label)
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
            name: () => '건수',
          },
          onshown() {
            const tooltip = this.api.$.tooltip
            const val = tooltip.select('.bb-tooltip th').text()
            self.cursorData = val
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
        size: {
          height: 400,
        },
        padding: {
          bottom: 50,
        },
        resize: {
          auto: false,
        },
        bindto: '#process-dash-banner-graph',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
      const xAxis = document.querySelectorAll(
        '#process-dash-banner-graph .bb-axis.bb-axis-x g',
      )
      xAxis.forEach((item, index) => {
        const xlabel = item.querySelector('tspan').innerHTML
        item.innerHTML = ''
        var group = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        const width = 54
        const height = 24
        const textPaddingTop = 4
        group.setAttribute(
          'transform',
          `translate(${-(width / 2)},${height / 2})`,
        )

        var rect = document.createElementNS(
          'http://www.w3.org/2000/svg',
          'rect',
        )
        rect.setAttribute('width', width)
        rect.setAttribute('height', height)
        rect.setAttribute('shape-rendering', 'geometricPrecision')
        rect.setAttribute('rx', 5)
        rect.setAttribute('ry', 5)
        rect.setAttribute(
          'class',
          `tag ${this.$options.filters.processStatusNameColor(xlabel)}`,
        )

        var text = document.createElementNS(
          'http://www.w3.org/2000/svg',
          'text',
        )
        text.innerHTML = xlabel
        text.setAttribute('x', width / 2)
        text.setAttribute('y', height / 2 + textPaddingTop)
        text.setAttribute('font-size', 12)
        text.setAttribute('text-anchor', 'middle')
        text.setAttribute('fill', 'white')
        group.appendChild(rect)
        group.appendChild(text)

        var valueText = document.createElementNS(
          'http://www.w3.org/2000/svg',
          'text',
        )
        valueText.innerHTML = this.graphData[index]
        valueText.setAttribute('x', width / 2)
        valueText.setAttribute('y', height * 2 + textPaddingTop * 2)
        valueText.setAttribute('font-size', 20)
        valueText.setAttribute('text-anchor', 'middle')
        valueText.setAttribute('fill', '#385370')
        group.appendChild(rect)
        group.appendChild(text)
        group.appendChild(valueText)
        item.appendChild(group)
      })
    },
  },
}
</script>
