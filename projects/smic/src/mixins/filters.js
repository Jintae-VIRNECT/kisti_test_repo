export default {
  filters: {
    statusFilterName(value) {
      if (value == 'complete') return '완료'
      else if (value == 'progress') return '진행'
      else if (value == 'idle') return '미진행'
      else if (value == 'imcomplete') return '미흡'
    },
    limitAuthsLength(array) {
      let answer = ''
      let sumOfStrings = 0

      const divider = ', '
      for (let i = 0; i < array.length; i++) {
        answer += array[i]
        sumOfStrings += array[i].length + divider.length
        if (sumOfStrings > 13) {
          const midfix = sumOfStrings - divider.length <= 13 ? '' : '...'
          const suffix =
            array.length - 1 > i ? ` +${array.length - (i + 1)}` : ''
          return answer.slice(0, 13) + midfix + suffix
        } else if (array.length - 1 === i) break
        answer += divider
      }
      return answer
    },
  },
}
