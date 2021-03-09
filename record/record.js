const url = new URL(window.location.href)
const token = url.searchParams.get('token')
let metaData = url.searchParams.get('metaData')
let options = url.searchParams.get('options')

var OV = new OpenVidu()
OV.enableProdMode()

//나갔을때는 제거해주셈
//key = 커넥션 id
//value는 panoViewer 객체
const panoViewerMap = new Map()

var session = OV.initSession()

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

session.on('signal:linkflow', event => {
  const connectionId = event.from.connectionId
  console.log('signal:linkflow')
  console.log(connectionId)

  let data = JSON.parse(event.data)
  if (data.type === 'streamMode') {
    // const targetVideoEl = document.querySelector(
    //   `[id*='${event.stream.connection.connectionId}']`,
    // )
    // console.log('targetVideoEl::', targetVideoEl)
    const targetVideoEl = document.querySelector(`[id*='${connectionId}']`)
    console.log('targetVideoEl::', targetVideoEl)
    const newDiv = document.createElement('div')
    //근데..video tag
    //div도 display none으로 만들어서 준비해놓고 해야할듯..
  } else if (data.type === 'rotation') {
    //회전 정보 처리
    //커넥션 id를 파노 뷰어 맵에서 찾아서 해당 파노 뷰어를 컨트롤 합니다.
  }
  //이벤트를 받아서 처리해야하는것

  //1. 대상 커넥션 id를 찾아 video tag를 찾아 pano viewer 적용
  //2. 대상 커넥션 id를 찾아 pano viewer 컨트롤

  //파노 뷰어 목록..?

  // console.log(params)
  // panoViewer.lookAt({
  //   yaw: params.panoSync.yaw,
  //   pitch: params.panoSync.pitch,
  //   fov: params.panoSync.fov,
  // })
})
