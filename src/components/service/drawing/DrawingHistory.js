/* eslint-disable camelcase */
export default {
  data() {
    return {
      receivedList: {
        // connectionId: [{
        //   id: 0,
        //   owner: owner,
        //   data: {},
        //   path: [] / {},
        // }]
      },
    }
  },
  methods: {
    /**
     * 이미지 목록 수정 메소드
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
