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
          setTimeout(() => this.initCanvas(), 500)
        }
      },
    },
    view(val, oldVal) {
      if (val !== oldVal && val === VIEW.DRAWING) {
        setTimeout(() => this.optimizeCanvasSize(), 500)
        this.$nextTick(() => {
          if (!this.isInit) return
          this.receiveRender()
        })
      }
    },
    viewAction(value) {
      if (this.view !== VIEW.DRAWING) return
      if (this.canvas) {
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
    //사용자가 드로잉 굴기를 변경할때마다 드로잉 브러쉬 크기, 커서 크기를 캔버스 크기를 기준으로 계산해서 업데이트
    'tools.lineWidth'(lineWidth, oldLineWidth) {
      if (lineWidth !== oldLineWidth) this.updateCanvasBrushWidth(lineWidth)
    },
    undoList() {
      this.toolAble()
    },
    redoList() {
      this.toolAble()
    },
  },
  computed: {
    isDrawingView() {
      return this.view === VIEW.DRAWING
    },
  },
  methods: {
    toolAble() {
      if (this.undoList.length > 0 || this.redoList.length > 0) {
        this.$eventBus.$emit('tool:clear', true)
        this.$eventBus.$emit('tool:clearall', true)
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
        if (
          Object.keys(this.receiveUndoList).length > 0 ||
          Object.keys(this.receiveRedoList).length > 0
        ) {
          this.$eventBus.$emit('tool:clearall', true)
        } else {
          this.$eventBus.$emit('tool:clearall', false)
        }
        this.$eventBus.$emit('tool:undo', false)
        this.$eventBus.$emit('tool:redo', false)
        this.$eventBus.$emit('tool:clear', false)
      }
    },
  },
}
