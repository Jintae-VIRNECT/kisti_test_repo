/* eslint-disable camelcase */
export default {
  methods: {
    /**
     * 이미지 히스토리 업데이트 메소드
     */
    updateHistory() {
      const json = this.canvas.toJSON().objects
      const url = this.backCanvas.toDataURL({
        multiplier: 1,
        left: 0,
        top: 0,
      })
      const params = {
        id: this.file.id,
        img: url,
        json: json,
      }
      //backCanvas의 크기가 0인 상태에서 update 시도시
      //empty image가 업데이트 되는것을 방지하기 위함.
      const isEmptyBackCanvas =
        this.backCanvas.width === 0 && this.backCanvas.height === 0
      if (isEmptyBackCanvas) return

      this.$store.dispatch('updateHistory', params)
    },
  },
}
