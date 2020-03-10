import { processStatus } from '@/models/process'

function colorNameToHash(name) {
  return {
    gray: '#8d939c',
    silver: '#aebbcc',
    blue: '#186ae2',
    orange: '#f89637',
    green: '#2cbc65',
    'dark-gray': '#6e7277',
    'dark-blue': '#1d5cb7',
    'dark-red': '#de3636',
  }[name]
}

export default {
  filters: {
    processStatusFilterName(value) {
      return processStatus.find(status => status.value === value).label
    },
    processStatusNameColor(name) {
      return processStatus.find(status => status.label === name).color
    },
    processStatusColorFilter(name) {
      return colorNameToHash(
        processStatus.find(status => status.label === name).color,
      )
    },
    limitAuthsLength(array) {
      const SLICE_COUNT = 17
      let answer = ''
      let sumOfStrings = 0

      const divider = ', '
      for (let i = 0; i < array.length; i++) {
        answer += array[i]
        sumOfStrings += array[i].length + divider.length
        if (sumOfStrings > SLICE_COUNT) {
          const midfix = sumOfStrings - divider.length <= 13 ? '' : '...'
          const suffix =
            array.length - 1 > i ? ` +${array.length - (i + 1)}` : ''
          return answer.slice(0, SLICE_COUNT) + midfix + suffix
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
