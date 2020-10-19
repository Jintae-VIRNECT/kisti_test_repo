<template>
  <section class="board">
    <card :customClass="'custom-card-chart'">
      <div name="header" class="board__header">
        <span class="board__header--title">일일 협업</span>
        <span class="board__header--description">
          설정한 날짜의 일일 협업 내용을 보여줍니다.
        </span>
        <datepicker
          class="board__header--datepicker"
          :pickerName="'daily'"
          :minimumView="'day'"
          :maximumView="'day'"
        ></datepicker>
      </div>
      <div class="chart-legend">
        <chart-legend text="개인 협업 내역" shape="round"></chart-legend>
        <chart-legend
          text="전체 협업 내역"
          shape="round"
          customClass="grey"
        ></chart-legend>
      </div>
      <div class="chart-holder">
        <canvas id="chart-dayily" width="1145" height="230"></canvas></div
    ></card>
    <div class="board-figures">
      <figure-board
        header="전체 일별 협업 수"
        :count="48"
        :imgSrc="require('assets/image/figure/ic_figure_calendar.svg')"
      ></figure-board>
      <figure-board
        header="전체 일별 협업 시간"
        :time="999999"
        :imgSrc="require('assets/image/figure/ic_figure_date_all.svg')"
      ></figure-board>
      <figure-board
        header="나의 일별 협업 수"
        :onlyMe="true"
        :count="999999"
        :imgSrc="require('assets/image/figure/ic_figure_chart.svg')"
      ></figure-board>
      <figure-board
        header="나의 일별 협업 시간"
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
  name: 'BoardDaily',
  components: {
    Card,
    ChartLegend,
    FigureBoard,
    Datepicker,
  },
  mounted() {
    const ctx = document.getElementById('chart-dayily').getContext('2d')

    const custom = function(tooltipModel) {
      // Tooltip Element
      let tooltipEl = document.getElementById('chartjs-tooltip')

      // Create element on first render
      if (!tooltipEl) {
        tooltipEl = document.createElement('div')
        tooltipEl.id = 'chartjs-tooltip'
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
          console.log(tooltipModel)
          let colors = tooltipModel.labelColors[i]
          let style = 'background:' + colors.backgroundColor
          style += '; border-color:' + colors.borderColor
          style += '; border-width: 1px'
          let span =
            '<span class="tooil-tip-legend" style="display:inline-block; background:#fff; border-radius: 50%; border: 3px solid' +
            colors.borderColor +
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
      type: 'line',
      data: {
        labels: [
          '00',
          '01',
          '02',
          '03',
          '04',
          '05',
          '06',
          '07',
          '08',
          '09',
          '10',
          '11',
          '12',
          '13',
          '14',
          '15',
          '16',
          '17',
          '18',
          '19',
          '20',
          '21',
          '22',
          '23',
        ],
        datasets: [
          {
            label: '개인 협업 내역',
            data: [
              0,
              2,
              4,
              5,
              10,
              8,
              2,
              0,
              0,
              4,
              0,
              5,
              6,
              5,
              0,
              1,
              2,
              3,
              4,
              0,
              0,
              0,
              0,
              0,
            ],
            borderColor: '#0f75f5',
            borderWidth: 4,
            pointRadius: 0,
            pointBackgroundColor: '#fff',
            borderJoinStyle: 'bevel',
            lineTension: 0,
            fill: false,
          },
          {
            label: '전체 협업 내역',
            data: [
              0,
              2,
              4,
              5,
              10,
              8,
              2,
              0,
              0,
              0,
              0,
              0,
              0,
              0,
              0,
              0,
              20,
              22,
              25,
              0,
              0,
              0,
              0,
              0,
            ],
            borderColor: '#bbc8d9',
            borderWidth: 4,
            pointRadius: 0,
            pointBackgroundColor: '#fff',
            borderJoinStyle: 'bevel',
            lineTension: 0,
            fill: false,
          },
        ],
      },
      options: {
        hover: {
          mode: 'nearest',
          intersect: false,
        },
        tooltips: {
          mode: 'nearest',
          intersect: false,
          position: 'average',
          enabled: false,
          custom: custom,
          titleFontSize: '15',
          bodyFontSize: '14',
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: function() {
              return '시간별 완료 협업'
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
              gridLines: {
                display: false,
              },
            },
          ],
          yAxes: [
            {
              ticks: {
                beginAtZero: true,
                min: 0,
                stepSize: 4,
              },
              gridLines: {
                borderDash: [1, 2],
              },
            },
          ],
        },
      },
    })
    console.log(myChart)
  },
}
</script>

<style lang="scss">
#chartjs-tooltip {
  position: absolute;
  width: 128px;
  height: 80px;
  // min-height: 40px;
  color: white;
  background: #3b3b3b;
  // border: 1px solid rgb(52, 65, 81);
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

    thead {
      color: rgb(255, 255, 255);
      font-weight: 400;
      font-size: 15px;
    }

    &::after {
      position: absolute;
      top: 73px;
      right: 50px;
      z-index: 999;
      width: 0;
      height: 0;
      border-top: 9px solid #3b3b3b;
      border-right: 9px solid transparent;
      border-left: 9px solid transparent;
      content: '';
    }
  }

  .tooil-tip-legend {
    width: 10px;
    height: 10px;
    margin-right: 5px;
    margin-left: 5px;
  }
}

.chart-legend {
  display: flex;
  align-items: center;
  height: 68px;
  padding-left: 30px;

  & > .legend.round.grey {
    &::before {
      border: 4px solid #bbc8d9;
    }
  }
}
</style>
