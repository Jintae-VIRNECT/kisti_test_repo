// import { getCanvasSize } from 'utils/drawing'
import { hexToRGBA } from 'utils/color'

export default {
  watch: {
    file: {
      deep: true,
      handler(value) {
        if (value && value.img) {
          setTimeout(() => {
            this.initCanvas()
          }, 500)
        }
      },
    },
    'tools.color'(color) {
      if (this.canvas) {
        this.canvas.freeDrawingBrush.color = hexToRGBA(color, 1)
      }
      if (this.cursor) {
        this.cursor.setColor(hexToRGBA(color, 1))
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
