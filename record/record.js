const url = new URL(window.location.href)
const token = url.searchParams.get('token')
let metaData = url.searchParams.get('metaData')
let options = url.searchParams.get('options')

const OV = new OpenVidu()
OV.enableProdMode()

//파노 뷰어 객체 저장
const panoViewerMap = new Map()

//파노 뷰어의 컨테이너 element 저장
const panoContainerMap = new Map()

const session = OV.initSession()

options = JSON.parse(options)
metaData = JSON.parse(metaData)

let streamCount = 0

let subscriber
session.on('streamCreated', event => {
  console.log('event streamCreated::', event)

  const connectionId = event.stream.connection.connectionId
  const metaData = JSON.parse(event.stream.connection.data.split('%/%')[0])

  const deviceType = metaData.deviceType
  console.log('streamCreated :: deviceType::', deviceType)

  subscriber = session.subscribe(event.stream, 'videos')

  subscriber.on('videoElementCreated', event => {
    event.element.addEventListener('loadeddata', () => {
      setTimeout(() => {
        rePositionPanoViewer()
      }, 100)
    })
  })

  if (deviceType === 'FITT360') {
    createPanoViewer(connectionId)
  }

  streamCount++
  layoutSelector(streamCount)
  console.log('streamCount::', streamCount)

  rePositionPanoViewer()
})

session.on('streamDestroyed', event => {
  const connectionId = event.stream.connection.connectionId
  console.log('event streamDestroyed::', event)
  streamCount--
  console.log('streamCount::', streamCount)
  layoutSelector(streamCount)
  if (streamCount <= 0) {
    console.log('session::disconnect')
    session.disconnect()
  }

  deletePanoViewer(connectionId)
  rePositionPanoViewer()
})

session
  .connect(token, metaData, options)
  .then(() => {
    console.log('Recorder participant connected')
  })
  .catch(error => {
    console.error(error)
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

session.on('signal:linkflow', event => {
  const connectionId = event.from.connectionId

  let data = JSON.parse(event.data)
  //panoViewer는 stream 연결후에 초기화 되어있어야 합니다.
  if (data.type === 'rotation') {
    let panoViewer
    if (data.origin) {
      panoViewer = panoViewerMap.get(data.origin)
    } else {
      panoViewer = panoViewerMap.get(connectionId)
    }

    const info = {
      yaw: data.yaw,
      pitch: data.pitch,
      fov: 85, //default
    }

    if (panoViewer) {
      panoViewer.lookAt(info)
    }
  }
})

/**
 * pano viewer 생성
 * @param {String} connectionId 커넥션 id
 */
const createPanoViewer = connectionId => {
  if (panoContainerMap.has(connectionId)) return

  const targetVideoEl = document.querySelector(`video[id*='${connectionId}']`)

  const panoContainer = document.createElement('div')

  document.body.appendChild(panoContainer)

  panoContainer.id = connectionId
  panoContainer.className = 'pano-container'
  panoContainer.style.left = targetVideoEl.offsetLeft + 'px'
  panoContainer.style.top = targetVideoEl.offsetTop + 'px'
  panoContainer.style.height = targetVideoEl.offsetHeight + 'px'
  panoContainer.style.width = targetVideoEl.offsetWidth + 'px'

  const PanoViewer = eg.view360.PanoViewer
  const panoViewer = new PanoViewer(panoContainer, {
    video: targetVideoEl,
  })

  panoViewer.setYawRange([-360, 0])

  panoContainerMap.set(connectionId, panoContainer)
  panoViewerMap.set(connectionId, panoViewer)
}

/**
 * 호출 타이밍
 * - 스트림 추가 or 삭제
 * - 새로운 스트림이 들어와서 파노뷰어를 만들기 전
 * - 이전 스트림이 나가서 파노 뷰어를 삭제하고 재배치
 */
const rePositionPanoViewer = () => {
  console.log('rePositionPanoViewer called')

  const videos = document.querySelectorAll('video')

  for (let [connectionId, panoContainer] of panoContainerMap) {
    for (let i = 0; i < videos.length; i++) {
      console.log('videos[i].className::', videos[i].className)
      console.log('connectionId::', connectionId)
      if (videos[i].id.includes(connectionId)) {
        console.log('resize called')
        panoContainer.style.left = videos[i].offsetLeft + 'px'
        panoContainer.style.top = videos[i].offsetTop + 'px'
        panoContainer.style.height = videos[i].offsetHeight + 'px'
        panoContainer.style.width = videos[i].offsetWidth + 'px'
      }
    }
  }

  for (let [connectionId, panoViewer] of panoViewerMap) {
    if (panoViewer) {
      panoViewer.updateViewportDimensions()
    }
  }
}

const deletePanoViewer = connectionId => {
  if (panoContainerMap.has(connectionId)) {
    panoContainerMap.get(connectionId).remove()
  }
  if (panoViewerMap.has(connectionId)) {
    panoViewerMap.get(connectionId).destroy()
  }
}
