/* eslint-disable no-undef */
import { DRAWING } from 'configs/remote.config'
import { fabric } from 'plugins/remote/fabric.custom'
import { hexToRGBA } from 'utils/color'
export default {
  data() {
    return {
      fontFamily: 'Sans-Serif',
      fontStyle: 'normal',
      fontWeight: 'normal',
      lineHeight: 1,
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
        fontSize: this.tools.fontSize / this.origin.scale,
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
          this.canvas.remove(this.textObj)
        } else {
          if (this.textObj.initialized) {
            this._sendAction(DRAWING.TEXT_UPDATE, this.textObj)
            this.stackAdd('text', this.textObj.id)
          } else {
            this._sendAction(DRAWING.TEXT_ADD, this.textObj)
            this.stackAdd('add', this.textObj.id)
          }
          this.textObj.initialized = true
          const obj = new fabric.IText(this.textObj.text, {
            left: this.textObj.left,
            top: this.textObj.top,
            fill: this.textObj.fill,
            fontFamily: this.fontFamily,
            fontStyle: this.fontStyle,
            fontWeight: this.fontWeight,
            fontSize: this.textObj.fontSize,
            lineHeight: this.lineHeight,
            hasControls: false,
          })
          this.backCanvas.add(obj)
        }

        setTimeout(() => {
          this.editingMode = false
          this.textObj = null
        }, 100)
      })
    },
  },
}
