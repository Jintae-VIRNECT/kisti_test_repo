export default {
  data() {
    return {
      timeout: null,
      useClick: true,
    }
  },
  methods: {
    touch(e) {
      if (this.useClick) {
        this.dblClick()
      } else {
        this.touchStart(e)
      }
    },
    dblClick() {
      if (this.timeout) {
        clearTimeout(this.timeout)
        this.timeout = null
        if (typeof this.doEvent === 'function') {
          this.doEvent()
        }
        return
      }
      this.timeout = setTimeout(() => {
        clearTimeout(this.timeout)
        this.timeout = null
        if (typeof this.oneClick === 'function') {
          this.oneClick()
        }
      }, 500)
    },
    touchStart(e) {
      e.preventDefault()
      if (this.timeout !== null) {
        clearTimeout(this.timeout)
      }
      this.timeout = setTimeout(() => {
        if (typeof this.doEvent === 'function') {
          this.doEvent()
        }
        clearTimeout(this.timeout)
        this.timeout = null
      }, 1000)
      return false
    },
    touchEnd(e) {
      e.preventDefault()
      if (this.useClick) return
      if (this.timeout === null) return false
      clearTimeout(this.timeout)
      this.timeout = null
      return false
    },
  },
}
