<template>
  <div class="ardrawing-canvas">
    <canvas id="arDrawingCanvas" ref="arDrawingCanvas"></canvas>
    <canvas id="arCursorCanvas"></canvas>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'

import { getCanvasSize, getSignalParams } from 'utils/drawing'
import DrawingWatch from './DrawingWatch'
import DrawingObject from './DrawingObject'
import DrawingStack from './DrawingStack'
import DrawingHandler from './DrawingHandler'

import MixinToast from 'mixins/toast'
import { hexToRGBA } from 'utils/color'
import { AR_DRAWING } from 'configs/remote.config'

export default {
  name: 'ARDrawingCanvas',
  props: {
    file: Object,
  },
  mixins: [
    MixinToast,
    DrawingWatch,
    DrawingObject,
    DrawingStack,
    DrawingHandler,
  ],
  data() {
    return {
      fileReader: null,
      isInit: false,
      canvas: null,
      cursor: null,
      undoList: [],
      redoList: [],
      origin: {
        width: 0,
        height: 0,
      },
    }
  },
  computed: {
    ...mapGetters(['tools', 'view', 'viewAction', 'mainView']),
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
    setBG(image) {
      const canvas = this.canvas
      canvas.setBackgroundColor('#000000')

      return new Promise(resolve => {
        canvas.setBackgroundImage(image, image => {
          const parent = this.$el.parentNode
          const canvasSize = getCanvasSize(
            parent.offsetWidth,
            parent.offsetHeight,
            image.width,
            image.height,
          )

          canvas.setWidth(canvasSize.width)
          canvas.setHeight(canvasSize.height)
          canvas.backgroundImage.set({
            scaleX: canvasSize.scale,
            scaleY: canvasSize.scale,
          })
          this.origin.width = canvasSize.width
          this.origin.height = canvasSize.height
          this.origin.scale = 1
          canvas.renderAll.bind(canvas)()

          resolve(canvas)
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
        canvas.freeDrawingBrush.color = this.tools.color
        canvas.freeDrawingBrush.color = hexToRGBA(this.tools.color, 1)

        // 커서 캔버스 생성.
        const cursor = new fabric.StaticCanvas('arCursorCanvas')
        cursor.setWidth(canvas.getWidth())
        cursor.setHeight(canvas.getHeight())

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
    initCanvas() {
      this.isInit = false
      if (this.canvas === null) {
        const canvas = new fabric.Canvas('arDrawingCanvas', {
          backgroundColor: '#000000',
          isDrawingMode: true,
          freeDrawingCursor: 'default',
        })
        // canvas.setWidth(this.videoWidth);
        // canvas.setHeight(this.videoHeight);

        this.canvas = canvas

        // 커서 설정
        this.cursor = this.createCursor(this.canvas)

        this.appendListener()
      } else {
        this.canvas.clear()
      }

      // 배경이미지 설정
      const bgImage = new Image()
      bgImage.onload = () => {
        const fabricImage = new fabric.Image(bgImage)
        this.setBG(fabricImage).then(canvas => {
          this.cursor.canvas.setWidth(canvas.getWidth())
          this.cursor.canvas.setHeight(canvas.getHeight())
          this.origin.width = canvas.getWidth()
          this.origin.height = canvas.getHeight()

          // 히스토리 초기화
          this.stackClear()

          this.isInit = true
          this.$emit('loading')
          this.$call.sendArDrawing(AR_DRAWING.START_DRAWING, {}, [])
        })
      }
      bgImage.onerror = error => {
        console.log(error)
      }
      bgImage.src = this.file.img

      return this.canvas
    },

    /**
     * 드로잉 액션 상대기기 전송 내부 메소드
     * @param {String} type   ::액션 종류( getSignalParams 참고 )
     * @param {Object} object ::변화대상 드로잉객체
     */
    _sendAction(type, object, custom) {
      const aId = this.undoList.length
      const state = {
        color: this.tools.color,
        opacity: 1,
        width: this.tools.lineWidth,
        size: this.tools.fontSize,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
      }
      const param = getSignalParams(type, aId, object, state)
      param.imgId = this.file.id
      param.action = type

      if (object) {
        param.oId = object.id
      }

      if (this.videoVPad) {
        state.vpad = this.videoVPad
      }

      if ([AR_DRAWING.UNDO, AR_DRAWING.REDO, AR_DRAWING.CLEAR].includes(type)) {
        this.$call.sendArDrawing(type, { ...param, ...custom }, [
          this.mainView.connectionId,
        ])
      } else {
        this.$call.sendArDrawing(
          AR_DRAWING.AR_DRAWING,
          { ...param, ...custom },
          [this.mainView.connectionId],
        )
      }

      // tId 업데이트
      if (
        ['drawMove', 'drawScale', 'rotate', 'remove', 'updateText'].indexOf(
          type,
        ) >= 0
      ) {
        object.tId = aId
      }
    },
    windowResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          this.optimizeCanvasSize()
        }, 1000)
      })
    },
    optimizeCanvasSize() {
      if (!this.file || !this.file.id) return
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

      const scale = canvasSize.width / this.origin.width

      this.origin.scale = scale

      canvas.setZoom(scale)
      cursor.setZoom(scale)

      canvas.setWidth(canvasSize.width)
      canvas.setHeight(canvasSize.height)
      cursor.setWidth(canvas.getWidth())
      cursor.setHeight(canvas.getHeight())
      canvas.backgroundImage.set({
        scaleX: canvasSize.scale / scale,
        scaleY: canvasSize.scale / scale,
      })
    },
  },
  /* Lifecycles */
  created() {
    window.addEventListener('resize', this.windowResize)
    if (this.file && this.file.id) {
      setTimeout(() => {
        this.initCanvas()
      }, 500)
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.windowResize)
  },
}
</script>

<style lang="scss" scoped>
.ardrawing-canvas {
  position: absolute;
  top: 50%;
  left: 50%;
  width: fit-content;
  max-width: 100%;
  height: fit-content;
  max-height: 100%;
  margin: auto;
  transform: translate(-50%, -50%);
}
.drawing-toolbox {
  position: fixed;
  top: 74px;
  left: 10px;
  z-index: 1;
  border-radius: 6px;
}
#arCursorCanvas {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}
</style>
