<template>
  <section class="board-monthly">
    <div class="board-monthly__header">
      <span class="board-monthly__header--title">월별 협업</span>
      <span class="board-monthly__header--description">
        설정한 기간의 협업 내용을 보여줍니다.
      </span>
      <datepicker
        class="board-daily__header--datepicker"
        :pickerName="'monthly'"
        :minimumView="'month'"
        :maximumView="'month'"
        :format="'yyyy-MM'"
      ></datepicker>
    </div>
    <div class="board-monthly__body">
      <card :customClass="'custom-card-chart'">
        <div name="header">asdf</div>
        <div class="chart-holder">
          <canvas id="chart-month" width="890" height="260"></canvas>
        </div>
      </card>
      <card-board
        :count="5"
        :countDescription="'월별 총 협업 수'"
        :time="36555"
        :timeDescription="'월별 총 협업 시간'"
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
  name: 'BoardMonthly',
  components: {
    Card,
    CardBoard,
    Datepicker,
  },
  mounted() {
    const ctx = document.getElementById('chart-month').getContext('2d')
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
      type: 'bar',
      data: {
        labels: ['09.11', '08', '10', '12', '14', '16', '18', '20', '22', '24'],
        datasets: [
          {
            label: '',
            data: [7, 5, 10, 10, 11, 10, 2, 11, 5, 5],
            backgroundColor: '#bdcade',
            borderColor: 'none',
            borderWidth: 4,
            pointRadius: 4,
            pointBackgroundColor: '#fff',
            fill: 'start',
            borderJoinStyle: 'bevel',
            lineTension: 0,
            hoverBackgroundColor: '#0f75f5',
            hoverBorderColor: 'none',
            barThickness: 11,
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
            // { barPercentage: 0.4 },
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
                max: 15,
                min: 0,
                stepSize: 5,
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
.board-monthly {
  height: 371px;
  margin-top: 40px;
}
.board-monthly__header {
  position: relative;
  height: 60px;
}

.board-monthly__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.board-monthly__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}

.board-monthly__header--datepicker {
  &.datepicker {
    position: absolute;
    top: 0px;
    right: 0px;
  }
}

.board-monthly__body {
  display: flex;
}

.custom-card-chart {
  width: 968px;
  height: 310px;
  margin-right: 12px;
}

.chart-holder {
  width: 900px;
  height: 270px;
  padding-left: 30px;
}
</style>
