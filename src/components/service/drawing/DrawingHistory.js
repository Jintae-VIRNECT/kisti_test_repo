/* eslint-disable camelcase */
export default {
  methods: {
    /**
     * 이미지 목록 추가 메소드
     */
    addHistory(image) {
      // 모바일 수신부 타입: Int32
      const imgId = Date.now()
        .toString()
        .substr(-9) // getID('history');
      const ext = 'png'
      // const imgData = this.fileReader.result //this.canvas.lowerCanvasEl.toDataURL('image/png')
      const imgData = image.src // this.canvas.lowerCanvasEl.toDataURL('image/png')
      // const smallImgData = this.resizing(image)
      const fileName = this.fileReader.fileName

      this.$store
        .dispatch('addShareDoc', {
          id: imgId,
          json: this.canvas.toJSON(),
          origin: imgData,
          thumbnail: imgData,
          fileName: fileName,
          // scale: this.imageScaled
        })
        .then(() => {
          this.$store
            .dispatch('selectShareDoc', this.shareDocListLen - 1)
            .then(() => {
              this.isInit = true
            })
        })

      if (this.$remoteSDK) {
        const img = new Image()
        img.onload = event => {
          this.$remoteSDK.message('showImage', {
            imgId,
            ext,
            fileName: this.fileReader.fileName,
            width: event.target.width,
            height: event.target.height,
            imgData: imgData.replace(/data:image\/.+;base64,/, ''),
          })
        }
        img.src = imgData
      }
    },

    /**
     * 이미지 목록 수정 메소드
     */
    updateHistory() {
      const json = this.canvas.toJSON()
      const thumbnail = this.canvas.lowerCanvasEl.toDataURL()
      // const thumbnail = this.resizeCanvas(this.canvas.lowerCanvasEl);
      const hist = this.shareDocList

      if (
        hist.length < 0 ||
        JSON.stringify(hist[hist.length - 1].json) !== JSON.stringify(json)
      ) {
        this.$store.dispatch('updateShareDoc', { thumbnail, json })
      }
    },

    /**
     * 이미지 목록 삭제 메소드
     */
    clearHistory() {
      // clear history
    },
    resizing(imageUrl) {
      return new Promise((resolve, reject) => {
        const image = new Image()
        image.onload = event => {
          const canvas = document.createElement('canvas')
          const max_size = 4096
          // 최대 기준을 1280으로 잡음.
          let width = image.width
          let height = image.height

          if (width > height) {
            // 가로가 길 경우
            if (width > max_size) {
              height *= max_size / width
              width = max_size
            }
          } else {
            // 세로가 길 경우
            if (height > max_size) {
              width *= max_size / height
              height = max_size
            }
          }
          canvas.width = width
          canvas.height = height
          canvas.getContext('2d').drawImage(image, 0, 0, width, height)
          const dataUrl = canvas.toDataURL('image/jpeg')
          resolve(dataUrl)
        }
        image.src = imageUrl
      })
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
