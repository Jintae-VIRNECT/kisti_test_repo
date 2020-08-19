const url = new URL(window.location.href)
const token = url.searchParams.get('token')
let options = url.searchParams.get('options')

const TOKEN = token
//"wss://192.168.6.3:8000?sessionId=ses_PvxWe21rKY&token=tok_PJ6RnigJnFA8r2LO&role=PUBLISHER&version=2.0.0&recorder=true"

var OV = new OpenVidu()
var session = OV.initSession()
const metaData = {}

options = JSON.parse(options)
// {
//     iceServers: [
//                     {
//                         credential: "remote",
//                         url: "turn:192.168.6.3:3478",
//                         username: "remote"
//                     }],
//     role: "PUSLISHER",
//     wsUri: "wss://192.168.6.3:8073/remote/websocket",
// }
//"str_CAM_OTmN_con_YJAFBzGmP3"

let streamCount = 0

let subscriber
session.on('streamCreated', event => {
  console.log('event streamCreated::', event)
  if (event.stream.videoActive == true) {
    // document.querySelector('#videos').srcObject = event.stream;
    // document.querySelector('#videos').play()

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
  console.log('streamCount::', streamCount)

  streamCount--
  layoutSelector(streamCount)
})

console.log(TOKEN)
console.log(metaData)
session
  .connect(TOKEN, metaData, options)
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
