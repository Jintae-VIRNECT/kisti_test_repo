/* eslint-disable no-undef */
import { DRAWING } from 'configs/remote.config'
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

      const objHist = this.undoList.pop()

      this.updateObjTarget('undo', objHist)

      this.redoList.unshift(objHist)
      this.canvas.setActiveObject(tempActiveObj)

      this.canvas.renderAll()
      this.backCanvas.renderAll()
      this._sendAction(DRAWING.UNDO)

      return this.undoList.length
    },
    receiveStackUndo(data) {
      const owner = 'export'
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

      this.updateObjTarget('undo', objHist, owner)

      this.receiveRedoList[owner].unshift(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this.backCanvas.renderAll()

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
      this.backCanvas.renderAll()
      this._sendAction(DRAWING.REDO)

      return this.redoList.length
    },
    receiveStackRedo(data) {
      const owner = 'export'
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

      this.updateObjTarget('redo', objHist, owner)

      this.receiveUndoList[owner].push(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this.backCanvas.renderAll()

      return this.receiveRedoList[owner].length
    },

    /**
     * 드로잉 히스토리 초기화 메소드
     */
    stackClear() {
      this.$set(this, 'undoList', [])
      this.$set(this, 'redoList', [])
    },

    updateObjTarget(_do, objHist, owner) {
      const objTarget = this.canvas.getObjects()
      const backObjTarget = this.backCanvas.getObjects()
      const id = objHist.id
      const obj = objTarget.find(_ => _.id === id)
      const backObj = backObjTarget.find(_ => _.id === id)
      if (objHist.type === 'add') {
        if (obj) {
          if (_do === 'undo') {
            obj.visible = false
          } else {
            obj.visible = true
          }
        }
        if (backObj) {
          if (_do === 'undo') {
            backObj.visible = false
          } else {
            backObj.visible = true
          }
        }
      } else if (objHist.type === 'text') {
        const idx = objTarget.findIndex(_ => _.id === id)
        const backIdx = backObjTarget.find(_ => _.id === id)
        if (_do === 'undo') {
          let beforeObj, list
          if (owner) {
            list = this.receiveUndoList[owner]
          } else {
            list = this.undoList
          }
          for (let i = list.length - 1; i >= 0; i--) {
            if (list[i].id === id) {
              beforeObj = list[i].object
              break
            }
          }
          objTarget[idx].set({
            text: beforeObj.text,
          })
          if (backIdx > -1) {
            backObjTarget[backIdx].set({
              text: beforeObj.text,
            })
          }
        } else {
          objTarget[idx].set({
            text: objHist.object.text,
          })
          if (backIdx > -1) {
            backObjTarget[backIdx].set({
              text: objHist.text,
            })
          }
        }
      }
    },
  },
}
