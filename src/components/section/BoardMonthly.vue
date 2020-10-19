<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">월별 협업</span>
        <span class="board__header--description">
          설정한 기간의 협업 내용을 보여줍니다.
        </span>
        <datepicker
          class="board__header--datepicker"
          :pickerName="'monthly'"
          :minimumView="'month'"
          :maximumView="'month'"
          :format="'yyyy-MM'"
        ></datepicker>
      </div>
      <div class="chart-legend">
        <chart-legend text="개인 협업 내역" shape="square"></chart-legend>
        <chart-legend
          text="전체 협업 내역"
          shape="square"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder">
        <canvas id="chart-month" width="1145" height="230"></canvas>
      </div>
    </card>
    <div class="board-figures">
      <figure-board
        header="전체 월별 협업 수"
        :count="48"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        header="전체 월별 협업 시간"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        header="나의 월별 협업 수"
        :onlyMe="true"
        :count="999999"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        header="나의 월별 협업 시간"
        :onlyMe="true"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_time.svg')"
      ></figure-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import Datepicker from 'Datepicker'
import Chart from 'chart.js'
import ChartLegend from 'Legend'
import FigureBoard from 'FigureBoard'
export default {
  name: 'BoardMonthly',
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    Datepicker,
  },
  mounted() {
    const ctx = document.getElementById('chart-month').getContext('2d')

    const custom = function(tooltipModel) {
      // Tooltip Element
      let tooltipEl = document.getElementById('chartjs-noarrow')

      // Create element on first render
      if (!tooltipEl) {
        tooltipEl = document.createElement('div')
        tooltipEl.id = 'chartjs-noarrow'
        tooltipEl.innerHTML = '<table></table>'
        document.body.appendChild(tooltipEl)
      }

      // Hide if no tooltip
      if (tooltipModel.opacity === 0) {
        tooltipEl.style.opacity = 0
        return
      }

      // Set caret Position
      tooltipEl.classList.remove('above', 'below', 'no-transform')
      if (tooltipModel.yAlign) {
        tooltipEl.classList.add(tooltipModel.yAlign)
      } else {
        tooltipEl.classList.add('no-transform')
      }

      function getBody(bodyItem) {
        return bodyItem.lines
      }

      // Set Text
      if (tooltipModel.body) {
        let titleLines = tooltipModel.title || []
        let bodyLines = tooltipModel.body.map(getBody)

        let innerHtml = '<thead class="chartjs-tooltip-head">'

        titleLines.forEach(function(title) {
          innerHtml += '<tr><th><p>' + title + '</p></th></tr>'
        })
        innerHtml += '</thead><tbody>'

        bodyLines.forEach(function(body, i) {
          let colors = tooltipModel.labelColors[i]
          let style = 'background:' + colors.backgroundColor
          style += '; border-color:' + colors.borderColor
          style += '; border-width: 1px'
          let span =
            '<span class="tooil-tip-legend" style="display:inline-block; background:' +
            colors.backgroundColor +
            '"></span><span style="' +
            style +
            '"></span>'
          innerHtml += '<tr><td>' + span + body + '</td></tr>'
        })
        innerHtml += '</tbody>'

        let tableRoot = tooltipEl.querySelector('table')
        tableRoot.innerHTML = innerHtml
      }

      // `this` will be the overall tooltip
      let position = this._chart.canvas.getBoundingClientRect()

      // Display, position, and set styles for font
      tooltipEl.style.opacity = 1
      tooltipEl.style.position = 'absolute'
      tooltipEl.style.left =
        position.left + window.pageXOffset + tooltipModel.caretX + 'px'
      tooltipEl.style.top =
        position.top + window.pageYOffset + tooltipModel.caretY + -100 + 'px'
      tooltipEl.style.fontSize = tooltipModel.bodyFontSize + 'px'
      tooltipEl.style.fontStyle = tooltipModel._bodyFontStyle
      tooltipEl.style.padding =
        tooltipModel.yPadding + 'px ' + tooltipModel.xPadding + 'px'
      tooltipEl.style.pointerEvents = 'none'
    }

    let myChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: this.getMonth(),
        datasets: [
          {
            label: '개인 협업 내역',
            data: [5, 3],
            backgroundColor: '#0f75f5',
            borderColor: 'none',
            borderWidth: 4,
            hoverBackgroundColor: '#0f75f5',
            hoverBorderColor: 'none',
            barThickness: 11,
          },
          {
            label: '전체 협업 내역',
            data: [1, 2],
            backgroundColor: '#bbc8d9',
            borderColor: 'none',
            borderWidth: 4,
            hoverBackgroundColor: '#bbc8d9',
            hoverBorderColor: 'none',
            barThickness: 11,
          },
        ],
      },
      options: {
        hover: {
          mode: 'index',
        },
        tooltips: {
          mode: 'index',
          position: 'average',
          enabled: false,
          custom: custom,
          titleFontSize: '15',
          bodyFontSize: '14',
          // caretSize: 7,
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: function() {
              return '일별 완료 협업'
            },
            label: function(tooltipItem) {
              return Number(tooltipItem.yLabel) + '건'
            },
          },
        },
        legend: {
          display: false,
        },
        scales: {
          xAxes: [
            {
              stacked: true,
            },
          ],
          yAxes: [
            {
              stacked: true,
            },
          ],
        },
        // scales: {
        //   xAxes: [
        //     {
        //       stacked: true,
        //     },
        //     {
        //       gridLines: {
        //         display: false,
        //       },
        //     },
        //   ],
        //   yAxes: [
        //     {
        //       stacked: true,
        //     },
        //     // {
        //     //   gridLines: {
        //     //     borderDash: [1, 2],
        //     //   },
        //     // },
        //   ],
        // },
      },
    })
    console.log(myChart)
  },

  methods: {
    //temp
    getMonth() {
      const dayList = []
      for (let i = 1; i <= 31; i++) {
        dayList.push(i + '일')
      }
      return dayList
    },
  },
}
</script>

<style lang="scss">
#chartjs-noarrow {
  position: absolute;
  width: 128px;
  height: 80px;
  color: white;
  background: #3b3b3b;
  border: 1px solid rgb(52, 65, 81);
  border-radius: 4px;
  -webkit-transform: translate(-50%, 0);
  transform: translate(-50%, 0);
  opacity: 1;
  -webkit-transition: all 0.1s ease;
  transition: all 0.1s ease;
  pointer-events: none;

  table {
    position: relative;
    width: 100%;
  }

  .tooil-tip-legend {
    width: 10px;
    height: 10px;
    margin-right: 5px;
    margin-left: 5px;
    border-radius: 50%;
  }
}
.chart-legend {
  display: flex;
  align-items: center;
  height: 68px;
  padding-left: 30px;

  & > .legend.square.grey {
    &::before {
      background-color: #bbc8d9;
    }
  }
}
</style>
