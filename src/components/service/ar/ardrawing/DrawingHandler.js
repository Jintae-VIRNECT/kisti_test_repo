import { AR_DRAWING } from 'configs/remote.config'
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
          this._sendAction(AR_DRAWING.LINE_DOWN, object)
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
            this._sendAction(AR_DRAWING.LINE_MOVE, object)
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
          this._sendAction(AR_DRAWING.LINE_UP, object)
        }
      })

      canvas.on('mouse:out', () => {
        // prevent freedrawing outside of canvas boundaries
        if (canvas._isCurrentlyDrawing) {
          canvas._isCurrentlyDrawing = false
          canvas.freeDrawingBrush.onMouseUp()
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
  },

  /* Lifecycles */
  created() {
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
