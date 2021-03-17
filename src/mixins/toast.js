export default {
  methods: {
    toastError(message) {
      this.$toasted.error(message, {
        position: 'bottom-center',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
    },
    toastNotify(message) {
      this.$toasted.show(message, {
        position: 'bottom-center',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
      })
    },
    toastDefault(message, options) {
      this.$toasted.clear()
      this.$toasted.show(message, {
        position: 'top-center',
        className: ['remote-toast'],
        type: 'info',
        duration: 3000,
        action: null,
        containerClass: 'toast-default',
        ...options,
      })
    },
  },
}
