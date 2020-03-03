import { mapGetters } from 'vuex'

// global mixin
export default {
  computed: {
    ...mapGetters(['toast']),
  },
  watch: {
    'toast.type'(value) {
      if (value === null) return
      let text
      if (this.$t(this.toast.text) === this.toast.text) {
        text = this.toast.text
      } else {
        text = this.$t(this.toast.text)
      }

      if (value === 'notice') {
        this.toastNotice(text)
      } else if (value === 'error') {
        this.toastError(text)
      } else if (value === 'alarm') {
        this.toastAlarm(text)
      }
      this.$store.dispatch('initToast')
    },
  },
  methods: {
    toastAlarm(message) {
      this.$toasted.error(message, {
        position: 'bottom-center',
        duration: 5000,
      })
    },
    toastError(message, options) {
      if (options) {
        if (typeof options.icon === 'string') {
          options.icon += ' material-icons'
        } else {
          options.icon.name += ' material-icons'
        }
      }

      this.$toasted.show(message, {
        position: 'bottom-center',
        type: 'error',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
        ...options,
      })
    },
    toastNotice(message, options) {
      if (options && options.icon) {
        if (typeof options.icon === 'string') {
          options.icon += ' material-icons'
        } else {
          options.icon.name += ' material-icons'
        }
      }

      this.$toasted.show(message, {
        position: 'bottom-center',
        type: 'info',
        duration: 5000,
        action: {
          icon: 'close',
          onClick: (e, toastObject) => {
            toastObject.goAway(0)
          },
        },
        ...options,
      })
    },
  },
}
