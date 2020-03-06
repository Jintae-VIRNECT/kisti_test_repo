export default {
  filters: {
    statusFilterName(value) {
      if (value == 'complete') return '완료'
      else if (value == 'progress') return '진행'
      else if (value == 'idle') return '미진행'
      else if (value == 'imcomplete') return '미흡'
    },
    processStatusNameFilter(value) {
      if (value === '대기') return 'gray'
      else if (value === '미진행') return 'silver'
      else if (value === '진행') return 'blue'
      else if (value === '미흡') return 'orange'
      else if (value === '완료') return 'green'
      else if (value === '미완수') return 'dark-gray'
      else if (value === '완수') return 'dark-blue'
      else if (value === '결함') return 'dark-red'
    },
    processStatusColorFilter(value) {
      if (value === '대기') return '#8d939c'
      else if (value === '미진행') return '#aebbcc'
      else if (value === '진행') return '#186ae2'
      else if (value === '미흡') return '#f89637'
      else if (value === '완료') return '#2cbc65'
      else if (value === '미완수') return '#6e7277'
      else if (value === '완수') return '#1d5cb7'
      else if (value === '결함') return '#de3636'
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
    leftZeroPad(num) {
      let s = num + ''
      if (s.length < 2) s = '0' + s
      return s
    },
  },
}
