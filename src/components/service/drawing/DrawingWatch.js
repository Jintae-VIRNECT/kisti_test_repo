// import { getCanvasSize } from 'utils/drawing'
import { hexToRGBA } from 'utils/color'
import { VIEW, ACTION } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'

export default {
  watch: {
    file: {
      deep: true,
      handler(value) {
        this.isInit = false
        if (value && value.id) {
          this.$emit('loadingStart')
          setTimeout(() => {
            this.initCanvas()
          }, 500)
        }
      },
    },
    view(val, oldVal) {
      if (val !== oldVal && val === VIEW.DRAWING) {
        this.optimizeCanvasSize()
        this.$nextTick(() => {
          this.receiveRender()
        })
      }
    },
    viewAction(value) {
      if (this.view !== VIEW.DRAWING) return
      if (this.canvas && this.account.roleType === ROLE.LEADER) {
        this.canvas.isDrawingMode = value === ACTION.DRAWING_LINE
        this.canvas.freeDrawingCursor =
          value === ACTION.DRAWING_TEXT ? 'text' : 'default'
        this.canvas.defaultCursor =
          value === ACTION.DRAWING_TEXT ? 'text' : 'default'
        // let cursor
        // if (value === 'text') {
        //   cursor = 'text'
        // }
        // this.canvas.defaultCursor = cursor
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
      if (this.textObj) {
        this.textObj.set(
          'fill',
          hexToRGBA(this.tools.color, this.tools.opacity),
        )
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
      if (this.textObj) {
        this.textObj.set(
          'fill',
          hexToRGBA(this.tools.color, this.tools.opacity),
        )
      }
    },
    scaleWidth(size) {
      if (this.canvas) {
        this.canvas.freeDrawingBrush.width = size
      }
      if (this.cursor) {
        this.cursor.setRadius(size / 2)
      }
    },
    'tools.lineWidth'(size) {
      this.scaleWidth = size / this.origin.scale
      // if (this.canvas) {
      //   this.canvas.freeDrawingBrush.width = this.scaleWidth
      // }
      // if (this.cursor) {
      //   this.cursor.setRadius(this.scaleWidth / 2)
      // }
    },
    'tools.fontSize'(size) {
      this.scaleFont = size / this.origin.scale
      if (this.textObj) {
        this.textObj.set('fontSize', this.scaleFont)
      }
    },
    undoList() {
      this.toolAble()
    },
    redoList() {
      this.toolAble()
    },
  },
  computed: {
    drawingView() {
      return this.view === VIEW.DRAWING
    },
  },
  methods: {
    toolAble() {
      if (this.undoList.length > 0 || this.redoList.length > 0) {
        this.$eventBus.$emit('tool:clear', true)
        if (this.undoList.length === 0) {
          this.$eventBus.$emit('tool:undo', false)
          this.$eventBus.$emit('tool:redo', true)
        } else if (this.redoList.length === 0) {
          this.$eventBus.$emit('tool:undo', true)
          this.$eventBus.$emit('tool:redo', false)
        } else {
          this.$eventBus.$emit('tool:undo', true)
          this.$eventBus.$emit('tool:redo', true)
        }
      } else {
        this.$eventBus.$emit('tool:undo', false)
        this.$eventBus.$emit('tool:redo', false)
        this.$eventBus.$emit('tool:clear', false)
      }
    },
  },
}
