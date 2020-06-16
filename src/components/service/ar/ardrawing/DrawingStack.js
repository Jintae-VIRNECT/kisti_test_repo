/* eslint-disable no-undef */
import { AR_DRAWING } from 'configs/remote.config'
import { arCount } from 'utils/callOptions'
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
      if (this.undoList.length > arCount) {
        this.undoList.splice(0, 1)
        this.canvas.remove(this.canvas.getObjects()[0])
      }
    },

    /**
     * 드로잉 작업취소 메소드
     */
    stackUndo() {
      if (this.undoList.length === 0) {
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
      this._sendAction(AR_DRAWING.UNDO)

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

      this.updateObjTarget('redo', objHist)

      this.undoList.push(objHist)
      this.canvas.setActiveObject(tempActiveObj)
      this.canvas.renderAll()
      this._sendAction(AR_DRAWING.REDO)
      console.log('>>undo: ', this.undoList.length)
      console.log('>>redo: ', this.redoList.length)

      return this.redoList.length
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
      }
    },
  },
}
