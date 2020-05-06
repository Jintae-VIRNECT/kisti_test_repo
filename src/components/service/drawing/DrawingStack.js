/* eslint-disable no-undef */
export default {
  methods: {
    /**
     * 드로잉 히스토리 추가 메소드
     * @param {String} ::변화 종류(add/remove/scale/move)
     * @param {Number[,Array]} ::변화 대상 타겟 드로잉 객체ID (현재 다중처리 지원 X)
     * @param {Array} ::현 상태 드로잉 객체목록
     */
    stackAddFirst() {
      const objects = this.canvas.getObjects()

      this.$set(this, 'redoList', [])
      objects.map(_ => {
        const stack = {
          type: 'first',
          ids: [_.id],
          objects: [],
        }

        stack.objects.push(fabric.util.object.clone(_, true))
        this.undoList.push(stack)

        if (_.text && _.text.length > 0) {
          // _.initialized = false;

          _.on('editing:exited', () => {
            if (_.text.trim() === '') {
              _.canvas.remove(_)
            } else {
              this._sendAction('updateText', _)
              this.stackAdd('text', [_.id])
            }

            setTimeout(_ => {
              this.editingMode = false
            }, 100)
          })
        }
      })
    },

    stackAdd(type, ids) {
      const stack = {
        type,
        ids,
        objects: [],
      }

      // Remove만 ids가 들어오는것 같으나..사실 for문은 필요 없어보임.. 개별 선택 삭제 외에 여러개 선택 삭제하는 기능은 없으므로..
      for (const id of ids) {
        stack.objects.push(
          fabric.util.object.clone(
            this.canvas.getObjects().find(_ => _.id === id),
            true,
          ),
        )
      }

      this.$set(this, 'redoList', [])
      this.undoList.push(stack)
    },

    /**
     * 드로잉 작업취소 메소드
     */
    stackUndo() {
      if (
        this.undoList.length === 0 ||
        this.undoList[this.undoList.length - 1].type === 'first'
      ) {
        return false
      }

      const tempActiveObj = this.canvas.getActiveObject()
      this.canvas.discardActiveObject()

      const objTarget = this.canvas.getObjects()
      const objHist = this.undoList.pop()

      objHist.ids.forEach(id => {
        // remove, add는 문제 없음.
        if (objHist.type === 'remove') {
          const obj = objTarget.find(_ => _.id === id)

          if (obj) {
            obj.visible = true
          }
        } else if (objHist.type === 'add') {
          const obj = objTarget.find(_ => _.id === id)

          if (obj) {
            obj.visible = false
          }
        } else if (['scale', 'move'].indexOf(objHist.type) >= 0) {
          let obj
          for (let idx = this.undoList.length - 1; idx >= 0; idx--) {
            const stack = this.undoList[idx]
            if (stack.objects[0].id === id) {
              obj = stack.objects[0]
              break
            }
          }

          for (let idx = objTarget.length - 1; idx >= 0; idx--) {
            if (id === objTarget[idx].id) {
              objTarget[idx].set({
                top: obj.top,
                left: obj.left,
                width: obj.width,
                height: obj.height,
                scaleX: obj.scaleX || 1,
                scaleY: obj.scaleY || 1,
                aCoords: obj.aCoords,
                oCoords: obj.oCoords,
                // zoom은 scale 전 상태인 듯 싶다.
                // "zoomX": obj.zoomX,
                // "zoomY": obj.zoomY,
              })
              break
            }
          }
        } else if (objHist.type === 'text') {
          // const obj = this.undoList[this.undoList.length-1].objects.find(_=>_.id===id);

          // for(let idx=objTarget.length-1; idx>=0; idx--) {
          //     if( id === objTarget[idx].id ) {
          //         objTarget[idx].set({
          //             "text": obj.text
          //         });
          //         break;
          //     }
          // }
          let obj
          for (let idx = this.undoList.length - 1; idx >= 0; idx--) {
            const stack = this.undoList[idx]
            if (stack.objects[0].id === id) {
              obj = stack.objects[0]
              break
            }
          }

          for (let idx = objTarget.length - 1; idx >= 0; idx--) {
            if (id === objTarget[idx].id) {
              objTarget[idx].set({
                text: obj.text,
              })
              break
            }
          }
        }
      })

      this.redoList.unshift(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction('undo')

      return this.undoList.length
    },

    /**
     * 드로잉 작업복원 메소드
     */
    stackRedo() {
      const objHist = this.redoList.shift()
      if (undefined === objHist) {
        return this.redoList.length
      }

      const tempActiveObj = this.canvas.getActiveObject()
      this.canvas.discardActiveObject()

      const objTarget = this.canvas._objects
      objHist.ids.forEach(id => {
        const obj = objTarget.find(_ => _.id === id)
        if (objHist.type === 'add') {
          if (obj) {
            obj.visible = true
          }
        } else if (objHist.type === 'remove') {
          if (obj) {
            obj.visible = false
          }
        } else if (['scale', 'move'].indexOf(objHist.type) >= 0) {
          const obj = objHist.objects.find(_ => _.id === id)

          for (let idx = objTarget.length - 1; idx >= 0; idx--) {
            if (id === objTarget[idx].id) {
              objTarget[idx].set({
                top: obj.top,
                left: obj.left,
                width: obj.width,
                height: obj.height,
                scaleX: obj.scaleX || 1,
                scaleY: obj.scaleY || 1,
                aCoords: obj.aCoords,
                oCoords: obj.oCoords,
                // zoomX: obj.zoomX,
                // zoomY: obj.zoomY,
              })
              break
            }
          }
        } else if (objHist.type === 'text') {
          const obj = objHist.objects.find(_ => _.id === id)

          for (let idx = objTarget.length - 1; idx >= 0; idx--) {
            if (id === objTarget[idx].id) {
              objTarget[idx].set({
                text: obj.text,
              })
              break
            }
          }
        }
      })

      this.undoList.push(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction('redo')

      return this.redoList.length
    },

    /**
     * 드로잉 히스토리 초기화 메소드
     */
    stackClear() {
      this.$set(this, 'undoList', [])
      this.$set(this, 'redoList', [])
    },
  },
}
