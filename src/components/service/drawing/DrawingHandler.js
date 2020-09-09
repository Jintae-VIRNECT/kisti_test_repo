import { fabric } from 'plugins/remote/fabric.custom'
import { DRAWING } from 'configs/remote.config'
import { VIEW, ACTION } from 'configs/view.config'

export default {
  data() {
    return {
      winResizeID: 0,
      receivePath: [],
    }
  },
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
        } else {
          this.backCanvas.add(object)
          object.set({
            id: objID,
            tId: this.undoList.length,
          })
          if (object instanceof fabric.IText) {
            // this.$remoteSDK.message('drawText', getParam('text', object));
            // this._sendAction('drawText', object);
            return
          }
          this.stackAdd('add', object.id)
        }
      })

      /* Object - move */
      // canvas.on('object:moving', event => {
      //   // console.log('[Fabric] Object moving');
      //   const object = event.target

      //   // if object is too big ignore
      //   if (
      //     object.currentHeight > object.canvas.height ||
      //     object.currentWidth > object.canvas.width
      //   ) {
      //     return
      //   }
      //   object.setCoords()
      //   // top-left  corner
      //   if (
      //     object.getBoundingRect().top < 0 ||
      //     object.getBoundingRect().left < 0
      //   ) {
      //     object.top = Math.max(
      //       object.top,
      //       object.top - object.getBoundingRect().top,
      //     )
      //     object.left = Math.max(
      //       object.left,
      //       object.left - object.getBoundingRect().left,
      //     )
      //   }
      //   // bot-right corner
      //   if (
      //     object.getBoundingRect().top + object.getBoundingRect().height >
      //       object.canvas.height ||
      //     object.getBoundingRect().left + object.getBoundingRect().width >
      //       object.canvas.width
      //   ) {
      //     object.top = Math.min(
      //       object.top,
      //       object.canvas.height -
      //         object.getBoundingRect().height +
      //         object.top -
      //         object.getBoundingRect().top,
      //     )
      //     object.left = Math.min(
      //       object.left,
      //       object.canvas.width -
      //         object.getBoundingRect().width +
      //         object.left -
      //         object.getBoundingRect().left,
      //     )
      //   }
      // })
      // canvas.on('object:moved', event => {
      //   console.log('[Fabric] Object moved')
      //   const object = event.target
      //   console.log(object)

      //   // this.$remoteSDK.message('drawMove', getParam('move', object));
      //   this._sendAction('drawMove', object)
      //   this.stackAdd('move', object.id)
      // })
      /* Object - scale */
      //   canvas.on('object:scaling', event => {
      //     // console.log('[Fabric] Object scaling');
      //     // const object = event.target
      //   })
      //   canvas.on('object:scaled', event => {
      //     console.log('[Fabric] Object scaled')
      //     const object = event.target
      //     const option = {
      //       direction: event.transform.corner,
      //     }

      //     // this.$remoteSDK.message('drawScale', getParam('scale', object));
      //     this._sendAction('drawScale', object, option)
      //     this.stackAdd('scale', [object.id])
      //   })

      //   /* TEXT */
      canvas.on('text:editing:entered', () => {
        this.editingMode = true
      })

      /* CANVAS */
      // canvas.on('event:changed', event => {
      //   console.log(event)
      // })
      canvas.on('after:render', () => {
        if (this.isInit === true) {
          this.updateHistory()
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
          canvas.defaultCursor = 'grab'
          return
        }

        if (this.viewAction === ACTION.DRAWING_TEXT) {
          canvas.defaultCursor = 'text'
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
          this.addTextObject(mouse.x, mouse.y - this.scaleFont / 2 - 1)
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
      // canvas.on('mouse:wheel', opt => {
      //   var delta = opt.e.deltaY
      //   var zoom = canvas.getZoom()
      //   zoom *= 0.999 ** delta
      //   if (zoom > 5) zoom = 5
      //   if (zoom < 1) zoom = 1
      //   canvas.zoomToPoint({ x: opt.e.offsetX, y: opt.e.offsetY }, zoom)
      //   this.cursor.canvas.zoomToPoint(
      //     { x: opt.e.offsetX, y: opt.e.offsetY },
      //     zoom,
      //   )
      //   opt.e.preventDefault()
      //   opt.e.stopPropagation()
      //   var vpt = canvas.viewportTransform
      //   var cursorVpt = this.cursor.canvas.viewportTransform
      //   if (zoom < 400 / 1000) {
      //     vpt[4] = 200 - (1000 * zoom) / 2
      //     vpt[5] = 200 - (1000 * zoom) / 2
      //     cursorVpt[4] = 200 - (1000 * zoom) / 2
      //     cursorVpt[5] = 200 - (1000 * zoom) / 2
      //   } else {
      //     if (vpt[4] >= 0) {
      //       vpt[4] = 0
      //       cursorVpt[4] = 0
      //     } else if (vpt[4] < canvas.getWidth() - 1000 * zoom) {
      //       vpt[4] = canvas.getWidth() - 1000 * zoom
      //       cursorVpt[4] = canvas.getWidth() - 1000 * zoom
      //     }
      //     if (vpt[5] >= 0) {
      //       vpt[5] = 0
      //       cursorVpt[5] = 0
      //     } else if (vpt[5] < canvas.getHeight() - 1000 * zoom) {
      //       vpt[5] = canvas.getHeight() - 1000 * zoom
      //       cursorVpt[5] = canvas.getHeight() - 1000 * zoom
      //     }
      //   }
      //   this.keepPositionInBounds(canvas)
      // })
    },
    // keepPositionInBounds(canvas) {
    //   var zoom = canvas.getZoom()
    //   var xMin = ((2 - zoom) * canvas.getWidth()) / 2
    //   var xMax = (zoom * canvas.getWidth()) / 2
    //   var yMin = ((2 - zoom) * canvas.getHeight()) / 2
    //   var yMax = (zoom * canvas.getHeight()) / 2

    //   var point = new fabric.Point(
    //     canvas.getWidth() / 2,
    //     canvas.getHeight() / 2,
    //   )
    //   var center = fabric.util.transformPoint(point, canvas.viewportTransform)

    //   var clampedCenterX = this.clamp(center.x, xMin, xMax)
    //   var clampedCenterY = this.clamp(center.y, yMin, yMax)

    //   var diffX = clampedCenterX - center.x
    //   var diffY = clampedCenterY - center.y

    //   if (diffX != 0 || diffY != 0) {
    //     canvas.relativePan(new fabric.Point(diffX, diffY))
    //     this.cursor.canvas.relativePan(new fabric.Point(diffX, diffY))
    //   }
    // },

    // clamp(value, min, max) {
    //   return Math.max(min, Math.min(value, max))
    // },

    /**
     * 키보드 입력 핸들러
     * @param {Event} event ::입력 이벤트 객체
     */
    keyEventHandler(event) {
      // For window event
      if (this.canvas) {
        const keycode = parseInt(event.keyCode)
        if (keycode === 32) {
          this.canvas.defaultCursor = 'grab'
          // this.canvas.setCursor('grab')
          this.cursor.canvas.renderAll()
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
  created() {
    this.$eventBus.$on(`control:${VIEW.DRAWING}:undo`, this.stackUndo)
    this.$eventBus.$on(`control:${VIEW.DRAWING}:redo`, this.stackRedo)
    this.$eventBus.$on(`control:${VIEW.DRAWING}:clear`, this.drawingClear)
    // this.$eventBus.$on(`control:${this.mode}:focus`, this.focusCanvas)
  },
  mounted() {
    // window.addEventListener('keydown', this.keyEventHandler)
    // window.addEventListener('keyup', this.keyUpEventHandler)
    // window.addEventListener('resize', this.resizeEventHandler)
  },
  beforeDestroy() {
    this.$eventBus.$off(`control:${VIEW.DRAWING}:undo`, this.stackUndo)
    this.$eventBus.$off(`control:${VIEW.DRAWING}:redo`, this.stackRedo)
    this.$eventBus.$off(`control:${VIEW.DRAWING}:clear`, this.drawingClear)
    // this.$eventBus.$off(`control:${this.mode}:focus`, this.focusCanvas)
    // window.removeEventListener('keydown', this.keyEventHandler)
    // window.removeEventListener('keyup', this.keyUpEventHandler)
    // window.removeEventListener('resize', this.resizeEventHandler)
  },
}
