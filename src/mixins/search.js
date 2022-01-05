/**
 * 검색 API 공통 mixin
 * 페이지에서 Searchbar 컴포넌트들에 ref를 달아주고 이 mixin을 임포트
 * 컴포넌트들의 값이 변경되면 changedSearchParams() 이벤트가 발생하고 searchParams{} 의 값이 변경된다.
 */

export default {
  data() {
    return {
      searchParams: {},
    }
  },
  methods: {
    emitChangedSearchParams(customParams = {}) {
      const { filter, sort, keyword, page } = this.$refs
      if (this.changedSearchParams) {
        this.$nextTick(() => {
          this.searchParams = {
            ...this.searchParams,
            search: keyword && keyword.value,
            filter: filter && this.filterSetValue(filter),
            sort: sort && sort.value,
            page: page && page.value,
            ...customParams,
          }
          // null 값 삭제
          Object.keys(this.searchParams).forEach(key => {
            if (!this.searchParams[key]) delete this.searchParams[key]
          })

          // 각 페이지에서 선언되어 있는 changedSearchParams 실행
          this.changedSearchParams()
        })
      }
    },
    /**
     * @description filter ref의 $on 이벤트 메소드. filter 드롭다운 메뉴에서 특정 메뉴클릭시, 'ALL'메뉴 선택유무 판단 후 각 emitChangedSearchParams 함수 실행.
     * @param {HTMLElement} filter
     * @param {HTMLElement} page
     */
    filterRefEvent(filter, page) {
      filter.$on('change', () => {
        const last = filter.myValue[filter.myValue.length - 1]
        if (last === 'ALL' || !filter.myValue.length) {
          filter.myValue = ['ALL']
        } else if (last !== 'ALL' && filter.myValue[0] === 'ALL') {
          filter.myValue.shift()
        }
        page.myPage = 1
        this.emitChangedSearchParams()
      })
    },
    /**
     * @description filter ref의 Array타입인 value 값을 꺼내어 String 값으로 반환.
     * @param {HTMLElement} filter
     * @returns {string} 선택한 filter 값. ex) "VR, QR"
     */
    filterSetValue(filter) {
      if (!filter.length) return filter.value.join(',')
      else {
        let result = {}
        filter.map(f => {
          result[f.$vnode.key] = f.value.join(',')
        })
        return result
      }
    },
  },
  async mounted() {
    await new Promise(_ => setTimeout(_, 100)) // ssr bug
    // 공통 동작
    const { filter, sort, keyword, page, table, mine } = this.$refs

    // 각 컴포넌트에 이벤트 리스너 적용
    if (keyword) {
      // 키워드 검색 시 1페이지로 이동
      keyword.$on('change', val =>
        this.emitChangedSearchParams({ search: val, page: 1 }),
      )
    }

    if (sort)
      sort.$on('change', val => this.emitChangedSearchParams({ sort: val }))
    if (page)
      page.$on('change', val => this.emitChangedSearchParams({ page: val }))
    if (filter) {
      filter.length > 1
        ? filter.map(f => this.filterRefEvent(f, page))
        : this.filterRefEvent(filter, page)
    }
    if (table) {
      table.$on('sort-change', ({ prop, order }) => {
        if (!order) this.emitChangedSearchParams()
        else {
          const sort = `${prop},${order.replace('ending', '')}`
          this.emitChangedSearchParams({ sort })
        }
      })
    }
    if (mine) {
      mine.$on('change', label => {
        if (label === this.$t('common.all')) this.searchParams.mine = false
        else this.searchParams.mine = true
        page.myPage = 1
        this.emitChangedSearchParams()
      })
    }
  },
}
