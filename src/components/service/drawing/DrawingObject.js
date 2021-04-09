/* eslint-disable no-undef */
import { DRAWING } from 'configs/remote.config'
import { fabric } from 'plugins/remote/fabric.custom'
import { hexToRGBA } from 'utils/color'
export default {
  data() {
    return {
      lineWidth: 4,
      fontFamily: 'Sans-Serif',
      fontStyle: 'normal',
      fontWeight: 'normal',
      lineHeight: 1,
      fontSize: 16,
      textObj: null,
    }
  },
  methods: {
    /**
     * 텍스트 추가 메소드
     * @param {Number} left ::추가할 X좌표(Offset)
     * @param {Number} top  ::추가할 Y좌표(Offset)
     */
    addTextObject(left, top) {
      this.textObj = new fabric.IText('', {
        left: left,
        top: top,
        fill: hexToRGBA(this.tools.color, this.tools.opacity),
        fontFamily: this.fontFamily,
        fontStyle: this.fontStyle,
        fontWeight: this.fontWeight,
        fontSize: this.scaleFont * 0.9,
        lineHeight: this.lineHeight,
        hasControls: false,
      })
      this.canvas.add(this.textObj)
      this.canvas.setActiveObject(this.textObj)

      this.textObj.enterEditing()
      this.textObj.hiddenTextarea.focus()
      this.textObj.initialized = false

      this.textObj.on('editing:exited', () => {
        if (this.textObj.text.trim() === '') {
          this.textObj.canvas.remove(this.textObj)
        } else {
          if (this.textObj.initialized) {
            this._sendAction(DRAWING.TEXT_UPDATE, this.textObj)
            this.stackAdd('text', this.textObj.id)
          } else {
            this._sendAction(DRAWING.TEXT_ADD, this.textObj)
            this.stackAdd('add', this.textObj.id)
          }
          this.textObj.initialized = true
          const cloneObj = new fabric.IText('', {
            left: left,
            top: top,
            fill: hexToRGBA(this.tools.color, this.tools.opacity),
            fontFamily: this.fontFamily,
            fontStyle: this.fontStyle,
            fontWeight: this.fontWeight,
            fontSize: this.scaleFont * 0.9,
            lineHeight: this.lineHeight,
            hasControls: false,
            text: this.textObj.text,
            id: this.textObj.id,
            tId: this.textObj.tId,
          })
          this.backCanvas.add(cloneObj)
        }

        setTimeout(() => {
          this.editingMode = false
          this.textObj = null
        }, 100)
      })
    },

    /**
     * 드로잉 초기화 객체 메소드
     */
    drawingClear() {
      const ids = this.canvas
        .getObjects()
        .filter(_ => _.opacity === 1)
        .map(_ => _.id)

      this.canvas.discardActiveObject()

      if (ids.length > 0) {
        this.canvas.getObjects().forEach(object => {
          if (!('owner' in object)) {
            this.canvas.remove(object)
          }
        })
        this.backCanvas.getObjects().forEach(object => {
          if (!('owner' in object)) {
            this.backCanvas.remove(object)
          }
        })
        this.canvas.renderAll()
        this.backCanvas.renderAll()
        // this.stackAdd('remove', [...ids]); //삭제 히스토리 쌓기
        this.stackClear() // 전체 삭제

        if (this.$call) {
          this.$call.sendDrawing(DRAWING.CLEAR_ALL, { imgId: this.file.id })
        }
      }
    },
  },
}
