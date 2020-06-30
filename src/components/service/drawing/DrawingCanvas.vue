<template>
  <div class="drawing-canvas">
    <canvas id="drawingCanvas" ref="drawingCanvas"></canvas>
    <canvas id="cursorCanvas"></canvas>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'
import { ROLE, DRAWING } from 'configs/remote.config'

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
          canvas.renderAll.bind(canvas)()
          canvas.renderAll()

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
      if (this.canvas === null) {
        const canvas = new fabric.Canvas('drawingCanvas', {
          backgroundColor: '#000000',
          isDrawingMode:
            !!this.viewAction && this.account.roleType === ROLE.EXPERT_LEADER,
          freeDrawingCursor: 'default',
        })

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

          // 히스토리 초기화
          this.stackClear()

          if (this.account.roleType === ROLE.EXPERT_LEADER) {
            const params = {
              imgId: this.file.id,
              imgName: this.file.oriName
                ? this.file.oriName
                : this.file.fileName,
              width: bgImage.width,
              height: bgImage.height,
            }

            const chunk = getChunk(this.file.img)

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

              this.$call.drawing(type, params)
            }
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
     * 드로잉 액션 상대기기 전송 내부 메소드
     * @param {String} type   ::액션 종류( getSignalParams 참고 )
     * @param {Object} object ::변화대상 드로잉객체
     */
    _sendAction(type, object, custom) {
      const aId = this.undoList.length
      const state = {
        color: this.tools.color,
        opacity: this.tools.opacity,
        width: this.tools.lineWidth,
        size: this.tools.fontSize,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
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
        this.$call.drawing(type, { ...param, ...custom })
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
      const image = canvas.backgroundImage
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
    },
    receiveRender() {
      if (this.receivedList.length === 0) return

      for (let received of this.receivedList) {
        this.addReceiveObject(received)
      }
      this.receivedList = []
    },
  },
  /* Lifecycles */
  beforeDestroy() {
    window.removeEventListener('resize', this.optimizeCanvasSize)
  },
  beforeCreate() {
    this.$emit('loadingStart')
  },
  created() {
    window.addEventListener('resize', this.optimizeCanvasSize)
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
