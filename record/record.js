const url = new URL(window.location.href)
const token = url.searchParams.get('token')
let metaData = url.searchParams.get('metaData')
let options = url.searchParams.get('options')

var OV = new OpenVidu()
// OV.enableProdMode()

var session = OV.initSession()

// const metaData = {
//   clientData: '40247ff4cbe04a1e8ae3203298996f4c',
//   device: 0,
//   deviceType: 'DESKTOP',
//   roleType: 'EXPERT',
// }
// const metaData = {}

options = JSON.parse(options)
metaData = JSON.parse(metaData)

let streamCount = 0

let subscriber
session.on('streamCreated', event => {
  console.log('event streamCreated::', event)
  if (event.stream.videoActive == true) {
    subscriber = session.subscribe(event.stream, 'videos')
    subscriber.on('videoElementCreated', event => {
      console.log('videoElementCreated')
      document.querySelector('video').muted = false
      document.querySelector('video').play()
    })

    console.log('streamCount::', streamCount)

    streamCount++
    layoutSelector(streamCount)
  }
})

session.on('streamDestroyed', event => {
  console.log('event streamDestroyed::', event)
  streamCount--
  console.log('streamCount::', streamCount)
  layoutSelector(streamCount)
  if (streamCount <= 0) {
    console.log('session::disconnect')
    session.disconnect()
  }
})

session
  .connect(token, metaData, options)
  .then(() => {
    console.log('Recorder participant connected')
  })
  .catch(error => {
    console.error(error)
  })

const layoutSelector = streamCount => {
  const videoContainer = document.getElementById('videos')

  videoContainer.classList.remove(
    'container',
    'one',
    'two',
    'three',
    'four',
    'five',
    'six',
  )

  videoContainer.classList.add('container')

  if (streamCount === 1) {
    videoContainer.classList.add('one')
  } else if (streamCount === 2) {
    videoContainer.classList.add('two')
  } else if (streamCount === 3) {
    videoContainer.classList.add('three')
  } else if (streamCount === 4) {
    videoContainer.classList.add('four')
  } else if (streamCount === 5) {
    videoContainer.classList.add('five')
  } else if (streamCount === 6) {
    videoContainer.classList.add('six')
  }
}
