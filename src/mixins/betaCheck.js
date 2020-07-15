import toastMixin from 'mixins/toast'
export default {
  mixins: [toastMixin],
  methods: {
    checkBeta() {
      this.toastDefault('베타 버전에서는 지원하지 않는 기능입니다.')
      return true
    },
  },
}
