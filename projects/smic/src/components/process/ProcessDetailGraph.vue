<template lang="pug">
  div
    .box-wrapper
      .box
        #process-dash-banner-graph
    process-detail-graph-tooltip(
      user='작업자 1'
      sceneGroupName=`Scene Group's name 1`
      startAt='2020.02.03 14:00'
      endAt='2020.02.03 16:00'
      :issue='false'
      status='complete'
      :progress='10'
    )
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

function getRandomArbitrary() {
  return Math.floor(Math.random() * (100 - 0) + 0)
}
function jsonData() {
  return [
    {
      user: '작업자 1',
      sceneGroupName: `Scene Group's name 1`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: false,
      status: 'complete',
      // progress: 20,
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 2',
      sceneGroupName: `Scene Group's name 2`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: false,
      status: 'progress',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 3',
      sceneGroupName: `Scene Group's name 3`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'idle',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 4',
      sceneGroupName: `Scene Group's name 4`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'imcomplete',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 5',
      sceneGroupName: `Scene Group's name 5`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'imcomplete',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 6',
      sceneGroupName: `Scene Group's name 6`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'imcomplete',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 7',
      sceneGroupName: `Scene Group's name 7`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'imcomplete',
      progress: getRandomArbitrary(),
    },
    {
      user: '작업자 8',
      sceneGroupName: `Scene Group's name 8`,
      startAt: '2020.02.03 14:00',
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'imcomplete',
      progress: getRandomArbitrary(),
    },
  ]
}
export default {
  components: { ProcessDetailGraphTooltip },
  props: {
    tableData: Array,
  },
  data() {
    return {
      barChart: null,
    }
  },
  mounted() {
    this.initProcessGraph(jsonData())
  },
  methods: {
    initProcessGraph(json) {
      const xAxisTicks = json.map(row => row.sceneGroupName)
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
                const tmp = `${data.sceneGroupName}\n \n${data.user}`
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
          left: 50 + maxLeftPadding * 7.5,
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
