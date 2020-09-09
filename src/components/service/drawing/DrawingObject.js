/* eslint-disable no-undef */
import { DRAWING } from 'configs/remote.config'
export default {
  data() {
    return {
      lineWidth: 4,
      fontFamily: 'Sans-Serif',
      fontStyle: 'normal',
      fontWeight: 'normal',
      lineHeight: 1,
      fontSize: 16,
    }
  },
  methods: {
    /**
     * 텍스트 추가 메소드
     * @param {Number} left ::추가할 X좌표(Offset)
     * @param {Number} top  ::추가할 Y좌표(Offset)
     */
    addTextObject(left, top) {
      const object = new fabric.IText('', {
        left: left,
        top: top,
        fill: this.tools.color,
        fontFamily: this.fontFamily,
        fontStyle: this.fontStyle,
        fontWeight: this.fontWeight,
        fontSize: this.scaleFont,
        lineHeight: this.lineHeight,
        hasControls: false,
      })
      this.canvas.add(object)
      this.canvas.setActiveObject(object)

      object.enterEditing()
      object.hiddenTextarea.focus()
      object.initialized = false

      object.on('editing:exited', () => {
        if (object.text.trim() === '') {
          object.canvas.remove(object)
        } else {
          if (object.initialized) {
            this._sendAction(DRAWING.TEXT_UPDATE, object)
            this.stackAdd('text', object.id)
          } else {
            this._sendAction(DRAWING.TEXT_ADD, object)
            this.stackAdd('add', object.id)
          }
          object.initialized = true
        }

        setTimeout(() => {
          this.editingMode = false
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
            this.backCanvas.remove(object)
          }
          // object.visible = false;
        })
        this.canvas.renderAll()
        // this.stackAdd('remove', [...ids]); //삭제 히스토리 쌓기
        this.stackClear() // 전체 삭제

        if (this.$call) {
          this.$call.drawing(DRAWING.CLEAR_ALL, { imgId: this.file.id })
        }
      }
    },
  },
}
