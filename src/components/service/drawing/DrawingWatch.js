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
      //협업보드 탭에 진입 시 실행 (첫진입 포함)
      if (val !== oldVal && val === VIEW.DRAWING) {
        this.$nextTick(() => {
          if (!this.isInit) return

          /*
            캔버스 사이즈를 먼저 맞춘 후,
            수신해왔던 드로잉 객체들을 추가해주어야 한다

            이 타이밍을 nextTick만으로 맞춰지지 않아 아래와 같은 이슈가 발생하여, timeout을 통한 조정하였음
            - 실시간 공유 전체화면 모드에서 협업보드로 진입시 드로잉 보드 크기 이슈 발생
            - 협업보드 첫 진입 시 이전 드로잉 추가되지 않는 이슈 발생
          */
          setTimeout(() => {
            this.optimizeCanvasSize()
            this.receiveRender()
          }, 300)
        })
      }
      // 협업보드 탭에서 이탈 시 기존 줌 상태 초기화
      else if (oldVal === VIEW.DRAWING) {
        if (this.canvas)
          setTimeout(
            () => this.canvas.setViewportTransform([1, 0, 0, 1, 0, 0]),
            500,
          )
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
