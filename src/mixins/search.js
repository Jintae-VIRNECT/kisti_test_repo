import { mapGetters } from 'vuex'
import dayjs from 'dayjs'
import confirmMixin from 'mixins/confirm'
export default {
  mixins: [confirmMixin],
  computed: {
    ...mapGetters(['searchFilter']),
    fromTo() {
      if (this.checkDate()) {
        const formattedFrom = dayjs(this.from).format('YYYY-MM-DD')
        const formattedTo = dayjs(this.to).format('YYYY-MM-DD')
        return `${formattedFrom},${formattedTo}`
      } else {
        console.log('유효하지않은 기간값')
        // this.confirmDefault('유효하지 않은 기간입니다.')
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
    //3개월 제한
    //dayjs 체크
    checkDate() {
      if (this.from === null || this.to === null) {
        return false
      }

      if (dayjs(this.from).isAfter(dayjs(this.to))) {
        return false
      }

      const dayDiff = dayjs(this.to).diff(dayjs(this.from), 'day')

      console.log('dayDiff::', dayDiff)
      if (dayDiff > 90) {
        this.confirmDefault('3개월 이상 조회하실 수 없습니다.')
        return false
      }

      return true
    },
    getParams(paging, page) {
      return {
        userId: this.account.uuid,
        workspaceId: this.workspace.uuid,
        paging: paging,
        page,
        searchWord: this.searchWord,
        fromTo: this.fromTo,
        sort: `${this.sortColumn},${this.sortDirection}`,
        status: this.status,
      }
    },
  },
}
