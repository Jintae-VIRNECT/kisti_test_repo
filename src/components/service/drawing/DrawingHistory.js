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

      this.$store.dispatch('updateHistory', params)
    },
  },
}
