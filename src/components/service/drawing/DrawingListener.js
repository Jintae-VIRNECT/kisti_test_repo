import { fabric } from 'plugins/remote/fabric.custom'
import { ahexToRGBA } from 'utils/color'
import { getReceiveParams, calcPosition } from 'utils/drawing'
import { SIGNAL } from 'plugins/remote/call/remote.config'

export default {
  data() {
    return {
      winResizeID: 0,
      receivePath: [],
    }
  },
  methods: {
    drawingListener(receive) {
      this.receivePath = []
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      switch (data.type) {
        case 'lineStart':
        case 'lineMove':
        case 'lineEnd':
          this.drawingLine(data)
          break
        case 'drawMove':
          this.drawingMove(data)
          break
        case 'drawText':
          this.drawingText(data)
          break
        case 'updateText':
          this.updateText(data)
          break
        case 'undo':
          this.receiveStackUndo(data)
          break
        case 'redo':
          this.receiveStackRedo(data)
          break
        case 'clearAll':
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

      if (data.type === 'lineStart') {
        this.receivePath = []
      }

      this.receivePath.push(getReceiveParams(data.type, params))

      if (data.type === 'lineEnd') {
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
    drawingMove(data) {
      const object = this.canvas.getObjects().find(_ => _.id === data.oId)

      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }

      params = getReceiveParams('drawMove', params)
      object.set(params)
      this.canvas.renderAll()
    },
    drawingText(data) {
      const params = getReceiveParams('drawText', {
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
