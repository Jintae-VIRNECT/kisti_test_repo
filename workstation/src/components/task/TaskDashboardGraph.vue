<template>
  <div id="task-dashboard-graph"></div>
</template>

<script>
import { conditions } from '@/models/task/Task'
import colorMap from '@/models/color'

// ssr error
let bb = null
try {
  bb = require('billboard.js').bb
} catch (e) {}

export default {
  props: {
    data: Object,
  },
  data() {
    return {
      barChart: null,
      cursorData: null,
    }
  },
  methods: {
    initProcessGraph() {
      const xAxisColors = conditions.map(condition => condition.color)
      const xAxisTicks = conditions.map(condition => condition.label)
      const xAxisValues = [
        this.data.wait,
        this.data.unprogressing,
        this.data.progressing,
        this.data.incompleted,
        this.data.completed,
        this.data.failed,
        this.data.success,
        this.data.fault,
      ]
      // 여백
      xAxisColors.push('white')
      xAxisTicks.push(' ')
      xAxisValues.push(null)

      const that = this
      this.barChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...xAxisTicks],
            ['value', ...xAxisValues],
          ],
          color(color, d) {
            return colorMap[xAxisColors[d.index]]
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
                return that.$t(val)
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
            that.cursorData = val
          },
        },
        grid: {
          y: {
            show: true,
          },
        },
        bar: {
          width: {
            max: 18,
          },
        },
        size: {
          height: 300,
        },
        padding: {
          bottom: 50,
        },
        resize: {
          auto: false,
        },
        regions: [
          {
            start: 0.5,
            end: 4.5,
          },
        ],
        bindto: '#task-dashboard-graph',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
      const xAxis = document.querySelectorAll(
        '#task-dashboard-graph .bb-axis.bb-axis-x g',
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
        rect.setAttribute('fill', colorMap[xAxisColors[index]])
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
        valueText.innerHTML = xAxisValues[index]
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
      // yGrid style
      const yGrid = document.querySelectorAll(
        '#task-dashboard-graph .bb-ygrids .bb-ygrid',
      )
      yGrid.forEach((item, index) => {
        if (index % 2) {
          item.remove()
          return false
        }
        item.style.stroke = '#eaedf3'
        item.style['stroke-dasharray'] = 0
      })
      // region style
      const region = document.querySelector(
        '#task-dashboard-graph .bb-regions .bb-region',
      )
      region.style.stroke = 'none'
      region.style.fill = 'none'
    },
  },
  mounted() {
    this.initProcessGraph()
  },
}
</script>

<style lang="scss">
#__nuxt #task-dashboard-graph {
  .bb-tooltip {
    th,
    td {
      padding: 2px 5px;
    }
  }
}
</style>
