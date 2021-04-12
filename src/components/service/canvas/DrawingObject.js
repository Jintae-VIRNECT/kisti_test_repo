/* eslint-disable no-undef */
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
        fontSize: this.scaleFont,
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
          this.textObj.initialized = true
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
        this.canvas.renderAll()
        // this.stackAdd('remove', [...ids]); //삭제 히스토리 쌓기
        this.stackClear() // 전체 삭제
      }
    },
  },
}
