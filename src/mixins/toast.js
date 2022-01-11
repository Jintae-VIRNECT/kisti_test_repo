export default {
  computed: {
    toastPosition() {
      if (this.isMobileSize) return 'top-center'
      else {
        if (this.$route.name === 'service') return 'top-center'
        else return 'bottom-center'
      }
    },
    className() {
      if (this.isMobileSize) return ['remote-toast', 'mobile']
      else return ['remote-toast']
    },
  },
  methods: {
    //.error
    toastError(message) {
      this.$toasted.error(message, {
        position: this.toastPosition, //'bottom-center',
        className: this.className,
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
    },
    //.default
    toastNotify(message) {
      this.$toasted.show(message, {
        position: this.toastPosition, //'bottom-center',
        className: this.className,
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
    },
    //.info
    toastDefault(message, options) {
      this.$toasted.clear()
      this.$toasted.show(message, {
        position: this.toastPosition, //'top-center',
        className: this.className,
        type: 'info',
        duration: 3000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
        containerClass: 'toast-default',
        ...options,
      })
    },
  },
}
