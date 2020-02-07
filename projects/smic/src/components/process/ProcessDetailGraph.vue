<template lang="pug">
  div
    .box-wrapper
      .box
        #process-dash-banner-graph
</template>
<style lang="scss">
#process-dash-banner-graph .bb-axis.bb-axis-x g.tick text {
  tspan:nth-child(1) {
    font-size: 14px;
    font-weight: 600;
    fill: #0d2a58;
  }
  tspan:nth-child(3) {
    font-size: 13px;
    fill: #566173;
  }
}
</style>
<script>
import ProcessDetailGraphTooltip from '@/components/process/ProcessDetailGraphTooltip.vue'
import Vue from 'vue'

import bb from 'billboard.js'

import customColors from '@/models/colors.js'

import sceneGroup from '@/data/sceneGroup'
const jsonData = sceneGroup.tableData
export default {
  props: {
    tableData: Array,
  },
  data() {
    return {
      barChart: null,
    }
  },
  mounted() {
    this.initProcessGraph(jsonData)
  },
  methods: {
    initProcessGraph(json) {
      const xAxisTicks = json.map(row => row.name)
      const maxLeftPadding = xAxisTicks.reduce((a, b) =>
        a.length > b.length ? a : b,
      ).length
      const heightSize = 200 + json.length * 60

      const self = this
      const graphData = json.map(j => j.progress)
      this.barChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...xAxisTicks],
            ['value', ...graphData],
          ],
          color(color, d) {
            return customColors[d.index]
          },
          type: 'bar',
          axes: {
            value: 'y',
          },
        },
        axis: {
          rotated: true,
          x: {
            type: 'category',
            tick: {
              show: false,
              text: {
                show: true,
              },
              format(index) {
                const data = json[index]
                if (!data) return
                const tmp = `${data.name}\n \n${data.user}`
                return tmp
              },
            },
          },
          y: {
            max: 100,
            show: false,
            tick: {
              show: false,
              count: 6,
            },
          },
          y2: {
            show: true,
            tick: {
              show: false,
              format(val) {
                return val * 100
              },
            },
          },
        },
        grid: {
          y: {
            show: true,
          },
        },
        legend: {
          show: false,
        },
        tooltip: {
          contents(rows) {
            const { index } = rows[0]

            const data = json[index]
            const dataSet = {
              sceneGroupName: data.sceneGroupName,
              startAt: data.startAt,
              endAt: data.endAt,
              issue: data.issue,
              progress: data.progress,
              status: data.status,
            }
            const renderedTooltip = new Vue({
              ...ProcessDetailGraphTooltip,
              parent: self,
              propsData: dataSet,
            }).$mount().$el.outerHTML

            return renderedTooltip
          },
        },
        bar: {
          width: {
            max: 20,
          },
        },
        size: {
          height: heightSize,
        },
        padding: {
          top: 50,
          left: 50 + maxLeftPadding * 10,
          right: 70,
          bottom: 20,
        },
        bindto: '#process-dash-banner-graph',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }

      const firstGridLine = document.querySelector(
        '.bb-ygrids .bb-ygrid:first-child',
      )
      const XpositionOfFirstGridLine = firstGridLine.getAttribute('x1')
      firstGridLine.setAttribute('x1', XpositionOfFirstGridLine + 0.5)
      firstGridLine.setAttribute('x2', XpositionOfFirstGridLine + 0.5)

      const lastGridLine = document.querySelector(
        '.bb-ygrids .bb-ygrid:last-child',
      )
      const XpositionOfLastGridLine = lastGridLine.getAttribute('x1')
      lastGridLine.setAttribute('x1', XpositionOfLastGridLine - 2)
      lastGridLine.setAttribute('x2', XpositionOfLastGridLine - 2)
    },
  },
}
</script>
