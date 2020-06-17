/* eslint-disable no-undef */
import { AR_DRAWING } from 'configs/remote.config'
export default {
  data() {
    return {
      lineWidth: 4,
      lineHeight: 1,
    }
  },
  methods: {
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
        })
        this.canvas.renderAll()
        this.stackClear() // 전체 삭제

        if (this.$call) {
          this.$call.arDrawing(AR_DRAWING.CLEAR_ALL, { imgId: this.file.id })
        }
      }
    },
  },
}
