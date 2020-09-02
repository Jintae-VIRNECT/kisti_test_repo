'use strict'

// Custom by ykmo-VIRNECT

// __________________________
// MediaStreamRecorder v1.3.4

// Open-Sourced: https://github.com/streamproc/MediaStreamRecorder

// --------------------------------------------------
// Muaz Khan     - www.MuazKhan.com
// MIT License   - www.WebRTC-Experiment.com/licence
// --------------------------------------------------

// ______________________
// MediaStreamRecorder.js

let canvas = null
let orientation = 'landscape'

function MediaStreamRecorder(mediaStream) {
  if (!mediaStream) {
    throw 'MediaStream is mandatory.'
  }

  // void start(optional long timeSlice)
  // timestamp to fire "ondataavailable"
  this.start = function(timeSlice) {
    var Recorder

    if (typeof MediaRecorder !== 'undefined') {
      Recorder = MediaRecorderWrapper
    } else {
      throw 'MediaRecorder is not support'
    }

    // audio/wav is supported only via StereoAudioRecorder
    // audio/pcm (int16) is supported only via StereoAudioRecorder
    if (this.mimeType === 'audio/wav' || this.mimeType === 'audio/pcm') {
      Recorder = StereoAudioRecorder
    }

    // allows forcing StereoAudioRecorder.js on Edge/Firefox
    if (this.recorderType) {
      Recorder = this.recorderType
    }

    mediaRecorder = new Recorder(mediaStream)
    mediaRecorder.blobs = []

    var self = this
    mediaRecorder.ondataavailable = function(data) {
      mediaRecorder.blobs.push(data)
      self.ondataavailable(data)
    }
    mediaRecorder.onstop = this.onstop
    mediaRecorder.onStartedDrawingNonBlankFrames = this.onStartedDrawingNonBlankFrames

    // Merge all data-types except "function"
    mediaRecorder = mergeProps(mediaRecorder, this)

    mediaRecorder.start(timeSlice)
  }

  this.onStartedDrawingNonBlankFrames = function() {}
  this.clearOldRecordedFrames = function() {
    if (!mediaRecorder) {
      return
    }

    mediaRecorder.clearOldRecordedFrames()
  }

  this.stop = function() {
    if (mediaRecorder) {
      mediaRecorder.stop()
    }
  }

  this.ondataavailable = function(blob) {
    if (this.disableLogs) return
    console.log('ondataavailable..', blob)
  }

  this.onstop = function(error) {
    console.warn('stopped..', error)
  }

  this.save = function(file, fileName) {
    if (!file) {
      if (!mediaRecorder) {
        return
      }

      ConcatenateBlobs(
        mediaRecorder.blobs,
        mediaRecorder.blobs[0].type,
        function(concatenatedBlob) {
          invokeSaveAsDialog(concatenatedBlob)
        },
      )
      return
    }
    invokeSaveAsDialog(file, fileName)
  }

  this.pause = function() {
    if (!mediaRecorder) {
      return
    }
    mediaRecorder.pause()

    if (this.disableLogs) return
    console.log('Paused recording.', this.mimeType || mediaRecorder.mimeType)
  }

  this.resume = function() {
    if (!mediaRecorder) {
      return
    }
    mediaRecorder.resume()

    if (this.disableLogs) return
    console.log('Resumed recording.', this.mimeType || mediaRecorder.mimeType)
  }

  // StereoAudioRecorder || MediaRecorderWrapper
  this.recorderType = null

  // video/webm or audio/webm or audio/ogg or audio/wav
  this.mimeType = 'video/webm'

  // logs are enabled by default
  this.disableLogs = false

  // Reference to "MediaRecorder.js"
  var mediaRecorder
}

// ______________________
// MultiStreamRecorder.js

function MultiStreamRecorder(arrayOfMediaStreams, options) {
  arrayOfMediaStreams = arrayOfMediaStreams || []

  if (arrayOfMediaStreams instanceof MediaStream) {
    arrayOfMediaStreams = [arrayOfMediaStreams]
  }

  var self = this

  var mixer
  var mediaRecorder

  options = options || {
    mimeType: 'video/webm',
    video: {
      width: 360,
      height: 240,
    },
  }

  if (!options.frameInterval) {
    options.frameInterval = 25
  }

  if (!options.video) {
    options.video = {}
  }

  if (!options.video.width) {
    options.video.width = 360
  }

  if (!options.video.height) {
    options.video.height = 240
  }

  this.start = function(timeSlice) {
    // github/muaz-khan/MultiStreamsMixer
    mixer = new MultiStreamsMixer(arrayOfMediaStreams)

    if (getVideoTracks().length) {
      mixer.frameInterval = options.frameInterval || 10
      mixer.width = options.video.width || 360
      mixer.height = options.video.height || 240
      mixer.startDrawingFrames()
    }

    if (typeof self.previewStream === 'function') {
      self.previewStream(mixer.getMixedStream())
    }

    // record using MediaRecorder API
    mediaRecorder = new MediaStreamRecorder(mixer.getMixedStream())

    for (var prop in self) {
      if (typeof self[prop] !== 'function') {
        mediaRecorder[prop] = self[prop]
      }
    }

    mediaRecorder.ondataavailable = function(blob) {
      self.ondataavailable(blob)
    }

    mediaRecorder.onstop = self.onstop

    mediaRecorder.start(timeSlice)
  }

  function getVideoTracks() {
    var tracks = []
    arrayOfMediaStreams.forEach(function(stream) {
      stream.getVideoTracks().forEach(function(track) {
        tracks.push(track)
      })
    })
    return tracks
  }

  this.stop = function(callback) {
    if (!mediaRecorder) {
      return
    }

    mediaRecorder.stop(function(blob) {
      callback(blob)
    })
  }

  this.pause = function() {
    if (mediaRecorder) {
      mediaRecorder.pause()
    }
  }

  this.resume = function() {
    if (mediaRecorder) {
      mediaRecorder.resume()
    }
  }

  this.clearRecordedData = function() {
    if (mediaRecorder) {
      // Ahhhh!!!! there are no clearRecordedData function in mediaRecorder!!!
      // mediaRecorder.clearRecordedData()
      mediaRecorder = null
    }

    //if you don't call this
    //then cpu usage will not drop
    if (mixer) {
      mixer.releaseStreams()
      mixer = null
    }
  }

  /**
   * for portrait stream
   * landscape - maintain width, height
   * portrait - swith width, heigth
   * @param {String} orien "landscape","portrait"
   */
  this.changeCanvasOrientation = function(orien) {
    orientation = orien
  }

  this.addStreams = this.addStream = function(streams) {
    if (!streams) {
      throw 'First parameter is required.'
    }

    if (!(streams instanceof Array)) {
      streams = [streams]
    }

    arrayOfMediaStreams.concat(streams)

    if (!mediaRecorder || !mixer) {
      return
    }

    mixer.appendStreams(streams)
  }

  this.resetVideoStreams = function(streams) {
    if (!mixer) {
      return
    }

    if (streams && !(streams instanceof Array)) {
      streams = [streams]
    }

    mixer.resetVideoStreams(streams)
  }

  this.ondataavailable = function(blob) {
    if (self.disableLogs) {
      return
    }

    console.log('ondataavailable', blob)
  }

  this.onstop = function() {}

  // for debugging
  this.name = 'MultiStreamRecorder'
  this.toString = function() {
    return this.name
  }
}

if (typeof MediaStreamRecorder !== 'undefined') {
  MediaStreamRecorder.MultiStreamRecorder = MultiStreamRecorder
}

// Last time updated: 2017-08-31 2:56:12 AM UTC

// ________________________
// MultiStreamsMixer v1.0.2

// Open-Sourced: https://github.com/muaz-khan/MultiStreamsMixer

// --------------------------------------------------
// Muaz Khan     - www.MuazKhan.com
// MIT License   - www.WebRTC-Experiment.com/licence
// --------------------------------------------------

function MultiStreamsMixer(arrayOfMediaStreams) {
  // requires: chrome://flags/#enable-experimental-web-platform-features

  var videos = []
  var isStopDrawingFrames = false

  canvas = document.createElement('canvas')

  //remove alpha channel
  var context = canvas.getContext('2d', { alpha: false })

  //so.. where is delete canvas??????
  canvas.style =
    'display:none;opacity:0;position:absolute;z-index:-1;top: -100000000;left:-1000000000; margin-top:-1000000000;margin-left:-1000000000;'
  ;(document.body || document.documentElement).appendChild(canvas)

  this.disableLogs = false
  this.frameInterval = 10

  this.width = 360
  this.height = 240

  // use gain node to prevent echo
  this.useGainNode = true

  var self = this

  // _____________________________
  // Cross-Browser-Declarations.js

  // WebAudio API representer
  var AudioContext = window.AudioContext

  if (typeof AudioContext === 'undefined') {
    if (typeof webkitAudioContext !== 'undefined') {
      /*global AudioContext:true */
      AudioContext = webkitAudioContext
    }

    if (typeof mozAudioContext !== 'undefined') {
      /*global AudioContext:true */
      AudioContext = mozAudioContext
    }
  }

  /*jshint -W079 */
  var URL = window.URL

  if (typeof URL === 'undefined' && typeof webkitURL !== 'undefined') {
    /*global URL:true */
    URL = webkitURL
  }

  if (
    typeof navigator !== 'undefined' &&
    typeof navigator.getUserMedia === 'undefined'
  ) {
    // maybe window.navigator?
    if (typeof navigator.webkitGetUserMedia !== 'undefined') {
      navigator.getUserMedia = navigator.webkitGetUserMedia
    }

    if (typeof navigator.mozGetUserMedia !== 'undefined') {
      navigator.getUserMedia = navigator.mozGetUserMedia
    }
  }

  var MediaStream = window.MediaStream

  if (
    typeof MediaStream === 'undefined' &&
    typeof webkitMediaStream !== 'undefined'
  ) {
    MediaStream = webkitMediaStream
  }

  /*global MediaStream:true */
  if (typeof MediaStream !== 'undefined') {
    if (!('getVideoTracks' in MediaStream.prototype)) {
      MediaStream.prototype.getVideoTracks = function() {
        if (!this.getTracks) {
          return []
        }

        var tracks = []
        this.getTracks.forEach(function(track) {
          if (track.kind.toString().indexOf('video') !== -1) {
            tracks.push(track)
          }
        })
        return tracks
      }

      MediaStream.prototype.getAudioTracks = function() {
        if (!this.getTracks) {
          return []
        }

        var tracks = []
        this.getTracks.forEach(function(track) {
          if (track.kind.toString().indexOf('audio') !== -1) {
            tracks.push(track)
          }
        })
        return tracks
      }
    }

    // override "stop" method for all browsers
    if (typeof MediaStream.prototype.stop === 'undefined') {
      MediaStream.prototype.stop = function() {
        this.getTracks().forEach(function(track) {
          track.stop()
        })
      }
    }
  }

  var Storage = {}

  if (typeof AudioContext !== 'undefined') {
    Storage.AudioContext = AudioContext
  } else if (typeof webkitAudioContext !== 'undefined') {
    Storage.AudioContext = webkitAudioContext
  }

  this.startDrawingFrames = function() {
    drawVideosToCanvas()
  }

  function drawVideosToCanvas() {
    if (isStopDrawingFrames) {
      return
    }

    var videosLength = videos.length

    var fullcanvas = false
    var remaining = []
    videos.forEach(function(video) {
      if (!video.stream) {
        video.stream = {}
      }

      if (video.stream.fullcanvas) {
        fullcanvas = video
      } else {
        remaining.push(video)
      }
    })

    if (orientation === 'landscape' || orientation === undefined) {
      if (fullcanvas) {
        canvas.width = fullcanvas.stream.width
        canvas.height = fullcanvas.stream.height
      } else if (remaining.length) {
        canvas.width =
          videosLength > 1 ? remaining[0].width * 2 : remaining[0].width
        canvas.height =
          videosLength > 2 ? remaining[0].height * 2 : remaining[0].height
      } else {
        canvas.width = self.width || 360
        canvas.height = self.height || 240
      }
    } else {
      if (fullcanvas) {
        canvas.width = fullcanvas.stream.height
        canvas.height = fullcanvas.stream.width
      } else if (remaining.length) {
        canvas.width =
          videosLength > 2 ? remaining[0].height * 2 : remaining[0].height
        canvas.height =
          videosLength > 1 ? remaining[0].width * 2 : remaining[0].width
      } else {
        canvas.width = self.height || 240
        canvas.height = self.width || 360
      }
    }

    if (fullcanvas && fullcanvas instanceof HTMLVideoElement) {
      drawImage(fullcanvas)
    }

    remaining.forEach(function(video, idx) {
      drawImage(video, idx)
    })

    setTimeout(drawVideosToCanvas, self.frameInterval)
  }

  function drawImage(video, idx) {
    if (isStopDrawingFrames) {
      return
    }

    let x = 0
    let y = 0
    let width = null
    let height = null

    if (orientation === 'landscape' || orientation === undefined) {
      width = video.width
      height = video.height
    } else {
      width = video.height
      height = video.width
    }

    if (idx === 1) {
      x = video.width
    }

    if (idx === 2) {
      y = video.height
    }

    if (idx === 3) {
      x = video.width
      y = video.height
    }

    if (typeof video.stream.left !== 'undefined') {
      x = video.stream.left
    }

    if (typeof video.stream.top !== 'undefined') {
      y = video.stream.top
    }

    if (typeof video.stream.width !== 'undefined') {
      width = video.stream.width
    }

    if (typeof video.stream.height !== 'undefined') {
      height = video.stream.height
    }

    context.drawImage(video, x, y, width, height)

    if (typeof video.stream.onRender === 'function') {
      video.stream.onRender(context, x, y, width, height, idx)
    }
  }

  function getMixedStream() {
    isStopDrawingFrames = false
    var mixedVideoStream = getMixedVideoStream()

    var mixedAudioStream = getMixedAudioStream()
    if (mixedAudioStream) {
      mixedAudioStream.getAudioTracks().forEach(function(track) {
        mixedVideoStream.addTrack(track)
      })
    }

    var fullcanvas
    arrayOfMediaStreams.forEach(function(stream) {
      if (stream.fullcanvas) {
        fullcanvas = true
      }
    })

    return mixedVideoStream
  }

  function getMixedVideoStream() {
    resetVideoStreams()

    var capturedStream

    if ('captureStream' in canvas) {
      capturedStream = canvas.captureStream()
    } else if ('mozCaptureStream' in canvas) {
      capturedStream = canvas.mozCaptureStream()
    } else if (!self.disableLogs) {
      console.error(
        'Upgrade to latest Chrome or otherwise enable this flag: chrome://flags/#enable-experimental-web-platform-features',
      )
    }

    var videoStream = new MediaStream()

    capturedStream.getVideoTracks().forEach(function(track) {
      videoStream.addTrack(track)
    })

    canvas.stream = videoStream

    return videoStream
  }

  function getMixedAudioStream() {
    // via: @pehrsons
    if (!Storage.AudioContextConstructor) {
      Storage.AudioContextConstructor = new Storage.AudioContext()
    }

    self.audioContext = Storage.AudioContextConstructor

    self.audioSources = []

    if (self.useGainNode === true) {
      self.gainNode = self.audioContext.createGain()
      self.gainNode.connect(self.audioContext.destination)
      self.gainNode.gain.value = 0 // don't hear self
    }

    var audioTracksLength = 0
    arrayOfMediaStreams.forEach(function(stream) {
      if (!stream.getAudioTracks().length) {
        return
      }

      audioTracksLength++

      var audioSource = self.audioContext.createMediaStreamSource(stream)

      if (self.useGainNode === true) {
        audioSource.connect(self.gainNode)
      }

      self.audioSources.push(audioSource)
    })

    if (!audioTracksLength) {
      return
    }

    self.audioDestination = self.audioContext.createMediaStreamDestination()
    self.audioSources.forEach(function(audioSource) {
      audioSource.connect(self.audioDestination)
    })
    return self.audioDestination.stream
  }

  function getVideo(stream) {
    var video = document.createElement('video')

    if ('srcObject' in video) {
      video.srcObject = stream
    } else {
      video.src = URL.createObjectURL(stream)
    }

    video.muted = true
    video.volume = 0

    video.width = stream.width || self.width || 360
    video.height = stream.height || self.height || 240

    video.play()

    return video
  }

  this.appendStreams = function(streams) {
    if (!streams) {
      throw 'First parameter is required.'
    }

    if (!(streams instanceof Array)) {
      streams = [streams]
    }

    arrayOfMediaStreams.concat(streams)

    streams.forEach(function(stream) {
      if (stream.getVideoTracks().length) {
        var video = getVideo(stream)
        video.stream = stream
        videos.push(video)
      }

      if (stream.getAudioTracks().length && self.audioContext) {
        var audioSource = self.audioContext.createMediaStreamSource(stream)
        audioSource.connect(self.audioDestination)
        self.audioSources.push(audioSource)
      }
    })
  }

  this.releaseStreams = function() {
    videos = []
    isStopDrawingFrames = true

    if (self.gainNode) {
      self.gainNode.disconnect()
      self.gainNode = null
    }

    if (self.audioSources.length) {
      self.audioSources.forEach(function(source) {
        source.disconnect()
      })
      self.audioSources = []
    }

    if (self.audioDestination) {
      self.audioDestination.disconnect()
      self.audioDestination = null
    }

    self.audioContext = null

    context.clearRect(0, 0, canvas.width, canvas.height)

    if (canvas.stream) {
      canvas.stream.stop()
      canvas.stream = null
    }
  }

  this.resetVideoStreams = function(streams) {
    if (streams && !(streams instanceof Array)) {
      streams = [streams]
    }

    resetVideoStreams(streams)
  }

  function resetVideoStreams(streams) {
    videos = []
    streams = streams || arrayOfMediaStreams

    // via: @adrian-ber
    streams.forEach(function(stream) {
      if (!stream.getVideoTracks().length) {
        return
      }

      var video = getVideo(stream)
      video.stream = stream
      videos.push(video)
    })
  }

  // for debugging
  this.name = 'MultiStreamsMixer'
  this.toString = function() {
    return this.name
  }

  this.getMixedStream = getMixedStream
}

// _____________________________
// Cross-Browser-Declarations.js

var browserFakeUserAgent =
  'Fake/5.0 (FakeOS) AppleWebKit/123 (KHTML, like Gecko) Fake/12.3.4567.89 Fake/123.45'

;(function(that) {
  if (typeof window !== 'undefined') {
    return
  }

  if (typeof window === 'undefined' && typeof global !== 'undefined') {
    global.navigator = {
      userAgent: browserFakeUserAgent,
      getUserMedia: function() {},
    }

    /*global window:true */
    that.window = global
  } else if (typeof window === 'undefined') {
    // window = this;
  }

  if (typeof document === 'undefined') {
    /*global document:true */
    that.document = {}

    document.createElement = document.captureStream = document.mozCaptureStream = function() {
      return {}
    }
  }

  if (typeof location === 'undefined') {
    /*global location:true */
    that.location = {
      protocol: 'file:',
      href: '',
      hash: '',
    }
  }

  if (typeof screen === 'undefined') {
    /*global screen:true */
    that.screen = {
      width: 0,
      height: 0,
    }
  }
})(typeof global !== 'undefined' ? global : window)

// WebAudio API representer
var AudioContext = window.AudioContext

if (typeof AudioContext === 'undefined') {
  if (typeof webkitAudioContext !== 'undefined') {
    /*global AudioContext:true */
    AudioContext = webkitAudioContext
  }

  if (typeof mozAudioContext !== 'undefined') {
    /*global AudioContext:true */
    AudioContext = mozAudioContext
  }
}

if (typeof window === 'undefined') {
  /*jshint -W020 */
  window = {}
}

// WebAudio API representer
var AudioContext = window.AudioContext

if (typeof AudioContext === 'undefined') {
  if (typeof webkitAudioContext !== 'undefined') {
    /*global AudioContext:true */
    AudioContext = webkitAudioContext
  }

  if (typeof mozAudioContext !== 'undefined') {
    /*global AudioContext:true */
    AudioContext = mozAudioContext
  }
}

/*jshint -W079 */
var URL = window.URL

if (typeof URL === 'undefined' && typeof webkitURL !== 'undefined') {
  /*global URL:true */
  URL = webkitURL
}

if (typeof navigator !== 'undefined') {
  if (typeof navigator.webkitGetUserMedia !== 'undefined') {
    navigator.getUserMedia = navigator.webkitGetUserMedia
  }

  if (typeof navigator.mozGetUserMedia !== 'undefined') {
    navigator.getUserMedia = navigator.mozGetUserMedia
  }
} else {
  navigator = {
    getUserMedia: function() {},
    userAgent: browserFakeUserAgent,
  }
}

var IsEdge =
  navigator.userAgent.indexOf('Edge') !== -1 &&
  (!!navigator.msSaveBlob || !!navigator.msSaveOrOpenBlob)

var IsOpera = false
if (
  typeof opera !== 'undefined' &&
  navigator.userAgent &&
  navigator.userAgent.indexOf('OPR/') !== -1
) {
  IsOpera = true
}
var IsChrome = !IsEdge && !IsEdge && !!navigator.webkitGetUserMedia

var MediaStream = window.MediaStream

if (
  typeof MediaStream === 'undefined' &&
  typeof webkitMediaStream !== 'undefined'
) {
  MediaStream = webkitMediaStream
}

/*global MediaStream:true */
if (typeof MediaStream !== 'undefined') {
  if (!('getVideoTracks' in MediaStream.prototype)) {
    MediaStream.prototype.getVideoTracks = function() {
      if (!this.getTracks) {
        return []
      }

      var tracks = []
      this.getTracks.forEach(function(track) {
        if (track.kind.toString().indexOf('video') !== -1) {
          tracks.push(track)
        }
      })
      return tracks
    }

    MediaStream.prototype.getAudioTracks = function() {
      if (!this.getTracks) {
        return []
      }

      var tracks = []
      this.getTracks.forEach(function(track) {
        if (track.kind.toString().indexOf('audio') !== -1) {
          tracks.push(track)
        }
      })
      return tracks
    }
  }

  if (!('stop' in MediaStream.prototype)) {
    MediaStream.prototype.stop = function() {
      this.getAudioTracks().forEach(function(track) {
        if (track.stop) {
          track.stop()
        }
      })

      this.getVideoTracks().forEach(function(track) {
        if (track.stop) {
          track.stop()
        }
      })
    }
  }
}

if (typeof location !== 'undefined') {
  if (location.href.indexOf('file:') === 0) {
    console.error('Please load this HTML file on HTTP or HTTPS.')
  }
}

// Merge all other data-types except "function"

function mergeProps(mergein, mergeto) {
  for (var t in mergeto) {
    if (typeof mergeto[t] !== 'function') {
      mergein[t] = mergeto[t]
    }
  }
  return mergein
}

/**
 * @param {Blob} file - File or Blob object. This parameter is required.
 * @param {string} fileName - Optional file name e.g. "Recorded-Video.webm"
 * @example
 * invokeSaveAsDialog(blob or file, [optional] fileName);
 * @see {@link https://github.com/muaz-khan/RecordRTC|RecordRTC Source Code}
 */
function invokeSaveAsDialog(file, fileName) {
  if (!file) {
    throw 'Blob object is required.'
  }

  if (!file.type) {
    try {
      file.type = 'video/webm'
    } catch (e) {}
  }

  var fileExtension = (file.type || 'video/webm').split('/')[1]

  if (fileName && fileName.indexOf('.') !== -1) {
    var splitted = fileName.split('.')
    fileName = splitted[0]
    fileExtension = splitted[1]
  }

  var fileFullName =
    (fileName || Math.round(Math.random() * 9999999999) + 888888888) +
    '.' +
    fileExtension

  if (typeof navigator.msSaveOrOpenBlob !== 'undefined') {
    return navigator.msSaveOrOpenBlob(file, fileFullName)
  } else if (typeof navigator.msSaveBlob !== 'undefined') {
    return navigator.msSaveBlob(file, fileFullName)
  }

  var hyperlink = document.createElement('a')
  hyperlink.href = URL.createObjectURL(file)
  hyperlink.target = '_blank'
  hyperlink.download = fileFullName

  if (navigator.mozGetUserMedia) {
    hyperlink.onclick = function() {
      ;(document.body || document.documentElement).removeChild(hyperlink)
    }
    ;(document.body || document.documentElement).appendChild(hyperlink)
  }

  var evt = new MouseEvent('click', {
    view: window,
    bubbles: true,
    cancelable: true,
  })

  hyperlink.dispatchEvent(evt)

  if (!navigator.mozGetUserMedia) {
    URL.revokeObjectURL(hyperlink.href)
  }
}

function bytesToSize(bytes) {
  var k = 1000
  var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  if (bytes === 0) {
    return '0 Bytes'
  }
  var i = parseInt(Math.floor(Math.log(bytes) / Math.log(k)), 10)
  return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i]
}

// ______________ (used to handle stuff like http://goo.gl/xmE5eg) issue #129
// ObjectStore.js
var ObjectStore = {
  AudioContext: AudioContext,
}

function isMediaRecorderCompatible() {
  var isOpera = !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0
  var isChrome = !!window.chrome && !isOpera
  var isFirefox = typeof window.InstallTrigger !== 'undefined'

  if (isFirefox) {
    return true
  }

  if (!isChrome) {
    return false
  }

  var nVer = navigator.appVersion
  var nAgt = navigator.userAgent
  var fullVersion = '' + parseFloat(navigator.appVersion)
  var majorVersion = parseInt(navigator.appVersion, 10)
  var nameOffset, verOffset, ix

  if (isChrome) {
    verOffset = nAgt.indexOf('Chrome')
    fullVersion = nAgt.substring(verOffset + 7)
  }

  // trim the fullVersion string at semicolon/space if present
  if ((ix = fullVersion.indexOf(';')) !== -1) {
    fullVersion = fullVersion.substring(0, ix)
  }

  if ((ix = fullVersion.indexOf(' ')) !== -1) {
    fullVersion = fullVersion.substring(0, ix)
  }

  majorVersion = parseInt('' + fullVersion, 10)

  if (isNaN(majorVersion)) {
    fullVersion = '' + parseFloat(navigator.appVersion)
    majorVersion = parseInt(navigator.appVersion, 10)
  }

  return majorVersion >= 49
}

// ==================
// MediaRecorder.js

/**
 * Implementation of https://dvcs.w3.org/hg/dap/raw-file/default/media-stream-capture/MediaRecorder.html
 * The MediaRecorder accepts a mediaStream as input source passed from UA. When recorder starts,
 * a MediaEncoder will be created and accept the mediaStream as input source.
 * Encoder will get the raw data by track data changes, encode it by selected MIME Type, then store the encoded in EncodedBufferCache object.
 * The encoded data will be extracted on every timeslice passed from Start function call or by RequestData function.
 * Thread model:
 * When the recorder starts, it creates a "Media Encoder" thread to read data from MediaEncoder object and store buffer in EncodedBufferCache object.
 * Also extract the encoded data and create blobs on every timeslice passed from start function or RequestData function called by UA.
 */

function MediaRecorderWrapper(mediaStream) {
  var self = this

  /**
   * This method records MediaStream.
   * @method
   * @memberof MediaStreamRecorder
   * @example
   * recorder.start(5000);
   */
  this.start = function(timeSlice, __disableLogs) {
    this.timeSlice = timeSlice || 5000

    if (!self.mimeType) {
      self.mimeType = 'video/webm'
    }

    if (self.mimeType.indexOf('audio') !== -1) {
      if (
        mediaStream.getVideoTracks().length &&
        mediaStream.getAudioTracks().length
      ) {
        var stream
        if (navigator.mozGetUserMedia) {
          stream = new MediaStream()
          stream.addTrack(mediaStream.getAudioTracks()[0])
        } else {
          // webkitMediaStream
          stream = new MediaStream(mediaStream.getAudioTracks())
        }
        mediaStream = stream
      }
    }

    if (self.mimeType.indexOf('audio') !== -1) {
      self.mimeType = IsChrome ? 'audio/webm' : 'audio/ogg'
    }

    self.dontFireOnDataAvailableEvent = false

    var recorderHints = {
      mimeType: self.mimeType,
    }

    //add bitrate option
    if (self.videoBitsPerSecond) {
      recorderHints.videoBitsPerSecond = self.videoBitsPerSecond
    }

    if (self.audioBitsPerSecond) {
      recorderHints.audioBitsPerSecond = self.audioBitsPerSecond
    }

    if (!self.disableLogs && !__disableLogs) {
      console.log(
        'Passing following params over MediaRecorder API.',
        recorderHints,
      )
    }

    if (mediaRecorder) {
      // mandatory to make sure Firefox doesn't fails to record streams 3-4 times without reloading the page.
      mediaRecorder = null
    }

    if (IsChrome && !isMediaRecorderCompatible()) {
      // to support video-only recording on stable
      recorderHints = 'video/vp8'
    }

    // http://dxr.mozilla.org/mozilla-central/source/content/media/MediaRecorder.cpp
    // https://wiki.mozilla.org/Gecko:MediaRecorder
    // https://dvcs.w3.org/hg/dap/raw-file/default/media-stream-capture/MediaRecorder.html

    // starting a recording session; which will initiate "Reading Thread"
    // "Reading Thread" are used to prevent main-thread blocking scenarios
    try {
      mediaRecorder = new MediaRecorder(mediaStream, recorderHints)
    } catch (e) {
      // if someone passed NON_supported mimeType
      // or if Firefox on Android
      mediaRecorder = new MediaRecorder(mediaStream)
    }

    if (
      'canRecordMimeType' in mediaRecorder &&
      mediaRecorder.canRecordMimeType(self.mimeType) === false
    ) {
      if (!self.disableLogs) {
        console.warn(
          'MediaRecorder API seems unable to record mimeType:',
          self.mimeType,
        )
      }
    }

    // i.e. stop recording when <video> is paused by the user; and auto restart recording
    // when video is resumed. E.g. yourStream.getVideoTracks()[0].muted = true; // it will auto-stop recording.
    if (self.ignoreMutedMedia === true) {
      mediaRecorder.ignoreMutedMedia = true
    }

    var firedOnDataAvailableOnce = false

    // Dispatching OnDataAvailable Handler
    mediaRecorder.ondataavailable = function(e) {
      // how to fix FF-corrupt-webm issues?
      // should we leave this?          e.data.size < 26800
      if (
        !e.data ||
        !e.data.size ||
        e.data.size < 26800 ||
        firedOnDataAvailableOnce
      ) {
        return
      }

      firedOnDataAvailableOnce = true

      var blob = self.getNativeBlob
        ? e.data
        : new Blob([e.data], {
            type: self.mimeType || 'video/webm',
          })

      self.ondataavailable(blob)

      // self.dontFireOnDataAvailableEvent = true;

      if (!!mediaRecorder && mediaRecorder.state === 'recording') {
        mediaRecorder.stop()
      }
      mediaRecorder = null

      if (self.dontFireOnDataAvailableEvent) {
        return
      }

      // record next interval
      self.start(timeSlice, '__disableLogs')
    }

    mediaRecorder.onerror = function(error) {
      if (!self.disableLogs) {
        if (error.name === 'InvalidState') {
          console.error(
            'The MediaRecorder is not in a state in which the proposed operation is allowed to be executed.',
          )
        } else if (error.name === 'OutOfMemory') {
          console.error(
            'The UA has exhaused the available memory. User agents SHOULD provide as much additional information as possible in the message attribute.',
          )
        } else if (error.name === 'IllegalStreamModification') {
          console.error(
            'A modification to the stream has occurred that makes it impossible to continue recording. An example would be the addition of a Track while recording is occurring. User agents SHOULD provide as much additional information as possible in the message attribute.',
          )
        } else if (error.name === 'OtherRecordingError') {
          console.error(
            'Used for an fatal error other than those listed above. User agents SHOULD provide as much additional information as possible in the message attribute.',
          )
        } else if (error.name === 'GenericError') {
          console.error(
            'The UA cannot provide the codec or recording option that has been requested.',
            error,
          )
        } else {
          console.error('MediaRecorder Error', error)
        }
      }

      // When the stream is "ended" set recording to 'inactive'
      // and stop gathering data. Callers should not rely on
      // exactness of the timeSlice value, especially
      // if the timeSlice value is small. Callers should
      // consider timeSlice as a minimum value

      if (
        !!mediaRecorder &&
        mediaRecorder.state !== 'inactive' &&
        mediaRecorder.state !== 'stopped'
      ) {
        mediaRecorder.stop()
      }
    }

    // void start(optional long mTimeSlice)
    // The interval of passing encoded data from EncodedBufferCache to onDataAvailable
    // handler. "mTimeSlice < 0" means Session object does not push encoded data to
    // onDataAvailable, instead, it passive wait the client side pull encoded data
    // by calling requestData API.
    try {
      mediaRecorder.start(3.6e6)
    } catch (e) {
      mediaRecorder = null
    }

    setTimeout(function() {
      if (!mediaRecorder) {
        return
      }

      if (mediaRecorder.state === 'recording') {
        // "stop" method auto invokes "requestData"!
        mediaRecorder.requestData()
        // mediaRecorder.stop();
      }
    }, timeSlice)

    // Start recording. If timeSlice has been provided, mediaRecorder will
    // raise a dataavailable event containing the Blob of collected data on every timeSlice milliseconds.
    // If timeSlice isn't provided, UA should call the RequestData to obtain the Blob data, also set the mTimeSlice to zero.
  }

  /**
   * This method stops recording MediaStream.
   * @param {function} callback - Callback function, that is used to pass recorded blob back to the callee.
   * @method
   * @memberof MediaStreamRecorder
   * @example
   * recorder.stop(function(blob) {
   *     video.src = URL.createObjectURL(blob);
   * });
   */
  this.stop = function(callback) {
    if (!mediaRecorder) {
      return
    }

    // mediaRecorder.state === 'recording' means that media recorder is associated with "session"
    // mediaRecorder.state === 'stopped' means that media recorder is detached from the "session" ... in this case; "session" will also be deleted.

    if (mediaRecorder.state === 'recording') {
      // "stop" method auto invokes "requestData"!
      mediaRecorder.requestData()

      setTimeout(function() {
        self.dontFireOnDataAvailableEvent = true
        if (!!mediaRecorder && mediaRecorder.state === 'recording') {
          mediaRecorder.stop()
        }
        mediaRecorder = null
        self.onstop()
      }, 2000)
    }
  }

  /**
   * This method pauses the recording process.
   * @method
   * @memberof MediaStreamRecorder
   * @example
   * recorder.pause();
   */
  this.pause = function() {
    if (!mediaRecorder) {
      return
    }

    if (mediaRecorder.state === 'recording') {
      mediaRecorder.pause()
    }

    this.dontFireOnDataAvailableEvent = true
  }

  /**
   * The recorded blobs are passed over this event.
   * @event
   * @memberof MediaStreamRecorder
   * @example
   * recorder.ondataavailable = function(data) {};
   */
  this.ondataavailable = function(blob) {
    console.log('recorded-blob', blob)
  }

  /**
   * This method resumes the recording process.
   * @method
   * @memberof MediaStreamRecorder
   * @example
   * recorder.resume();
   */
  this.resume = function() {
    if (this.dontFireOnDataAvailableEvent) {
      this.dontFireOnDataAvailableEvent = false

      var disableLogs = self.disableLogs
      self.disableLogs = true
      this.start(this.timeslice || 5000)
      self.disableLogs = disableLogs
      return
    }

    if (!mediaRecorder) {
      return
    }

    if (mediaRecorder.state === 'paused') {
      mediaRecorder.resume()
    }
  }

  /**
   * This method resets currently recorded data.
   * @method
   * @memberof MediaStreamRecorder
   * @example
   * recorder.clearRecordedData();
   */
  this.clearRecordedData = function() {
    if (!mediaRecorder) {
      return
    }

    this.pause()

    this.dontFireOnDataAvailableEvent = true
    this.stop()
  }

  this.onstop = function() {}

  // Reference to "MediaRecorder" object
  var mediaRecorder

  function isMediaStreamActive() {
    if ('active' in mediaStream) {
      if (!mediaStream.active) {
        return false
      }
    } else if ('ended' in mediaStream) {
      // old hack
      if (mediaStream.ended) {
        return false
      }
    }
    return true
  }

  // this method checks if media stream is stopped
  // or any track is ended.
  ;(function looper() {
    if (!mediaRecorder) {
      return
    }

    if (isMediaStreamActive() === false) {
      self.stop()
      return
    }

    setTimeout(looper, 1000) // check every second
  })()
}

if (typeof MediaStreamRecorder !== 'undefined') {
  MediaStreamRecorder.MediaRecorderWrapper = MediaRecorderWrapper
}

// ======================
// StereoAudioRecorder.js

function StereoAudioRecorder(mediaStream) {
  // void start(optional long timeSlice)
  // timestamp to fire "ondataavailable"
  this.start = function(timeSlice) {
    timeSlice = timeSlice || 1000

    mediaRecorder = new StereoAudioRecorderHelper(mediaStream, this)

    mediaRecorder.record()

    timeout = setInterval(function() {
      mediaRecorder.requestData()
    }, timeSlice)
  }

  this.stop = function() {
    if (mediaRecorder) {
      mediaRecorder.stop()
      clearTimeout(timeout)
      this.onstop()
    }
  }

  this.pause = function() {
    if (!mediaRecorder) {
      return
    }

    mediaRecorder.pause()
  }

  this.resume = function() {
    if (!mediaRecorder) {
      return
    }

    mediaRecorder.resume()
  }

  this.ondataavailable = function() {}
  this.onstop = function() {}

  // Reference to "StereoAudioRecorder" object
  var mediaRecorder
  var timeout
}

if (typeof MediaStreamRecorder !== 'undefined') {
  MediaStreamRecorder.StereoAudioRecorder = StereoAudioRecorder
}

// ============================
// StereoAudioRecorderHelper.js

// source code from: http://typedarray.org/wp-content/projects/WebAudioRecorder/script.js

function StereoAudioRecorderHelper(mediaStream, root) {
  // variables
  var deviceSampleRate = 44100 // range: 22050 to 96000

  if (!ObjectStore.AudioContextConstructor) {
    ObjectStore.AudioContextConstructor = new ObjectStore.AudioContext()
  }

  // check device sample rate
  deviceSampleRate = ObjectStore.AudioContextConstructor.sampleRate

  var leftchannel = []
  var rightchannel = []
  var scriptprocessornode
  var recording = false
  var recordingLength = 0
  var volume
  var audioInput
  var sampleRate = root.sampleRate || deviceSampleRate

  var mimeType = root.mimeType || 'audio/wav'
  var isPCM = mimeType.indexOf('audio/pcm') > -1

  var context

  var numChannels = root.audioChannels || 2

  this.record = function() {
    recording = true
    // reset the buffers for the new recording
    leftchannel.length = rightchannel.length = 0
    recordingLength = 0
  }

  this.requestData = function() {
    if (isPaused) {
      return
    }

    if (recordingLength === 0) {
      requestDataInvoked = false
      return
    }

    requestDataInvoked = true
    // clone stuff
    var internalLeftChannel = leftchannel.slice(0)
    var internalRightChannel = rightchannel.slice(0)
    var internalRecordingLength = recordingLength

    // reset the buffers for the new recording
    leftchannel.length = rightchannel.length = []
    recordingLength = 0
    requestDataInvoked = false

    // we flat the left and right channels down
    var leftBuffer = mergeBuffers(internalLeftChannel, internalRecordingLength)

    var interleaved = leftBuffer

    // we interleave both channels together
    if (numChannels === 2) {
      var rightBuffer = mergeBuffers(
        internalRightChannel,
        internalRecordingLength,
      ) // bug fixed via #70,#71
      interleaved = interleave(leftBuffer, rightBuffer)
    }

    if (isPCM) {
      // our final binary blob
      var blob = new Blob([convertoFloat32ToInt16(interleaved)], {
        type: 'audio/pcm',
      })

      console.debug('audio recorded blob size:', bytesToSize(blob.size))
      root.ondataavailable(blob)
      return
    }

    // we create our wav file
    var buffer = new ArrayBuffer(44 + interleaved.length * 2)
    var view = new DataView(buffer)

    // RIFF chunk descriptor
    writeUTFBytes(view, 0, 'RIFF')

    // -8 (via #97)
    view.setUint32(4, 44 + interleaved.length * 2 - 8, true)

    writeUTFBytes(view, 8, 'WAVE')
    // FMT sub-chunk
    writeUTFBytes(view, 12, 'fmt ')
    view.setUint32(16, 16, true)
    view.setUint16(20, 1, true)
    // stereo (2 channels)
    view.setUint16(22, numChannels, true)
    view.setUint32(24, sampleRate, true)
    view.setUint32(28, sampleRate * numChannels * 2, true) // numChannels * 2 (via #71)
    view.setUint16(32, numChannels * 2, true)
    view.setUint16(34, 16, true)
    // data sub-chunk
    writeUTFBytes(view, 36, 'data')
    view.setUint32(40, interleaved.length * 2, true)

    // write the PCM samples
    var lng = interleaved.length
    var index = 44
    var volume = 1
    for (var i = 0; i < lng; i++) {
      view.setInt16(index, interleaved[i] * (0x7fff * volume), true)
      index += 2
    }

    // our final binary blob
    var blob = new Blob([view], {
      type: 'audio/wav',
    })

    console.debug('audio recorded blob size:', bytesToSize(blob.size))

    root.ondataavailable(blob)
  }

  this.stop = function() {
    // we stop recording
    recording = false
    this.requestData()

    audioInput.disconnect()
    this.onstop()
  }

  function interleave(leftChannel, rightChannel) {
    var length = leftChannel.length + rightChannel.length
    var result = new Float32Array(length)

    var inputIndex = 0

    for (var index = 0; index < length; ) {
      result[index++] = leftChannel[inputIndex]
      result[index++] = rightChannel[inputIndex]
      inputIndex++
    }
    return result
  }

  function mergeBuffers(channelBuffer, recordingLength) {
    var result = new Float32Array(recordingLength)
    var offset = 0
    var lng = channelBuffer.length
    for (var i = 0; i < lng; i++) {
      var buffer = channelBuffer[i]
      result.set(buffer, offset)
      offset += buffer.length
    }
    return result
  }

  function writeUTFBytes(view, offset, string) {
    var lng = string.length
    for (var i = 0; i < lng; i++) {
      view.setUint8(offset + i, string.charCodeAt(i))
    }
  }

  function convertoFloat32ToInt16(buffer) {
    var l = buffer.length
    var buf = new Int16Array(l)

    while (l--) {
      buf[l] = buffer[l] * 0xffff //convert to 16 bit
    }
    return buf.buffer
  }

  // creates the audio context
  var context = ObjectStore.AudioContextConstructor

  // creates a gain node
  ObjectStore.VolumeGainNode = context.createGain()

  var volume = ObjectStore.VolumeGainNode

  // creates an audio node from the microphone incoming stream
  ObjectStore.AudioInput = context.createMediaStreamSource(mediaStream)

  // creates an audio node from the microphone incoming stream
  var audioInput = ObjectStore.AudioInput

  // connect the stream to the gain node
  audioInput.connect(volume)

  /* From the spec: This value controls how frequently the audioprocess event is
    dispatched and how many sample-frames need to be processed each call.
    Lower values for buffer size will result in a lower (better) latency.
    Higher values will be necessary to avoid audio breakup and glitches 
    Legal values are 256, 512, 1024, 2048, 4096, 8192, and 16384.*/
  var bufferSize = root.bufferSize || 2048
  if (root.bufferSize === 0) {
    bufferSize = 0
  }

  if (context.createJavaScriptNode) {
    scriptprocessornode = context.createJavaScriptNode(
      bufferSize,
      numChannels,
      numChannels,
    )
  } else if (context.createScriptProcessor) {
    scriptprocessornode = context.createScriptProcessor(
      bufferSize,
      numChannels,
      numChannels,
    )
  } else {
    throw 'WebAudio API has no support on this browser.'
  }

  bufferSize = scriptprocessornode.bufferSize

  console.debug('using audio buffer-size:', bufferSize)

  var requestDataInvoked = false

  // sometimes "scriptprocessornode" disconnects from he destination-node
  // and there is no exception thrown in this case.
  // and obviously no further "ondataavailable" events will be emitted.
  // below global-scope variable is added to debug such unexpected but "rare" cases.
  window.scriptprocessornode = scriptprocessornode

  if (numChannels === 1) {
    console.debug('All right-channels are skipped.')
  }

  var isPaused = false

  this.pause = function() {
    isPaused = true
  }

  this.resume = function() {
    isPaused = false
  }

  this.onstop = function() {}

  // http://webaudio.github.io/web-audio-api/#the-scriptprocessornode-interface
  scriptprocessornode.onaudioprocess = function(e) {
    if (!recording || requestDataInvoked || isPaused) {
      return
    }

    var left = e.inputBuffer.getChannelData(0)
    leftchannel.push(new Float32Array(left))

    if (numChannels === 2) {
      var right = e.inputBuffer.getChannelData(1)
      rightchannel.push(new Float32Array(right))
    }
    recordingLength += bufferSize
  }

  volume.connect(scriptprocessornode)
  scriptprocessornode.connect(context.destination)
}

if (typeof MediaStreamRecorder !== 'undefined') {
  MediaStreamRecorder.StereoAudioRecorderHelper = StereoAudioRecorderHelper
}

// Last time updated at Nov 18, 2014, 08:32:23

// Latest file can be found here: https://cdn.webrtc-experiment.com/ConcatenateBlobs.js

// Muaz Khan    - www.MuazKhan.com
// MIT License  - www.WebRTC-Experiment.com/licence
// Source Code  - https://github.com/muaz-khan/ConcatenateBlobs
// Demo         - https://www.WebRTC-Experiment.com/ConcatenateBlobs/

// ___________________
// ConcatenateBlobs.js

// Simply pass array of blobs.
// This javascript library will concatenate all blobs in single "Blob" object.

;(function() {
  window.ConcatenateBlobs = function(blobs, type, callback) {
    var buffers = []

    var index = 0

    function readAsArrayBuffer() {
      if (!blobs[index]) {
        return concatenateBuffers()
      }
      var reader = new FileReader()
      reader.onload = function(event) {
        buffers.push(event.target.result)
        index++
        readAsArrayBuffer()
      }
      reader.readAsArrayBuffer(blobs[index])
    }

    readAsArrayBuffer()

    function concatenateBuffers() {
      var byteLength = 0
      buffers.forEach(function(buffer) {
        byteLength += buffer.byteLength
      })

      var tmp = new Uint16Array(byteLength)
      var lastOffset = 0
      buffers.forEach(function(buffer) {
        // BYTES_PER_ELEMENT == 2 for Uint16Array
        var reusableByteLength = buffer.byteLength
        if (reusableByteLength % 2 != 0) {
          buffer = buffer.slice(0, reusableByteLength - 1)
        }
        tmp.set(new Uint16Array(buffer), lastOffset)
        lastOffset += reusableByteLength
      })

      var blob = new Blob([tmp.buffer], {
        type: type,
      })

      callback(blob)
    }
  }
})()

// https://github.com/streamproc/MediaStreamRecorder/issues/42
if (typeof module !== 'undefined' /* && !!module.exports*/) {
  module.exports = MediaStreamRecorder
}

if (typeof define === 'function' && define.amd) {
  define('MediaStreamRecorder', [], function() {
    return MediaStreamRecorder
  })
}
