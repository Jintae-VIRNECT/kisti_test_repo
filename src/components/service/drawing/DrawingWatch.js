// import { getCanvasSize } from 'utils/drawing'
import { hexToRGBA } from 'utils/color'
import { VIEW } from 'configs/view.config'

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
          this.canvas.isDrawingMode = this.viewAction === 'line'
          this.canvas.freeDrawingCursor =
            this.viewAction === 'text' ? 'text' : 'default'
          this.canvas.defaultCursor =
            this.viewAction === 'text' ? 'text' : 'default'
          this.canvas.renderAll()
        }
      }
    },
    view(val, oldVal) {
      if (val !== oldVal && val === VIEW.DRAWING) {
        this.optimizeCanvasSize()
      }
    },
    viewAction(value) {
      if (this.canvas) {
        this.canvas.isDrawingMode = value === 'line'
        this.canvas.freeDrawingCursor = value === 'text' ? 'text' : 'default'
        // this.canvas.defaultCursor = (value === 'text') ? 'text' : 'default'
        let cursor
        if (value === 'text') {
          cursor = 'text'
        } else if (this.viewAction === 'zoom') {
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
  },
}
