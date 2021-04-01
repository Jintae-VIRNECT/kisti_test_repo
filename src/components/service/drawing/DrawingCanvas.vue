<template>
  <div class="drawing-canvas">
    <canvas id="drawingCanvas" ref="drawingCanvas"></canvas>
    <canvas id="cursorCanvas"></canvas>
    <div
      style=" position: absolute; top: 0; left: 0; width: 100%; height: 100%;visibility: hidden"
    >
      <canvas id="backCanvas" ref="backCanvas"></canvas>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'
import { ROLE, DRAWING } from 'configs/remote.config'
import { ACTION } from 'configs/view.config'

import { getCanvasSize, getSignalParams, getChunk } from 'utils/drawing'
import DrawingWatch from './DrawingWatch'
import DrawingObject from './DrawingObject'
import DrawingHistory from './DrawingHistory'
import DrawingStack from './DrawingStack'
import DrawingHandler from './DrawingHandler'
import DrawingListener from './DrawingListener'

import MixinToast from 'mixins/toast'
import { hexToRGBA } from 'utils/color'

export default {
  name: 'DrawingCanvas',
  props: {
    file: Object,
    videoVPad: {
      type: Number,
      default: 0,
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
  ],
  data() {
    return {
      fileReader: null,
      isInit: false,
      canvas: null,
      backCanvas: null,
      cursor: null,
      // viewAction: 'line', // ('line' / 'text' / false)
      editingMode: false, // check text in editing (true / false)
      undoList: [],
      receiveUndoList: {},
      redoList: [],
      receiveRedoList: {},
      history: [],
      receivedList: [
        // {
        //   id: 0,
        //   data: {},
        //   path: [] / {},
        // }
      ],
      origin: {
        width: 0,
        height: 0,
      },
      img: {
        width: 0,
        height: 0,
      },
      scaleWidth: 0,
      scaleFont: 0,
      zoom: false,
    }
  },
  computed: {
    ...mapGetters(['tools', 'view', 'viewAction']),
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
    setBG(img, bgImage) {
      const canvas = this.canvas
      canvas.setBackgroundColor('#000000')

      return new Promise(resolve => {
        const parent = this.$el.parentNode

        const canvasSize = getCanvasSize(
          parent.offsetWidth,
          parent.offsetHeight,
          bgImage.width,
          bgImage.height,
        )
        img.set({
          originX: 'left',
          originY: 'top',
          scaleX: canvasSize.width < 10 ? 1 : canvasSize.width / this.img.width,
          scaleY: canvasSize.width < 10 ? 1 : canvasSize.width / this.img.width,
        })
        canvas.setBackgroundImage(img, () => {
          canvas.setWidth(canvasSize.width)
          canvas.setHeight(canvasSize.height)
          this.origin.width = canvasSize.width
          this.origin.height = canvasSize.height
          this.scaleWidth = this.tools.lineWidth
          this.scaleFont = this.tools.fontSize
          this.origin.scale = 1
          canvas.renderAll.bind(canvas)()
          canvas.renderAll()
          img.clone(cbImg => {
            this.backCanvas.setBackgroundImage(cbImg, () => {
              this.backCanvas.setWidth(canvasSize.width)
              this.backCanvas.setHeight(canvasSize.height)
              this.backCanvas.backgroundImage.set({
                scaleX: canvasSize.scale,
                scaleY: canvasSize.scale,
              })
              this.backCanvas.renderAll()
            })
          })

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
        canvas.freeDrawingBrush.color = hexToRGBA(
          this.tools.color,
          this.tools.opacity,
        )

        // 커서 캔버스 생성.
        const cursor = new fabric.StaticCanvas('cursorCanvas')
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
      if (this.canvas) {
        this.canvas.dispose()
        this.canvas = null
        this.receivedList = []
      }
      this.editingMode = false

      const canvas = new fabric.Canvas('drawingCanvas', {
        backgroundColor: '#000000',
        isDrawingMode:
          this.viewAction === ACTION.DRAWING_LINE &&
          this.account.roleType === ROLE.LEADER,
        freeDrawingCursor:
          this.account.roleType === ROLE.LEADER &&
          this.viewAction === ACTION.DRAWING_TEXT
            ? 'text'
            : 'default',
        defaultCursor:
          this.account.roleType === ROLE.LEADER &&
          this.viewAction === ACTION.DRAWING_TEXT
            ? 'text'
            : 'default',
      })

      const backCanvas = new fabric.Canvas('backCanvas', {
        backgroundColor: '#000000',
      })

      this.canvas = canvas
      this.backCanvas = backCanvas

      // 커서 설정
      this.cursor = this.createCursor(this.canvas)

      this.appendListener()

      // 배경이미지 설정
      const bgImage = new Image()
      bgImage.onload = () => {
        this.img.width = bgImage.width
        this.img.height = bgImage.height
        const fabricImage = new fabric.Image(bgImage)
        this.setBG(fabricImage, bgImage).then(canvas => {
          this.cursor.canvas.setWidth(canvas.getWidth())
          this.cursor.canvas.setHeight(canvas.getHeight())
          this.origin.width = canvas.getWidth()
          this.origin.height = canvas.getHeight()

          // 히스토리 초기화
          this.stackClear()

          if (this.account.roleType === ROLE.LEADER) {
            const params = {
              imgId: this.file.id,
              // imgName: this.file.oriName
              //   ? this.file.oriName
              //   : this.file.fileName,
              imgName: this.file.fileName,
              image: this.file.img,
            }
            this.sendImage(params)
          }

          this.isInit = true
          this.$emit('loadingSuccess')
        })
      }
      bgImage.onerror = error => {
        console.error(error)
      }
      bgImage.src = this.file.img

      return this.canvas
    },

    /**
     * chunk 이미지 전송
     * @param {String} imgId
     * @param {String} imgName
     * @param {String} image 이미지 dataURL
     */
    sendImage(params, target = null) {
      const chunk = getChunk(params['image'])
      delete params['image']

      params.width = this.img.width
      params.height = this.img.height

      let type

      for (let i = 0; i < chunk.length; i++) {
        if (i === 0) {
          type = DRAWING.FIRST_FRAME
          if (chunk.length === 1) {
            type = DRAWING.LAST_FRAME
          }
        } else if (i === chunk.length - 1) {
          type = DRAWING.LAST_FRAME
        } else {
          type = DRAWING.FRAME
        }
        params.chunk = chunk[i]

        this.$call.sendDrawing(type, params, target)
      }
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
        opacity: this.tools.opacity,
        width: this.scaleWidth,
        size: this.scaleFont,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
        // oriWidth: this.origin.width,
        // oriHeight: this.origin.height,
        posScale: this.canvas.getWidth() / this.origin.width,
        widthScale: this.origin.width / this.img.width,
      }
      const param = getSignalParams(type, aId, object, state)
      param.imgId = this.file.id

      if (object) {
        param.oId = object.id
      }

      if (this.videoVPad) {
        state.vpad = this.videoVPad
      }

      if (this.$call.session) {
        this.$call.sendDrawing(type, { ...param, ...custom })
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
    optimizeCanvasSize() {
      if (!this.file || !this.file.id || !this.canvas) return
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
      this.scaleWidth = this.tools.lineWidth / scale
      this.scaleFont = this.tools.fontSize / scale

      canvas.setZoom(scale)
      cursor.setZoom(scale)

      canvas.setWidth(canvasSize.width)
      canvas.setHeight(canvasSize.height)
      cursor.setWidth(canvas.getWidth())
      cursor.setHeight(canvas.getHeight())
      canvas.freeDrawingBrush.width = this.scaleWidth
      if (this.cursor) {
        this.cursor.setRadius(this.scaleWidth / 2)
      }
      canvas.backgroundImage.set({
        scaleX: canvasSize.scale / scale,
        scaleY: canvasSize.scale / scale,
      })
    },
    receiveRender() {
      if (this.receivedList.length === 0) return

      for (let received of this.receivedList) {
        this.addReceiveObject(received)
      }
      this.receivedList = []
    },
    windowResize() {
      this.$nextTick(() => {
        setTimeout(() => {
          this.optimizeCanvasSize()
        }, 1000)
      })
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
