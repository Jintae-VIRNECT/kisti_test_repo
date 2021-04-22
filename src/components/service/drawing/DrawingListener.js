import { fabric } from 'plugins/remote/fabric.custom'
import { ahexToRGBA } from 'utils/color'
import { getReceiveParams, calcPosition } from 'utils/drawing'
import { SIGNAL, DRAWING } from 'configs/remote.config'

export default {
  data() {
    return {
      receivePath: {},
    }
  },
  methods: {
    drawingListener(receive) {
      const data = JSON.parse(receive.data)
      if (receive.from.connectionId === this.myInfo.connectionId) return
      // if (this.account.roleType === ROLE.LEADER) return
      if (
        ![
          DRAWING.LINE_DOWN,
          DRAWING.LINE_MOVE,
          DRAWING.LINE_UP,
          DRAWING.TEXT_ADD,
          DRAWING.UNDO,
          DRAWING.REDO,
          DRAWING.CLEAR,
          DRAWING.CLEAR_ALL,
        ].includes(data.type)
      )
        return
      if (this.drawingView) {
        this.addReceiveObject({ data, owner: receive.from.connectionId })
      } else {
        this.receivedList[receive.from.connectionId].push({
          data,
          owner: receive.from.connectionId,
        })
      }
    },
    addReceiveObject({ data, owner }) {
      switch (data.type) {
        case DRAWING.LINE_DOWN:
        case DRAWING.LINE_MOVE:
        case DRAWING.LINE_UP:
          this.drawingLine(data, owner)
          break
        case DRAWING.TEXT_ADD:
          this.drawingText(data, owner)
          break
        case DRAWING.UNDO:
          this.receiveStackUndo(owner)
          break
        case DRAWING.REDO:
          this.receiveStackRedo(owner)
          break
        case DRAWING.CLEAR:
          this.clear(owner)
          break
        case DRAWING.CLEAR_ALL:
          this.clearAll()
          break
      }
    },
    drawingLine(data, owner) {
      let params = {
        posX: data.posX,
        posY: data.posY,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
      }

      if (data.type === DRAWING.LINE_DOWN) {
        this.receivePath[owner] = []
      }
      let receiveParams = getReceiveParams(data.type, params, this.origin.scale)

      this.receivePath[owner].push(receiveParams)

      if (data.type === DRAWING.LINE_UP) {
        const width =
          parseFloat(data.width) * (this.origin.width / this.img.width)
        // const width = parseInt(data.width)
        const pos = calcPosition(this.receivePath[owner], width)
        const path = new fabric.Path(this.receivePath[owner], {
          left: pos.left,
          top: pos.top,
          fill: null,
          stroke: ahexToRGBA(data.color), //data.color,
          strokeWidth: width,
          strokeMiterLimit: width,
          strokeLineCap: 'round',
          strokeLineJoin: 'round',
          owner: owner,
          hasControls: false,
          selectable: false,
          hoverCursor: 'auto',
        })
        // path.set()
        this.canvas.add(path)
        this.canvas.renderAll()
        this.backCanvas.add(fabric.util.object.clone(path))
        this.backCanvas.renderAll()
        this.$nextTick(() => {
          delete this.receivePath[owner]
        })
      }
    },
    drawingText(data, owner) {
      const params = getReceiveParams(
        DRAWING.TEXT_ADD,
        {
          ...data,
          scale: 1 / this.canvas.backgroundImage.scaleX,
          imgWidth: this.canvas.getWidth(),
          imgHeight: this.canvas.getHeight(),
          sizeScale: this.origin.width / this.img.width,
        },
        this.origin.scale,
      )
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
        owner: owner,
      })
      this.canvas.add(object)
      this.canvas.renderAll()
      this.backCanvas.add(fabric.util.object.clone(object))
      this.backCanvas.renderAll()
    },
    clear(owner) {
      this.canvas.getObjects().forEach(object => {
        if (object.owner === owner) {
          object.canvas.remove(object)
        }
      })
      this.backCanvas.getObjects().forEach(object => {
        if (object.owner === owner) {
          object.canvas.remove(object)
        }
      })
      this.canvas.renderAll()
      this.backCanvas.renderAll()
      delete this.receiveUndoList[owner]
      delete this.receiveRedoList[owner]

      this.toolAble()
    },
    clearAll() {
      this.canvas.remove(...this.canvas.getObjects())
      this.backCanvas.remove(...this.backCanvas.getObjects())
      this.canvas.renderAll()
      this.backCanvas.renderAll()
      this.stackClear()
      this.receivedStackClear()
    },
  },

  /* Lifecycles */
  created() {
    if (this.$call) {
      this.$eventBus.$on(SIGNAL.DRAWING, this.drawingListener)
    }
  },
  beforeDestroy() {
    this.$eventBus.$off(SIGNAL.DRAWING, this.drawingListener)
  },
}
