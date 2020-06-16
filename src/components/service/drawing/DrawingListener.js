import { fabric } from 'plugins/remote/fabric.custom'
import { ahexToRGBA } from 'utils/color'
import { getReceiveParams, calcPosition } from 'utils/drawing'
import { SIGNAL } from 'configs/remote.config'
import { EVENT } from 'configs/drawing.config'

export default {
  data() {
    return {
      winResizeID: 0,
      receivePath: [],
    }
  },
  methods: {
    drawingListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      switch (data.type) {
        case EVENT.LINE_DOWN:
        case EVENT.LINE_MOVE:
        case EVENT.LINE_UP:
          this.drawingLine(data)
          break
        case EVENT.TEXT_ADD:
          this.drawingText(data)
          break
        case EVENT.TEXT_UPDATE:
          this.updateText(data)
          break
        case EVENT.UNDO:
          this.receiveStackUndo(data)
          break
        case EVENT.REDO:
          this.receiveStackRedo(data)
          break
        case EVENT.CLEAR_ALL:
          this.clearAll(data)
          break
      }
    },
    drawingLine(data) {
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }

      if (data.type === EVENT.LINE_DOWN) {
        this.receivePath = []
      }

      this.receivePath.push(getReceiveParams(data.type, params))

      if (data.type === EVENT.LINE_UP) {
        const width = parseInt(data.width) / params.scale
        const pos = calcPosition(this.receivePath, width)
        const path = new fabric.Path(this.receivePath, {
          left: pos.left,
          top: pos.top,
          fill: null,
          stroke: ahexToRGBA(data.color), //data.color,
          strokeWidth: width,
          strokeMiterLimit: width,
          strokeLineCap: 'round',
          strokeLineJoin: 'round',
          owner: data.from,
          hasControls: false,
          selectable: false,
          hoverCursor: 'default',
        })
        // path.set()
        this.canvas.add(path)
        this.canvas.renderAll()
        this.$nextTick(() => {
          this.receivePath = []
        })
      }
    },
    drawingText(data) {
      const params = getReceiveParams(EVENT.TEXT_ADD, {
        ...data,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      })
      const object = new fabric.IText(data.text, {
        left: params.posX,
        top: params.posY,
        fill: ahexToRGBA(params.color),
        fontFamily: this.fontFamily,
        fontStyle: this.fontStyle,
        fontWeight: this.fontWeight,
        fontSize: params.size,
        lineHeight: this.lineHeight,
        hasControls: false,
        selectable: false,
        hoverCursor: 'auto',
        owner: data.from,
      })
      this.canvas.add(object)
      this.canvas.renderAll()
    },
    updateText(data) {
      const object = this.canvas.getObjects().find(_ => _.id === data.oId)
      object.set({
        text: data.text,
      })
      this.canvas.renderAll()
    },
    clearAll(data) {
      this.canvas.getObjects().forEach(object => {
        if (object.owner === data.from) {
          object.canvas.remove(object)
        }
      })
      this.canvas.renderAll()
      delete this.receiveUndoList[data.from]
      delete this.receiveRedoList[data.from]
    },
  },

  /* Lifecycles */
  created() {
    if (this.$call) {
      this.$call.addListener(SIGNAL.DRAWING, this.drawingListener)
    }
  },
  beforeDestroy() {},
}
