import { fabric } from 'plugins/remote/fabric.custom'
import { DRAWING, ROLE } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'

export default {
  methods: {
    appendListener() {
      const canvas = this.canvas
      const cursor = this.cursor

      /* Object */
      /* object 선택 시 필요한가????
      canvas.on('object:selected', event => {
        console.log('[Fabric] Object selected')
        const activeObject = canvas.getActiveObject()

        if (activeObject instanceof fabric.IText === true) {
        } else if (activeObject.selectable === true) {
          this.$eventBus.$emit(`control:${this.mode}:mode`, false)
        }
      })
      */
      // history stack
      canvas.on('object:added', event => {
        console.log('[Fabric] Object added')
        this.debug(event)
        const object = event.target

        const objID = canvas.getObjects().length - 1

        // object.set('id',getID('object'));
        // object.set('id',canvas.getObjects().length-1);

        //자신이 그린 드로잉이 아닌 경우
        if (object.owner && object.owner !== this.uuid) {
          if (!(object.owner in this.receiveUndoList)) {
            this.receiveUndoList[object.owner] = []
          }
          object.set({
            id: objID,
            tId: this.receiveUndoList[object.owner].length,
            owner: object.owner,
          })
          this.receiveStackAdd('add', object.id, object.owner)
        }
        //자신이 그린 드로잉인 경우
        else {
          object.set({
            id: objID,
            tId: this.undoList.length,
          })
          if (object instanceof fabric.IText) {
            // this.$remoteSDK.message('drawText', getParam('text', object));
            // this._sendAction('drawText', object);
            return
          }
          this.backCanvas &&
            this.backCanvas.add(fabric.util.object.clone(object))
          this.stackAdd('add', object.id)
        }
      })

      /* TEXT */
      canvas.on('text:editing:entered', () => {
        this.editingMode = true
      })

      /* CANVAS */
      canvas.on('after:render', () => {
        if (this.isInit === true && this.account.roleType === ROLE.LEADER) {
          setTimeout(() => {
            this.updateHistory()
          }, 100)
        }
      })

      //   /* MOUSE */
      // canvas.on('mouse:down:before', event => {
      //   if (!this.canUseChannel) {
      //     this.toastError(this.$t('service.call_history_busy'))
      //     return
      //   }
      // })
      canvas.on('mouse:down', event => {
        const mouse = canvas.getPointer(event.e)
        if (this.zoom === true) {
          canvas.panning = true
          return
        }

        if (canvas.isDrawingMode) {
          canvas.onDrag = true

          const object = {
            id: canvas.getObjects().length,
            left: mouse.x,
            top: mouse.y,
          }
          this._sendAction(DRAWING.LINE_DOWN, object)
        }
      })

      canvas.on('mouse:move', event => {
        const mouse = canvas.getPointer(event.e)
        if (this.zoom === true) {
          if (canvas.panning === true) {
            canvas.defaultCursor = 'grabbing'
            var delta = new fabric.Point(event.e.movementX, event.e.movementY)
            canvas.relativePan(delta)
            this.cursor.canvas.relativePan(delta)
            this.keepPositionInBounds(canvas)
          } else {
            canvas.defaultCursor = 'grab'
          }
          if (event.target !== null) {
            event.target.hoverCursor = canvas.defaultCursor
          }
          return
        }

        if (this.viewAction === ACTION.DRAWING_TEXT) {
          canvas.defaultCursor = 'text'
        }
        if (event.target !== null) {
          event.target.hoverCursor = canvas.defaultCursor
        }

        if (
          mouse.x > this.origin.width ||
          mouse.x < 0 ||
          mouse.y > this.origin.height ||
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
            this._sendAction(DRAWING.LINE_MOVE, object)
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
        if (this.zoom === true) {
          canvas.panning = false
          return
        }

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
          this._sendAction(DRAWING.LINE_UP, object)
        }

        // 텍스트 삽입
        if (
          this.viewAction === ACTION.DRAWING_TEXT &&
          this.editingMode === false &&
          !(canvas.getActiveObject() instanceof fabric.IText)
        ) {
          event.e.preventDefault()
          // this.addTextObject(mouse.x, mouse.y)
          this.addTextObject(
            mouse.x,
            mouse.y - this.tools.fontSize / this.origin.scale / 2 - 1,
          )
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
      canvas.on('mouse:wheel', opt => {
        if (canvas.onDrag === true) return
        const delta = opt.e.deltaY
        let zoom = canvas.getZoom() // / this.origin.scale
        zoom *= 0.999 ** delta
        if (zoom > 5 * this.origin.scale) zoom = 5 * this.origin.scale
        if (zoom < 1 * this.origin.scale) zoom = 1 * this.origin.scale
        canvas.zoomToPoint({ x: opt.e.offsetX, y: opt.e.offsetY }, zoom)
        this.cursor.canvas.zoomToPoint(
          { x: opt.e.offsetX, y: opt.e.offsetY },
          zoom,
        )
        opt.e.preventDefault()
        opt.e.stopPropagation()
        const vpt = canvas.viewportTransform
        const cursorVpt = this.cursor.canvas.viewportTransform
        if (this.origin.scale === 1 && zoom < 400 / 1000) {
          vpt[4] = 200 - (1000 * zoom) / 2
          vpt[5] = 200 - (1000 * zoom) / 2
          cursorVpt[4] = 200 - (1000 * zoom) / 2
          cursorVpt[5] = 200 - (1000 * zoom) / 2
        } else {
          if (vpt[4] >= 0) {
            vpt[4] = 0
            cursorVpt[4] = 0
          } else if (vpt[4] < canvas.getWidth() - 1000 * zoom) {
            vpt[4] = canvas.getWidth() - 1000 * zoom
            cursorVpt[4] = canvas.getWidth() - 1000 * zoom
          }
          if (vpt[5] >= 0) {
            vpt[5] = 0
            cursorVpt[5] = 0
          } else if (vpt[5] < canvas.getHeight() - 1000 * zoom) {
            vpt[5] = canvas.getHeight() - 1000 * zoom
            cursorVpt[5] = canvas.getHeight() - 1000 * zoom
          }
        }
        this.keepPositionInBounds(canvas)
      })
    },
    keepPositionInBounds(canvas) {
      const zoom = canvas.getZoom()
      const xMin = ((2 - zoom) * canvas.getWidth()) / 2
      const xMax = (zoom * canvas.getWidth()) / 2
      const yMin = ((2 - zoom) * canvas.getHeight()) / 2
      const yMax = (zoom * canvas.getHeight()) / 2

      const point = new fabric.Point(
        canvas.getWidth() / 2,
        canvas.getHeight() / 2,
      )
      const center = fabric.util.transformPoint(point, canvas.viewportTransform)

      const clampedCenterX = this.clamp(center.x, xMin, xMax)
      const clampedCenterY = this.clamp(center.y, yMin, yMax)

      const diffX = clampedCenterX - center.x
      const diffY = clampedCenterY - center.y

      if (diffX != 0 || diffY != 0) {
        canvas.relativePan(new fabric.Point(diffX, diffY))
        this.cursor.canvas.relativePan(new fabric.Point(diffX, diffY))
      }
    },

    clamp(value, min, max) {
      return Math.max(min, Math.min(value, max))
    },

    /**
     * 키보드 입력 핸들러
     * @param {Event} event ::입력 이벤트 객체
     */
    keyEventHandler(event) {
      if (!this.isDrawingView) return
      if (!this.canvas || this.canvas.onDrag === true) return
      // For window event
      if (this.canvas) {
        if (this.canvas.getZoom() === 1) return
        const keycode = parseInt(event.keyCode)
        if (keycode === 32) {
          // this.canvas.defaultCursor = 'grab'
          // this.canvas.setCursor('grab')
          // this.cursor.canvas.renderAll()
          this.zoom = true
          this.canvas.isDrawingMode = false
        }
      }
    },
    /**
     * 키보드 입력 핸들러 (keyboard up)
     * @param {Event} event ::입력 이벤트 객체
     */
    keyUpEventHandler(event) {
      if (!this.isDrawingView) return
      if (!this.canvas || this.canvas.onDrag === true) return
      if (this.canvas) {
        const keycode = parseInt(event.keyCode)
        if (this.zoom === false) return

        if (keycode === 32) {
          this.canvas.defaultCursor =
            this.viewAction === ACTION.DRAWING_TEXT ? 'text' : 'default'
          this.cursor.canvas.renderAll()
          this.zoom = false
          this.canvas.isDrawingMode = this.viewAction === ACTION.DRAWING_LINE
        }
      }
    },
  },

  /* Lifecycles */
  mounted() {
    window.addEventListener('keydown', this.keyEventHandler)
    window.addEventListener('keyup', this.keyUpEventHandler)
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.keyEventHandler)
    window.removeEventListener('keyup', this.keyUpEventHandler)
  },
}
