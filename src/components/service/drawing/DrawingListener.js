import { fabric } from 'plugins/remote/fabric.custom'
import { ahexToRGBA, hexToRGBA } from 'utils/color'
import { getReceiveParams, calcPosition } from 'utils/drawing'

export default {
  data() {
    return {
      winResizeID: 0,
      receivePath: [],
    }
  },
  methods: {
    lineStartListener(receive) {
      this.receivePath = []
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return
      // 드로잉
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }
      this.receivePath.push(getReceiveParams('lineStart', params))
    },
    lineMoveListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }
      this.receivePath.push(getReceiveParams('lineMove', params))
    },
    lineEndListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }
      const width = parseInt(data.width) / params.scale
      this.receivePath.push(getReceiveParams('lineEnd', params))
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
    },
    drawMoveListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

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
    drawTextListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

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
    updateTextListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      const object = this.canvas.getObjects().find(_ => _.id === data.oId)
      object.set({
        text: data.text,
      })
      this.canvas.renderAll()
    },
    undoListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      this.receiveStackUndo(data.from)
    },
    redoListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      this.receiveStackRedo(data.from)
    },
    clearAllListener(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

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
      this.$call.addListener('signal:lineStart', this.lineStartListener)
      this.$call.addListener('signal:lineMove', this.lineMoveListener)
      this.$call.addListener('signal:lineEnd', this.lineEndListener)

      this.$call.addListener('signal:drawText', this.drawTextListener)
      this.$call.addListener('signal:updateText', this.updateTextListener)

      this.$call.addListener('signal:undo', this.undoListener)
      this.$call.addListener('signal:redo', this.redoListener)
      this.$call.addListener('signal:drawMove', this.drawMoveListener)
      this.$call.addListener('signal:clearAll', this.clearAllListener)
      // this.$call.addListener('signal:clear')
    }
  },
  beforeDestroy() {},
}
