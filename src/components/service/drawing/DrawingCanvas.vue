<template>
  <div class="drawing-canvas">
    <div class="pinchzoom-layer" ref="pinchzoom-layer">
      <tooltip
        v-if="showExitButton"
        :content="`${$t('service.drawing')} ${$t('button.exit')}`"
        customClass="drawing-box__exit-btn-tooltip"
        placement="left"
      >
        <button
          slot="body"
          class="drawing-box__exit-btn"
          @click="exitDrawing"
        ></button>
      </tooltip>
      <canvas id="drawingCanvas" ref="drawingCanvas"></canvas>
      <canvas id="cursorCanvas"></canvas>
      <div
        style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;visibility: hidden"
      >
        <canvas id="backCanvas" ref="backCanvas"></canvas>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'

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
import Tooltip from 'Tooltip'
import PinchZoom from 'pinch-zoom-js'

const MOBILE_FIX_LINE_SIZE = 3

export default {
  name: 'DrawingCanvas',
  components: {
    Tooltip,
  },
  props: {
    file: Object,
    showExitButton: {
      type: Boolean,
    },
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

      resizeObserveIntervalId: null,
      parentOffsetWidth: 0,

      pinchZoom: null,
      //pinchzoom 내 중앙 배치하기 위한 offset
      offsetX: 0,
      offsetY: 0,
    }
  },
  computed: {
    ...mapGetters(['tools', 'view', 'viewAction', 'myInfo']),
    uuid() {
      return this.account.uuid
    },
  },
  watch: {
    viewAction(newVal) {
      if (newVal === ACTION.DRAWING_LOCK && this.isMobileSize) {
        if (this.pinchZoom) this.pinchZoom.enable()
      } else {
        if (this.pinchZoom) this.pinchZoom.disable()
      }
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
        const imgUrl =
          this.file.contentType.indexOf('pdf') > -1
            ? this.file.img
            : `${this.file.img}?t=${new Date().getTime()}` //이미지의 경우 캐시를 통해 cors 발생을 막기 위해 타임스탬프를 추가한다

        fabric.Image.fromURL(
          imgUrl,
          fabricImage => {
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
                    crossOrigin: 'anonymous',
                    scaleX: canvasSize.scale,
                    scaleY: canvasSize.scale,
                  })
                  this.backCanvas.renderAll()

                  this.updateHistory()
                })
              })

              resolve(canvas)
            })
          },
          {
            crossOrigin: 'anonymous',
          },
        )
      })
    },

    /**
     * 커서 생성 메소드
     * @param {Object} canvas ::대상 캔버스 객체
     */
    createCursor(canvas) {
      if (canvas) {
        //커서를 생성하지만 실제 캔버스 사이즈에 비례한 커서와 브러쉬 사이즈는 캔버스 사이즈가 업데이트 된 후 재 설정된다
        //optimizeCanvasSize함수에서 실제 드로잉 브러쉬와 커서 사이즈가 캔버스 사이즈에 비례하게 계산되어 정해진다.
        const width = this.isMobileSize
          ? MOBILE_FIX_LINE_SIZE
          : this.tools.lineWidth

        // Set custom cursor
        canvas.freeDrawingBrush.width = width
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

      //히스토리 초기화
      this.stackClear() //자신의 히스토리 초기화
      this.receivedStackClear() // 타참가자에게 받은 히스토리 초기화 및 tool설정 초기화

      this.isInit = true
      this.$emit('loadingSuccess')
      this.receiveRender()
      this.optimizeCanvasSize() //캔버스 사이즈, scale, 브러시, 커서 크기를 명시적으로 초기화/업데이트 한다.

      this.$nextTick(() => this.initPinchZoom())

      return this.canvas
    },

    //pinch zoom 내 canvas에 해당하는 element를 중앙으로 배치하기 위한 offset 값 계산
    optimizePinchZoomLayer() {
      const pzContainerEl = document.querySelector('.pinch-zoom-container')
      const pzLayerEl = document.querySelector('.pinchzoom-layer')

      this.offsetY = (pzContainerEl.clientHeight - pzLayerEl.clientHeight) / 2

      this.offsetX = (pzContainerEl.clientWidth - pzLayerEl.clientWidth) / 2

      //중앙으로 배치되는 offset으로 업데이트
      pzLayerEl.style.transform = `translate(
                ${this.offsetX}px,  
                ${this.offsetY}px)`
    },

    updatePinchZoomOffset() {
      this.pinchZoom.initialOffset.x = -this.offsetX
      this.pinchZoom.offset.x = -this.offsetX

      this.pinchZoom.initialOffset.y = -this.offsetY
      this.pinchZoom.offset.y = -this.offsetY
    },

    initPinchZoom() {
      //이미 pinchzoom 객체가 선언되어 dom이 추가된 경우 재생성 되지 않도록 방지하되, offset만 재계산한다
      if (document.querySelector('.pinch-zoom-container')) {
        this.optimizePinchZoomLayer()
        this.updatePinchZoomOffset() //이미지가 변경되어 이미지 크기가 변경되는 경우, pinchzoom 객체 내에 offset 값을 업데이트 해준다.
        return
      }

      const el = this.$refs['pinchzoom-layer']
      this.pinchZoom = new PinchZoom(el, {
        minZoom: 1,
        maxZoom: 5,
        animationDuration: 0,
        draggableUnzoomed: false,
        tapZoomFactor: 2,
        onZoomEnd: object => {
          //zoom in 이 되지 않은 상태에서 두개 손가락으로 panning을 하는 경우 이미지가 중앙을 벗어난채로 유지되는 현상이 발생함
          //zoom in 되지 않은 상태에서 바운더리 벗어나는 현상을 방지하기 위해 아래 로직을 추가하였음
          if (object.zoomFactor === 1) {
            //이벤트 콜백에서 실행되는 transform 이후에 원위치로 교정해줘야 하기 떄문에 timeout으로 딜레이를 부여함.
            setTimeout(() => {
              document.querySelector(
                '.pinch-zoom-container',
              ).firstChild.style.transform = `translate(
                ${Math.abs(object.initialOffset.x)}px,  
                ${Math.abs(object.initialOffset.y)}px)`
            }, 100)
          }
        },
      })
      this.pinchZoom.disable()
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

      this.origin.scale = scale

      canvas.setZoom(scale)
      cursor.setZoom(scale)

      canvas.setWidth(canvasSize.width)
      canvas.setHeight(canvasSize.height)
      cursor.setWidth(canvas.getWidth())
      cursor.setHeight(canvas.getHeight())

      //드로잉 굵기를 현재 창 크기에서 캔버스 사이즈 기준으로 계산
      this.updateCanvasBrushWidth(
        this.isMobileSize ? MOBILE_FIX_LINE_SIZE : this.tools.lineWidth,
      )

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
      this.optimizeCanvasSize()
      this.keepPositionInBounds(this.canvas)
      if (document.querySelector('.pinch-zoom-container')) {
        this.optimizePinchZoomLayer()
        this.updatePinchZoomOffset()
      }
    },

    //드로잉 브러시 lineWidth가 변경될 때마다 실제 브러쉬 크기를 캔버스 사이즈에 비례하여 업데이트하는 함수
    updateCanvasBrushWidth(lineWidth) {
      if (this.canvas) {
        //드로잉 굵기를 현재 창 크기에서 캔버스 사이즈 기준으로 계산
        const width = lineWidth * (this.origin.width / this.img.width)
        this.canvas.freeDrawingBrush.width = width
        //커서크기 업데이트
        if (this.cursor) this.cursor.setRadius(width / 2)
      }
    },

    drawingResizeObserve() {
      //parentNode의 크기의 변동이 감지되지 않아, polling방식으로 크기 변동을 체크하여 canvas 크기를 optimize한다

      if (this.resizeObserveIntervalId) this.stopResizeObserveInterval()
      //변경 된 경우
      if (this.$el.parentNode.offsetWidth !== this.parentOffsetWidth)
        this.windowResize()
      this.parentOffsetWidth = this.$el.parentNode.offsetWidth
      this.resizeObserveIntervalId = requestAnimationFrame(
        this.drawingResizeObserve,
      )
    },

    stopResizeObserveInterval() {
      if (this.resizeObserveIntervalId) {
        cancelAnimationFrame(this.resizeObserveIntervalId)
        this.resizeObserveIntervalId = null
      }
    },

    exitDrawing() {
      if (this.pinchZoom) {
        this.pinchZoom.disable()
      }
      this.$emit('exitDrawing')
    },
  },
  /* Lifecycles */
  beforeDestroy() {
    //window.removeEventListener('resize', this.windowResize)
    this.stopResizeObserveInterval()
  },
  beforeCreate() {
    this.$emit('loadingStart')
  },
  created() {
    this.$nextTick(() => this.drawingResizeObserve())
    //window.addEventListener('resize', this.windowResize)
    if (this.file && this.file.id) {
      setTimeout(() => {
        this.initCanvas()
      }, 500)
    }
  },
}
</script>

<style lang="scss">
.drawing-canvas.not-mobile > .pinch-zoom-container > div {
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  display: flex;
  justify-content: center;
  align-items: center;
}
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
