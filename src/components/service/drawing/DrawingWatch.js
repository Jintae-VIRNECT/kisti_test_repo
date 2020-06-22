// import { getCanvasSize } from 'utils/drawing'
import { hexToRGBA } from 'utils/color'
import { VIEW } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'

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
    view(val, oldVal) {
      if (val !== oldVal && val === VIEW.DRAWING) {
        this.optimizeCanvasSize()
      }
    },
    viewAction(value) {
      if (this.view !== VIEW.DRAWING) return
      if (this.canvas && this.account.roleType === ROLE.EXPERT_LEADER) {
        this.canvas.isDrawingMode = value === 'line'
        this.canvas.freeDrawingCursor = value === 'text' ? 'text' : 'default'
        // this.canvas.defaultCursor = (value === 'text') ? 'text' : 'default'
        let cursor
        if (value === 'text') {
          cursor = 'text'
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
    undoList() {
      this.toolAble()
    },
    redoList() {
      this.toolAble()
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
