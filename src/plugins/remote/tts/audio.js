//safari에서는 audio play()를 유저 인터렉션 없이 호출할 수 없다.
//유저 인터렉션으로 한번 play 했던 HTMLMediaElement에 대해서는 이 다음에 play()호출이 가능하다
//WorkspaceLayout에서 필수적으로 일어나는 인터렉션을 통해 미리 play권한을 얻어논 후 해당 audio로 tts를 play할때 사용하도록 한다.

export let audio

//touch/mousedown시 실행될 이벤트 콜백
const loadTtsAudio = () => {
  audio.play()
  audio.pause()
  window.removeEventListener('touchstart', loadTtsAudio)
  window.removeEventListener('mousedown', loadTtsAudio)
  audio.onloadeddata = () => {}
  audio.muted = false
}

//tts에서도 사용할 audio 객체 생성 및 무의미한 소리파일 미리 로드, touch/mousedown 시 play하도록 함
export const initAudio = () => {
  audio = new Audio()
  audio.playsinline = true
  audio.preload = 'auto'
  audio.muted = true
  audio.src = require('assets/media/end.mp3')

  //onloadeddata - 미디어의 첫번째 프레임이 로딩 완료된 시점
  audio.onloadeddata = () => {
    window.addEventListener('touchstart', loadTtsAudio)
    window.addEventListener('mousedown', loadTtsAudio)
  }
  audio.load()
}

//audio 객체 초기화
export const clearAudio = () => {
  audio.onloadeddata = () => {}
  window.removeEventListener('touchstart', loadTtsAudio)
  window.removeEventListener('mousedown', loadTtsAudio)
  audio = null
}
