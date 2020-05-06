<template>
  <div>
    <canvas id="drawingCanvas" ref="drawingCanvas"></canvas>
    <canvas id="cursorCanvas"></canvas>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
import { fabric } from 'plugins/remote/fabric.custom'

import { getCanvasSize, getSignalParams } from 'utils/drawing'
import DrawingWatch from './DrawingWatch'
import DrawingObject from './DrawingObject'
import DrawingHistory from './DrawingHistory'
import DrawingStack from './DrawingStack'
import DrawingListener from './DrawingListener'

import MixinToast from 'mixins/toast'
import { hexToRGBA } from 'utils/color'

export default {
  name: 'DrawingCanvas',
  props: {
    fileReader: FileReader,
    videoWidth: Number,
    videoHeight: Number,
    videoVPad: {
      type: Number,
      default: 0,
    },
    scale: Number,
  },
  mixins: [
    MixinToast,
    DrawingWatch,
    DrawingObject,
    DrawingHistory,
    DrawingStack,
    DrawingListener,
  ],
  data() {
    return {
      mode: 'document',
      isInit: false,
      canvas: null,
      cursor: null,
      canDrag: true,
      drawingMode: 'line', // ('line' / 'text' / false)
      editingMode: false, // check text in editing (true / false)
      undoList: [],
      redoList: [],
      history: [],
      zoomPointer: [],
    }
  },
  computed: {
    ...mapGetters([
      'canUseChannel',
      'shareDocList',
      'shareDocListLen',
      'shareDocSelect',
      'shareDocIndex',
      'tools',
      'remote',
    ]),
  },
  methods: {
    /**
     * 모드 변경 메소드
     * @param {String} mode :: 변경 모드  // ('line' / 'text' / false)
     */
    changeMode(mode) {
      this.drawingMode = mode
    },

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

          resolve(canvas)
        })
      })

      // //배경을 드로잉 객체로 등록하는 방식.
      // fabric.Image.fromURL(bgImage, function(img){
      //     canvas.add(img);
      // },{ crossOrigin: 'Anonymous' })
    },

    /**
     * 커서 생성 메소드
     * @param {Object} canvas ::대상 캔버스 객체
     */
    createCursor(canvas) {
      if (canvas) {
        // Set custom cursor
        canvas.freeDrawingBrush.width = this.tools.lineSize
        canvas.freeDrawingBrush.color = this.tools.drawingColor
        canvas.freeDrawingBrush.color = hexToRGBA(
          this.tools.drawingColor,
          this.tools.drawingOpacity,
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
          fill: this.tools.drawingColor,
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
          isDrawingMode: !!this.drawingMode,
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

          this.addHistory(bgImage)

          // 히스토리 초기화
          this.stackClear()

          this.isInit = true
        })
      }
      bgImage.onerror = error => {
        console.log(error)
      }
      this.resizing(this.fileReader.result).then(result => {
        bgImage.src = result
      })

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
        color: this.tools.drawingColor,
        opacity: this.tools.drawingOpacity,
        width: this.tools.lineSize,
        size: this.tools.textSize,
        scale: 1 / this.canvas.backgroundImage.scaleX,
      }
      const param = getSignalParams(type, aId, object, state)
      param.imgId = this.shareDocSelect.id

      if (object) {
        param.oId = object.id
      }

      if (this.videoVPad) {
        state.vpad = this.videoVPad
      }

      if (this.$remoteSDK) {
        this.$remoteSDK.message(type, { ...param, ...custom })
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
