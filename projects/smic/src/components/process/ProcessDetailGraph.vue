<template lang="pug">
  div
    .box-wrapper
      .box
        #process-dash-banner-graph
    .card.tooltip(ref="tooltip")
      .card__header--secondary
        .card__header--left
          span.sub-title 세부공정 정보
        .card__header--right
          .text-right
            router-link.more-link(type="text" to="/process") 더보기
      .card__body.tooltip__body
        .item
          label 세부공정 이름
          p#tooltip-sceneGroupName(ref="tooltip-sceneGroupName") {{tooltip.sceneGroupName}}
        .item.box-wrapper
          .box
            label 진행상태
            button#tooltip-status 완료
          .box
            label 진행률
            p#tooltip-progress 100%
        .item
          label 세부공정 일정
          p#tooltip-date {{tooltip.startAt}} - {{tooltip.endAt}}
        .item
          label 작업 이슈
          p#tooltip-issue 있음



</template>
<style lang="scss" scoped>
.tooltip {
  width: 300px;
  .tooltip__body {
    padding: 16px;
  }
}
</style>
<script>
import bb from 'billboard.js'

function getRandomArbitrary() {
  return Math.floor(Math.random() * (40 - 0) + 0)
}
function jsonData() {
  return [
    {
      user: '작업자 1',
      sceneGroupName: "Scene Group's name 1",
      value: getRandomArbitrary(),
    },
    {
      user: '작업자 2',
      sceneGroupName: "Scene Group's name 2",
      value: getRandomArbitrary(),
    },
    {
      user: '작업자 3',
      sceneGroupName: "Scene Group's name 3",
      value: getRandomArbitrary(),
    },
  ]
}
export default {
  props: {
    tableData: Array,
  },
  data() {
    return {
      graphData: [],
      barChart: null,
      tooltip: {
        sceneGroupName: null,
        startAt: null,
        endAt: null,
      },
    }
  },
  mounted() {
    this.initProcessGraph(jsonData())
  },
  methods: {
    initProcessGraph(json) {
      const xAxisTicks = json.map(row => row.user)
      const self = this
      this.graphData = json.map(j => j.value)
      this.barChart = bb.generate({
        data: {
          x: 'x',
          columns: [
            ['x', ...xAxisTicks],
            ['value', ...this.graphData],
          ],
          // color(color, d) {
          //   const label = xAxisTicks[d.index]
          //   return self.$options.filters.processStatusColorFilter(label)
          // },
          type: 'bar',
          axes: {
            value: 'y2',
          },
        },
        axis: {
          rotated: true,
          // min: {
          //   y: 0,
          // },
          x: {
            type: 'category',
            tick: {
              show: false,
              text: {
                show: true,
              },
              format: function(index, val) {
                if (!json[index]) return val
                const tmp = `
                ${json[index].sceneGroupName}
                  ${json[index].user}
                `
                return tmp
              },
            },
          },
          y: {
            show: false,
            // tick: {
            //   show: false,
            //   text: {
            //     show: true,
            //   },
            //   // count: 8,
            // },
          },
          y2: {
            show: true,
            tick: {
              show: false,
              text: {
                show: true,
              },
              // count: 8,
            },
          },
        },
        legend: {
          show: false,
        },
        tooltip: {
          // format: {
          //   name: () => '건수',
          // },
          contents(rows) {
            const { index } = rows[0]
            console.log(
              'json[index].sceneGroupName : ',
              json[index].sceneGroupName,
            )
            self
              .$nextTick()
              .then(() => {
                console.log(
                  'self.$refs.tooltip-sceneGroupName : ',
                  self.$refs['tooltip-sceneGroupName'],
                )
                return self.$refs.tooltip.outerHTML
              })
              .catch(e => console.log('e : ', e))
            self.tooltip.sceneGroupName = json[index].sceneGroupName
            // self.$nextTick().then(() => {
            //   // DOM updated
            //   self.$el.querySelector('#tooltip-sceneGroupName').innerHTML =
            //     self.tooltip.sceneGroupName
            //   // self.$el.querySelector(
            //   //   '#tooltip-date',
            //   // ).innerHTML = `${self.tooltip.startAt} - ${self.tooltip.endAt}`
            //   console.log('eeeee')
            //   // return self.$refs.tooltip.outerHTML
            // })
            // self.$el.querySelector('#tooltip-sceneGroupName').innerHTML =
            //   self.tooltip.sceneGroupName
            // console.log(
            //   "self.$el.querySelector('#tooltip-sceneGroupName') : ",
            //   self.$el.querySelector('#tooltip-sceneGroupName'),
            // )
          },
          // contents: {
          //   bindto: self.$refs.tooltip,
          // },
        },
        grid: {
          y: {
            show: true,
            // ticks: 8,
          },
        },
        bar: {
          width: {
            max: 20,
          },
        },
        size: {
          height: 400,
        },
        padding: {
          top: 50,
          left: 150,
          right: 70,
          bottom: 20,
        },
        bindto: '#process-dash-banner-graph',
      })
      const domains = document.querySelectorAll('path.domain')
      for (let i = 0; i < domains.length; i++) {
        domains[i].style.stroke = 'none'
      }
    },
  },
}
</script>
