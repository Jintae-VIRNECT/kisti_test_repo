<template>
  <div class="drawing-canvas">
    <canvas id="drawingCanvas" ref="drawingCanvas"></canvas>
    <canvas id="cursorCanvas"></canvas>
    <div
      style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;visibility: hidden"
    >
      <canvas id="backCanvas" ref="backCanvas"></canvas>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'
import { ROLE } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'

import { getCanvasSize } from 'utils/drawing'
import DrawingWatch from './DrawingWatch'
import DrawingObject from './DrawingObject'
import DrawingHistory from './DrawingHistory'
import DrawingStack from './DrawingStack'
import DrawingHandler from './DrawingHandler'
import DrawingListener from './DrawingListener'
import DrawingAction from './DrawingAction'

import MixinToast from 'mixins/toast'
import { hexToRGBA } from 'utils/color'

export default {
  name: 'DrawingCanvas',
  props: {
    file: Object,
  },
  mixins: [
    MixinToast,
    DrawingWatch,
    DrawingObject,
    DrawingHistory,
    DrawingStack,
    DrawingHandler,
    DrawingListener,
    DrawingAction,
  ],
  data() {
    return {
      isInit: false,
      canvas: null,
      backCanvas: null,
      cursor: null,
      // viewAction: 'line', // ('line' / 'text' / false)
      editingMode: false, // check text in editing (true / false)
      origin: {
        width: 0,
        height: 0,
      },
      img: {
        width: 0,
        height: 0,
      },
      zoom: false,
    }
  },
  computed: {
    ...mapGetters(['tools', 'view', 'viewAction', 'myInfo']),
    uuid() {
      return this.account.uuid
    },
  },
  methods: {
    /* initialize */
    /**
     * 배경이미지 생성 메소드
     * @param {Object}   canvas   ::대상 캔버스 객체
     * @param {Function} callback ::배경 생성 후 콜백함수
     */
    setBG() {
      return new Promise((resolve, reject) => {
        this.img.width = this.file.width
        this.img.height = this.file.height
        fabric.Image.fromURL(this.file.img, fabricImage => {
          const canvas = this.canvas
          const parent = this.$el.parentNode

          const canvasSize = getCanvasSize(
            parent.offsetWidth,
            parent.offsetHeight,
            // bgImage.width,
            // bgImage.height,
            this.img.width,
            this.img.height,
          )
          fabricImage.set({
            crossOrigin: 'anonymous',
            originX: 'left',
            originY: 'top',
            scaleX:
              canvasSize.width < 10 ? 1 : canvasSize.width / this.img.width,
            scaleY:
              canvasSize.width < 10 ? 1 : canvasSize.width / this.img.width,
          })
          canvas.setBackgroundImage(fabricImage, () => {
            canvas.setWidth(canvasSize.width)
            canvas.setHeight(canvasSize.height)
            this.origin = {
              scale: 1,
              width: canvasSize.width,
              height: canvasSize.height,
            }

            this.cursor.canvas.setWidth(canvasSize.width)
            this.cursor.canvas.setHeight(canvasSize.height)

            canvas.renderAll.bind(canvas)()
            canvas.renderAll()

            fabricImage.clone(cbImg => {
              this.backCanvas.setBackgroundImage(cbImg, () => {
                this.backCanvas.setWidth(canvasSize.width)
                this.backCanvas.setHeight(canvasSize.height)
                this.backCanvas.backgroundImage.set({
                  scaleX: canvasSize.scale,
                  scaleY: canvasSize.scale,
                })
                this.backCanvas.renderAll()

                this.updateHistory()
              })
            })

            resolve(canvas)
          })
        })
      })
    },

    /**
     * 커서 생성 메소드
     * @param {Object} canvas ::대상 캔버스 객체
     */
    createCursor(canvas) {
      if (canvas) {
        // Set custom cursor
        canvas.freeDrawingBrush.width = this.tools.lineWidth
        canvas.freeDrawingBrush.color = hexToRGBA(
          this.tools.color,
          this.tools.opacity,
        )

        // 커서 캔버스 생성.
        const cursor = new fabric.StaticCanvas('cursorCanvas')

        // 실제 커서 생성.
        const mousecursor = new fabric.Circle({
          selectable: false,
          top: 0,
          left: 0,
          radius: canvas.freeDrawingBrush.width / 2,
          fill: this.tools.color,
          borderColor: '#000000',
          originX: 'center',
          originY: 'center',
        })
        cursor.add(mousecursor)

        return mousecursor
      }
    },

    /**
     * 드로잉 캔버스 초기화 메소드
     */
    async initCanvas() {
      this.isInit = false
      if (this.canvas) {
        this.canvas.dispose()
        this.canvas = null
        this.receivedList = {}
      }
      this.editingMode = false

      const canvas = new fabric.Canvas('drawingCanvas', {
        backgroundColor: '#000000',
        isDrawingMode: this.viewAction === ACTION.DRAWING_LINE,
        freeDrawingCursor:
          this.viewAction === ACTION.DRAWING_TEXT ? 'text' : 'default',
        defaultCursor:
          this.viewAction === ACTION.DRAWING_TEXT ? 'text' : 'default',
      })

      const backCanvas = new fabric.StaticCanvas('backCanvas', {
        backgroundColor: '#000000',
      })

      this.canvas = canvas
      this.backCanvas = backCanvas

      // 커서 설정
      this.cursor = this.createCursor(this.canvas)

      this.appendListener()

      // 배경이미지 설정
      await this.setBG()
      // 히스토리 초기화
      this.stackClear()

      this.isInit = true
      this.$emit('loadingSuccess')
      this.receiveRender()

      return this.canvas
    },

    optimizeCanvasSize() {
      if (!this.file || !this.file.objectName || !this.canvas) return
      const canvas = this.canvas
      const cursor = this.cursor.canvas
      const image = canvas.backgroundImage
      const parent = this.$el.parentNode

      const canvasSize = getCanvasSize(
        parent.offsetWidth,
        parent.offsetHeight,
        image.width,
        image.height,
      )

      if (this.origin.width === 0) {
        this.origin.width = canvasSize.width
        this.origin.height = canvasSize.height
      }
      if (canvasSize.width === 0) return

      const scale = canvasSize.width / this.origin.width

      // let zoom = canvas.getZoom()

      this.origin.scale = scale

      canvas.setZoom(scale)
      cursor.setZoom(scale)

      canvas.setWidth(canvasSize.width)
      canvas.setHeight(canvasSize.height)
      cursor.setWidth(canvas.getWidth())
      cursor.setHeight(canvas.getHeight())
      canvas.freeDrawingBrush.width = this.tools.lineWidth / this.origin.scale
      if (this.cursor) {
        this.cursor.setRadius(this.tools.lineWidth / this.origin.scale / 2)
      }
      canvas.backgroundImage.set({
        scaleX: canvasSize.scale / scale,
        scaleY: canvasSize.scale / scale,
      })
    },
    receiveRender() {
      if (Object.keys(this.receivedList).length === 0) return

      for (let key in this.receivedList) {
        for (let received of this.receivedList[key]) {
          this.addReceiveObject({ data: received.data, owner: received.owner })
        }
        delete this.receivedList[key]
      }
    },
    windowResize() {
      setTimeout(() => {
        this.optimizeCanvasSize()
      }, 1000)
    },
  },
  /* Lifecycles */
  beforeDestroy() {
    window.removeEventListener('resize', this.windowResize)
  },
  beforeCreate() {
    this.$emit('loadingStart')
  },
  created() {
    window.addEventListener('resize', this.windowResize)
    if (this.file && this.file.id) {
      setTimeout(() => {
        this.initCanvas()
      }, 500)
    }
  },
}
</script>

<style lang="scss">
.drawing-toolbox {
  position: fixed;
  top: 74px;
  left: 10px;
  z-index: 1;
  border-radius: 6px;
}
#cursorCanvas {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}
</style>
