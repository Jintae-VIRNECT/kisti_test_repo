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
    toastCallNotify() {
      this.$toasted.global.callNotify({
        userName: '이름',
        message: 'message',
        dateTime: 'dateTime',
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
  },
}
