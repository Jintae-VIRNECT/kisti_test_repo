import { mapGetters } from 'vuex'
import _ from 'lodash'
const ChosungSearch = require('plugins/remote/hangul-chosung')

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
    /**
     *
     * Returns the object at the specified path
     *
     * if error occur then return raw list
     *
     * @param {Array} list array of object
     * @param {Array} param1 array of property path
     *
     * @example this.getFilter(this.historyList, [
        'title',
        'memberList[].nickname',
      ])
     *
     */
    getFilter(list, [...params]) {
      if (this.searchFilter === '') {
        return list
      }

      try {
        const filterList = []
        for (const content of list) {
          let pushFlag = false
          for (const param of [...params]) {
            if (pushFlag === true) break
            let target = null
            let spts = null

            //find .(dot) and '[]' text
            if (param.includes('.') && param.includes('[]')) {
              spts = param.split('.')

              target = content[spts[0].replace('[]', '')]

              //case of a.b.c.d...
            } else if (param.includes('.')) {
              target = _.get(content, param)
            } else {
              target = content[param]
            }
            if (!target) continue

            //if target is array
            if (Array.isArray(target) && spts.length === 2) {
              target.forEach(obj => {
                if (!pushFlag) {
                  if (obj.hasOwnProperty(spts[1])) {
                    if (obj[spts[1]] && obj[spts[1]].length > 0) {
                      if (
                        obj[spts[1]]
                          .toLowerCase()
                          .includes(this.searchFilter.toLowerCase())
                      ) {
                        filterList.push(content)
                        pushFlag = true
                      } else if (
                        ChosungSearch.is(this.searchFilter, obj[spts[1]], false)
                      ) {
                        filterList.push(content)
                        pushFlag = true
                      }
                    }
                  }
                }
              })
            } else {
              if (
                target.toLowerCase().includes(this.searchFilter.toLowerCase())
              ) {
                filterList.push(content)
                pushFlag = true
              } else if (ChosungSearch.is(this.searchFilter, target, false)) {
                filterList.push(content)
                pushFlag = true
              }
            }
          }
        }
        return filterList
      } catch (e) {
        console.error(e)
        return list
      }
    },
    scrollReset() {
      this.$eventBus.$emit('scroll:reset:workspace')
    },
  },
  mounted() {
    // this.$eventBus.$emit('clear')
  },
}
