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
