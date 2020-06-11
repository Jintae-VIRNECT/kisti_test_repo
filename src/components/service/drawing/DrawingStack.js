/* eslint-disable no-undef */
/**
 * undoList
 * {
 *  type: 'add',
 *  ids: [id],
 *  objects: [objects]
 * }
 */
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

            setTimeout(() => {
              this.editingMode = false
            }, 100)
          })
        }
      })
    },

    stackAdd(type, id) {
      const stack = {
        type,
        id,
        object: fabric.util.object.clone(
          this.canvas.getObjects().find(_ => _.id === id),
          true,
        ),
      }

      this.$set(this, 'redoList', [])
      this.undoList.push(stack)
    },

    receiveStackAdd(type, id, owner) {
      const stack = {
        type,
        id,
        object: fabric.util.object.clone(
          this.canvas.getObjects().find(_ => _.id === id),
          true,
        ),
      }

      this.receiveRedoList[owner] = []
      this.receiveUndoList[owner].push(stack)
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

      // const objTarget = this.canvas.getObjects()
      const objHist = this.undoList.pop()

      this.updateObjTarget('undo', objHist)

      this.redoList.unshift(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction('undo')

      return this.undoList.length
    },
    receiveStackUndo(owner) {
      if (
        !(owner in this.receiveUndoList) ||
        this.receiveUndoList[owner].length === 0
      )
        return

      const tempActiveObj = this.canvas.getActiveObject()
      this.canvas.discardActiveObject()

      // const objTarget = this.canvas
      //   .getObjects()
      //   .filter(obj => obj.owner === owner)

      const objHist = this.receiveUndoList[owner].pop()

      this.updateObjTarget('undo', objHist)

      this.receiveRedoList[owner].unshift(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()

      return this.receiveUndoList[owner].length
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

      this.updateObjTarget('redo', objHist)

      this.undoList.push(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction('redo')

      return this.redoList.length
    },
    receiveStackRedo(owner) {
      if (
        !(owner in this.receiveRedoList) ||
        this.receiveRedoList[owner].length === 0
      )
        return
      const objHist = this.receiveRedoList[owner].shift()
      if (undefined === objHist) {
        return this.receiveRedoList[owner].length
      }

      const tempActiveObj = this.canvas.getActiveObject()
      this.canvas.discardActiveObject()

      this.updateObjTarget('redo', objHist)

      this.receiveUndoList[owner].push(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction('redo')

      return this.receiveRedoList[owner].length
    },

    /**
     * 드로잉 히스토리 초기화 메소드
     */
    stackClear() {
      this.$set(this, 'undoList', [])
      this.$set(this, 'redoList', [])
    },

    updateObjTarget(_do, objHist) {
      const objTarget = this.canvas.getObjects()
      const id = objHist.id
      const obj = objTarget.find(_ => _.id === id)
      if (objHist.type === 'add') {
        if (obj) {
          if (_do === 'undo') {
            obj.visible = false
          } else {
            obj.visible = true
          }
        }
      } else if (objHist.type === 'remove') {
        if (obj) {
          if (_do === 'undo') {
            obj.visible = true
          } else {
            obj.visible = false
          }
        }
      } else if (['scale', 'move'].indexOf(objHist.type) >= 0) {
        const idx = objTarget.findIndex(target => target.id === id)
        objTarget[idx].set({
          top: obj.top,
          left: obj.left,
          width: obj.width,
          height: obj.height,
          scaleX: obj.scaleX || 1,
          scaleY: obj.scaleY || 1,
          aCoords: obj.aCoords,
          oCoords: obj.oCoords,
        })
      } else if (objHist.type === 'text') {
        const idx = objTarget.findIndex(target => target.id === id)
        objTarget[idx].set({
          text: obj.text,
        })
      }
    },
  },
}
