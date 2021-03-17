// logging with trace

console.logger = (...value) => {
  // console.groupCollapsed(...value)
  /* 1. 로그 trace 출력 */
  // console.trace(...value)
  // getTrace(value)
  console.log(...value)
  // console.groupEnd()
}
const isDev = window.localStorage.getItem('env') === 'develop'

const getTrace = () => {
  var obj = {}
  Error.stackTraceLimit = 30
  Error.captureStackTrace(obj, logger)
  /* 2. 로그 eval, step 제거 출력 */
  // Error.stackTraceLimit = 10
  // const stacks = obj.stack.split('    ')
  // let fullStack = ''
  // for (let stack of stacks) {
  //   if (!stack.includes('step') && !stack.includes('eval')) {
  //     fullStack += stack
  //   }
  // }
  // console.log(fullStack)
  /* 3. Error 객체로 trace 출력 */
  console.log(obj.stack)
  /* 4. Error 객체로 Error 제거해서 trace 출력 */
  // console.log(obj.stack.replace(/Error/, 'Trace'))
}
/**
 * type: dev / type
 */
export const logger = (type = 'dev', ...value) => {
  console.log(`${type}::`, ...value)
}

export const debug = (...value) => {
  if (process.env.NODE_ENV === 'production') {
    if ((window.env && window.env === 'develop') || isDev) {
      console.log(...value)
      window.env = 'develop'
    }
  } else {
    console.logger(...value)
  }
}

export default logger
