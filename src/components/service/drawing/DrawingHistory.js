/* eslint-disable camelcase */
export default {
  methods: {
    /**
     * 이미지 목록 수정 메소드
     */
    updateHistory() {
      const json = this.canvas.toJSON()
      const thumbnail = this.canvas.lowerCanvasEl.toDataURL()
      // const thumbnail = this.resizeCanvas(this.canvas.lowerCanvasEl);

      this.$store.dispatch('updateHistory', {
        id: this.file.id,
        img: thumbnail,
        json: json,
      })
    },

    /**
     * 이미지 목록 삭제 메소드
     */
    clearHistory() {
      // clear history
    },
    resizeCanvas(canvas) {
      let width = canvas.width
      let height = canvas.height
      const max_width = 120
      const max_height = 88

      if (width / max_width > height / max_height) {
        // 가로가 길 경우
        if (width > max_width) {
          height *= max_width / width
          width = max_width
        }
      } else {
        // 세로가 길 경우
        if (height > max_height) {
          width *= max_height / height
          height = max_height
        }
      }
      canvas.getContext('2d').width = width
      canvas.getContext('2d').height = height
      const dataUrl = canvas.toDataURL('image/jpeg')
      return dataUrl
    },
  },
}
