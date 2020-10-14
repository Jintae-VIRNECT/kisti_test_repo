<template>
  <section class="board-daily">
    <div class="board-daily__header">
      <span class="board-daily__header--title">일일 협업</span>
      <span class="board-daily__header--description">
        설정한 날짜의 일일 협업 내용을 보여줍니다.
      </span>
      <datepicker
        class="board-daily__header--datepicker"
        :pickerName="'daily'"
        :minimumView="'day'"
        :maximumView="'day'"
      ></datepicker>
    </div>
    <div class="board-daily__body">
      <card :customClass="'custom-card-chart'">
        <div name="header">asdf</div>
        <div class="chart-holder">
          <canvas id="myChart" width="890" height="260"></canvas></div
      ></card>
      <card-board
        :count="5"
        :countDescription="'일별 총 협업 수'"
        :time="5219611"
        :timeDescription="'일별 총 협업 시간'"
      ></card-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import CardBoard from 'CardBoard'
import Datepicker from 'Datepicker'
import Chart from 'chart.js'
export default {
  name: 'BoardDaily',
  components: {
    Card,
    CardBoard,
    Datepicker,
  },
  mounted() {
    const ctx = document.getElementById('myChart').getContext('2d')
    const gradientFill = ctx.createLinearGradient(0, 500, 0, 100)
    gradientFill.addColorStop(1, 'rgba(137,187,250, 1)')
    gradientFill.addColorStop(0, 'rgba(255, 255, 255, 0.6)')

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

        let innerHtml = '<thead>'

        titleLines.forEach(function(title) {
          innerHtml += '<tr><th>' + title + '</th></tr>'
        })
        innerHtml += '</thead><tbody>'

        bodyLines.forEach(function(body, i) {
          let colors = tooltipModel.labelColors[i]
          let style = 'background:' + colors.backgroundColor
          style += '; border-color:' + colors.borderColor
          style += '; border-width: 1px'
          let span = '<span style="' + style + '"></span>'
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
    console.log(custom)
    let myChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['06', '08', '10', '12', '14', '16', '18', '20', '22', '24'],
        datasets: [
          {
            label: '',
            data: [0, 0, 2, 0, 6, 8, 2, 0, 0, 0],
            backgroundColor: gradientFill,
            borderColor: '#0f75f5',
            borderWidth: 4,
            pointRadius: 4,
            pointBackgroundColor: '#fff',
            fill: 'start',
            borderJoinStyle: 'bevel',
            lineTension: 0,
          },
        ],
      },
      options: {
        hover: {
          mode: 'index',
          intersect: false,
        },
        tooltips: {
          mode: 'index',
          intersect: false,
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
                max: 10,
                min: 0,
                stepSize: 2,
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
.board-daily {
  height: 371px;
  margin-top: 40px;
}

.board-daily__header {
  position: relative;
  height: 60px;
}

.board-daily__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.board-daily__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}

.board-daily__header--datepicker {
  &.datepicker {
    position: absolute;
    top: 0px;
    right: 0px;
  }
}

.board-daily__body {
  display: flex;
}

.custom-card-chart {
  width: 968px;
  height: 310px;
  margin-right: 12px;
}

.chart-holder {
  position: relative;
  width: 64.2857rem;
  height: 19.2857rem;
  padding-left: 30px;
}

#chartjs-tooltip {
  position: absolute;
  width: 132px;
  height: 69px;
  color: white;
  background: rgb(81, 98, 119);
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

    &::after {
      position: absolute;
      top: 60px;
      right: 31px;
      z-index: 999;
      width: 0;
      height: 0;
      border-top: 9px solid rgb(81, 98, 119);
      border-right: 9px solid transparent;
      border-left: 9px solid transparent;
      content: '';
    }

    &::before {
      position: absolute;
      top: 60px;
      right: 29px;
      width: 0;
      height: 0;
      border-top: 11px solid rgb(52, 65, 81);
      border-right: 11px solid transparent;
      border-left: 11px solid transparent;
      content: '';
    }
  }
}
</style>
