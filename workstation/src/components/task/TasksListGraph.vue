<template>
  <div id="tasks-list-graph" />
</template>

<script>
import Vue from 'vue'
import SubTasksGraphTooltip from '@/components/task/SubTasksGraphTooltip'
import StepsGraphTooltip from '@/components/task/StepsGraphTooltip'

// ssr error
let bb = null
if (process.client) {
  bb = require('billboard.js').bb
}

export default {
  props: {
    data: Array,
    type: String,
  },
  watch: {
    data() {
      this.initProcessGraph()
    },
  },
  methods: {
    initProcessGraph() {
      const json = this.data
      const heightSize = 200 + json.length * 60
      const self = this
      const graphData = json.map(j => j.progressRate)
      this.barChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...json.map(item => item.id || item.subTaskId)],
            ['value', ...graphData],
          ],
          color(color, d) {
            const colors = ['#f89637', '#2cbc65', '#186ae2']
            return colors[d.index % 3]
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
                position: {
                  x: -20,
                },
              },
              format(index) {
                const data = json[index]
                if (!data) return
                let str = `${data.name || data.subTaskName}`
                if (data.workerName) str += `\n \n${data.workerName}`
                return str
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
            padding: {
              top: 0,
            },
          },
          y2: {
            max: 100,
            show: true,
            tick: {
              show: false,
            },
            padding: {
              top: 0,
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
            const tooltip =
              self.type === 'subTask' ? SubTasksGraphTooltip : StepsGraphTooltip
            const renderedTooltip = new Vue({
              ...tooltip,
              parent: self,
              propsData: { data, index },
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
          top: 30,
          left: 185,
          right: 60,
          bottom: 30,
        },
        bindto: '#tasks-list-graph',
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
    initTooltip() {
      this.$el.addEventListener('mouseleave', () => {
        this.$el.querySelector('.bb-tooltip-container').innerHTML = ''
      })
      this.$el.addEventListener('click', ({ target }) => {
        if (target.nodeName === 'SPAN') {
          target = target.parentElement
        }
        const btnType = target.getAttribute('data-type')
        const id = target.getAttribute('data-id')
        if (btnType === 'issue') this.moveToIssue(id)
        if (btnType === 'paper') this.moveToPaper(id)
      })
    },
    moveToIssue(issueId) {
      this.$router.replace(
        `${this.$router.currentRoute.path}/issues/${issueId}`,
      )
    },
    moveToPaper(paperId) {
      this.$router.replace(
        `${this.$router.currentRoute.path}/papers/${paperId}`,
      )
    },
  },
  mounted() {
    this.initProcessGraph()
    this.initTooltip()
  },
}
</script>

<style lang="scss">
#tasks-list-graph {
  .bb-axis.bb-axis-x g.tick text {
    tspan:nth-child(1) {
      font-weight: 600;
      font-size: 14px;
      fill: #0b1f48;
    }
    tspan:nth-child(3) {
      font-size: 13px;
      fill: #5e6b81;
    }
  }

  .bb-tooltip-container {
    top: 0 !important;
    right: 40px;
    bottom: 0 !important;
    left: auto !important;
    display: block !important;
    visibility: visible !important;
    pointer-events: auto !important;
    &:before {
      display: inline-block;
      height: 100%;
      vertical-align: middle;
      content: '';
    }
  }
  // tooltip
  .bb-tooltip-container .el-card {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 30px;
    width: 300px;
    margin: auto 0;

    .el-card__header {
      height: 48px;
      padding: 14px 20px;
    }
    .el-card__body {
      padding: 20px 24px;
      .el-divider {
        margin: 18px 0;
        background: rgba(226, 231, 237, 0.8);
      }
      dt {
        margin-bottom: 4px;
        color: $font-color-desc;
        font-size: 12px;
        line-height: 20px;
      }
      dd {
        margin-bottom: 12px;
        line-height: 20px;
      }
    }
    .progress {
      margin: 18px 0 24px;
      dd {
        margin-bottom: 0;
      }
      .el-col:last-child {
        padding-right: 80px;
        padding-left: 27px;
        border-left: solid 1px #e6e9ee;

        dd {
          color: #0d2a58;
          font-size: 24px;
          line-height: 36px;
        }
      }
    }
    .el-button {
      padding: 8px 10px;
      pointer-events: inherit;
    }
  }
}
</style>
