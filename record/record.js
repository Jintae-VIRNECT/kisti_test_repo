const url = new URL(window.location.href)
const token = url.searchParams.get('token')
let metaData = url.searchParams.get('metaData')
let options = url.searchParams.get('options')

const OV = new OpenVidu()
OV.enableProdMode()

const session = OV.initSession()

options = JSON.parse(options)
metaData = JSON.parse(metaData)

let streamCount = 0

let subscriber
session.on('streamCreated', event => {
  console.log('event streamCreated::', event)

  const metaData = JSON.parse(event.stream.connection.data.split('%/%')[0])

  const deviceType = metaData.deviceType
  console.log('streamCreated :: deviceType::', deviceType)

  subscriber = session.subscribe(event.stream, 'videos')

  subscriber.on('videoElementCreated', event => {
    event.element.muted = false
  })

  streamCount++
  layoutSelector(streamCount)
  console.log('streamCount::', streamCount)
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
    const errorMsgContainer = document.getElementById('errorMsg')
    errorMsgContainer.innerText = `session connection fail\n${error}`
  })

/**
 * video element 갯수에 따라 레이아웃을 변경
 *
 * @param {Number} streamCount 현재 video element 갯수
 */
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
