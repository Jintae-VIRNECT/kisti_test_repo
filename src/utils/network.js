//JUST AN EXAMPLE, PLEASE USE YOUR OWN PICTURE!
// const imageAddr =
// 'http://www.kenrockwell.com/contax/images/g2/examples/31120037-5mb.jpg'
/* example
function network() {
  detectNetworkSpead().then(speed => {
    console.log('bps::', speed.toFixed(2))
    console.log('kbps::', (speed / 1024).toFixed(2))
    console.log('mbps::', (speed / 1024 / 1024).toFixed(2))
  })
}
*/
import Axios from 'axios'

const axios = Axios.create({
  timeout: process.env.NODE_ENV === 'production' ? 5000 : 10000,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Content-Type': 'application/json',
  },
})
const imageAddr =
  'https://media.geeksforgeeks.org/wp-content/cdn-uploads/20200714180638/CIP_Launch-banner.png'

// const downloadSize = 4995374 //bytes
const downloadSize = 5616998 //bytes

const detectNetworkSpead = () => {
  return new Promise((resolve, reject) => {
    let startTime, endTime
    var download = new Image()
    download.onload = function() {
      endTime = new Date().getTime()
      const bps = calcBps(startTime, endTime)
      resolve(bps)
    }

    download.onerror = (err, msg) => {
      console.error(msg)
      reject('Invalid image, or error downloading')
    }

    startTime = new Date().getTime()
    let cacheBuster = '?nnn=' + startTime
    download.src = imageAddr + cacheBuster
  })
}

const calcBps = (startTime, endTime) => {
  var duration = (endTime - startTime) / 1000
  var bitsLoaded = downloadSize * 8
  return bitsLoaded / duration
  // var speedBps = (bitsLoaded / duration).toFixed(2) // bps
  // var speedKbps = (speedBps / 1024).toFixed(2) // kbps
  // var speedMbps = (speedKbps / 1024).toFixed(2) // Mbps
}

export const checkOnline = async () => {
  try {
    const res = await axios.get('/healthcheck')
    if (res.status === 200) {
      return true
    }
    return false
  } catch (err) {
    return false
  }
}

export default detectNetworkSpead
