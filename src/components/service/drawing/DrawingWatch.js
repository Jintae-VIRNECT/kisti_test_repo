// import { getCanvasSize } from 'utils/drawing'
import { hexToRGBA } from 'utils/color'

export default {
  watch: {
    file: {
      deep: true,
      handler(value) {
        console.log(value)
        if (value && value.id) {
          setTimeout(() => {
            this.initCanvas()
          }, 500)
        }
      },
    },
    canUseChannel(value) {
      if (this.canvas) {
        if (!value) {
          this.canvas.isDrawingMode = false
          this.canvas.freeDrawingCursor = 'default'
          this.canvas.defaultCursor = 'default'
          this.canvas.renderAll()
        } else {
          this.canvas.isDrawingMode = this.drawingMode === 'line'
          this.canvas.freeDrawingCursor =
            this.drawingMode === 'text' ? 'text' : 'default'
          this.canvas.defaultCursor =
            this.drawingMode === 'text' ? 'text' : 'default'
          this.canvas.renderAll()
        }
      }
    },
    drawingMode(value) {
      if (this.canvas) {
        this.canvas.isDrawingMode = value === 'line'
        this.canvas.freeDrawingCursor = value === 'text' ? 'text' : 'default'
        // this.canvas.defaultCursor = (value === 'text') ? 'text' : 'default'
        let cursor
        if (value === 'text') {
          cursor = 'text'
        } else if (this.drawingMode === 'zoom') {
          cursor = 'zoom-out'
        }
        this.canvas.defaultCursor = cursor
        this.canvas.renderAll()
      }
    },
    'tools.color'(color) {
      if (this.canvas) {
        this.canvas.freeDrawingBrush.color = hexToRGBA(
          color,
          this.tools.opacity,
        )
      }
      if (this.cursor) {
        this.cursor.setColor(hexToRGBA(color, this.tools.opacity))
      }
    },
    'tools.opacity'(opacity) {
      if (this.canvas) {
        this.canvas.freeDrawingBrush.color = hexToRGBA(
          this.tools.color,
          opacity,
        )
      }
      if (this.cursor) {
        this.cursor.setColor(hexToRGBA(this.tools.color, opacity))
      }
    },
    'tools.lineWidth'(size) {
      if (this.canvas) {
        this.canvas.freeDrawingBrush.width = size
      }
      if (this.cursor) {
        this.cursor.setRadius(size / 2)
      }
    },
    // shareDocSelect: {
    //   handler(select, oldSelect) {
    //     if (select && oldSelect && select.id !== oldSelect.id) {
    //       if (this.canvas) {
    //         this.canvas.discardActiveObject()
    //       }
    //     }

    //     if (select) {
    //       const canvas = this.canvas
    //       const json = select.json

    //       if (
    //         oldSelect === null ||
    //         JSON.stringify(canvas.toJSON()) !== JSON.stringify(json)
    //       ) {
    //         canvas.loadFromJSON(json, () => {
    //           const parent = this.$el.parentNode
    //           const canvasSize = getCanvasSize(
    //             parent.offsetWidth,
    //             parent.offsetHeight,
    //             json.backgroundImage.width,
    //             json.backgroundImage.height,
    //           )
    //           canvas.setWidth(canvasSize.width)
    //           canvas.setHeight(canvasSize.height)
    //           canvas.backgroundImage.set({
    //             scaleX: canvasSize.scale,
    //             scaleY: canvasSize.scale,
    //           })
    //           canvas.renderAll.bind(canvas)()

    //           this.stackClear()

    //           if (this.canvas.getObjects().length > 0) {
    //             this.stackAddFirst()
    //           }
    //         })
    //       }
    //     }
    //   },
    //   deep: true,
    // },
  },
}
