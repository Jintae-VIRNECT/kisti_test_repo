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
          span.value {{ processStatistics.categoryWait + processStatistics.categoryStarted + processStatistics.categoryEnded }}
        .text-item
          span.key 시작 대기 공정
          span.value {{ processStatistics.categoryWait }}
        .text-item
          span.key 시작된 공정
          span.value {{ processStatistics.categoryStarted }}
        .text-item
          span.key 마감된 공정
          span.value {{ processStatistics.categoryEnded }}
          
    .box
      #process-dash-banner-graph
</template>

<script>
import { mapGetters } from 'vuex'
import bb from 'billboard.js'
import { processStatus } from '@/models/process'

import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  data() {
    return {
      barChart: null,
      cursorData: null,
    }
  },
  computed: {
    ...mapGetters(['processStatistics']),
    graphData() {
      if (!Object.keys(this.processStatistics).length)
        return [0, 0, 0, 0, 0, 0, 0, 0]
      else {
        return processStatus.map(status => {
          return Object.entries(this.processStatistics).find(
            ([key]) => status.name === key.toUpperCase(),
          )[1]
        })
      }
    },
  },
  watch: {
    processStatistics() {
      this.initProcessGraph()
    },
  },
  mounted() {
    this.initProcessGraph()
    document
      .querySelector('#process-dash-banner-graph')
      .addEventListener('click', () => {
        this.$router.push(`/process?filter=${this.cursorData}`)
      })
  },
  methods: {
    initProcessGraph() {
      const xAxisTicks = processStatus.map(status => status.label)
      const self = this
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

      // yGrid style
      const yGrid = document.querySelectorAll(
        '#process-dash-banner-graph .bb-ygrids .bb-ygrid',
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
        '#process-dash-banner-graph .bb-regions .bb-region',
      )
      region.style.stroke = 'none'
      region.style.fill = 'none'
    },
  },
}
</script>
