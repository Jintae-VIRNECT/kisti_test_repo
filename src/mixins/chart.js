import Chart from 'chart.js'
import { hourLabels } from 'utils/chartDatas'

export default {
  methods: {
    initMonthlyChart(chartId) {
      this.setRoundedBar()

      const custom = this.initTooltips(chartId, 'tooltip-no-arrow')
      return {
        type: 'roundedBar',
        data: {
          labels: [],
          datasets: [
            {
              label: this.$t('chart.my_collabo_list'),
              data: this.monthly ? this.monthly.my.set : [],
              backgroundColor: '#203cdd',
              barThickness: 9,
            },
            {
              label: this.$t('chart.total_collabo_list'),
              data: this.monthly ? this.monthly.total.set : [],
              backgroundColor: '#6ed6f1',
              barThickness: 9,
            },
          ],
        },
        options: this.getOptionMonthly(custom),
      }
    },
    initDailyChart(chartId) {
      const custom = this.initTooltips(chartId, 'tooltip-arrow', 'inner')
      const chartData = {
        type: 'line',
        data: {
          labels: hourLabels,
          datasets: [
            {
              label: this.$t('chart.my_collabo_list'),
              data: this.daily ? this.daily.my.set : [],
              borderColor: '#0f75f5',
              borderWidth: 4,
              pointRadius: 0,
              pointBackgroundColor: '#ffffff',
              borderJoinStyle: 'bevel',
              lineTension: 0,
              fill: false,
              hoverBorderWidth: 4,
            },
            {
              label: this.$t('chart.total_collabo_list'),
              data: this.daily ? this.daily.total.set : [],
              borderColor: '#bbc8d9',
              borderWidth: 4,
              pointRadius: 0,
              pointBackgroundColor: '#ffffff',
              borderJoinStyle: 'bevel',
              lineTension: 0,
              fill: false,
              hoverBorderWidth: 4,
            },
          ],
        },
        options: this.getOptionDaily(custom),
      }
      return chartData
    },
    initTooltips(chartId, toolTipId, legendShape) {
      return tooltipModel => {
        // Tooltip Element
        let tooltipEl = document.getElementById(toolTipId)

        // Create element on first render
        if (!tooltipEl) {
          tooltipEl = document.createElement('div')
          tooltipEl.id = toolTipId
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

        const getBody = bodyItem => {
          return bodyItem.lines
        }

        // Set Text
        if (tooltipModel.body) {
          let titleLines = tooltipModel.title || []
          let bodyLines = tooltipModel.body.map(getBody)

          let innerHtml = '<thead class="chartjs-tooltip-head">'

          titleLines.forEach(title => {
            innerHtml += '<tr><th><p>' + title + '</p></th></tr>'
          })
          innerHtml += '</thead><tbody>'

          bodyLines.forEach((body, i) => {
            let colors = tooltipModel.labelColors[i]
            let style = 'background:' + colors.backgroundColor
            style += '; border-color:' + colors.borderColor
            style += '; border-width: 1px'

            if (legendShape === 'inner') {
              let span =
                '<span class="tool-tip-legend" style="display:inline-block; background:#fff; border-radius: 50%; border: 3px solid' +
                colors.borderColor +
                '"></span><span style="' +
                style +
                '"></span>'
              innerHtml += '<tr><td>' + span + body + '</td></tr>'
            } else {
              let span =
                '<span class="tool-tip-legend" style="display:inline-block; background:' +
                colors.backgroundColor +
                '"></span><span style="' +
                style +
                '"></span>'
              innerHtml += '<tr><td>' + span + body + '</td></tr>'
            }
          })
          innerHtml += '</tbody>'

          let tableRoot = tooltipEl.querySelector('table')
          tableRoot.innerHTML = innerHtml
        }

        // `this` will be the overall tooltip
        let position = document.getElementById(chartId).getBoundingClientRect()

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
    },

    setRoundedBar() {
      Chart.helpers.drawRoundedTopRectangle = (
        ctx,
        x,
        y,
        width,
        height,
        radius,
      ) => {
        ctx.beginPath()
        ctx.moveTo(x + radius, y)
        // top right corner
        ctx.lineTo(x + width - radius, y)
        ctx.quadraticCurveTo(x + width, y, x + width, y + radius)
        // bottom right   corner
        ctx.lineTo(x + width, y + height)
        // bottom left corner
        ctx.lineTo(x, y + height)
        // top left
        ctx.lineTo(x, y + radius)
        ctx.quadraticCurveTo(x, y, x + radius, y)
        ctx.closePath()
      }

      Chart.elements.RoundedTopRectangle = Chart.elements.Rectangle.extend({
        draw: function() {
          let ctx = this._chart.ctx
          let vm = this._view
          let left, right, top, bottom, signX, signY, borderSkipped
          let borderWidth = vm.borderWidth

          if (!vm.horizontal) {
            // bar
            left = vm.x - vm.width / 2
            right = vm.x + vm.width / 2
            top = vm.y
            bottom = vm.base
            signX = 1
            signY = bottom > top ? 1 : -1
            borderSkipped = vm.borderSkipped || 'bottom'
          } else {
            // horizontal bar
            left = vm.base
            right = vm.x
            top = vm.y - vm.height / 2
            bottom = vm.y + vm.height / 2
            signX = right > left ? 1 : -1
            signY = 1
            borderSkipped = vm.borderSkipped || 'left'
          }

          // Canvas doesn't allow us to stroke inside the width so we can
          // adjust the sizes to fit if we're setting a stroke on the line
          // console.log('borderWidth::', borderWidth)
          if (borderWidth) {
            // borderWidth shold be less than bar width and bar height.
            var barSize = Math.min(
              Math.abs(left - right),
              Math.abs(top - bottom),
            )
            borderWidth = borderWidth > barSize ? barSize : borderWidth

            var halfStroke = borderWidth / 2

            // Adjust borderWidth when bar top position is near vm.base(zero).
            var borderLeft =
              left + (borderSkipped !== 'left' ? halfStroke * signX : 0)
            var borderRight =
              right + (borderSkipped !== 'right' ? -halfStroke * signX : 0)
            var borderTop =
              top + (borderSkipped !== 'top' ? halfStroke * signY : 0)
            var borderBottom =
              bottom + (borderSkipped !== 'bottom' ? -halfStroke * signY : 0)

            // not become a vertical line?
            if (borderLeft !== borderRight) {
              // console.log('not become a vertical line?')
              top = borderTop
              bottom = borderBottom
            }
            // not become a horizontal line?
            if (borderTop !== borderBottom) {
              // console.log('not become a horizontal line?')
              left = borderLeft
              right = borderRight
            }
          }

          // calculate the bar width and roundess
          var barWidth = Math.abs(left - right)
          var roundness = this._chart.config.options.barRoundness || 0.5
          var radius = barWidth * roundness * 0.5

          // keep track of the original top of the bar
          var prevTop = top

          // move the top down so there is room to draw the rounded top
          top = prevTop + radius
          var barRadius = top - prevTop

          ctx.beginPath()
          ctx.fillStyle = vm.backgroundColor
          ctx.strokeStyle = vm.borderColor
          ctx.lineWidth = borderWidth

          // draw the rounded top rectangle
          Chart.helpers.drawRoundedTopRectangle(
            ctx,
            left,
            top - barRadius + 1,
            barWidth,
            bottom - barRadius + 2,
            barRadius,
          )

          ctx.fill()
          if (borderWidth) {
            ctx.stroke()
          }

          // restore the original top value so tooltips and scales still work
          top = prevTop
        },
      })

      Chart.defaults.roundedBar = Chart.helpers.clone(Chart.defaults.bar)

      Chart.controllers.roundedBar = Chart.controllers.bar.extend({
        dataElementType: Chart.elements.RoundedTopRectangle,
      })
    },

    getOptionDaily(custom) {
      return {
        maintainAspectRatio: false,
        aspectRatio: 5,
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
          titleFontSize: '15rem',
          bodyFontSize: '14rem',
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: () => {
              return this.$t('chart.collabo_count_by_time')
            },
            label: tooltipItem => {
              return this.$t('chart.count', {
                count: Number(tooltipItem.yLabel),
              })
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
                // zeroLineWidth: 0,
                // zeroLineBorderDashOffset: 1,
                drawBorder: false,
              },
            },
          ],
        },
      }
    },

    getOptionMonthly(custom) {
      return {
        maintainAspectRatio: false,
        aspectRatio: 5,
        barRoundness: 1.2,
        hover: {
          mode: 'index',
        },
        tooltips: {
          mode: 'index',
          position: 'average',
          enabled: false,
          custom: custom,
          titleFontSize: '15rem',
          bodyFontSize: '14rem',
          displayColors: false,
          backgroundColor: '#516277',
          bodyFontStyle: 'bold',
          callbacks: {
            title: () => {
              return this.$t('chart.collabo_count_by_day')
            },
            label: tooltipItem => {
              return this.$t('chart.count', {
                count: Number(tooltipItem.yLabel),
              })
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
              stacked: true,
              gridLines: {
                borderDash: [1, 2],
                drawBorder: false,
              },
            },
          ],
        },
      }
    },

    toggle(chart, target) {
      if (chart) {
        let index = -1
        switch (target) {
          case 'my':
            index = 0
            break
          case 'total':
            index = 1
            break
          default:
            return
        }
        const hidden = chart.data.datasets[index].hidden
        chart.data.datasets[index].hidden = !hidden
        chart.update()
      }
    },
  },

  beforeMount() {
    Chart.defaults.global.defaultFontFamily = 'Roboto-Regular'
    Chart.defaults.global.defaultFontColor = 'rgb(136, 136, 136)'
    Chart.defaults.global.defaultFontSize = 12
  },
}
