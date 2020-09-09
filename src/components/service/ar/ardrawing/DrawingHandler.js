import { SIGNAL, AR_DRAWING, AR_DRAWING_ACTION } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'

export default {
  data() {
    return {
      winResizeID: 0,
    }
  },
  methods: {
    appendListener() {
      const canvas = this.canvas
      const cursor = this.cursor

      // history stack
      canvas.on('object:added', event => {
        const object = event.target

        // const objID = canvas.getObjects().length - 1
        let objectLength = canvas.getObjects().length
        let objID
        if (objectLength === 1) {
          objID = 0
        } else {
          objID = canvas.getObjects()[objectLength - 2].id + 1
        }

        object.set({
          id: objID,
          tId: this.undoList.length,
        })
        this.stackAdd('add', object.id)
      })

      canvas.on('mouse:down', event => {
        const mouse = canvas.getPointer(event.e)

        if (canvas.isDrawingMode) {
          canvas.onDrag = true

          const object = {
            id: canvas.getObjects().length,
            left: mouse.x,
            top: mouse.y,
          }
          this._sendAction(AR_DRAWING_ACTION.LINE_DOWN, object)
        }
      })

      canvas.on('mouse:move', event => {
        const mouse = canvas.getPointer(event.e)

        if (
          mouse.x > canvas.width ||
          mouse.x < 0 ||
          mouse.y > canvas.height ||
          mouse.y < 0
        ) {
          return false
        }

        if (canvas.isDrawingMode) {
          if (canvas.onDrag) {
            const object = {
              id: canvas.getObjects().length,
              left: mouse.x,
              top: mouse.y,
            }
            this._sendAction(AR_DRAWING_ACTION.LINE_MOVE, object)
          }

          if (cursor) {
            cursor
              .set({ visible: true, top: mouse.y, left: mouse.x })
              .setCoords()
            cursor.canvas.renderAll()
          }
        }
      })

      canvas.on('mouse:up', event => {
        const mouse = canvas.getPointer(event.e)

        // 드로우 객체 삭제 버튼 이벤트
        if (event.transform && event.transform.corner === 'mt') {
          this.removeObject(event.target)
          return false
        }

        if (canvas.onDrag === true) {
          canvas.onDrag = false

          const object = {
            id: canvas.getObjects().length - 1,
            left: mouse.x,
            top: mouse.y,
          }
          this._sendAction(AR_DRAWING_ACTION.LINE_UP, object)
        }
      })

      canvas.on('mouse:out', event => {
        // prevent freedrawing outside of canvas boundaries
        if (canvas._isCurrentlyDrawing) {
          canvas._isCurrentlyDrawing = false
          canvas.freeDrawingBrush.onMouseUp(event)
          canvas.trigger('mouse:up', { target: canvas })
          return
        }

        // put circle off screen
        if (canvas.isDrawingMode) {
          if (cursor) {
            cursor
              .set({ visible: false, top: cursor.top, left: cursor.left })
              .setCoords()
            cursor.canvas.renderAll()
          }
        }
      })
    },
    receiveDrawing(receive) {
      const data = JSON.parse(receive.data)
      if (data.from === this.account.uuid) return

      if (data.type === AR_DRAWING.UNDO_ABLE) {
        this.$eventBus.$emit(`tool:undo`, data.isAvailable)
      }
      if (data.type === AR_DRAWING.REDO_ABLE) {
        this.$eventBus.$emit(`tool:redo`, data.isAvailable)
      }
      if (data.type === AR_DRAWING.CLEAR_ABLE) {
        this.$eventBus.$emit(`tool:clear`, data.isAvailable)
      }
    },
  },

  /* Lifecycles */
  created() {
    this.$call.addListener(SIGNAL.AR_DRAWING, this.receiveDrawing)
    this.$eventBus.$on(`control:${ACTION.AR_DRAWING}:undo`, this.stackUndo)
    this.$eventBus.$on(`control:${ACTION.AR_DRAWING}:redo`, this.stackRedo)
    this.$eventBus.$on(`control:${ACTION.AR_DRAWING}:clear`, this.drawingClear)
  },
  beforeDestroy() {
    this.$eventBus.$off(`control:${ACTION.AR_DRAWING}:undo`, this.stackUndo)
    this.$eventBus.$off(`control:${ACTION.AR_DRAWING}:redo`, this.stackRedo)
    this.$eventBus.$off(`control:${ACTION.AR_DRAWING}:clear`, this.drawingClear)
  },
}
