import { mapGetters } from 'vuex'
import dayjs from 'dayjs'
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
export default {
  mixins: [confirmMixin, toastMixin],
  computed: {
    ...mapGetters(['searchFilter']),
    fromTo() {
      const result = this.checkDate()
      if (result === true) {
        const formattedFrom = dayjs(this.from).format('YYYY-MM-DD')
        const formattedTo = dayjs(this.to).format('YYYY-MM-DD')
        return `${formattedFrom},${formattedTo}`
      } else {
        const defaultTo = dayjs().format('YYYY-MM-DD')
        const defaultFrom = dayjs()
          .subtract(7, 'day')
          .format('YYYY-MM-DD')
        return `${defaultFrom},${defaultTo}`
      }
    },
    from() {
      return this.searchFilter.date.from
    },
    to() {
      return this.searchFilter.date.to
    },
    searchWord() {
      return this.searchFilter.input.text
    },
    status() {
      return this.searchFilter.status.status
    },
    useDate() {
      return this.searchFilter.useDate.useDate
    },
    sortColumn() {
      return this.searchFilter.sort.column
    },
    sortDirection() {
      return this.searchFilter.sort.direction
    },
  },
  methods: {
    checkDate() {
      const showToast = this.useDate

      if (this.from === null || this.to === null) {
        if (showToast) {
          this.toastDefault('올바른 날짜를 지정해주세요.')
        }
        return 'INVALID_DATE'
      }

      if (dayjs(this.from).isAfter(dayjs(this.to))) {
        if (showToast) {
          this.toastDefault('유효하지 않은 기간입니다.')
        }
        return 'INVALID_PERIOD'
      }

      const dayDiff = dayjs(this.to).diff(dayjs(this.from), 'day')

      if (dayDiff > 90) {
        if (showToast) {
          this.toastDefault('3개월이상 조회하실 수 없습니다.')
        }
        return 'OVER_PERIOD'
      }

      return true
    },
    getParams(paging, page) {
      const params = {
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        paging: paging,
        page,
        searchWord: this.searchWord,
        fromTo: this.fromTo,
        sort: `${this.sortColumn},${this.sortDirection}`,
        status: this.status,
      }

      return params
    },
  },
}
