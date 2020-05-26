import { mapGetters } from 'vuex'
const Hangul = require('hangul-js')

export default {
  computed: {
    ...mapGetters(['searchFilter']),
  },
  watch: {
    // searchFilter(filter) {
    //   this.getFilter()
    // }
  },
  methods: {
    getFilter(list, [...params], depth = null) {
      if (this.searchFilter === '') {
        return list
      }

      const filterList = []
      // const filteringValue = this.searchFilter.toLowerCase()
      const filteringSearcher = new Hangul.Searcher(
        this.searchFilter.toLowerCase(),
      )
      for (const object of list) {
        let addObject = null
        for (const param of [...params]) {
          if (depth !== null) {
            // if (object[depth][param].toLowerCase().match(filteringValue)) {
            if (filteringSearcher.search(object[depth][param].toLowerCase())) {
              // filterList.push(object)
              addObject = object
            }
          } else {
            // if (object[param].toLowerCase().match(filteringValue)) {
            if (filteringSearcher(object[param].toLowerCase())) {
              // filterList.push(object)
              addObject = object
            }
          }
        }
        if (addObject !== null) {
          filterList.push(addObject)
        }
      }
      return filterList
    },
  },
  mounted() {
    // this.$eventBus.$emit('clear')
  },
}
