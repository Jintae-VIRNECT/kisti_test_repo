/* eslint-disable no-undef */
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
        // top: top - (this.tools.textSize / 2),
        top: top,
        fill: this.tools.drawingColor,
        fontFamily: this.fontFamily,
        fontStyle: this.fontStyle,
        fontWeight: this.fontWeight,
        fontSize: this.tools.textSize,
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
            this._sendAction('updateText', object)
            this.stackAdd('text', [object.id])
          } else {
            this._sendAction('drawText', object)
            this.stackAdd('add', [object.id])
          }
          object.initialized = true
        }

        setTimeout(_ => {
          this.editingMode = false
        }, 100)
      })
      object.on('changed', (event, b) => {
        // 실시간 텍스트 변경 시
        // this._sendAction('updateText', object);
      })
    },

    /**
     * 드로잉 객체 삭제 메소드
     * @param {Object} target ::삭제 대상 드로잉 객체
     */
    removeObject(target) {
      const ids = []

      this.canvas.discardActiveObject()

      if (target._objects) {
        target._objects.forEach(function(object) {
          ids.push(object.id)
          target._objects.forEach(obj => {
            obj.visible = false
          })
        })
      } else {
        ids.push(target.id)
        target.visible = false
      }
      this.canvas.renderAll()
      this.stackAdd('remove', [...ids])

      if (this.$remoteSDK) {
        this.$remoteSDK.message('clear', {
          aId: this.undoList.length,
          oId: target.id,
          tId: target.tId,
        })
      }
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
          object.canvas.remove(object)
          // object.visible = false;
        })
        this.canvas.renderAll()
        // this.stackAdd('remove', [...ids]); //삭제 히스토리 쌓기
        this.stackClear() // 전체 삭제

        if (this.$remoteSDK) {
          this.$remoteSDK.message('clearAll', { imgId: this.shareDocSelect.id })
        }
      }
    },
  },
}
