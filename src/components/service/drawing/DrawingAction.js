import { getSignalParams } from 'utils/drawing'

export default {
  methods: {
    /**
     * chunk 이미지 전송
     * @param {String} imgId
     * @param {String} imgName
     * @param {String} image 이미지 dataURL
     */
    // sendImage(params, target = null) {
    //   return
    //   const chunk = getChunk(params['image'])
    //   delete params['image']

    //   params.width = this.img.width
    //   params.height = this.img.height

    //   let type

    //   for (let i = 0; i < chunk.length; i++) {
    //     if (i === 0) {
    //       type = DRAWING.FIRST_FRAME
    //       if (chunk.length === 1) {
    //         type = DRAWING.LAST_FRAME
    //       }
    //     } else if (i === chunk.length - 1) {
    //       type = DRAWING.LAST_FRAME
    //     } else {
    //       type = DRAWING.FRAME
    //     }
    //     params.chunk = chunk[i]

    //     this.$call.sendDrawing(type, params, target)
    //   }
    // },

    /**
     * 드로잉 액션 상대기기 전송 내부 메소드
     * @param {String} type   ::액션 종류( getSignalParams 참고 )
     * @param {Object} object ::변화대상 드로잉객체
     */
    _sendAction(type, object, custom) {
      const aId = this.undoList.length
      const state = {
        color: this.tools.color,
        opacity: this.tools.opacity,
        width: this.tools.lineWidth / this.origin.scale,
        size: this.tools.fontSize / this.origin.scale,
        scale: 1 / this.canvas.backgroundImage.scaleX,
        imgWidth: this.canvas.getWidth(),
        imgHeight: this.canvas.getHeight(),
        // oriWidth: this.origin.width,
        // oriHeight: this.origin.height,
        posScale: this.canvas.getWidth() / this.origin.width,
        widthScale: this.origin.width / this.img.width,
      }
      const param = getSignalParams(type, aId, object, state)
      param.imgId = this.file.id

      if (object) {
        param.oId = object.id
      }

      if (this.$call.session) {
        this.$call.sendDrawing(type, { ...param, ...custom })
      }

      // tId 업데이트
      if (
        ['drawMove', 'drawScale', 'rotate', 'remove', 'updateText'].indexOf(
          type,
        ) >= 0
      ) {
        object.tId = aId
      }
    },
  },
}
