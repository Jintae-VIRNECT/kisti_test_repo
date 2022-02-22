import confirmMixin from 'mixins/confirm'

export default {
  mixins: [confirmMixin],
  props: {
    visible: {
      type: Boolean,
      dafault: true,
    },
    btnLoading: {
      type: Boolean,
    },
    beforeClose: {
      type: Function,
    },
    users: {
      type: Array,
    },
    selection: {
      type: Array,
    },
    roomInfo: {
      type: Object,
    },
    loading: {
      type: Boolean,
    },
  },
  computed: {
    btnDisabled() {
      return this.selection.length < 1
    },
    shortName() {
      if (this.account.nickname.length > 10) {
        return this.account.nickname.substr(0, 10)
      } else {
        return this.account.nickname
      }
    },
    onlineMemeberOfSelection() {
      return this.selection.filter(user => user.accessType === 'LOGIN').length
    },
  },
  methods: {
    close() {
      this.beforeClose()
    },
    selectUser(user) {
      this.$emit('userSelect', user)
    },
    inviteRefresh() {
      this.$emit('inviteRefresh')
    },
  },
}
