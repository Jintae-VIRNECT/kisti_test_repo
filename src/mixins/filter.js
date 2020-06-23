import { mapGetters } from 'vuex'
import _ from 'lodash'
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
        'participants[].nickname',
      ])
     *
     */
    getFilter(list, [...params]) {
      if (this.searchFilter === '') {
        return list
      }

      try {
        const filterList = []

        //target [["ㅎ", "ㅗ", "ㅇ"], ["ㄱ", "ㅣ", "ㄹ"], ["ㄷ", "ㅗ", "ㅇ"]]
        //-> merge to 'ㅎㄱㄷ'
        const mergeChos = (prev, chos) => {
          chos = chos[0] ? chos[0] : chos
          return prev + chos
        }

        const inputDis = Hangul.disassemble(
          this.searchFilter.toLowerCase(),
          true,
        )
        const inputChos = inputDis.reduce(mergeChos, '')

        for (const record of list) {
          let newRecord = null
          for (const param of [...params]) {
            let target = null
            let spts = null

            //find .(dot) and '[]' text
            if (param.includes('.') && param.includes('[]')) {
              spts = param.split('.')

              //access array property
              if (spts[0].includes('[]')) {
                target = record[spts[0].replace('[]', '')]
              }

              //case of a.b.c.d...
            } else if (param.includes('.')) {
              target = _.get(record, param)
            } else {
              target = record[param].toLowerCase()
            }

            //if target is array
            if (Array.isArray(target) && spts.length === 2) {
              target.forEach(obj => {
                if (obj.hasOwnProperty(spts[1])) {
                  const targetDis = Hangul.disassemble(obj[spts[1]], true)
                  const targetChos = targetDis.reduce(mergeChos, '')

                  if (targetChos.includes(inputChos)) {
                    newRecord = record
                  }
                }
              })
            } else {
              const targetDis = Hangul.disassemble(target, true)
              const targetChos = targetDis.reduce(mergeChos, '')

              if (targetChos.includes(inputChos)) {
                newRecord = record
              }
            }
          }

          if (newRecord !== null) {
            filterList.push(newRecord)
          }
        }
        return filterList
      } catch (e) {
        console.error(e)
        return list
      }
    },
  },
  mounted() {
    // this.$eventBus.$emit('clear')
  },
}
