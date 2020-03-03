// import Axios from 'axios'

// const axios = Axios.create({
//   timeout: 10000,
//   withCredentials: true,
//   headers: {
//     'Access-Control-Allow-Origin': 'https://virnectremote.com',
//     'Content-Type': 'application/json',
//     client: 'web',
//   },
// })

export const log = function log(...value) {
  // axios.post('/logs', { data: [...value] })
  // 	.then(res => {
  // 		console.log(res.data)
  // 	})
  // if(window.origin && window.origin.indexOf('virnectremote') > -1) {
  if (process.env.NODE_ENV === 'production') {
    if (window.env && window.env === 'develop') {
      console.log(...value)
    }
    // 로그 기록남기는 프로세스 필요함
  } else {
    // console.trace()
    console.log(...value)
  }
}
