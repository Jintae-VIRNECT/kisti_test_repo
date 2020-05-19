;(function() {
  function r(e, n, t) {
    function o(i, f) {
      if (!n[i]) {
        if (!e[i]) {
          var c = 'function' == typeof require && require
          if (!f && c) return c(i, !0)
          if (u) return u(i, !0)
          var a = new Error("Cannot find module '" + i + "'")
          throw ((a.code = 'MODULE_NOT_FOUND'), a)
        }
        var p = (n[i] = { exports: {} })
        e[i][0].call(
          p.exports,
          function(r) {
            var n = e[i][1][r]
            return o(n || r)
          },
          p,
          p.exports,
          r,
          e,
          n,
          t,
        )
      }
      return n[i].exports
    }
    for (
      var u = 'function' == typeof require && require, i = 0;
      i < t.length;
      i++
    )
      o(t[i])
    return o
  }
  return r
})()(
  {
    1: [
      function(require, module, exports) {
        // Copyright Joyent, Inc. and other Node contributors.
        //
        // Permission is hereby granted, free of charge, to any person obtaining a
        // copy of this software and associated documentation files (the
        // "Software"), to deal in the Software without restriction, including
        // without limitation the rights to use, copy, modify, merge, publish,
        // distribute, sublicense, and/or sell copies of the Software, and to permit
        // persons to whom the Software is furnished to do so, subject to the
        // following conditions:
        //
        // The above copyright notice and this permission notice shall be included
        // in all copies or substantial portions of the Software.
        //
        // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
        // OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
        // MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
        // NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
        // DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
        // OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
        // USE OR OTHER DEALINGS IN THE SOFTWARE.

        var objectCreate = Object.create || objectCreatePolyfill
        var objectKeys = Object.keys || objectKeysPolyfill
        var bind = Function.prototype.bind || functionBindPolyfill

        function EventEmitter() {
          if (
            !this._events ||
            !Object.prototype.hasOwnProperty.call(this, '_events')
          ) {
            this._events = objectCreate(null)
            this._eventsCount = 0
          }

          this._maxListeners = this._maxListeners || undefined
        }
        module.exports = EventEmitter

        // Backwards-compat with node 0.10.x
        EventEmitter.EventEmitter = EventEmitter

        EventEmitter.prototype._events = undefined
        EventEmitter.prototype._maxListeners = undefined

        // By default EventEmitters will print a warning if more than 10 listeners are
        // added to it. This is a useful default which helps finding memory leaks.
        var defaultMaxListeners = 10

        var hasDefineProperty
        try {
          var o = {}
          if (Object.defineProperty) Object.defineProperty(o, 'x', { value: 0 })
          hasDefineProperty = o.x === 0
        } catch (err) {
          hasDefineProperty = false
        }
        if (hasDefineProperty) {
          Object.defineProperty(EventEmitter, 'defaultMaxListeners', {
            enumerable: true,
            get: function() {
              return defaultMaxListeners
            },
            set: function(arg) {
              // check whether the input is a positive number (whose value is zero or
              // greater and not a NaN).
              if (typeof arg !== 'number' || arg < 0 || arg !== arg)
                throw new TypeError(
                  '"defaultMaxListeners" must be a positive number',
                )
              defaultMaxListeners = arg
            },
          })
        } else {
          EventEmitter.defaultMaxListeners = defaultMaxListeners
        }

        // Obviously not all Emitters should be limited to 10. This function allows
        // that to be increased. Set to zero for unlimited.
        EventEmitter.prototype.setMaxListeners = function setMaxListeners(n) {
          if (typeof n !== 'number' || n < 0 || isNaN(n))
            throw new TypeError('"n" argument must be a positive number')
          this._maxListeners = n
          return this
        }

        function $getMaxListeners(that) {
          if (that._maxListeners === undefined)
            return EventEmitter.defaultMaxListeners
          return that._maxListeners
        }

        EventEmitter.prototype.getMaxListeners = function getMaxListeners() {
          return $getMaxListeners(this)
        }

        // These standalone emit* functions are used to optimize calling of event
        // handlers for fast cases because emit() itself often has a variable number of
        // arguments and can be deoptimized because of that. These functions always have
        // the same number of arguments and thus do not get deoptimized, so the code
        // inside them can execute faster.
        function emitNone(handler, isFn, self) {
          if (isFn) handler.call(self)
          else {
            var len = handler.length
            var listeners = arrayClone(handler, len)
            for (var i = 0; i < len; ++i) listeners[i].call(self)
          }
        }
        function emitOne(handler, isFn, self, arg1) {
          if (isFn) handler.call(self, arg1)
          else {
            var len = handler.length
            var listeners = arrayClone(handler, len)
            for (var i = 0; i < len; ++i) listeners[i].call(self, arg1)
          }
        }
        function emitTwo(handler, isFn, self, arg1, arg2) {
          if (isFn) handler.call(self, arg1, arg2)
          else {
            var len = handler.length
            var listeners = arrayClone(handler, len)
            for (var i = 0; i < len; ++i) listeners[i].call(self, arg1, arg2)
          }
        }
        function emitThree(handler, isFn, self, arg1, arg2, arg3) {
          if (isFn) handler.call(self, arg1, arg2, arg3)
          else {
            var len = handler.length
            var listeners = arrayClone(handler, len)
            for (var i = 0; i < len; ++i)
              listeners[i].call(self, arg1, arg2, arg3)
          }
        }

        function emitMany(handler, isFn, self, args) {
          if (isFn) handler.apply(self, args)
          else {
            var len = handler.length
            var listeners = arrayClone(handler, len)
            for (var i = 0; i < len; ++i) listeners[i].apply(self, args)
          }
        }

        EventEmitter.prototype.emit = function emit(type) {
          var er, handler, len, args, i, events
          var doError = type === 'error'

          events = this._events
          if (events) doError = doError && events.error == null
          else if (!doError) return false

          // If there is no 'error' event listener then throw.
          if (doError) {
            if (arguments.length > 1) er = arguments[1]
            if (er instanceof Error) {
              throw er // Unhandled 'error' event
            } else {
              // At least give some kind of context to the user
              var err = new Error('Unhandled "error" event. (' + er + ')')
              err.context = er
              throw err
            }
            return false
          }

          handler = events[type]

          if (!handler) return false

          var isFn = typeof handler === 'function'
          len = arguments.length
          switch (len) {
            // fast cases
            case 1:
              emitNone(handler, isFn, this)
              break
            case 2:
              emitOne(handler, isFn, this, arguments[1])
              break
            case 3:
              emitTwo(handler, isFn, this, arguments[1], arguments[2])
              break
            case 4:
              emitThree(
                handler,
                isFn,
                this,
                arguments[1],
                arguments[2],
                arguments[3],
              )
              break
            // slower
            default:
              args = new Array(len - 1)
              for (i = 1; i < len; i++) args[i - 1] = arguments[i]
              emitMany(handler, isFn, this, args)
          }

          return true
        }

        function _addListener(target, type, listener, prepend) {
          var m
          var events
          var existing

          if (typeof listener !== 'function')
            throw new TypeError('"listener" argument must be a function')

          events = target._events
          if (!events) {
            events = target._events = objectCreate(null)
            target._eventsCount = 0
          } else {
            // To avoid recursion in the case that type === "newListener"! Before
            // adding it to the listeners, first emit "newListener".
            if (events.newListener) {
              target.emit(
                'newListener',
                type,
                listener.listener ? listener.listener : listener,
              )

              // Re-assign `events` because a newListener handler could have caused the
              // this._events to be assigned to a new object
              events = target._events
            }
            existing = events[type]
          }

          if (!existing) {
            // Optimize the case of one listener. Don't need the extra array object.
            existing = events[type] = listener
            ++target._eventsCount
          } else {
            if (typeof existing === 'function') {
              // Adding the second element, need to change to array.
              existing = events[type] = prepend
                ? [listener, existing]
                : [existing, listener]
            } else {
              // If we've already got an array, just append.
              if (prepend) {
                existing.unshift(listener)
              } else {
                existing.push(listener)
              }
            }

            // Check for listener leak
            if (!existing.warned) {
              m = $getMaxListeners(target)
              if (m && m > 0 && existing.length > m) {
                existing.warned = true
                var w = new Error(
                  'Possible EventEmitter memory leak detected. ' +
                    existing.length +
                    ' "' +
                    String(type) +
                    '" listeners ' +
                    'added. Use emitter.setMaxListeners() to ' +
                    'increase limit.',
                )
                w.name = 'MaxListenersExceededWarning'
                w.emitter = target
                w.type = type
                w.count = existing.length
                if (typeof console === 'object' && console.warn) {
                  console.warn('%s: %s', w.name, w.message)
                }
              }
            }
          }

          return target
        }

        EventEmitter.prototype.addListener = function addListener(
          type,
          listener,
        ) {
          return _addListener(this, type, listener, false)
        }

        EventEmitter.prototype.on = EventEmitter.prototype.addListener

        EventEmitter.prototype.prependListener = function prependListener(
          type,
          listener,
        ) {
          return _addListener(this, type, listener, true)
        }

        function onceWrapper() {
          if (!this.fired) {
            this.target.removeListener(this.type, this.wrapFn)
            this.fired = true
            switch (arguments.length) {
              case 0:
                return this.listener.call(this.target)
              case 1:
                return this.listener.call(this.target, arguments[0])
              case 2:
                return this.listener.call(
                  this.target,
                  arguments[0],
                  arguments[1],
                )
              case 3:
                return this.listener.call(
                  this.target,
                  arguments[0],
                  arguments[1],
                  arguments[2],
                )
              default:
                var args = new Array(arguments.length)
                for (var i = 0; i < args.length; ++i) args[i] = arguments[i]
                this.listener.apply(this.target, args)
            }
          }
        }

        function _onceWrap(target, type, listener) {
          var state = {
            fired: false,
            wrapFn: undefined,
            target: target,
            type: type,
            listener: listener,
          }
          var wrapped = bind.call(onceWrapper, state)
          wrapped.listener = listener
          state.wrapFn = wrapped
          return wrapped
        }

        EventEmitter.prototype.once = function once(type, listener) {
          if (typeof listener !== 'function')
            throw new TypeError('"listener" argument must be a function')
          this.on(type, _onceWrap(this, type, listener))
          return this
        }

        EventEmitter.prototype.prependOnceListener = function prependOnceListener(
          type,
          listener,
        ) {
          if (typeof listener !== 'function')
            throw new TypeError('"listener" argument must be a function')
          this.prependListener(type, _onceWrap(this, type, listener))
          return this
        }

        // Emits a 'removeListener' event if and only if the listener was removed.
        EventEmitter.prototype.removeListener = function removeListener(
          type,
          listener,
        ) {
          var list, events, position, i, originalListener

          if (typeof listener !== 'function')
            throw new TypeError('"listener" argument must be a function')

          events = this._events
          if (!events) return this

          list = events[type]
          if (!list) return this

          if (list === listener || list.listener === listener) {
            if (--this._eventsCount === 0) this._events = objectCreate(null)
            else {
              delete events[type]
              if (events.removeListener)
                this.emit('removeListener', type, list.listener || listener)
            }
          } else if (typeof list !== 'function') {
            position = -1

            for (i = list.length - 1; i >= 0; i--) {
              if (list[i] === listener || list[i].listener === listener) {
                originalListener = list[i].listener
                position = i
                break
              }
            }

            if (position < 0) return this

            if (position === 0) list.shift()
            else spliceOne(list, position)

            if (list.length === 1) events[type] = list[0]

            if (events.removeListener)
              this.emit('removeListener', type, originalListener || listener)
          }

          return this
        }

        EventEmitter.prototype.removeAllListeners = function removeAllListeners(
          type,
        ) {
          var listeners, events, i

          events = this._events
          if (!events) return this

          // not listening for removeListener, no need to emit
          if (!events.removeListener) {
            if (arguments.length === 0) {
              this._events = objectCreate(null)
              this._eventsCount = 0
            } else if (events[type]) {
              if (--this._eventsCount === 0) this._events = objectCreate(null)
              else delete events[type]
            }
            return this
          }

          // emit removeListener for all listeners on all events
          if (arguments.length === 0) {
            var keys = objectKeys(events)
            var key
            for (i = 0; i < keys.length; ++i) {
              key = keys[i]
              if (key === 'removeListener') continue
              this.removeAllListeners(key)
            }
            this.removeAllListeners('removeListener')
            this._events = objectCreate(null)
            this._eventsCount = 0
            return this
          }

          listeners = events[type]

          if (typeof listeners === 'function') {
            this.removeListener(type, listeners)
          } else if (listeners) {
            // LIFO order
            for (i = listeners.length - 1; i >= 0; i--) {
              this.removeListener(type, listeners[i])
            }
          }

          return this
        }

        function _listeners(target, type, unwrap) {
          var events = target._events

          if (!events) return []

          var evlistener = events[type]
          if (!evlistener) return []

          if (typeof evlistener === 'function')
            return unwrap ? [evlistener.listener || evlistener] : [evlistener]

          return unwrap
            ? unwrapListeners(evlistener)
            : arrayClone(evlistener, evlistener.length)
        }

        EventEmitter.prototype.listeners = function listeners(type) {
          return _listeners(this, type, true)
        }

        EventEmitter.prototype.rawListeners = function rawListeners(type) {
          return _listeners(this, type, false)
        }

        EventEmitter.listenerCount = function(emitter, type) {
          if (typeof emitter.listenerCount === 'function') {
            return emitter.listenerCount(type)
          } else {
            return listenerCount.call(emitter, type)
          }
        }

        EventEmitter.prototype.listenerCount = listenerCount
        function listenerCount(type) {
          var events = this._events

          if (events) {
            var evlistener = events[type]

            if (typeof evlistener === 'function') {
              return 1
            } else if (evlistener) {
              return evlistener.length
            }
          }

          return 0
        }

        EventEmitter.prototype.eventNames = function eventNames() {
          return this._eventsCount > 0 ? Reflect.ownKeys(this._events) : []
        }

        // About 1.5x faster than the two-arg version of Array#splice().
        function spliceOne(list, index) {
          for (var i = index, k = i + 1, n = list.length; k < n; i += 1, k += 1)
            list[i] = list[k]
          list.pop()
        }

        function arrayClone(arr, n) {
          var copy = new Array(n)
          for (var i = 0; i < n; ++i) copy[i] = arr[i]
          return copy
        }

        function unwrapListeners(arr) {
          var ret = new Array(arr.length)
          for (var i = 0; i < ret.length; ++i) {
            ret[i] = arr[i].listener || arr[i]
          }
          return ret
        }

        function objectCreatePolyfill(proto) {
          var F = function() {}
          F.prototype = proto
          return new F()
        }
        function objectKeysPolyfill(obj) {
          var keys = []
          for (var k in obj)
            if (Object.prototype.hasOwnProperty.call(obj, k)) {
              keys.push(k)
            }
          return k
        }
        function functionBindPolyfill(context) {
          var fn = this
          return function() {
            return fn.apply(context, arguments)
          }
        }
      },
      {},
    ],
    2: [
      function(require, module, exports) {
        /* jshint node: true */
        'use strict'

        var normalice = require('normalice')

        /**
  # freeice

  The `freeice` module is a simple way of getting random STUN or TURN server
  for your WebRTC application.  The list of servers (just STUN at this stage)
  were sourced from this [gist](https://gist.github.com/zziuni/3741933).

  ## Example Use

  The following demonstrates how you can use `freeice` with
  [rtc-quickconnect](https://github.com/rtc-io/rtc-quickconnect):

  <<< examples/quickconnect.js

  As the `freeice` module generates ice servers in a list compliant with the
  WebRTC spec you will be able to use it with raw `RTCPeerConnection`
  constructors and other WebRTC libraries.

  ## Hey, don't use my STUN/TURN server!

  If for some reason your free STUN or TURN server ends up in the
  list of servers ([stun](https://github.com/DamonOehlman/freeice/blob/master/stun.json) or
  [turn](https://github.com/DamonOehlman/freeice/blob/master/turn.json))
  that is used in this module, you can feel
  free to open an issue on this repository and those servers will be removed
  within 24 hours (or sooner).  This is the quickest and probably the most
  polite way to have something removed (and provides us some visibility
  if someone opens a pull request requesting that a server is added).

  ## Please add my server!

  If you have a server that you wish to add to the list, that's awesome! I'm
  sure I speak on behalf of a whole pile of WebRTC developers who say thanks.
  To get it into the list, feel free to either open a pull request or if you
  find that process a bit daunting then just create an issue requesting
  the addition of the server (make sure you provide all the details, and if
  you have a Terms of Service then including that in the PR/issue would be
  awesome).

  ## I know of a free server, can I add it?

  Sure, if you do your homework and make sure it is ok to use (I'm currently
  in the process of reviewing the terms of those STUN servers included from
  the original list).  If it's ok to go, then please see the previous entry
  for how to add it.

  ## Current List of Servers

  * current as at the time of last `README.md` file generation

  ### STUN

  <<< stun.json

  ### TURN

  <<< turn.json

**/

        var freeice = function(opts) {
          // if a list of servers has been provided, then use it instead of defaults
          var servers = {
            stun: (opts || {}).stun || require('./stun.json'),
            turn: (opts || {}).turn || require('./turn.json'),
          }

          var stunCount = (opts || {}).stunCount || 2
          var turnCount = (opts || {}).turnCount || 0
          var selected

          function getServers(type, count) {
            var out = []
            var input = [].concat(servers[type])
            var idx

            while (input.length && out.length < count) {
              idx = (Math.random() * input.length) | 0
              out = out.concat(input.splice(idx, 1))
            }

            return out.map(function(url) {
              //If it's a not a string, don't try to "normalice" it otherwise using type:url will screw it up
              if (typeof url !== 'string' && !(url instanceof String)) {
                return url
              } else {
                return normalice(type + ':' + url)
              }
            })
          }

          // add stun servers
          selected = [].concat(getServers('stun', stunCount))

          if (turnCount) {
            selected = selected.concat(getServers('turn', turnCount))
          }

          return selected
        }

        module.exports = freeice
      },
      { './stun.json': 3, './turn.json': 4, normalice: 7 },
    ],
    3: [
      function(require, module, exports) {
        module.exports = [
          'stun.l.google.com:19302',
          'stun1.l.google.com:19302',
          'stun2.l.google.com:19302',
          'stun3.l.google.com:19302',
          'stun4.l.google.com:19302',
          'stun.ekiga.net',
          'stun.ideasip.com',
          'stun.schlund.de',
          'stun.stunprotocol.org:3478',
          'stun.voiparound.com',
          'stun.voipbuster.com',
          'stun.voipstunt.com',
          'stun.voxgratia.org',
        ]
      },
      {},
    ],
    4: [
      function(require, module, exports) {
        module.exports = []
      },
      {},
    ],
    5: [
      function(require, module, exports) {
        var WildEmitter = require('wildemitter')

        function getMaxVolume(analyser, fftBins) {
          var maxVolume = -Infinity
          analyser.getFloatFrequencyData(fftBins)

          for (var i = 4, ii = fftBins.length; i < ii; i++) {
            if (fftBins[i] > maxVolume && fftBins[i] < 0) {
              maxVolume = fftBins[i]
            }
          }

          return maxVolume
        }

        var audioContextType
        if (typeof window !== 'undefined') {
          audioContextType = window.AudioContext || window.webkitAudioContext
        }
        // use a single audio context due to hardware limits
        var audioContext = null
        module.exports = function(stream, options) {
          var harker = new WildEmitter()

          // make it not break in non-supported browsers
          if (!audioContextType) return harker

          //Config
          var options = options || {},
            smoothing = options.smoothing || 0.1,
            interval = options.interval || 50,
            threshold = options.threshold,
            play = options.play,
            history = options.history || 10,
            running = true

          // Ensure that just a single AudioContext is internally created
          audioContext =
            options.audioContext || audioContext || new audioContextType()

          var sourceNode, fftBins, analyser

          analyser = audioContext.createAnalyser()
          analyser.fftSize = 512
          analyser.smoothingTimeConstant = smoothing
          fftBins = new Float32Array(analyser.frequencyBinCount)

          if (stream.jquery) stream = stream[0]
          if (
            stream instanceof HTMLAudioElement ||
            stream instanceof HTMLVideoElement
          ) {
            //Audio Tag
            sourceNode = audioContext.createMediaElementSource(stream)
            if (typeof play === 'undefined') play = true
            threshold = threshold || -50
          } else {
            //WebRTC Stream
            sourceNode = audioContext.createMediaStreamSource(stream)
            threshold = threshold || -50
          }

          sourceNode.connect(analyser)
          if (play) analyser.connect(audioContext.destination)

          harker.speaking = false

          harker.suspend = function() {
            return audioContext.suspend()
          }
          harker.resume = function() {
            return audioContext.resume()
          }
          Object.defineProperty(harker, 'state', {
            get: function() {
              return audioContext.state
            },
          })
          audioContext.onstatechange = function() {
            harker.emit('state_change', audioContext.state)
          }

          harker.setThreshold = function(t) {
            threshold = t
          }

          harker.setInterval = function(i) {
            interval = i
          }

          harker.stop = function() {
            running = false
            harker.emit('volume_change', -100, threshold)
            if (harker.speaking) {
              harker.speaking = false
              harker.emit('stopped_speaking')
            }
            analyser.disconnect()
            sourceNode.disconnect()
          }
          harker.speakingHistory = []
          for (var i = 0; i < history; i++) {
            harker.speakingHistory.push(0)
          }

          // Poll the analyser node to determine if speaking
          // and emit events if changed
          var looper = function() {
            setTimeout(function() {
              //check if stop has been called
              if (!running) {
                return
              }

              var currentVolume = getMaxVolume(analyser, fftBins)

              harker.emit('volume_change', currentVolume, threshold)

              var history = 0
              if (currentVolume > threshold && !harker.speaking) {
                // trigger quickly, short history
                for (
                  var i = harker.speakingHistory.length - 3;
                  i < harker.speakingHistory.length;
                  i++
                ) {
                  history += harker.speakingHistory[i]
                }
                if (history >= 2) {
                  harker.speaking = true
                  harker.emit('speaking')
                }
              } else if (currentVolume < threshold && harker.speaking) {
                for (var i = 0; i < harker.speakingHistory.length; i++) {
                  history += harker.speakingHistory[i]
                }
                if (history == 0) {
                  harker.speaking = false
                  harker.emit('stopped_speaking')
                }
              }
              harker.speakingHistory.shift()
              harker.speakingHistory.push(0 + (currentVolume > threshold))

              looper()
            }, interval)
          }
          looper()

          return harker
        }
      },
      { wildemitter: 14 },
    ],
    6: [
      function(require, module, exports) {
        if (typeof Object.create === 'function') {
          // implementation from standard node.js 'util' module
          module.exports = function inherits(ctor, superCtor) {
            if (superCtor) {
              ctor.super_ = superCtor
              ctor.prototype = Object.create(superCtor.prototype, {
                constructor: {
                  value: ctor,
                  enumerable: false,
                  writable: true,
                  configurable: true,
                },
              })
            }
          }
        } else {
          // old school shim for old browsers
          module.exports = function inherits(ctor, superCtor) {
            if (superCtor) {
              ctor.super_ = superCtor
              var TempCtor = function() {}
              TempCtor.prototype = superCtor.prototype
              ctor.prototype = new TempCtor()
              ctor.prototype.constructor = ctor
            }
          }
        }
      },
      {},
    ],
    7: [
      function(require, module, exports) {
        /**
  # normalice

  Normalize an ice server configuration object (or plain old string) into a format
  that is usable in all browsers supporting WebRTC.  Primarily this module is designed
  to help with the transition of the `url` attribute of the configuration object to
  the `urls` attribute.

  ## Example Usage

  <<< examples/simple.js

**/

        var protocols = ['stun:', 'turn:']

        module.exports = function(input) {
          var url = (input || {}).url || input
          var protocol
          var parts
          var output = {}

          // if we don't have a string url, then allow the input to passthrough
          if (typeof url != 'string' && !(url instanceof String)) {
            return input
          }

          // trim the url string, and convert to an array
          url = url.trim()

          // if the protocol is not known, then passthrough
          protocol = protocols[protocols.indexOf(url.slice(0, 5))]
          if (!protocol) {
            return input
          }

          // now let's attack the remaining url parts
          url = url.slice(5)
          parts = url.split('@')

          output.username = input.username
          output.credential = input.credential
          // if we have an authentication part, then set the credentials
          if (parts.length > 1) {
            url = parts[1]
            parts = parts[0].split(':')

            // add the output credential and username
            output.username = parts[0]
            output.credential = (input || {}).credential || parts[1] || ''
          }

          output.url = protocol + url
          output.urls = [output.url]

          return output
        }
      },
      {},
    ],
    8: [
      function(require, module, exports) {
        ;(function(global) {
          /*!
           * Platform.js <https://mths.be/platform>
           * Copyright 2014-2018 Benjamin Tan <https://bnjmnt4n.now.sh/>
           * Copyright 2011-2013 John-David Dalton <http://allyoucanleet.com/>
           * Available under MIT license <https://mths.be/mit>
           */
          ;(function() {
            'use strict'

            /** Used to determine if values are of the language type `Object`. */
            var objectTypes = {
              function: true,
              object: true,
            }

            /** Used as a reference to the global object. */
            var root = (objectTypes[typeof window] && window) || this

            /** Backup possible global object. */
            var oldRoot = root

            /** Detect free variable `exports`. */
            var freeExports = objectTypes[typeof exports] && exports

            /** Detect free variable `module`. */
            var freeModule =
              objectTypes[typeof module] && module && !module.nodeType && module

            /** Detect free variable `global` from Node.js or Browserified code and use it as `root`. */
            var freeGlobal =
              freeExports && freeModule && typeof global == 'object' && global
            if (
              freeGlobal &&
              (freeGlobal.global === freeGlobal ||
                freeGlobal.window === freeGlobal ||
                freeGlobal.self === freeGlobal)
            ) {
              root = freeGlobal
            }

            /**
             * Used as the maximum length of an array-like object.
             * See the [ES6 spec](http://people.mozilla.org/~jorendorff/es6-draft.html#sec-tolength)
             * for more details.
             */
            var maxSafeInteger = Math.pow(2, 53) - 1

            /** Regular expression to detect Opera. */
            var reOpera = /\bOpera/

            /** Possible global object. */
            var thisBinding = this

            /** Used for native method references. */
            var objectProto = Object.prototype

            /** Used to check for own properties of an object. */
            var hasOwnProperty = objectProto.hasOwnProperty

            /** Used to resolve the internal `[[Class]]` of values. */
            var toString = objectProto.toString

            /*--------------------------------------------------------------------------*/

            /**
             * Capitalizes a string value.
             *
             * @private
             * @param {string} string The string to capitalize.
             * @returns {string} The capitalized string.
             */
            function capitalize(string) {
              string = String(string)
              return string.charAt(0).toUpperCase() + string.slice(1)
            }

            /**
             * A utility function to clean up the OS name.
             *
             * @private
             * @param {string} os The OS name to clean up.
             * @param {string} [pattern] A `RegExp` pattern matching the OS name.
             * @param {string} [label] A label for the OS.
             */
            function cleanupOS(os, pattern, label) {
              // Platform tokens are defined at:
              // http://msdn.microsoft.com/en-us/library/ms537503(VS.85).aspx
              // http://web.archive.org/web/20081122053950/http://msdn.microsoft.com/en-us/library/ms537503(VS.85).aspx
              var data = {
                '10.0': '10',
                '6.4': '10 Technical Preview',
                '6.3': '8.1',
                '6.2': '8',
                '6.1': 'Server 2008 R2 / 7',
                '6.0': 'Server 2008 / Vista',
                '5.2': 'Server 2003 / XP 64-bit',
                '5.1': 'XP',
                '5.01': '2000 SP1',
                '5.0': '2000',
                '4.0': 'NT',
                '4.90': 'ME',
              }
              // Detect Windows version from platform tokens.
              if (
                pattern &&
                label &&
                /^Win/i.test(os) &&
                !/^Windows Phone /i.test(os) &&
                (data = data[/[\d.]+$/.exec(os)])
              ) {
                os = 'Windows ' + data
              }
              // Correct character case and cleanup string.
              os = String(os)

              if (pattern && label) {
                os = os.replace(RegExp(pattern, 'i'), label)
              }

              os = format(
                os
                  .replace(/ ce$/i, ' CE')
                  .replace(/\bhpw/i, 'web')
                  .replace(/\bMacintosh\b/, 'Mac OS')
                  .replace(/_PowerPC\b/i, ' OS')
                  .replace(/\b(OS X) [^ \d]+/i, '$1')
                  .replace(/\bMac (OS X)\b/, '$1')
                  .replace(/\/(\d)/, ' $1')
                  .replace(/_/g, '.')
                  .replace(/(?: BePC|[ .]*fc[ \d.]+)$/i, '')
                  .replace(/\bx86\.64\b/gi, 'x86_64')
                  .replace(/\b(Windows Phone) OS\b/, '$1')
                  .replace(/\b(Chrome OS \w+) [\d.]+\b/, '$1')
                  .split(' on ')[0],
              )

              return os
            }

            /**
             * An iteration utility for arrays and objects.
             *
             * @private
             * @param {Array|Object} object The object to iterate over.
             * @param {Function} callback The function called per iteration.
             */
            function each(object, callback) {
              var index = -1,
                length = object ? object.length : 0

              if (
                typeof length == 'number' &&
                length > -1 &&
                length <= maxSafeInteger
              ) {
                while (++index < length) {
                  callback(object[index], index, object)
                }
              } else {
                forOwn(object, callback)
              }
            }

            /**
             * Trim and conditionally capitalize string values.
             *
             * @private
             * @param {string} string The string to format.
             * @returns {string} The formatted string.
             */
            function format(string) {
              string = trim(string)
              return /^(?:webOS|i(?:OS|P))/.test(string)
                ? string
                : capitalize(string)
            }

            /**
             * Iterates over an object's own properties, executing the `callback` for each.
             *
             * @private
             * @param {Object} object The object to iterate over.
             * @param {Function} callback The function executed per own property.
             */
            function forOwn(object, callback) {
              for (var key in object) {
                if (hasOwnProperty.call(object, key)) {
                  callback(object[key], key, object)
                }
              }
            }

            /**
             * Gets the internal `[[Class]]` of a value.
             *
             * @private
             * @param {*} value The value.
             * @returns {string} The `[[Class]]`.
             */
            function getClassOf(value) {
              return value == null
                ? capitalize(value)
                : toString.call(value).slice(8, -1)
            }

            /**
             * Host objects can return type values that are different from their actual
             * data type. The objects we are concerned with usually return non-primitive
             * types of "object", "function", or "unknown".
             *
             * @private
             * @param {*} object The owner of the property.
             * @param {string} property The property to check.
             * @returns {boolean} Returns `true` if the property value is a non-primitive, else `false`.
             */
            function isHostType(object, property) {
              var type = object != null ? typeof object[property] : 'number'
              return (
                !/^(?:boolean|number|string|undefined)$/.test(type) &&
                (type == 'object' ? !!object[property] : true)
              )
            }

            /**
             * Prepares a string for use in a `RegExp` by making hyphens and spaces optional.
             *
             * @private
             * @param {string} string The string to qualify.
             * @returns {string} The qualified string.
             */
            function qualify(string) {
              return String(string).replace(/([ -])(?!$)/g, '$1?')
            }

            /**
             * A bare-bones `Array#reduce` like utility function.
             *
             * @private
             * @param {Array} array The array to iterate over.
             * @param {Function} callback The function called per iteration.
             * @returns {*} The accumulated result.
             */
            function reduce(array, callback) {
              var accumulator = null
              each(array, function(value, index) {
                accumulator = callback(accumulator, value, index, array)
              })
              return accumulator
            }

            /**
             * Removes leading and trailing whitespace from a string.
             *
             * @private
             * @param {string} string The string to trim.
             * @returns {string} The trimmed string.
             */
            function trim(string) {
              return String(string).replace(/^ +| +$/g, '')
            }

            /*--------------------------------------------------------------------------*/

            /**
             * Creates a new platform object.
             *
             * @memberOf platform
             * @param {Object|string} [ua=navigator.userAgent] The user agent string or
             *  context object.
             * @returns {Object} A platform object.
             */
            function parse(ua) {
              /** The environment context object. */
              var context = root

              /** Used to flag when a custom context is provided. */
              var isCustomContext =
                ua && typeof ua == 'object' && getClassOf(ua) != 'String'

              // Juggle arguments.
              if (isCustomContext) {
                context = ua
                ua = null
              }

              /** Browser navigator object. */
              var nav = context.navigator || {}

              /** Browser user agent string. */
              var userAgent = nav.userAgent || ''

              ua || (ua = userAgent)

              /** Used to flag when `thisBinding` is the [ModuleScope]. */
              var isModuleScope = isCustomContext || thisBinding == oldRoot

              /** Used to detect if browser is like Chrome. */
              var likeChrome = isCustomContext
                ? !!nav.likeChrome
                : /\bChrome\b/.test(ua) &&
                  !/internal|\n/i.test(toString.toString())

              /** Internal `[[Class]]` value shortcuts. */
              var objectClass = 'Object',
                airRuntimeClass = isCustomContext
                  ? objectClass
                  : 'ScriptBridgingProxyObject',
                enviroClass = isCustomContext ? objectClass : 'Environment',
                javaClass =
                  isCustomContext && context.java
                    ? 'JavaPackage'
                    : getClassOf(context.java),
                phantomClass = isCustomContext ? objectClass : 'RuntimeObject'

              /** Detect Java environments. */
              var java = /\bJava/.test(javaClass) && context.java

              /** Detect Rhino. */
              var rhino = java && getClassOf(context.environment) == enviroClass

              /** A character to represent alpha. */
              var alpha = java ? 'a' : '\u03b1'

              /** A character to represent beta. */
              var beta = java ? 'b' : '\u03b2'

              /** Browser document object. */
              var doc = context.document || {}

              /**
               * Detect Opera browser (Presto-based).
               * http://www.howtocreate.co.uk/operaStuff/operaObject.html
               * http://dev.opera.com/articles/view/opera-mini-web-content-authoring-guidelines/#operamini
               */
              var opera = context.operamini || context.opera

              /** Opera `[[Class]]`. */
              var operaClass = reOpera.test(
                (operaClass =
                  isCustomContext && opera
                    ? opera['[[Class]]']
                    : getClassOf(opera)),
              )
                ? operaClass
                : (opera = null)

              /*------------------------------------------------------------------------*/

              /** Temporary variable used over the script's lifetime. */
              var data

              /** The CPU architecture. */
              var arch = ua

              /** Platform description array. */
              var description = []

              /** Platform alpha/beta indicator. */
              var prerelease = null

              /** A flag to indicate that environment features should be used to resolve the platform. */
              var useFeatures = ua == userAgent

              /** The browser/environment version. */
              var version =
                useFeatures &&
                opera &&
                typeof opera.version == 'function' &&
                opera.version()

              /** A flag to indicate if the OS ends with "/ Version" */
              var isSpecialCasedOS

              /* Detectable layout engines (order is important). */
              var layout = getLayout([
                { label: 'EdgeHTML', pattern: 'Edge' },
                'Trident',
                { label: 'WebKit', pattern: 'AppleWebKit' },
                'iCab',
                'Presto',
                'NetFront',
                'Tasman',
                'KHTML',
                'Gecko',
              ])

              /* Detectable browser names (order is important). */
              var name = getName([
                'Adobe AIR',
                'Arora',
                'Avant Browser',
                'Breach',
                'Camino',
                'Electron',
                'Epiphany',
                'Fennec',
                'Flock',
                'Galeon',
                'GreenBrowser',
                'iCab',
                'Iceweasel',
                'K-Meleon',
                'Konqueror',
                'Lunascape',
                'Maxthon',
                { label: 'Microsoft Edge', pattern: 'Edge' },
                'Midori',
                'Nook Browser',
                'PaleMoon',
                'PhantomJS',
                'Raven',
                'Rekonq',
                'RockMelt',
                { label: 'Samsung Internet', pattern: 'SamsungBrowser' },
                'SeaMonkey',
                { label: 'Silk', pattern: '(?:Cloud9|Silk-Accelerated)' },
                'Sleipnir',
                'SlimBrowser',
                { label: 'SRWare Iron', pattern: 'Iron' },
                'Sunrise',
                'Swiftfox',
                'Waterfox',
                'WebPositive',
                'Opera Mini',
                { label: 'Opera Mini', pattern: 'OPiOS' },
                'Opera',
                { label: 'Opera', pattern: 'OPR' },
                'Chrome',
                { label: 'Chrome Mobile', pattern: '(?:CriOS|CrMo)' },
                { label: 'Firefox', pattern: '(?:Firefox|Minefield)' },
                { label: 'Firefox for iOS', pattern: 'FxiOS' },
                { label: 'IE', pattern: 'IEMobile' },
                { label: 'IE', pattern: 'MSIE' },
                'Safari',
              ])

              /* Detectable products (order is important). */
              var product = getProduct([
                { label: 'BlackBerry', pattern: 'BB10' },
                'BlackBerry',
                { label: 'Galaxy S', pattern: 'GT-I9000' },
                { label: 'Galaxy S2', pattern: 'GT-I9100' },
                { label: 'Galaxy S3', pattern: 'GT-I9300' },
                { label: 'Galaxy S4', pattern: 'GT-I9500' },
                { label: 'Galaxy S5', pattern: 'SM-G900' },
                { label: 'Galaxy S6', pattern: 'SM-G920' },
                { label: 'Galaxy S6 Edge', pattern: 'SM-G925' },
                { label: 'Galaxy S7', pattern: 'SM-G930' },
                { label: 'Galaxy S7 Edge', pattern: 'SM-G935' },
                'Google TV',
                'Lumia',
                'iPad',
                'iPod',
                'iPhone',
                'Kindle',
                {
                  label: 'Kindle Fire',
                  pattern: '(?:Cloud9|Silk-Accelerated)',
                },
                'Nexus',
                'Nook',
                'PlayBook',
                'PlayStation Vita',
                'PlayStation',
                'TouchPad',
                'Transformer',
                { label: 'Wii U', pattern: 'WiiU' },
                'Wii',
                'Xbox One',
                { label: 'Xbox 360', pattern: 'Xbox' },
                'Xoom',
              ])

              /* Detectable manufacturers. */
              var manufacturer = getManufacturer({
                Apple: { iPad: 1, iPhone: 1, iPod: 1 },
                Archos: {},
                Amazon: { Kindle: 1, 'Kindle Fire': 1 },
                Asus: { Transformer: 1 },
                'Barnes & Noble': { Nook: 1 },
                BlackBerry: { PlayBook: 1 },
                Google: { 'Google TV': 1, Nexus: 1 },
                HP: { TouchPad: 1 },
                HTC: {},
                LG: {},
                Microsoft: { Xbox: 1, 'Xbox One': 1 },
                Motorola: { Xoom: 1 },
                Nintendo: { 'Wii U': 1, Wii: 1 },
                Nokia: { Lumia: 1 },
                Samsung: {
                  'Galaxy S': 1,
                  'Galaxy S2': 1,
                  'Galaxy S3': 1,
                  'Galaxy S4': 1,
                },
                Sony: { PlayStation: 1, 'PlayStation Vita': 1 },
              })

              /* Detectable operating systems (order is important). */
              var os = getOS([
                'Windows Phone',
                'Android',
                'CentOS',
                { label: 'Chrome OS', pattern: 'CrOS' },
                'Debian',
                'Fedora',
                'FreeBSD',
                'Gentoo',
                'Haiku',
                'Kubuntu',
                'Linux Mint',
                'OpenBSD',
                'Red Hat',
                'SuSE',
                'Ubuntu',
                'Xubuntu',
                'Cygwin',
                'Symbian OS',
                'hpwOS',
                'webOS ',
                'webOS',
                'Tablet OS',
                'Tizen',
                'Linux',
                'Mac OS X',
                'Macintosh',
                'Mac',
                'Windows 98;',
                'Windows ',
              ])

              /*------------------------------------------------------------------------*/

              /**
               * Picks the layout engine from an array of guesses.
               *
               * @private
               * @param {Array} guesses An array of guesses.
               * @returns {null|string} The detected layout engine.
               */
              function getLayout(guesses) {
                return reduce(guesses, function(result, guess) {
                  return (
                    result ||
                    (RegExp(
                      '\\b' + (guess.pattern || qualify(guess)) + '\\b',
                      'i',
                    ).exec(ua) &&
                      (guess.label || guess))
                  )
                })
              }

              /**
               * Picks the manufacturer from an array of guesses.
               *
               * @private
               * @param {Array} guesses An object of guesses.
               * @returns {null|string} The detected manufacturer.
               */
              function getManufacturer(guesses) {
                return reduce(guesses, function(result, value, key) {
                  // Lookup the manufacturer by product or scan the UA for the manufacturer.
                  return (
                    result ||
                    ((value[product] ||
                      value[/^[a-z]+(?: +[a-z]+\b)*/i.exec(product)] ||
                      RegExp(
                        '\\b' + qualify(key) + '(?:\\b|\\w*\\d)',
                        'i',
                      ).exec(ua)) &&
                      key)
                  )
                })
              }

              /**
               * Picks the browser name from an array of guesses.
               *
               * @private
               * @param {Array} guesses An array of guesses.
               * @returns {null|string} The detected browser name.
               */
              function getName(guesses) {
                return reduce(guesses, function(result, guess) {
                  return (
                    result ||
                    (RegExp(
                      '\\b' + (guess.pattern || qualify(guess)) + '\\b',
                      'i',
                    ).exec(ua) &&
                      (guess.label || guess))
                  )
                })
              }

              /**
               * Picks the OS name from an array of guesses.
               *
               * @private
               * @param {Array} guesses An array of guesses.
               * @returns {null|string} The detected OS name.
               */
              function getOS(guesses) {
                return reduce(guesses, function(result, guess) {
                  var pattern = guess.pattern || qualify(guess)
                  if (
                    !result &&
                    (result = RegExp(
                      '\\b' + pattern + '(?:/[\\d.]+|[ \\w.]*)',
                      'i',
                    ).exec(ua))
                  ) {
                    result = cleanupOS(result, pattern, guess.label || guess)
                  }
                  return result
                })
              }

              /**
               * Picks the product name from an array of guesses.
               *
               * @private
               * @param {Array} guesses An array of guesses.
               * @returns {null|string} The detected product name.
               */
              function getProduct(guesses) {
                return reduce(guesses, function(result, guess) {
                  var pattern = guess.pattern || qualify(guess)
                  if (
                    !result &&
                    (result =
                      RegExp('\\b' + pattern + ' *\\d+[.\\w_]*', 'i').exec(
                        ua,
                      ) ||
                      RegExp('\\b' + pattern + ' *\\w+-[\\w]*', 'i').exec(ua) ||
                      RegExp(
                        '\\b' +
                          pattern +
                          '(?:; *(?:[a-z]+[_-])?[a-z]+\\d+|[^ ();-]*)',
                        'i',
                      ).exec(ua))
                  ) {
                    // Split by forward slash and append product version if needed.
                    if (
                      (result = String(
                        guess.label && !RegExp(pattern, 'i').test(guess.label)
                          ? guess.label
                          : result,
                      ).split('/'))[1] &&
                      !/[\d.]+/.test(result[0])
                    ) {
                      result[0] += ' ' + result[1]
                    }
                    // Correct character case and cleanup string.
                    guess = guess.label || guess
                    result = format(
                      result[0]
                        .replace(RegExp(pattern, 'i'), guess)
                        .replace(RegExp('; *(?:' + guess + '[_-])?', 'i'), ' ')
                        .replace(
                          RegExp('(' + guess + ')[-_.]?(\\w)', 'i'),
                          '$1 $2',
                        ),
                    )
                  }
                  return result
                })
              }

              /**
               * Resolves the version using an array of UA patterns.
               *
               * @private
               * @param {Array} patterns An array of UA patterns.
               * @returns {null|string} The detected version.
               */
              function getVersion(patterns) {
                return reduce(patterns, function(result, pattern) {
                  return (
                    result ||
                    (RegExp(
                      pattern +
                        '(?:-[\\d.]+/|(?: for [\\w-]+)?[ /-])([\\d.]+[^ ();/_-]*)',
                      'i',
                    ).exec(ua) || 0)[1] ||
                    null
                  )
                })
              }

              /**
               * Returns `platform.description` when the platform object is coerced to a string.
               *
               * @name toString
               * @memberOf platform
               * @returns {string} Returns `platform.description` if available, else an empty string.
               */
              function toStringPlatform() {
                return this.description || ''
              }

              /*------------------------------------------------------------------------*/

              // Convert layout to an array so we can add extra details.
              layout && (layout = [layout])

              // Detect product names that contain their manufacturer's name.
              if (manufacturer && !product) {
                product = getProduct([manufacturer])
              }
              // Clean up Google TV.
              if ((data = /\bGoogle TV\b/.exec(product))) {
                product = data[0]
              }
              // Detect simulators.
              if (/\bSimulator\b/i.test(ua)) {
                product = (product ? product + ' ' : '') + 'Simulator'
              }
              // Detect Opera Mini 8+ running in Turbo/Uncompressed mode on iOS.
              if (name == 'Opera Mini' && /\bOPiOS\b/.test(ua)) {
                description.push('running in Turbo/Uncompressed mode')
              }
              // Detect IE Mobile 11.
              if (name == 'IE' && /\blike iPhone OS\b/.test(ua)) {
                data = parse(ua.replace(/like iPhone OS/, ''))
                manufacturer = data.manufacturer
                product = data.product
              }
              // Detect iOS.
              else if (/^iP/.test(product)) {
                name || (name = 'Safari')
                os =
                  'iOS' +
                  ((data = / OS ([\d_]+)/i.exec(ua))
                    ? ' ' + data[1].replace(/_/g, '.')
                    : '')
              }
              // Detect Kubuntu.
              else if (name == 'Konqueror' && !/buntu/i.test(os)) {
                os = 'Kubuntu'
              }
              // Detect Android browsers.
              else if (
                (manufacturer &&
                  manufacturer != 'Google' &&
                  ((/Chrome/.test(name) && !/\bMobile Safari\b/i.test(ua)) ||
                    /\bVita\b/.test(product))) ||
                (/\bAndroid\b/.test(os) &&
                  /^Chrome/.test(name) &&
                  /\bVersion\//i.test(ua))
              ) {
                name = 'Android Browser'
                os = /\bAndroid\b/.test(os) ? os : 'Android'
              }
              // Detect Silk desktop/accelerated modes.
              else if (name == 'Silk') {
                if (!/\bMobi/i.test(ua)) {
                  os = 'Android'
                  description.unshift('desktop mode')
                }
                if (/Accelerated *= *true/i.test(ua)) {
                  description.unshift('accelerated')
                }
              }
              // Detect PaleMoon identifying as Firefox.
              else if (
                name == 'PaleMoon' &&
                (data = /\bFirefox\/([\d.]+)\b/.exec(ua))
              ) {
                description.push('identifying as Firefox ' + data[1])
              }
              // Detect Firefox OS and products running Firefox.
              else if (
                name == 'Firefox' &&
                (data = /\b(Mobile|Tablet|TV)\b/i.exec(ua))
              ) {
                os || (os = 'Firefox OS')
                product || (product = data[1])
              }
              // Detect false positives for Firefox/Safari.
              else if (
                !name ||
                (data =
                  !/\bMinefield\b/i.test(ua) &&
                  /\b(?:Firefox|Safari)\b/.exec(name))
              ) {
                // Escape the `/` for Firefox 1.
                if (
                  name &&
                  !product &&
                  /[\/,]|^[^(]+?\)/.test(ua.slice(ua.indexOf(data + '/') + 8))
                ) {
                  // Clear name of false positives.
                  name = null
                }
                // Reassign a generic name.
                if (
                  (data = product || manufacturer || os) &&
                  (product ||
                    manufacturer ||
                    /\b(?:Android|Symbian OS|Tablet OS|webOS)\b/.test(os))
                ) {
                  name =
                    /[a-z]+(?: Hat)?/i.exec(
                      /\bAndroid\b/.test(os) ? os : data,
                    ) + ' Browser'
                }
              }
              // Add Chrome version to description for Electron.
              else if (
                name == 'Electron' &&
                (data = (/\bChrome\/([\d.]+)\b/.exec(ua) || 0)[1])
              ) {
                description.push('Chromium ' + data)
              }
              // Detect non-Opera (Presto-based) versions (order is important).
              if (!version) {
                version = getVersion([
                  '(?:Cloud9|CriOS|CrMo|Edge|FxiOS|IEMobile|Iron|Opera ?Mini|OPiOS|OPR|Raven|SamsungBrowser|Silk(?!/[\\d.]+$))',
                  'Version',
                  qualify(name),
                  '(?:Firefox|Minefield|NetFront)',
                ])
              }
              // Detect stubborn layout engines.
              if (
                (data =
                  (layout == 'iCab' && parseFloat(version) > 3 && 'WebKit') ||
                  (/\bOpera\b/.test(name) &&
                    (/\bOPR\b/.test(ua) ? 'Blink' : 'Presto')) ||
                  (/\b(?:Midori|Nook|Safari)\b/i.test(ua) &&
                    !/^(?:Trident|EdgeHTML)$/.test(layout) &&
                    'WebKit') ||
                  (!layout &&
                    /\bMSIE\b/i.test(ua) &&
                    (os == 'Mac OS' ? 'Tasman' : 'Trident')) ||
                  (layout == 'WebKit' &&
                    /\bPlayStation\b(?! Vita\b)/i.test(name) &&
                    'NetFront'))
              ) {
                layout = [data]
              }
              // Detect Windows Phone 7 desktop mode.
              if (
                name == 'IE' &&
                (data = (/; *(?:XBLWP|ZuneWP)(\d+)/i.exec(ua) || 0)[1])
              ) {
                name += ' Mobile'
                os = 'Windows Phone ' + (/\+$/.test(data) ? data : data + '.x')
                description.unshift('desktop mode')
              }
              // Detect Windows Phone 8.x desktop mode.
              else if (/\bWPDesktop\b/i.test(ua)) {
                name = 'IE Mobile'
                os = 'Windows Phone 8.x'
                description.unshift('desktop mode')
                version || (version = (/\brv:([\d.]+)/.exec(ua) || 0)[1])
              }
              // Detect IE 11 identifying as other browsers.
              else if (
                name != 'IE' &&
                layout == 'Trident' &&
                (data = /\brv:([\d.]+)/.exec(ua))
              ) {
                if (name) {
                  description.push(
                    'identifying as ' + name + (version ? ' ' + version : ''),
                  )
                }
                name = 'IE'
                version = data[1]
              }
              // Leverage environment features.
              if (useFeatures) {
                // Detect server-side environments.
                // Rhino has a global function while others have a global object.
                if (isHostType(context, 'global')) {
                  if (java) {
                    data = java.lang.System
                    arch = data.getProperty('os.arch')
                    os =
                      os ||
                      data.getProperty('os.name') +
                        ' ' +
                        data.getProperty('os.version')
                  }
                  if (rhino) {
                    try {
                      version = context
                        .require('ringo/engine')
                        .version.join('.')
                      name = 'RingoJS'
                    } catch (e) {
                      if (
                        (data = context.system) &&
                        data.global.system == context.system
                      ) {
                        name = 'Narwhal'
                        os || (os = data[0].os || null)
                      }
                    }
                    if (!name) {
                      name = 'Rhino'
                    }
                  } else if (
                    typeof context.process == 'object' &&
                    !context.process.browser &&
                    (data = context.process)
                  ) {
                    if (typeof data.versions == 'object') {
                      if (typeof data.versions.electron == 'string') {
                        description.push('Node ' + data.versions.node)
                        name = 'Electron'
                        version = data.versions.electron
                      } else if (typeof data.versions.nw == 'string') {
                        description.push(
                          'Chromium ' + version,
                          'Node ' + data.versions.node,
                        )
                        name = 'NW.js'
                        version = data.versions.nw
                      }
                    }
                    if (!name) {
                      name = 'Node.js'
                      arch = data.arch
                      os = data.platform
                      version = /[\d.]+/.exec(data.version)
                      version = version ? version[0] : null
                    }
                  }
                }
                // Detect Adobe AIR.
                else if (
                  getClassOf((data = context.runtime)) == airRuntimeClass
                ) {
                  name = 'Adobe AIR'
                  os = data.flash.system.Capabilities.os
                }
                // Detect PhantomJS.
                else if (getClassOf((data = context.phantom)) == phantomClass) {
                  name = 'PhantomJS'
                  version =
                    (data = data.version || null) &&
                    data.major + '.' + data.minor + '.' + data.patch
                }
                // Detect IE compatibility modes.
                else if (
                  typeof doc.documentMode == 'number' &&
                  (data = /\bTrident\/(\d+)/i.exec(ua))
                ) {
                  // We're in compatibility mode when the Trident version + 4 doesn't
                  // equal the document mode.
                  version = [version, doc.documentMode]
                  if ((data = +data[1] + 4) != version[1]) {
                    description.push('IE ' + version[1] + ' mode')
                    layout && (layout[1] = '')
                    version[1] = data
                  }
                  version =
                    name == 'IE' ? String(version[1].toFixed(1)) : version[0]
                }
                // Detect IE 11 masking as other browsers.
                else if (
                  typeof doc.documentMode == 'number' &&
                  /^(?:Chrome|Firefox)\b/.test(name)
                ) {
                  description.push('masking as ' + name + ' ' + version)
                  name = 'IE'
                  version = '11.0'
                  layout = ['Trident']
                  os = 'Windows'
                }
                os = os && format(os)
              }
              // Detect prerelease phases.
              if (
                version &&
                (data =
                  /(?:[ab]|dp|pre|[ab]\d+pre)(?:\d+\+?)?$/i.exec(version) ||
                  /(?:alpha|beta)(?: ?\d)?/i.exec(
                    ua + ';' + (useFeatures && nav.appMinorVersion),
                  ) ||
                  (/\bMinefield\b/i.test(ua) && 'a'))
              ) {
                prerelease = /b/i.test(data) ? 'beta' : 'alpha'
                version =
                  version.replace(RegExp(data + '\\+?$'), '') +
                  (prerelease == 'beta' ? beta : alpha) +
                  (/\d+\+?/.exec(data) || '')
              }
              // Detect Firefox Mobile.
              if (
                name == 'Fennec' ||
                (name == 'Firefox' && /\b(?:Android|Firefox OS)\b/.test(os))
              ) {
                name = 'Firefox Mobile'
              }
              // Obscure Maxthon's unreliable version.
              else if (name == 'Maxthon' && version) {
                version = version.replace(/\.[\d.]+/, '.x')
              }
              // Detect Xbox 360 and Xbox One.
              else if (/\bXbox\b/i.test(product)) {
                if (product == 'Xbox 360') {
                  os = null
                }
                if (product == 'Xbox 360' && /\bIEMobile\b/.test(ua)) {
                  description.unshift('mobile mode')
                }
              }
              // Add mobile postfix.
              else if (
                (/^(?:Chrome|IE|Opera)$/.test(name) ||
                  (name && !product && !/Browser|Mobi/.test(name))) &&
                (os == 'Windows CE' || /Mobi/i.test(ua))
              ) {
                name += ' Mobile'
              }
              // Detect IE platform preview.
              else if (name == 'IE' && useFeatures) {
                try {
                  if (context.external === null) {
                    description.unshift('platform preview')
                  }
                } catch (e) {
                  description.unshift('embedded')
                }
              }
              // Detect BlackBerry OS version.
              // http://docs.blackberry.com/en/developers/deliverables/18169/HTTP_headers_sent_by_BB_Browser_1234911_11.jsp
              else if (
                (/\bBlackBerry\b/.test(product) || /\bBB10\b/.test(ua)) &&
                (data =
                  (RegExp(
                    product.replace(/ +/g, ' *') + '/([.\\d]+)',
                    'i',
                  ).exec(ua) || 0)[1] || version)
              ) {
                data = [data, /BB10/.test(ua)]
                os =
                  (data[1]
                    ? ((product = null), (manufacturer = 'BlackBerry'))
                    : 'Device Software') +
                  ' ' +
                  data[0]
                version = null
              }
              // Detect Opera identifying/masking itself as another browser.
              // http://www.opera.com/support/kb/view/843/
              else if (
                this != forOwn &&
                product != 'Wii' &&
                ((useFeatures && opera) ||
                  (/Opera/.test(name) && /\b(?:MSIE|Firefox)\b/i.test(ua)) ||
                  (name == 'Firefox' && /\bOS X (?:\d+\.){2,}/.test(os)) ||
                  (name == 'IE' &&
                    ((os && !/^Win/.test(os) && version > 5.5) ||
                      (/\bWindows XP\b/.test(os) && version > 8) ||
                      (version == 8 && !/\bTrident\b/.test(ua))))) &&
                !reOpera.test(
                  (data = parse.call(forOwn, ua.replace(reOpera, '') + ';')),
                ) &&
                data.name
              ) {
                // When "identifying", the UA contains both Opera and the other browser's name.
                data =
                  'ing as ' +
                  data.name +
                  ((data = data.version) ? ' ' + data : '')
                if (reOpera.test(name)) {
                  if (/\bIE\b/.test(data) && os == 'Mac OS') {
                    os = null
                  }
                  data = 'identify' + data
                }
                // When "masking", the UA contains only the other browser's name.
                else {
                  data = 'mask' + data
                  if (operaClass) {
                    name = format(
                      operaClass.replace(/([a-z])([A-Z])/g, '$1 $2'),
                    )
                  } else {
                    name = 'Opera'
                  }
                  if (/\bIE\b/.test(data)) {
                    os = null
                  }
                  if (!useFeatures) {
                    version = null
                  }
                }
                layout = ['Presto']
                description.push(data)
              }
              // Detect WebKit Nightly and approximate Chrome/Safari versions.
              if ((data = (/\bAppleWebKit\/([\d.]+\+?)/i.exec(ua) || 0)[1])) {
                // Correct build number for numeric comparison.
                // (e.g. "532.5" becomes "532.05")
                data = [parseFloat(data.replace(/\.(\d)$/, '.0$1')), data]
                // Nightly builds are postfixed with a "+".
                if (name == 'Safari' && data[1].slice(-1) == '+') {
                  name = 'WebKit Nightly'
                  prerelease = 'alpha'
                  version = data[1].slice(0, -1)
                }
                // Clear incorrect browser versions.
                else if (
                  version == data[1] ||
                  version ==
                    (data[2] = (/\bSafari\/([\d.]+\+?)/i.exec(ua) || 0)[1])
                ) {
                  version = null
                }
                // Use the full Chrome version when available.
                data[1] = (/\bChrome\/([\d.]+)/i.exec(ua) || 0)[1]
                // Detect Blink layout engine.
                if (
                  data[0] == 537.36 &&
                  data[2] == 537.36 &&
                  parseFloat(data[1]) >= 28 &&
                  layout == 'WebKit'
                ) {
                  layout = ['Blink']
                }
                // Detect JavaScriptCore.
                // http://stackoverflow.com/questions/6768474/how-can-i-detect-which-javascript-engine-v8-or-jsc-is-used-at-runtime-in-androi
                if (!useFeatures || (!likeChrome && !data[1])) {
                  layout && (layout[1] = 'like Safari')
                  data =
                    ((data = data[0]),
                    data < 400
                      ? 1
                      : data < 500
                      ? 2
                      : data < 526
                      ? 3
                      : data < 533
                      ? 4
                      : data < 534
                      ? '4+'
                      : data < 535
                      ? 5
                      : data < 537
                      ? 6
                      : data < 538
                      ? 7
                      : data < 601
                      ? 8
                      : '8')
                } else {
                  layout && (layout[1] = 'like Chrome')
                  data =
                    data[1] ||
                    ((data = data[0]),
                    data < 530
                      ? 1
                      : data < 532
                      ? 2
                      : data < 532.05
                      ? 3
                      : data < 533
                      ? 4
                      : data < 534.03
                      ? 5
                      : data < 534.07
                      ? 6
                      : data < 534.1
                      ? 7
                      : data < 534.13
                      ? 8
                      : data < 534.16
                      ? 9
                      : data < 534.24
                      ? 10
                      : data < 534.3
                      ? 11
                      : data < 535.01
                      ? 12
                      : data < 535.02
                      ? '13+'
                      : data < 535.07
                      ? 15
                      : data < 535.11
                      ? 16
                      : data < 535.19
                      ? 17
                      : data < 536.05
                      ? 18
                      : data < 536.1
                      ? 19
                      : data < 537.01
                      ? 20
                      : data < 537.11
                      ? '21+'
                      : data < 537.13
                      ? 23
                      : data < 537.18
                      ? 24
                      : data < 537.24
                      ? 25
                      : data < 537.36
                      ? 26
                      : layout != 'Blink'
                      ? '27'
                      : '28')
                }
                // Add the postfix of ".x" or "+" for approximate versions.
                layout &&
                  (layout[1] +=
                    ' ' +
                    (data +=
                      typeof data == 'number'
                        ? '.x'
                        : /[.+]/.test(data)
                        ? ''
                        : '+'))
                // Obscure version for some Safari 1-2 releases.
                if (name == 'Safari' && (!version || parseInt(version) > 45)) {
                  version = data
                }
              }
              // Detect Opera desktop modes.
              if (name == 'Opera' && (data = /\bzbov|zvav$/.exec(os))) {
                name += ' '
                description.unshift('desktop mode')
                if (data == 'zvav') {
                  name += 'Mini'
                  version = null
                } else {
                  name += 'Mobile'
                }
                os = os.replace(RegExp(' *' + data + '$'), '')
              }
              // Detect Chrome desktop mode.
              else if (
                name == 'Safari' &&
                /\bChrome\b/.exec(layout && layout[1])
              ) {
                description.unshift('desktop mode')
                name = 'Chrome Mobile'
                version = null

                if (/\bOS X\b/.test(os)) {
                  manufacturer = 'Apple'
                  os = 'iOS 4.3+'
                } else {
                  os = null
                }
              }
              // Strip incorrect OS versions.
              if (
                version &&
                version.indexOf((data = /[\d.]+$/.exec(os))) == 0 &&
                ua.indexOf('/' + data + '-') > -1
              ) {
                os = trim(os.replace(data, ''))
              }
              // Add layout engine.
              if (
                layout &&
                !/\b(?:Avant|Nook)\b/.test(name) &&
                (/Browser|Lunascape|Maxthon/.test(name) ||
                  (name != 'Safari' &&
                    /^iOS/.test(os) &&
                    /\bSafari\b/.test(layout[1])) ||
                  (/^(?:Adobe|Arora|Breach|Midori|Opera|Phantom|Rekonq|Rock|Samsung Internet|Sleipnir|Web)/.test(
                    name,
                  ) &&
                    layout[1]))
              ) {
                // Don't add layout details to description if they are falsey.
                ;(data = layout[layout.length - 1]) && description.push(data)
              }
              // Combine contextual information.
              if (description.length) {
                description = ['(' + description.join('; ') + ')']
              }
              // Append manufacturer to description.
              if (
                manufacturer &&
                product &&
                product.indexOf(manufacturer) < 0
              ) {
                description.push('on ' + manufacturer)
              }
              // Append product to description.
              if (product) {
                description.push(
                  (/^on /.test(description[description.length - 1])
                    ? ''
                    : 'on ') + product,
                )
              }
              // Parse the OS into an object.
              if (os) {
                data = / ([\d.+]+)$/.exec(os)
                isSpecialCasedOS =
                  data && os.charAt(os.length - data[0].length - 1) == '/'
                os = {
                  architecture: 32,
                  family:
                    data && !isSpecialCasedOS ? os.replace(data[0], '') : os,
                  version: data ? data[1] : null,
                  toString: function() {
                    var version = this.version
                    return (
                      this.family +
                      (version && !isSpecialCasedOS ? ' ' + version : '') +
                      (this.architecture == 64 ? ' 64-bit' : '')
                    )
                  },
                }
              }
              // Add browser/OS architecture.
              if (
                (data = /\b(?:AMD|IA|Win|WOW|x86_|x)64\b/i.exec(arch)) &&
                !/\bi686\b/i.test(arch)
              ) {
                if (os) {
                  os.architecture = 64
                  os.family = os.family.replace(RegExp(' *' + data), '')
                }
                if (
                  name &&
                  (/\bWOW64\b/i.test(ua) ||
                    (useFeatures &&
                      /\w(?:86|32)$/.test(nav.cpuClass || nav.platform) &&
                      !/\bWin64; x64\b/i.test(ua)))
                ) {
                  description.unshift('32-bit')
                }
              }
              // Chrome 39 and above on OS X is always 64-bit.
              else if (
                os &&
                /^OS X/.test(os.family) &&
                name == 'Chrome' &&
                parseFloat(version) >= 39
              ) {
                os.architecture = 64
              }

              ua || (ua = null)

              /*------------------------------------------------------------------------*/

              /**
               * The platform object.
               *
               * @name platform
               * @type Object
               */
              var platform = {}

              /**
               * The platform description.
               *
               * @memberOf platform
               * @type string|null
               */
              platform.description = ua

              /**
               * The name of the browser's layout engine.
               *
               * The list of common layout engines include:
               * "Blink", "EdgeHTML", "Gecko", "Trident" and "WebKit"
               *
               * @memberOf platform
               * @type string|null
               */
              platform.layout = layout && layout[0]

              /**
               * The name of the product's manufacturer.
               *
               * The list of manufacturers include:
               * "Apple", "Archos", "Amazon", "Asus", "Barnes & Noble", "BlackBerry",
               * "Google", "HP", "HTC", "LG", "Microsoft", "Motorola", "Nintendo",
               * "Nokia", "Samsung" and "Sony"
               *
               * @memberOf platform
               * @type string|null
               */
              platform.manufacturer = manufacturer

              /**
               * The name of the browser/environment.
               *
               * The list of common browser names include:
               * "Chrome", "Electron", "Firefox", "Firefox for iOS", "IE",
               * "Microsoft Edge", "PhantomJS", "Safari", "SeaMonkey", "Silk",
               * "Opera Mini" and "Opera"
               *
               * Mobile versions of some browsers have "Mobile" appended to their name:
               * eg. "Chrome Mobile", "Firefox Mobile", "IE Mobile" and "Opera Mobile"
               *
               * @memberOf platform
               * @type string|null
               */
              platform.name = name

              /**
               * The alpha/beta release indicator.
               *
               * @memberOf platform
               * @type string|null
               */
              platform.prerelease = prerelease

              /**
               * The name of the product hosting the browser.
               *
               * The list of common products include:
               *
               * "BlackBerry", "Galaxy S4", "Lumia", "iPad", "iPod", "iPhone", "Kindle",
               * "Kindle Fire", "Nexus", "Nook", "PlayBook", "TouchPad" and "Transformer"
               *
               * @memberOf platform
               * @type string|null
               */
              platform.product = product

              /**
               * The browser's user agent string.
               *
               * @memberOf platform
               * @type string|null
               */
              platform.ua = ua

              /**
               * The browser/environment version.
               *
               * @memberOf platform
               * @type string|null
               */
              platform.version = name && version

              /**
               * The name of the operating system.
               *
               * @memberOf platform
               * @type Object
               */
              platform.os = os || {
                /**
                 * The CPU architecture the OS is built for.
                 *
                 * @memberOf platform.os
                 * @type number|null
                 */
                architecture: null,

                /**
                 * The family of the OS.
                 *
                 * Common values include:
                 * "Windows", "Windows Server 2008 R2 / 7", "Windows Server 2008 / Vista",
                 * "Windows XP", "OS X", "Ubuntu", "Debian", "Fedora", "Red Hat", "SuSE",
                 * "Android", "iOS" and "Windows Phone"
                 *
                 * @memberOf platform.os
                 * @type string|null
                 */
                family: null,

                /**
                 * The version of the OS.
                 *
                 * @memberOf platform.os
                 * @type string|null
                 */
                version: null,

                /**
                 * Returns the OS string.
                 *
                 * @memberOf platform.os
                 * @returns {string} The OS string.
                 */
                toString: function() {
                  return 'null'
                },
              }

              platform.parse = parse
              platform.toString = toStringPlatform

              if (platform.version) {
                description.unshift(version)
              }
              if (platform.name) {
                description.unshift(name)
              }
              if (
                os &&
                name &&
                !(
                  os == String(os).split(' ')[0] &&
                  (os == name.split(' ')[0] || product)
                )
              ) {
                description.push(product ? '(' + os + ')' : 'on ' + os)
              }
              if (description.length) {
                platform.description = description.join(' ')
              }
              return platform
            }

            /*--------------------------------------------------------------------------*/

            // Export platform.
            var platform = parse()

            // Some AMD build optimizers, like r.js, check for condition patterns like the following:
            if (
              typeof define == 'function' &&
              typeof define.amd == 'object' &&
              define.amd
            ) {
              // Expose platform on the global object to prevent errors when platform is
              // loaded by a script tag in the presence of an AMD loader.
              // See http://requirejs.org/docs/errors.html#mismatch for more details.
              root.platform = platform

              // Define as an anonymous module so platform can be aliased through path mapping.
              define(function() {
                return platform
              })
            }
            // Check for `exports` after `define` in case a build optimizer adds an `exports` object.
            else if (freeExports && freeModule) {
              // Export for CommonJS support.
              forOwn(platform, function(value, key) {
                freeExports[key] = value
              })
            } else {
              // Export to the global object.
              root.platform = platform
            }
          }.call(this))
        }.call(
          this,
          typeof global !== 'undefined'
            ? global
            : typeof self !== 'undefined'
            ? self
            : typeof window !== 'undefined'
            ? window
            : {},
        ))
      },
      {},
    ],
    9: [
      function(require, module, exports) {
        var v1 = require('./v1')
        var v4 = require('./v4')

        var uuid = v4
        uuid.v1 = v1
        uuid.v4 = v4

        module.exports = uuid
      },
      { './v1': 12, './v4': 13 },
    ],
    10: [
      function(require, module, exports) {
        /**
         * Convert array of 16 byte values to UUID string format of the form:
         * XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
         */
        var byteToHex = []
        for (var i = 0; i < 256; ++i) {
          byteToHex[i] = (i + 0x100).toString(16).substr(1)
        }

        function bytesToUuid(buf, offset) {
          var i = offset || 0
          var bth = byteToHex
          // join used to fix memory issue caused by concatenation: https://bugs.chromium.org/p/v8/issues/detail?id=3175#c4
          return [
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
            '-',
            bth[buf[i++]],
            bth[buf[i++]],
            '-',
            bth[buf[i++]],
            bth[buf[i++]],
            '-',
            bth[buf[i++]],
            bth[buf[i++]],
            '-',
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
            bth[buf[i++]],
          ].join('')
        }

        module.exports = bytesToUuid
      },
      {},
    ],
    11: [
      function(require, module, exports) {
        // Unique ID creation requires a high quality random # generator.  In the
        // browser this is a little complicated due to unknown quality of Math.random()
        // and inconsistent support for the `crypto` API.  We do the best we can via
        // feature-detection

        // getRandomValues needs to be invoked in a context where "this" is a Crypto
        // implementation. Also, find the complete implementation of crypto on IE11.
        var getRandomValues =
          (typeof crypto != 'undefined' &&
            crypto.getRandomValues &&
            crypto.getRandomValues.bind(crypto)) ||
          (typeof msCrypto != 'undefined' &&
            typeof window.msCrypto.getRandomValues == 'function' &&
            msCrypto.getRandomValues.bind(msCrypto))

        if (getRandomValues) {
          // WHATWG crypto RNG - http://wiki.whatwg.org/wiki/Crypto
          var rnds8 = new Uint8Array(16) // eslint-disable-line no-undef

          module.exports = function whatwgRNG() {
            getRandomValues(rnds8)
            return rnds8
          }
        } else {
          // Math.random()-based (RNG)
          //
          // If all else fails, use Math.random().  It's fast, but is of unspecified
          // quality.
          var rnds = new Array(16)

          module.exports = function mathRNG() {
            for (var i = 0, r; i < 16; i++) {
              if ((i & 0x03) === 0) r = Math.random() * 0x100000000
              rnds[i] = (r >>> ((i & 0x03) << 3)) & 0xff
            }

            return rnds
          }
        }
      },
      {},
    ],
    12: [
      function(require, module, exports) {
        var rng = require('./lib/rng')
        var bytesToUuid = require('./lib/bytesToUuid')

        // **`v1()` - Generate time-based UUID**
        //
        // Inspired by https://github.com/LiosK/UUID.js
        // and http://docs.python.org/library/uuid.html

        var _nodeId
        var _clockseq

        // Previous uuid creation time
        var _lastMSecs = 0
        var _lastNSecs = 0

        // See https://github.com/broofa/node-uuid for API details
        function v1(options, buf, offset) {
          var i = (buf && offset) || 0
          var b = buf || []

          options = options || {}
          var node = options.node || _nodeId
          var clockseq =
            options.clockseq !== undefined ? options.clockseq : _clockseq

          // node and clockseq need to be initialized to random values if they're not
          // specified.  We do this lazily to minimize issues related to insufficient
          // system entropy.  See #189
          if (node == null || clockseq == null) {
            var seedBytes = rng()
            if (node == null) {
              // Per 4.5, create and 48-bit node id, (47 random bits + multicast bit = 1)
              node = _nodeId = [
                seedBytes[0] | 0x01,
                seedBytes[1],
                seedBytes[2],
                seedBytes[3],
                seedBytes[4],
                seedBytes[5],
              ]
            }
            if (clockseq == null) {
              // Per 4.2.2, randomize (14 bit) clockseq
              clockseq = _clockseq =
                ((seedBytes[6] << 8) | seedBytes[7]) & 0x3fff
            }
          }

          // UUID timestamps are 100 nano-second units since the Gregorian epoch,
          // (1582-10-15 00:00).  JSNumbers aren't precise enough for this, so
          // time is handled internally as 'msecs' (integer milliseconds) and 'nsecs'
          // (100-nanoseconds offset from msecs) since unix epoch, 1970-01-01 00:00.
          var msecs =
            options.msecs !== undefined ? options.msecs : new Date().getTime()

          // Per 4.2.1.2, use count of uuid's generated during the current clock
          // cycle to simulate higher resolution clock
          var nsecs =
            options.nsecs !== undefined ? options.nsecs : _lastNSecs + 1

          // Time since last uuid creation (in msecs)
          var dt = msecs - _lastMSecs + (nsecs - _lastNSecs) / 10000

          // Per 4.2.1.2, Bump clockseq on clock regression
          if (dt < 0 && options.clockseq === undefined) {
            clockseq = (clockseq + 1) & 0x3fff
          }

          // Reset nsecs if clock regresses (new clockseq) or we've moved onto a new
          // time interval
          if ((dt < 0 || msecs > _lastMSecs) && options.nsecs === undefined) {
            nsecs = 0
          }

          // Per 4.2.1.2 Throw error if too many uuids are requested
          if (nsecs >= 10000) {
            throw new Error("uuid.v1(): Can't create more than 10M uuids/sec")
          }

          _lastMSecs = msecs
          _lastNSecs = nsecs
          _clockseq = clockseq

          // Per 4.1.4 - Convert from unix epoch to Gregorian epoch
          msecs += 12219292800000

          // `time_low`
          var tl = ((msecs & 0xfffffff) * 10000 + nsecs) % 0x100000000
          b[i++] = (tl >>> 24) & 0xff
          b[i++] = (tl >>> 16) & 0xff
          b[i++] = (tl >>> 8) & 0xff
          b[i++] = tl & 0xff

          // `time_mid`
          var tmh = ((msecs / 0x100000000) * 10000) & 0xfffffff
          b[i++] = (tmh >>> 8) & 0xff
          b[i++] = tmh & 0xff

          // `time_high_and_version`
          b[i++] = ((tmh >>> 24) & 0xf) | 0x10 // include version
          b[i++] = (tmh >>> 16) & 0xff

          // `clock_seq_hi_and_reserved` (Per 4.2.2 - include variant)
          b[i++] = (clockseq >>> 8) | 0x80

          // `clock_seq_low`
          b[i++] = clockseq & 0xff

          // `node`
          for (var n = 0; n < 6; ++n) {
            b[i + n] = node[n]
          }

          return buf ? buf : bytesToUuid(b)
        }

        module.exports = v1
      },
      { './lib/bytesToUuid': 10, './lib/rng': 11 },
    ],
    13: [
      function(require, module, exports) {
        var rng = require('./lib/rng')
        var bytesToUuid = require('./lib/bytesToUuid')

        function v4(options, buf, offset) {
          var i = (buf && offset) || 0

          if (typeof options == 'string') {
            buf = options === 'binary' ? new Array(16) : null
            options = null
          }
          options = options || {}

          var rnds = options.random || (options.rng || rng)()

          // Per 4.4, set bits for version and `clock_seq_hi_and_reserved`
          rnds[6] = (rnds[6] & 0x0f) | 0x40
          rnds[8] = (rnds[8] & 0x3f) | 0x80

          // Copy bytes to buffer, if provided
          if (buf) {
            for (var ii = 0; ii < 16; ++ii) {
              buf[i + ii] = rnds[ii]
            }
          }

          return buf || bytesToUuid(rnds)
        }

        module.exports = v4
      },
      { './lib/bytesToUuid': 10, './lib/rng': 11 },
    ],
    14: [
      function(require, module, exports) {
        /*
WildEmitter.js is a slim little event emitter by @henrikjoreteg largely based
on @visionmedia's Emitter from UI Kit.

Why? I wanted it standalone.

I also wanted support for wildcard emitters like this:

emitter.on('*', function (eventName, other, event, payloads) {

});

emitter.on('somenamespace*', function (eventName, payloads) {

});

Please note that callbacks triggered by wildcard registered events also get
the event name as the first argument.
*/

        module.exports = WildEmitter

        function WildEmitter() {}

        WildEmitter.mixin = function(constructor) {
          var prototype = constructor.prototype || constructor

          prototype.isWildEmitter = true

          // Listen on the given `event` with `fn`. Store a group name if present.
          prototype.on = function(event, groupName, fn) {
            this.callbacks = this.callbacks || {}
            var hasGroup = arguments.length === 3,
              group = hasGroup ? arguments[1] : undefined,
              func = hasGroup ? arguments[2] : arguments[1]
            func._groupName = group
            ;(this.callbacks[event] = this.callbacks[event] || []).push(func)
            return this
          }

          // Adds an `event` listener that will be invoked a single
          // time then automatically removed.
          prototype.once = function(event, groupName, fn) {
            var self = this,
              hasGroup = arguments.length === 3,
              group = hasGroup ? arguments[1] : undefined,
              func = hasGroup ? arguments[2] : arguments[1]
            function on() {
              self.off(event, on)
              func.apply(this, arguments)
            }
            this.on(event, group, on)
            return this
          }

          // Unbinds an entire group
          prototype.releaseGroup = function(groupName) {
            this.callbacks = this.callbacks || {}
            var item, i, len, handlers
            for (item in this.callbacks) {
              handlers = this.callbacks[item]
              for (i = 0, len = handlers.length; i < len; i++) {
                if (handlers[i]._groupName === groupName) {
                  //console.log('removing');
                  // remove it and shorten the array we're looping through
                  handlers.splice(i, 1)
                  i--
                  len--
                }
              }
            }
            return this
          }

          // Remove the given callback for `event` or all
          // registered callbacks.
          prototype.off = function(event, fn) {
            this.callbacks = this.callbacks || {}
            var callbacks = this.callbacks[event],
              i

            if (!callbacks) return this

            // remove all handlers
            if (arguments.length === 1) {
              delete this.callbacks[event]
              return this
            }

            // remove specific handler
            i = callbacks.indexOf(fn)
            if (i !== -1) {
              callbacks.splice(i, 1)
              if (callbacks.length === 0) {
                delete this.callbacks[event]
              }
            }
            return this
          }

          /// Emit `event` with the given args.
          // also calls any `*` handlers
          prototype.emit = function(event) {
            this.callbacks = this.callbacks || {}
            var args = [].slice.call(arguments, 1),
              callbacks = this.callbacks[event],
              specialCallbacks = this.getWildcardCallbacks(event),
              i,
              len,
              item,
              listeners

            if (callbacks) {
              listeners = callbacks.slice()
              for (i = 0, len = listeners.length; i < len; ++i) {
                if (!listeners[i]) {
                  break
                }
                listeners[i].apply(this, args)
              }
            }

            if (specialCallbacks) {
              len = specialCallbacks.length
              listeners = specialCallbacks.slice()
              for (i = 0, len = listeners.length; i < len; ++i) {
                if (!listeners[i]) {
                  break
                }
                listeners[i].apply(this, [event].concat(args))
              }
            }

            return this
          }

          // Helper for for finding special wildcard event handlers that match the event
          prototype.getWildcardCallbacks = function(eventName) {
            this.callbacks = this.callbacks || {}
            var item,
              split,
              result = []

            for (item in this.callbacks) {
              split = item.split('*')
              if (
                item === '*' ||
                (split.length === 2 &&
                  eventName.slice(0, split[0].length) === split[0])
              ) {
                result = result.concat(this.callbacks[item])
              }
            }
            return result
          }
        }

        WildEmitter.mixin(WildEmitter)
      },
      {},
    ],
    15: [
      function(require, module, exports) {
        /*!
         * EventEmitter v5.2.6 - git.io/ee
         * Unlicense - http://unlicense.org/
         * Oliver Caldwell - https://oli.me.uk/
         * @preserve
         */

        ;(function(exports) {
          'use strict'

          /**
           * Class for managing events.
           * Can be extended to provide event functionality in other classes.
           *
           * @class EventEmitter Manages event registering and emitting.
           */
          function EventEmitter() {}

          // Shortcuts to improve speed and size
          var proto = EventEmitter.prototype
          var originalGlobalValue = exports.EventEmitter

          /**
           * Finds the index of the listener for the event in its storage array.
           *
           * @param {Function[]} listeners Array of listeners to search through.
           * @param {Function} listener Method to look for.
           * @return {Number} Index of the specified listener, -1 if not found
           * @api private
           */
          function indexOfListener(listeners, listener) {
            var i = listeners.length
            while (i--) {
              if (listeners[i].listener === listener) {
                return i
              }
            }

            return -1
          }

          /**
           * Alias a method while keeping the context correct, to allow for overwriting of target method.
           *
           * @param {String} name The name of the target method.
           * @return {Function} The aliased method
           * @api private
           */
          function alias(name) {
            return function aliasClosure() {
              return this[name].apply(this, arguments)
            }
          }

          /**
           * Returns the listener array for the specified event.
           * Will initialise the event object and listener arrays if required.
           * Will return an object if you use a regex search. The object contains keys for each matched event. So /ba[rz]/ might return an object containing bar and baz. But only if you have either defined them with defineEvent or added some listeners to them.
           * Each property in the object response is an array of listener functions.
           *
           * @param {String|RegExp} evt Name of the event to return the listeners from.
           * @return {Function[]|Object} All listener functions for the event.
           */
          proto.getListeners = function getListeners(evt) {
            var events = this._getEvents()
            var response
            var key

            // Return a concatenated array of all matching events if
            // the selector is a regular expression.
            if (evt instanceof RegExp) {
              response = {}
              for (key in events) {
                if (events.hasOwnProperty(key) && evt.test(key)) {
                  response[key] = events[key]
                }
              }
            } else {
              response = events[evt] || (events[evt] = [])
            }

            return response
          }

          /**
           * Takes a list of listener objects and flattens it into a list of listener functions.
           *
           * @param {Object[]} listeners Raw listener objects.
           * @return {Function[]} Just the listener functions.
           */
          proto.flattenListeners = function flattenListeners(listeners) {
            var flatListeners = []
            var i

            for (i = 0; i < listeners.length; i += 1) {
              flatListeners.push(listeners[i].listener)
            }

            return flatListeners
          }

          /**
           * Fetches the requested listeners via getListeners but will always return the results inside an object. This is mainly for internal use but others may find it useful.
           *
           * @param {String|RegExp} evt Name of the event to return the listeners from.
           * @return {Object} All listener functions for an event in an object.
           */
          proto.getListenersAsObject = function getListenersAsObject(evt) {
            var listeners = this.getListeners(evt)
            var response

            if (listeners instanceof Array) {
              response = {}
              response[evt] = listeners
            }

            return response || listeners
          }

          function isValidListener(listener) {
            if (typeof listener === 'function' || listener instanceof RegExp) {
              return true
            } else if (listener && typeof listener === 'object') {
              return isValidListener(listener.listener)
            } else {
              return false
            }
          }

          /**
           * Adds a listener function to the specified event.
           * The listener will not be added if it is a duplicate.
           * If the listener returns true then it will be removed after it is called.
           * If you pass a regular expression as the event name then the listener will be added to all events that match it.
           *
           * @param {String|RegExp} evt Name of the event to attach the listener to.
           * @param {Function} listener Method to be called when the event is emitted. If the function returns true then it will be removed after calling.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.addListener = function addListener(evt, listener) {
            if (!isValidListener(listener)) {
              throw new TypeError('listener must be a function')
            }

            var listeners = this.getListenersAsObject(evt)
            var listenerIsWrapped = typeof listener === 'object'
            var key

            for (key in listeners) {
              if (
                listeners.hasOwnProperty(key) &&
                indexOfListener(listeners[key], listener) === -1
              ) {
                listeners[key].push(
                  listenerIsWrapped
                    ? listener
                    : {
                        listener: listener,
                        once: false,
                      },
                )
              }
            }

            return this
          }

          /**
           * Alias of addListener
           */
          proto.on = alias('addListener')

          /**
           * Semi-alias of addListener. It will add a listener that will be
           * automatically removed after its first execution.
           *
           * @param {String|RegExp} evt Name of the event to attach the listener to.
           * @param {Function} listener Method to be called when the event is emitted. If the function returns true then it will be removed after calling.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.addOnceListener = function addOnceListener(evt, listener) {
            return this.addListener(evt, {
              listener: listener,
              once: true,
            })
          }

          /**
           * Alias of addOnceListener.
           */
          proto.once = alias('addOnceListener')

          /**
           * Defines an event name. This is required if you want to use a regex to add a listener to multiple events at once. If you don't do this then how do you expect it to know what event to add to? Should it just add to every possible match for a regex? No. That is scary and bad.
           * You need to tell it what event names should be matched by a regex.
           *
           * @param {String} evt Name of the event to create.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.defineEvent = function defineEvent(evt) {
            this.getListeners(evt)
            return this
          }

          /**
           * Uses defineEvent to define multiple events.
           *
           * @param {String[]} evts An array of event names to define.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.defineEvents = function defineEvents(evts) {
            for (var i = 0; i < evts.length; i += 1) {
              this.defineEvent(evts[i])
            }
            return this
          }

          /**
           * Removes a listener function from the specified event.
           * When passed a regular expression as the event name, it will remove the listener from all events that match it.
           *
           * @param {String|RegExp} evt Name of the event to remove the listener from.
           * @param {Function} listener Method to remove from the event.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.removeListener = function removeListener(evt, listener) {
            var listeners = this.getListenersAsObject(evt)
            var index
            var key

            for (key in listeners) {
              if (listeners.hasOwnProperty(key)) {
                index = indexOfListener(listeners[key], listener)

                if (index !== -1) {
                  listeners[key].splice(index, 1)
                }
              }
            }

            return this
          }

          /**
           * Alias of removeListener
           */
          proto.off = alias('removeListener')

          /**
           * Adds listeners in bulk using the manipulateListeners method.
           * If you pass an object as the first argument you can add to multiple events at once. The object should contain key value pairs of events and listeners or listener arrays. You can also pass it an event name and an array of listeners to be added.
           * You can also pass it a regular expression to add the array of listeners to all events that match it.
           * Yeah, this function does quite a bit. That's probably a bad thing.
           *
           * @param {String|Object|RegExp} evt An event name if you will pass an array of listeners next. An object if you wish to add to multiple events at once.
           * @param {Function[]} [listeners] An optional array of listener functions to add.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.addListeners = function addListeners(evt, listeners) {
            // Pass through to manipulateListeners
            return this.manipulateListeners(false, evt, listeners)
          }

          /**
           * Removes listeners in bulk using the manipulateListeners method.
           * If you pass an object as the first argument you can remove from multiple events at once. The object should contain key value pairs of events and listeners or listener arrays.
           * You can also pass it an event name and an array of listeners to be removed.
           * You can also pass it a regular expression to remove the listeners from all events that match it.
           *
           * @param {String|Object|RegExp} evt An event name if you will pass an array of listeners next. An object if you wish to remove from multiple events at once.
           * @param {Function[]} [listeners] An optional array of listener functions to remove.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.removeListeners = function removeListeners(evt, listeners) {
            // Pass through to manipulateListeners
            return this.manipulateListeners(true, evt, listeners)
          }

          /**
           * Edits listeners in bulk. The addListeners and removeListeners methods both use this to do their job. You should really use those instead, this is a little lower level.
           * The first argument will determine if the listeners are removed (true) or added (false).
           * If you pass an object as the second argument you can add/remove from multiple events at once. The object should contain key value pairs of events and listeners or listener arrays.
           * You can also pass it an event name and an array of listeners to be added/removed.
           * You can also pass it a regular expression to manipulate the listeners of all events that match it.
           *
           * @param {Boolean} remove True if you want to remove listeners, false if you want to add.
           * @param {String|Object|RegExp} evt An event name if you will pass an array of listeners next. An object if you wish to add/remove from multiple events at once.
           * @param {Function[]} [listeners] An optional array of listener functions to add/remove.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.manipulateListeners = function manipulateListeners(
            remove,
            evt,
            listeners,
          ) {
            var i
            var value
            var single = remove ? this.removeListener : this.addListener
            var multiple = remove ? this.removeListeners : this.addListeners

            // If evt is an object then pass each of its properties to this method
            if (typeof evt === 'object' && !(evt instanceof RegExp)) {
              for (i in evt) {
                if (evt.hasOwnProperty(i) && (value = evt[i])) {
                  // Pass the single listener straight through to the singular method
                  if (typeof value === 'function') {
                    single.call(this, i, value)
                  } else {
                    // Otherwise pass back to the multiple function
                    multiple.call(this, i, value)
                  }
                }
              }
            } else {
              // So evt must be a string
              // And listeners must be an array of listeners
              // Loop over it and pass each one to the multiple method
              i = listeners.length
              while (i--) {
                single.call(this, evt, listeners[i])
              }
            }

            return this
          }

          /**
           * Removes all listeners from a specified event.
           * If you do not specify an event then all listeners will be removed.
           * That means every event will be emptied.
           * You can also pass a regex to remove all events that match it.
           *
           * @param {String|RegExp} [evt] Optional name of the event to remove all listeners for. Will remove from every event if not passed.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.removeEvent = function removeEvent(evt) {
            var type = typeof evt
            var events = this._getEvents()
            var key

            // Remove different things depending on the state of evt
            if (type === 'string') {
              // Remove all listeners for the specified event
              delete events[evt]
            } else if (evt instanceof RegExp) {
              // Remove all events matching the regex.
              for (key in events) {
                if (events.hasOwnProperty(key) && evt.test(key)) {
                  delete events[key]
                }
              }
            } else {
              // Remove all listeners in all events
              delete this._events
            }

            return this
          }

          /**
           * Alias of removeEvent.
           *
           * Added to mirror the node API.
           */
          proto.removeAllListeners = alias('removeEvent')

          /**
           * Emits an event of your choice.
           * When emitted, every listener attached to that event will be executed.
           * If you pass the optional argument array then those arguments will be passed to every listener upon execution.
           * Because it uses `apply`, your array of arguments will be passed as if you wrote them out separately.
           * So they will not arrive within the array on the other side, they will be separate.
           * You can also pass a regular expression to emit to all events that match it.
           *
           * @param {String|RegExp} evt Name of the event to emit and execute listeners for.
           * @param {Array} [args] Optional array of arguments to be passed to each listener.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.emitEvent = function emitEvent(evt, args) {
            var listenersMap = this.getListenersAsObject(evt)
            var listeners
            var listener
            var i
            var key
            var response

            for (key in listenersMap) {
              if (listenersMap.hasOwnProperty(key)) {
                listeners = listenersMap[key].slice(0)

                for (i = 0; i < listeners.length; i++) {
                  // If the listener returns true then it shall be removed from the event
                  // The function is executed either with a basic call or an apply if there is an args array
                  listener = listeners[i]

                  if (listener.once === true) {
                    this.removeListener(evt, listener.listener)
                  }

                  response = listener.listener.apply(this, args || [])

                  if (response === this._getOnceReturnValue()) {
                    this.removeListener(evt, listener.listener)
                  }
                }
              }
            }

            return this
          }

          /**
           * Alias of emitEvent
           */
          proto.trigger = alias('emitEvent')

          /**
           * Subtly different from emitEvent in that it will pass its arguments on to the listeners, as opposed to taking a single array of arguments to pass on.
           * As with emitEvent, you can pass a regex in place of the event name to emit to all events that match it.
           *
           * @param {String|RegExp} evt Name of the event to emit and execute listeners for.
           * @param {...*} Optional additional arguments to be passed to each listener.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.emit = function emit(evt) {
            var args = Array.prototype.slice.call(arguments, 1)
            return this.emitEvent(evt, args)
          }

          /**
           * Sets the current value to check against when executing listeners. If a
           * listeners return value matches the one set here then it will be removed
           * after execution. This value defaults to true.
           *
           * @param {*} value The new value to check for when executing listeners.
           * @return {Object} Current instance of EventEmitter for chaining.
           */
          proto.setOnceReturnValue = function setOnceReturnValue(value) {
            this._onceReturnValue = value
            return this
          }

          /**
           * Fetches the current value to check against when executing listeners. If
           * the listeners return value matches this one then it should be removed
           * automatically. It will return true by default.
           *
           * @return {*|Boolean} The current value to check for or the default, true.
           * @api private
           */
          proto._getOnceReturnValue = function _getOnceReturnValue() {
            if (this.hasOwnProperty('_onceReturnValue')) {
              return this._onceReturnValue
            } else {
              return true
            }
          }

          /**
           * Fetches the events object and creates one if required.
           *
           * @return {Object} The events storage object.
           * @api private
           */
          proto._getEvents = function _getEvents() {
            return this._events || (this._events = {})
          }

          /**
           * Reverts the global {@link EventEmitter} to its previous value and returns a reference to this version.
           *
           * @return {Function} Non conflicting EventEmitter class.
           */
          EventEmitter.noConflict = function noConflict() {
            exports.EventEmitter = originalGlobalValue
            return EventEmitter
          }

          // Expose the class either via AMD, CommonJS or the global object
          if (typeof define === 'function' && define.amd) {
            define(function() {
              return EventEmitter
            })
          } else if (typeof module === 'object' && module.exports) {
            module.exports = EventEmitter
          } else {
            exports.EventEmitter = EventEmitter
          }
        })(typeof window !== 'undefined' ? window : this || {})
      },
      {},
    ],
    16: [
      function(require, module, exports) {
        module.exports = {
          author: 'OpenVidu',
          dependencies: {
            '@types/node': '12.6.8',
            '@types/platform': '1.3.2',
            freeice: '2.2.2',
            hark: '1.2.3',
            platform: '1.3.5',
            uuid: '3.3.2',
            'wolfy87-eventemitter': '5.2.6',
          },
          description: 'OpenVidu Browser',
          devDependencies: {
            browserify: '16.3.0',
            grunt: '1.0.4',
            'grunt-autoprefixer': '3.0.4',
            'grunt-cli': '1.3.2',
            'grunt-contrib-copy': '1.0.0',
            'grunt-contrib-sass': '1.0.0',
            'grunt-contrib-uglify': '4.0.1',
            'grunt-contrib-watch': '1.1.0',
            'grunt-string-replace': '1.3.1',
            'grunt-ts': '6.0.0-beta.22',
            tsify: '4.0.1',
            tslint: '5.18.0',
            typedoc: '0.15.0',
            typescript: '3.5.3',
            'uglify-js': '3.6.0',
          },
          license: 'Apache-2.0',
          main: 'lib/index.js',
          name: 'openvidu-browser',
          repository: {
            type: 'git',
            url: 'git://github.com/OpenVidu/openvidu',
          },
          scripts: {
            browserify:
              'VERSION=${VERSION:-}; cd src && ../node_modules/browserify/bin/cmd.js Main.ts -p [ tsify ] --exclude kurento-browser-extensions --debug -o ../static/js/openvidu-browser-$VERSION.js -v',
            'browserify-prod':
              'VERSION=${VERSION:-}; cd src && ../node_modules/browserify/bin/cmd.js --debug Main.ts -p [ tsify ] --exclude kurento-browser-extensions | ../node_modules/uglify-js/bin/uglifyjs --source-map content=inline --output ../static/js/openvidu-browser-$VERSION.min.js',
            build:
              'cd src/OpenVidu && ./../../node_modules/typescript/bin/tsc && cd ../.. && ./node_modules/typescript/bin/tsc --declaration src/index.ts --outDir ./lib --sourceMap --lib dom,es5,es2015.promise,scripthost',
            docs:
              './node_modules/typedoc/bin/typedoc --options ./config/typedoc.js --out ./docs ./src && rm -rf ../../openvidu.io/api/openvidu-browser/* && cp -R ./docs/. ../../openvidu.io/api/openvidu-browser',
            test: 'echo "Error: no test specified" && exit 1',
          },
          types: 'lib/index.d.ts',
          version: '2.11.0',
        }
      },
      {},
    ],
    17: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var OpenVidu_1 = require('./OpenVidu/OpenVidu')
        if (window) {
          window['OpenVidu'] = OpenVidu_1.OpenVidu
        }
      },
      { './OpenVidu/OpenVidu': 21 },
    ],
    18: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var Stream_1 = require('./Stream')
        var Connection = (function() {
          function Connection(session, opts) {
            this.session = session
            this.disposed = false
            var msg = "'Connection' created "
            if (opts) {
              msg += "(remote) with 'connectionId' [" + opts.id + ']'
              this.options = opts
              this.connectionId = opts.id
              this.creationTime = opts.createdAt
              if (opts.metadata) {
                this.data = opts.metadata
              }
              if (opts.streams) {
                this.initRemoteStreams(opts.streams)
              }
            } else {
              msg += '(local)'
            }
            console.info(msg)
          }
          Connection.prototype.sendIceCandidate = function(candidate) {
            console.debug(
              (this.stream.outboundStreamOpts ? 'Local' : 'Remote') +
                'candidate for' +
                this.connectionId,
              candidate,
            )
            this.session.openvidu.sendRequest(
              'onIceCandidate',
              {
                endpointName: this.connectionId,
                candidate: candidate.candidate,
                sdpMid: candidate.sdpMid,
                sdpMLineIndex: candidate.sdpMLineIndex,
              },
              function(error, response) {
                if (error) {
                  console.error(
                    'Error sending ICE candidate: ' + JSON.stringify(error),
                  )
                }
              },
            )
          }
          Connection.prototype.initRemoteStreams = function(options) {
            var _this = this
            options.forEach(function(opts) {
              var streamOptions = {
                id: opts.id,
                createdAt: opts.createdAt,
                connection: _this,
                hasAudio: opts.hasAudio,
                hasVideo: opts.hasVideo,
                audioActive: opts.audioActive,
                videoActive: opts.videoActive,
                typeOfVideo: opts.typeOfVideo,
                frameRate: opts.frameRate,
                videoDimensions: opts.videoDimensions
                  ? JSON.parse(opts.videoDimensions)
                  : undefined,
                filter: opts.filter ? opts.filter : undefined,
              }
              var stream = new Stream_1.Stream(_this.session, streamOptions)
              _this.addStream(stream)
            })
            console.info(
              "Remote 'Connection' with 'connectionId' [" +
                this.connectionId +
                '] is now configured for receiving Streams with options: ',
              this.stream.inboundStreamOpts,
            )
          }
          Connection.prototype.addStream = function(stream) {
            stream.connection = this
            this.stream = stream
          }
          Connection.prototype.removeStream = function(streamId) {
            delete this.stream
          }
          Connection.prototype.dispose = function() {
            if (this.stream) {
              delete this.stream
            }
            this.disposed = true
          }
          return Connection
        })()
        exports.Connection = Connection
      },
      { './Stream': 24 },
    ],
    19: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var StreamPropertyChangedEvent_1 = require('../OpenViduInternal/Events/StreamPropertyChangedEvent')
        var OpenViduError_1 = require('../OpenViduInternal/Enums/OpenViduError')
        var Filter = (function() {
          function Filter(type, options) {
            this.handlers = {}
            this.type = type
            this.options = options
          }
          Filter.prototype.execMethod = function(method, params) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info(
                'Executing filter method to stream ' + _this.stream.streamId,
              )
              var stringParams
              if (typeof params !== 'string') {
                try {
                  stringParams = JSON.stringify(params)
                } catch (error) {
                  var errorMsg =
                    "'params' property must be a JSON formatted object"
                  console.error(errorMsg)
                  reject(errorMsg)
                }
              } else {
                stringParams = params
              }
              _this.stream.session.openvidu.sendRequest(
                'execFilterMethod',
                {
                  streamId: _this.stream.streamId,
                  method: method,
                  params: stringParams,
                },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error executing filter method for Stream ' +
                        _this.stream.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to execute a filter method",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    console.info(
                      'Filter method successfully executed on Stream ' +
                        _this.stream.streamId,
                    )
                    var oldValue = Object.assign({}, _this.stream.filter)
                    _this.stream.filter.lastExecMethod = {
                      method: method,
                      params: JSON.parse(stringParams),
                    }
                    _this.stream.session.emitEvent('streamPropertyChanged', [
                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                        _this.stream.session,
                        _this.stream,
                        'filter',
                        _this.stream.filter,
                        oldValue,
                        'execFilterMethod',
                      ),
                    ])
                    _this.stream.streamManager.emitEvent(
                      'streamPropertyChanged',
                      [
                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                          _this.stream.streamManager,
                          _this.stream,
                          'filter',
                          _this.stream.filter,
                          oldValue,
                          'execFilterMethod',
                        ),
                      ],
                    )
                    resolve()
                  }
                },
              )
            })
          }
          Filter.prototype.addEventListener = function(eventType, handler) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info(
                'Adding filter event listener to event ' +
                  eventType +
                  ' to stream ' +
                  _this.stream.streamId,
              )
              _this.stream.session.openvidu.sendRequest(
                'addFilterEventListener',
                { streamId: _this.stream.streamId, eventType: eventType },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error adding filter event listener to event ' +
                        eventType +
                        'for Stream ' +
                        _this.stream.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to add a filter event listener",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    _this.handlers[eventType] = handler
                    console.info(
                      'Filter event listener to event ' +
                        eventType +
                        ' successfully applied on Stream ' +
                        _this.stream.streamId,
                    )
                    resolve()
                  }
                },
              )
            })
          }
          Filter.prototype.removeEventListener = function(eventType) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info(
                'Removing filter event listener to event ' +
                  eventType +
                  ' to stream ' +
                  _this.stream.streamId,
              )
              _this.stream.session.openvidu.sendRequest(
                'removeFilterEventListener',
                { streamId: _this.stream.streamId, eventType: eventType },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error removing filter event listener to event ' +
                        eventType +
                        'for Stream ' +
                        _this.stream.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to add a filter event listener",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    delete _this.handlers[eventType]
                    console.info(
                      'Filter event listener to event ' +
                        eventType +
                        ' successfully removed on Stream ' +
                        _this.stream.streamId,
                    )
                    resolve()
                  }
                },
              )
            })
          }
          return Filter
        })()
        exports.Filter = Filter
      },
      {
        '../OpenViduInternal/Enums/OpenViduError': 28,
        '../OpenViduInternal/Events/StreamPropertyChangedEvent': 39,
      },
    ],
    20: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var LocalRecorderState_1 = require('../OpenViduInternal/Enums/LocalRecorderState')
        var platform = require('platform')
        var LocalRecorder = (function() {
          function LocalRecorder(stream) {
            this.stream = stream
            this.chunks = []
            this.connectionId = this.stream.connection
              ? this.stream.connection.connectionId
              : 'default-connection'
            this.id =
              this.stream.streamId + '_' + this.connectionId + '_localrecord'
            this.state = LocalRecorderState_1.LocalRecorderState.READY
          }
          LocalRecorder.prototype.record = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              try {
                if (typeof MediaRecorder === 'undefined') {
                  console.error(
                    'MediaRecorder not supported on your browser. See compatibility in https://caniuse.com/#search=MediaRecorder',
                  )
                  throw Error(
                    'MediaRecorder not supported on your browser. See compatibility in https://caniuse.com/#search=MediaRecorder',
                  )
                }
                if (
                  _this.state !== LocalRecorderState_1.LocalRecorderState.READY
                ) {
                  throw Error(
                    "'LocalRecord.record()' needs 'LocalRecord.state' to be 'READY' (current value: '" +
                      _this.state +
                      "'). Call 'LocalRecorder.clean()' or init a new LocalRecorder before",
                  )
                }
                console.log(
                  "Starting local recording of stream '" +
                    _this.stream.streamId +
                    "' of connection '" +
                    _this.connectionId +
                    "'",
                )
                if (typeof MediaRecorder.isTypeSupported === 'function') {
                  var options = void 0
                  if (MediaRecorder.isTypeSupported('video/webm;codecs=vp9')) {
                    options = { mimeType: 'video/webm;codecs=vp9' }
                  } else if (
                    MediaRecorder.isTypeSupported('video/webm;codecs=h264')
                  ) {
                    options = { mimeType: 'video/webm;codecs=h264' }
                  } else if (
                    MediaRecorder.isTypeSupported('video/webm;codecs=vp8')
                  ) {
                    options = { mimeType: 'video/webm;codecs=vp8' }
                  }
                  console.log('Using mimeType ' + options.mimeType)
                  _this.mediaRecorder = new MediaRecorder(
                    _this.stream.getMediaStream(),
                    options,
                  )
                } else {
                  console.warn(
                    'isTypeSupported is not supported, using default codecs for browser',
                  )
                  _this.mediaRecorder = new MediaRecorder(
                    _this.stream.getMediaStream(),
                  )
                }
                _this.mediaRecorder.start(10)
              } catch (err) {
                reject(err)
              }
              _this.mediaRecorder.ondataavailable = function(e) {
                _this.chunks.push(e.data)
              }
              _this.mediaRecorder.onerror = function(e) {
                console.error('MediaRecorder error: ', e)
              }
              _this.mediaRecorder.onstart = function() {
                console.log(
                  'MediaRecorder started (state=' +
                    _this.mediaRecorder.state +
                    ')',
                )
              }
              _this.mediaRecorder.onstop = function() {
                _this.onStopDefault()
              }
              _this.mediaRecorder.onpause = function() {
                console.log(
                  'MediaRecorder paused (state=' +
                    _this.mediaRecorder.state +
                    ')',
                )
              }
              _this.mediaRecorder.onresume = function() {
                console.log(
                  'MediaRecorder resumed (state=' +
                    _this.mediaRecorder.state +
                    ')',
                )
              }
              _this.mediaRecorder.onwarning = function(e) {
                console.log('MediaRecorder warning: ' + e)
              }
              _this.state = LocalRecorderState_1.LocalRecorderState.RECORDING
              resolve()
            })
          }
          LocalRecorder.prototype.stop = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              try {
                if (
                  _this.state ===
                    LocalRecorderState_1.LocalRecorderState.READY ||
                  _this.state ===
                    LocalRecorderState_1.LocalRecorderState.FINISHED
                ) {
                  throw Error(
                    "'LocalRecord.stop()' needs 'LocalRecord.state' to be 'RECORDING' or 'PAUSED' (current value: '" +
                      _this.state +
                      "'). Call 'LocalRecorder.start()' before",
                  )
                }
                _this.mediaRecorder.onstop = function() {
                  _this.onStopDefault()
                  resolve()
                }
                _this.mediaRecorder.stop()
              } catch (e) {
                reject(e)
              }
            })
          }
          LocalRecorder.prototype.pause = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              try {
                if (
                  _this.state !==
                  LocalRecorderState_1.LocalRecorderState.RECORDING
                ) {
                  reject(
                    Error(
                      "'LocalRecord.pause()' needs 'LocalRecord.state' to be 'RECORDING' (current value: '" +
                        _this.state +
                        "'). Call 'LocalRecorder.start()' or 'LocalRecorder.resume()' before",
                    ),
                  )
                }
                _this.mediaRecorder.pause()
                _this.state = LocalRecorderState_1.LocalRecorderState.PAUSED
              } catch (error) {
                reject(error)
              }
            })
          }
          LocalRecorder.prototype.resume = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              try {
                if (
                  _this.state !== LocalRecorderState_1.LocalRecorderState.PAUSED
                ) {
                  throw Error(
                    "'LocalRecord.resume()' needs 'LocalRecord.state' to be 'PAUSED' (current value: '" +
                      _this.state +
                      "'). Call 'LocalRecorder.pause()' before",
                  )
                }
                _this.mediaRecorder.resume()
                _this.state = LocalRecorderState_1.LocalRecorderState.RECORDING
              } catch (error) {
                reject(error)
              }
            })
          }
          LocalRecorder.prototype.preview = function(parentElement) {
            if (
              this.state !== LocalRecorderState_1.LocalRecorderState.FINISHED
            ) {
              throw Error(
                "'LocalRecord.preview()' needs 'LocalRecord.state' to be 'FINISHED' (current value: '" +
                  this.state +
                  "'). Call 'LocalRecorder.stop()' before",
              )
            }
            this.videoPreview = document.createElement('video')
            this.videoPreview.id = this.id
            this.videoPreview.autoplay = true
            if (platform.name === 'Safari') {
              this.videoPreview.setAttribute('playsinline', 'true')
            }
            if (typeof parentElement === 'string') {
              var parentElementDom = document.getElementById(parentElement)
              if (parentElementDom) {
                this.videoPreview = parentElementDom.appendChild(
                  this.videoPreview,
                )
              }
            } else {
              this.videoPreview = parentElement.appendChild(this.videoPreview)
            }
            this.videoPreview.src = this.videoPreviewSrc
            return this.videoPreview
          }
          LocalRecorder.prototype.clean = function() {
            var _this = this
            var f = function() {
              delete _this.blob
              _this.chunks = []
              delete _this.mediaRecorder
              _this.state = LocalRecorderState_1.LocalRecorderState.READY
            }
            if (
              this.state ===
                LocalRecorderState_1.LocalRecorderState.RECORDING ||
              this.state === LocalRecorderState_1.LocalRecorderState.PAUSED
            ) {
              this.stop()
                .then(function() {
                  return f()
                })
                .catch(function() {
                  return f()
                })
            } else {
              f()
            }
          }
          LocalRecorder.prototype.download = function() {
            if (
              this.state !== LocalRecorderState_1.LocalRecorderState.FINISHED
            ) {
              throw Error(
                "'LocalRecord.download()' needs 'LocalRecord.state' to be 'FINISHED' (current value: '" +
                  this.state +
                  "'). Call 'LocalRecorder.stop()' before",
              )
            } else {
              var a = document.createElement('a')
              a.style.display = 'none'
              document.body.appendChild(a)
              var url = window.URL.createObjectURL(this.blob)
              a.href = url
              a.download = this.id + '.webm'
              a.click()
              window.URL.revokeObjectURL(url)
              document.body.removeChild(a)
            }
          }
          LocalRecorder.prototype.getBlob = function() {
            if (
              this.state !== LocalRecorderState_1.LocalRecorderState.FINISHED
            ) {
              throw Error("Call 'LocalRecord.stop()' before getting Blob file")
            } else {
              return this.blob
            }
          }
          LocalRecorder.prototype.uploadAsBinary = function(endpoint, headers) {
            var _this = this
            return new Promise(function(resolve, reject) {
              if (
                _this.state !== LocalRecorderState_1.LocalRecorderState.FINISHED
              ) {
                reject(
                  Error(
                    "'LocalRecord.uploadAsBinary()' needs 'LocalRecord.state' to be 'FINISHED' (current value: '" +
                      _this.state +
                      "'). Call 'LocalRecorder.stop()' before",
                  ),
                )
              } else {
                var http_1 = new XMLHttpRequest()
                http_1.open('POST', endpoint, true)
                if (typeof headers === 'object') {
                  for (
                    var _i = 0, _a = Object.keys(headers);
                    _i < _a.length;
                    _i++
                  ) {
                    var key = _a[_i]
                    http_1.setRequestHeader(key, headers[key])
                  }
                }
                http_1.onreadystatechange = function() {
                  if (http_1.readyState === 4) {
                    if (http_1.status.toString().charAt(0) === '2') {
                      resolve(http_1.responseText)
                    } else {
                      reject(http_1.status)
                    }
                  }
                }
                http_1.send(_this.blob)
              }
            })
          }
          LocalRecorder.prototype.uploadAsMultipartfile = function(
            endpoint,
            headers,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              if (
                _this.state !== LocalRecorderState_1.LocalRecorderState.FINISHED
              ) {
                reject(
                  Error(
                    "'LocalRecord.uploadAsMultipartfile()' needs 'LocalRecord.state' to be 'FINISHED' (current value: '" +
                      _this.state +
                      "'). Call 'LocalRecorder.stop()' before",
                  ),
                )
              } else {
                var http_2 = new XMLHttpRequest()
                http_2.open('POST', endpoint, true)
                if (typeof headers === 'object') {
                  for (
                    var _i = 0, _a = Object.keys(headers);
                    _i < _a.length;
                    _i++
                  ) {
                    var key = _a[_i]
                    http_2.setRequestHeader(key, headers[key])
                  }
                }
                var sendable = new FormData()
                sendable.append('file', _this.blob, _this.id + '.webm')
                http_2.onreadystatechange = function() {
                  if (http_2.readyState === 4) {
                    if (http_2.status.toString().charAt(0) === '2') {
                      resolve(http_2.responseText)
                    } else {
                      reject(http_2.status)
                    }
                  }
                }
                http_2.send(sendable)
              }
            })
          }
          LocalRecorder.prototype.onStopDefault = function() {
            console.log(
              'MediaRecorder stopped  (state=' + this.mediaRecorder.state + ')',
            )
            this.blob = new Blob(this.chunks, { type: 'video/webm' })
            this.chunks = []
            this.videoPreviewSrc = window.URL.createObjectURL(this.blob)
            this.state = LocalRecorderState_1.LocalRecorderState.FINISHED
          }
          return LocalRecorder
        })()
        exports.LocalRecorder = LocalRecorder
      },
      { '../OpenViduInternal/Enums/LocalRecorderState': 27, platform: 8 },
    ],
    21: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var LocalRecorder_1 = require('./LocalRecorder')
        var Publisher_1 = require('./Publisher')
        var Session_1 = require('./Session')
        var StreamPropertyChangedEvent_1 = require('../OpenViduInternal/Events/StreamPropertyChangedEvent')
        var OpenViduError_1 = require('../OpenViduInternal/Enums/OpenViduError')
        var VideoInsertMode_1 = require('../OpenViduInternal/Enums/VideoInsertMode')
        var screenSharingAuto = require('../OpenViduInternal/ScreenSharing/Screen-Capturing-Auto')
        var screenSharing = require('../OpenViduInternal/ScreenSharing/Screen-Capturing')
        var EventEmitter = require('wolfy87-eventemitter')
        var RpcBuilder = require('../OpenViduInternal/KurentoUtils/kurento-jsonrpc')
        var platform = require('platform')
        platform['isIonicIos'] =
          (platform.product === 'iPhone' || platform.product === 'iPad') &&
          platform.ua.indexOf('Safari') === -1
        platform['isIonicAndroid'] =
          platform.os.family === 'Android' && platform.name == 'Android Browser'
        var packageJson = require('../../package.json')
        var OpenVidu = (function() {
          function OpenVidu() {
            var _this = this
            this.publishers = []
            this.secret = ''
            this.recorder = false
            this.advancedConfiguration = {}
            this.webrtcStatsInterval = 0
            this.ee = new EventEmitter()
            this.libraryVersion = packageJson.version
            console.info("'OpenVidu' initialized")
            console.info('openvidu-browser version: ' + this.libraryVersion)
            if (
              platform.os.family === 'iOS' ||
              platform.os.family === 'Android'
            ) {
              window.addEventListener('orientationchange', function() {
                _this.publishers.forEach(function(publisher) {
                  if (
                    publisher.stream.isLocalStreamPublished &&
                    !!publisher.stream &&
                    !!publisher.stream.hasVideo &&
                    !!publisher.stream.streamManager.videos[0]
                  ) {
                    var attempts_1 = 0
                    var oldWidth_1 = publisher.stream.videoDimensions.width
                    var oldHeight_1 = publisher.stream.videoDimensions.height
                    var getNewVideoDimensions_1 = function() {
                      return new Promise(function(resolve, reject) {
                        if (platform['isIonicIos']) {
                          resolve({
                            newWidth:
                              publisher.stream.streamManager.videos[0].video
                                .videoWidth,
                            newHeight:
                              publisher.stream.streamManager.videos[0].video
                                .videoHeight,
                          })
                        } else {
                          var firefoxSettings = publisher.stream
                            .getMediaStream()
                            .getVideoTracks()[0]
                            .getSettings()
                          var newWidth =
                            platform.name.toLowerCase().indexOf('firefox') !==
                            -1
                              ? firefoxSettings.width
                              : publisher.videoReference.videoWidth
                          var newHeight =
                            platform.name.toLowerCase().indexOf('firefox') !==
                            -1
                              ? firefoxSettings.height
                              : publisher.videoReference.videoHeight
                          resolve({ newWidth: newWidth, newHeight: newHeight })
                        }
                      })
                    }
                    var repeatUntilChange_1 = setInterval(function() {
                      getNewVideoDimensions_1().then(function(newDimensions) {
                        sendStreamPropertyChangedEvent_1(
                          oldWidth_1,
                          oldHeight_1,
                          newDimensions.newWidth,
                          newDimensions.newHeight,
                        )
                      })
                    }, 75)
                    var sendStreamPropertyChangedEvent_1 = function(
                      oldWidth,
                      oldHeight,
                      newWidth,
                      newHeight,
                    ) {
                      attempts_1++
                      if (attempts_1 > 10) {
                        clearTimeout(repeatUntilChange_1)
                      }
                      if (newWidth !== oldWidth || newHeight !== oldHeight) {
                        publisher.stream.videoDimensions = {
                          width: newWidth || 0,
                          height: newHeight || 0,
                        }
                        _this.sendRequest(
                          'streamPropertyChanged',
                          {
                            streamId: publisher.stream.streamId,
                            property: 'videoDimensions',
                            newValue: JSON.stringify(
                              publisher.stream.videoDimensions,
                            ),
                            reason: 'deviceRotated',
                          },
                          function(error, response) {
                            if (error) {
                              console.error(
                                "Error sending 'streamPropertyChanged' event",
                                error,
                              )
                            } else {
                              _this.session.emitEvent('streamPropertyChanged', [
                                new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                                  _this.session,
                                  publisher.stream,
                                  'videoDimensions',
                                  publisher.stream.videoDimensions,
                                  { width: oldWidth, height: oldHeight },
                                  'deviceRotated',
                                ),
                              ])
                              publisher.emitEvent('streamPropertyChanged', [
                                new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                                  publisher,
                                  publisher.stream,
                                  'videoDimensions',
                                  publisher.stream.videoDimensions,
                                  { width: oldWidth, height: oldHeight },
                                  'deviceRotated',
                                ),
                              ])
                            }
                          },
                        )
                        clearTimeout(repeatUntilChange_1)
                      }
                    }
                  }
                })
              })
            }
          }
          OpenVidu.prototype.initSession = function() {
            this.session = new Session_1.Session(this)
            return this.session
          }
          OpenVidu.prototype.initPublisher = function(
            targetElement,
            param2,
            param3,
          ) {
            var properties
            if (!!param2 && typeof param2 !== 'function') {
              properties = param2
              properties = {
                audioSource:
                  typeof properties.audioSource !== 'undefined'
                    ? properties.audioSource
                    : undefined,
                frameRate:
                  typeof MediaStreamTrack !== 'undefined' &&
                  properties.videoSource instanceof MediaStreamTrack
                    ? undefined
                    : typeof properties.frameRate !== 'undefined'
                    ? properties.frameRate
                    : undefined,
                insertMode:
                  typeof properties.insertMode !== 'undefined'
                    ? typeof properties.insertMode === 'string'
                      ? VideoInsertMode_1.VideoInsertMode[properties.insertMode]
                      : properties.insertMode
                    : VideoInsertMode_1.VideoInsertMode.APPEND,
                mirror:
                  typeof properties.mirror !== 'undefined'
                    ? properties.mirror
                    : true,
                publishAudio:
                  typeof properties.publishAudio !== 'undefined'
                    ? properties.publishAudio
                    : true,
                publishVideo:
                  typeof properties.publishVideo !== 'undefined'
                    ? properties.publishVideo
                    : true,
                resolution:
                  typeof MediaStreamTrack !== 'undefined' &&
                  properties.videoSource instanceof MediaStreamTrack
                    ? undefined
                    : typeof properties.resolution !== 'undefined'
                    ? properties.resolution
                    : '640x480',
                videoSource:
                  typeof properties.videoSource !== 'undefined'
                    ? properties.videoSource
                    : undefined,
                filter: properties.filter,
              }
            } else {
              properties = {
                insertMode: VideoInsertMode_1.VideoInsertMode.APPEND,
                mirror: true,
                publishAudio: true,
                publishVideo: true,
                resolution: '640x480',
              }
            }
            var publisher = new Publisher_1.Publisher(
              targetElement,
              properties,
              this,
            )
            var completionHandler
            if (!!param2 && typeof param2 === 'function') {
              completionHandler = param2
            } else if (param3) {
              completionHandler = param3
            }
            publisher
              .initialize()
              .then(function() {
                if (completionHandler !== undefined) {
                  completionHandler(undefined)
                }
                publisher.emitEvent('accessAllowed', [])
              })
              .catch(function(error) {
                if (completionHandler !== undefined) {
                  completionHandler(error)
                }
                publisher.emitEvent('accessDenied', [error])
              })
            this.publishers.push(publisher)
            return publisher
          }
          OpenVidu.prototype.initPublisherAsync = function(
            targetElement,
            properties,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var publisher
              var callback = function(error) {
                if (error) {
                  reject(error)
                } else {
                  resolve(publisher)
                }
              }
              if (properties) {
                publisher = _this.initPublisher(
                  targetElement,
                  properties,
                  callback,
                )
              } else {
                publisher = _this.initPublisher(targetElement, callback)
              }
            })
          }
          OpenVidu.prototype.initLocalRecorder = function(stream) {
            return new LocalRecorder_1.LocalRecorder(stream)
          }
          OpenVidu.prototype.checkSystemRequirements = function() {
            var browser = platform.name
            var family = platform.os.family
            var userAgent = platform.ua ? platform.ua : navigator.userAgent
            if (
              family === 'iOS' &&
              (browser !== 'Safari' ||
                userAgent.indexOf('CriOS') !== -1 ||
                userAgent.indexOf('FxiOS') !== -1)
            ) {
              return 0
            }
            if (
              browser !== 'Safari' &&
              browser !== 'Chrome' &&
              browser !== 'Chrome Mobile' &&
              browser !== 'Firefox' &&
              browser !== 'Firefox Mobile' &&
              browser !== 'Opera' &&
              browser !== 'Opera Mobile' &&
              browser !== 'Android Browser' &&
              browser !== 'Electron'
            ) {
              return 0
            } else {
              return 1
            }
          }
          OpenVidu.prototype.checkScreenSharingCapabilities = function() {
            var browser = platform.name
            var family = platform.os.family
            if (family === 'iOS' || family === 'Android') {
              return 0
            }
            if (
              browser !== 'Chrome' &&
              browser !== 'Firefox' &&
              browser !== 'Opera' &&
              browser !== 'Electron'
            ) {
              return 0
            } else {
              return 1
            }
          }
          OpenVidu.prototype.getDevices = function() {
            return new Promise(function(resolve, reject) {
              navigator.mediaDevices
                .enumerateDevices()
                .then(function(deviceInfos) {
                  var devices = []
                  if (
                    platform['isIonicAndroid'] &&
                    cordova.plugins &&
                    cordova.plugins.EnumerateDevicesPlugin
                  ) {
                    cordova.plugins.EnumerateDevicesPlugin.getEnumerateDevices().then(
                      function(pluginDevices) {
                        var pluginAudioDevices = []
                        var videoDevices = []
                        var audioDevices = []
                        pluginAudioDevices = pluginDevices.filter(function(
                          device,
                        ) {
                          return device.kind === 'audioinput'
                        })
                        videoDevices = deviceInfos.filter(function(device) {
                          return device.kind === 'videoinput'
                        })
                        audioDevices = deviceInfos.filter(function(device) {
                          return device.kind === 'audioinput'
                        })
                        videoDevices.forEach(function(deviceInfo, index) {
                          if (!deviceInfo.label) {
                            var label = ''
                            if (index === 0) {
                              label = 'Front Camera'
                            } else if (index === 1) {
                              label = 'Back Camera'
                            } else {
                              label = 'Unknown Camera'
                            }
                            devices.push({
                              kind: deviceInfo.kind,
                              deviceId: deviceInfo.deviceId,
                              label: label,
                            })
                          } else {
                            devices.push({
                              kind: deviceInfo.kind,
                              deviceId: deviceInfo.deviceId,
                              label: deviceInfo.label,
                            })
                          }
                        })
                        audioDevices.forEach(function(deviceInfo, index) {
                          if (!deviceInfo.label) {
                            var label = ''
                            switch (index) {
                              case 0:
                                label = 'Default'
                                break
                              case 1:
                                var defaultMatch = pluginAudioDevices.filter(
                                  function(d) {
                                    return d.label.includes('Built')
                                  },
                                )[0]
                                label = defaultMatch
                                  ? defaultMatch.label
                                  : 'Built-in Microphone'
                                break
                              case 2:
                                var wiredMatch = pluginAudioDevices.filter(
                                  function(d) {
                                    return d.label.includes('Wired')
                                  },
                                )[0]
                                if (wiredMatch) {
                                  label = wiredMatch.label
                                } else {
                                  label = 'Headset earpiece'
                                }
                                break
                              case 3:
                                var wirelessMatch = pluginAudioDevices.filter(
                                  function(d) {
                                    return d.label.includes('Bluetooth')
                                  },
                                )[0]
                                label = wirelessMatch
                                  ? wirelessMatch.label
                                  : 'Wireless'
                                break
                              default:
                                label = 'Unknown Microphone'
                                break
                            }
                            devices.push({
                              kind: deviceInfo.kind,
                              deviceId: deviceInfo.deviceId,
                              label: label,
                            })
                          } else {
                            devices.push({
                              kind: deviceInfo.kind,
                              deviceId: deviceInfo.deviceId,
                              label: deviceInfo.label,
                            })
                          }
                        })
                        resolve(devices)
                      },
                    )
                  } else {
                    deviceInfos.forEach(function(deviceInfo) {
                      if (
                        deviceInfo.kind === 'audioinput' ||
                        deviceInfo.kind === 'videoinput'
                      ) {
                        devices.push({
                          kind: deviceInfo.kind,
                          deviceId: deviceInfo.deviceId,
                          label: deviceInfo.label,
                        })
                      }
                    })
                    resolve(devices)
                  }
                })
                .catch(function(error) {
                  console.error('Error getting devices', error)
                  reject(error)
                })
            })
          }
          OpenVidu.prototype.getUserMedia = function(options) {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this
                .generateMediaConstraints(options)
                .then(function(constraints) {
                  navigator.mediaDevices
                    .getUserMedia(constraints)
                    .then(function(mediaStream) {
                      resolve(mediaStream)
                    })
                    .catch(function(error) {
                      var errorName
                      var errorMessage = error.toString()
                      if (!(options.videoSource === 'screen')) {
                        errorName =
                          OpenViduError_1.OpenViduErrorName.DEVICE_ACCESS_DENIED
                      } else {
                        errorName =
                          OpenViduError_1.OpenViduErrorName
                            .SCREEN_CAPTURE_DENIED
                      }
                      reject(
                        new OpenViduError_1.OpenViduError(
                          errorName,
                          errorMessage,
                        ),
                      )
                    })
                })
                .catch(function(error) {
                  reject(error)
                })
            })
          }
          OpenVidu.prototype.enableProdMode = function() {
            console.log = function() {}
            console.debug = function() {}
            console.info = function() {}
            console.warn = function() {}
          }
          OpenVidu.prototype.setAdvancedConfiguration = function(
            configuration,
          ) {
            this.advancedConfiguration = configuration
          }
          OpenVidu.prototype.generateMediaConstraints = function(
            publisherProperties,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var audio, video
              if (
                publisherProperties.audioSource === null ||
                publisherProperties.audioSource === false
              ) {
                audio = false
              } else if (publisherProperties.audioSource === undefined) {
                audio = true
              } else {
                audio = publisherProperties.audioSource
              }
              if (
                publisherProperties.videoSource === null ||
                publisherProperties.videoSource === false
              ) {
                video = false
              } else {
                video = {
                  height: {
                    ideal: 480,
                  },
                  width: {
                    ideal: 640,
                  },
                }
              }
              var mediaConstraints = {
                audio: audio,
                video: video,
              }
              if (typeof mediaConstraints.audio === 'string') {
                mediaConstraints.audio = {
                  deviceId: { exact: mediaConstraints.audio },
                }
              }
              if (mediaConstraints.video) {
                if (publisherProperties.resolution) {
                  var widthAndHeight = publisherProperties.resolution
                    .toLowerCase()
                    .split('x')
                  var width = Number(widthAndHeight[0])
                  var height = Number(widthAndHeight[1])
                  mediaConstraints.video.width.ideal = width
                  mediaConstraints.video.height.ideal = height
                }
                if (publisherProperties.frameRate) {
                  mediaConstraints.video.frameRate = {
                    ideal: publisherProperties.frameRate,
                  }
                }
                if (
                  !!publisherProperties.videoSource &&
                  typeof publisherProperties.videoSource === 'string'
                ) {
                  if (
                    publisherProperties.videoSource === 'screen' ||
                    (platform.name.indexOf('Firefox') !== -1 &&
                      publisherProperties.videoSource === 'window') ||
                    (platform.name === 'Electron' &&
                      publisherProperties.videoSource.startsWith('screen:'))
                  ) {
                    if (!_this.checkScreenSharingCapabilities()) {
                      var error = new OpenViduError_1.OpenViduError(
                        OpenViduError_1.OpenViduErrorName.SCREEN_SHARING_NOT_SUPPORTED,
                        'You can only screen share in desktop Chrome, Firefox, Opera or Electron. Detected client: ' +
                          platform.name,
                      )
                      console.error(error)
                      reject(error)
                    } else {
                      if (platform.name === 'Electron') {
                        var prefix = 'screen:'
                        var videoSourceString = publisherProperties.videoSource
                        var electronScreenId = videoSourceString.substr(
                          videoSourceString.indexOf(prefix) + prefix.length,
                        )
                        mediaConstraints['video'] = {
                          mandatory: {
                            chromeMediaSource: 'desktop',
                            chromeMediaSourceId: electronScreenId,
                          },
                        }
                        resolve(mediaConstraints)
                      } else {
                        if (
                          !!_this.advancedConfiguration
                            .screenShareChromeExtension &&
                          !(platform.name.indexOf('Firefox') !== -1) &&
                          !navigator.mediaDevices['getDisplayMedia']
                        ) {
                          screenSharing.getScreenConstraints(function(
                            error,
                            screenConstraints,
                          ) {
                            if (
                              !!error ||
                              (!!screenConstraints.mandatory &&
                                screenConstraints.mandatory
                                  .chromeMediaSource === 'screen')
                            ) {
                              if (
                                error === 'permission-denied' ||
                                error === 'PermissionDeniedError'
                              ) {
                                var error_1 = new OpenViduError_1.OpenViduError(
                                  OpenViduError_1.OpenViduErrorName.SCREEN_CAPTURE_DENIED,
                                  'You must allow access to one window of your desktop',
                                )
                                console.error(error_1)
                                reject(error_1)
                              } else {
                                var extensionId = _this.advancedConfiguration.screenShareChromeExtension
                                  .split('/')
                                  .pop()
                                  .trim()
                                screenSharing.getChromeExtensionStatus(
                                  extensionId,
                                  function(status) {
                                    if (status === 'installed-disabled') {
                                      var error_2 = new OpenViduError_1.OpenViduError(
                                        OpenViduError_1.OpenViduErrorName.SCREEN_EXTENSION_DISABLED,
                                        'You must enable the screen extension',
                                      )
                                      console.error(error_2)
                                      reject(error_2)
                                    }
                                    if (status === 'not-installed') {
                                      var error_3 = new OpenViduError_1.OpenViduError(
                                        OpenViduError_1.OpenViduErrorName.SCREEN_EXTENSION_NOT_INSTALLED,
                                        _this.advancedConfiguration.screenShareChromeExtension,
                                      )
                                      console.error(error_3)
                                      reject(error_3)
                                    }
                                  },
                                )
                              }
                            } else {
                              mediaConstraints.video = screenConstraints
                              resolve(mediaConstraints)
                            }
                          })
                        } else {
                          if (navigator.mediaDevices['getDisplayMedia']) {
                            resolve(mediaConstraints)
                          } else {
                            var firefoxString =
                              platform.name.indexOf('Firefox') !== -1
                                ? publisherProperties.videoSource
                                : undefined
                            screenSharingAuto.getScreenId(
                              firefoxString,
                              function(error, sourceId, screenConstraints) {
                                if (error) {
                                  if (error === 'not-installed') {
                                    var extensionUrl = _this
                                      .advancedConfiguration
                                      .screenShareChromeExtension
                                      ? _this.advancedConfiguration
                                          .screenShareChromeExtension
                                      : 'https://chrome.google.com/webstore/detail/openvidu-screensharing/lfcgfepafnobdloecchnfaclibenjold'
                                    var error_4 = new OpenViduError_1.OpenViduError(
                                      OpenViduError_1.OpenViduErrorName.SCREEN_EXTENSION_NOT_INSTALLED,
                                      extensionUrl,
                                    )
                                    console.error(error_4)
                                    reject(error_4)
                                  } else if (error === 'installed-disabled') {
                                    var error_5 = new OpenViduError_1.OpenViduError(
                                      OpenViduError_1.OpenViduErrorName.SCREEN_EXTENSION_DISABLED,
                                      'You must enable the screen extension',
                                    )
                                    console.error(error_5)
                                    reject(error_5)
                                  } else if (error === 'permission-denied') {
                                    var error_6 = new OpenViduError_1.OpenViduError(
                                      OpenViduError_1.OpenViduErrorName.SCREEN_CAPTURE_DENIED,
                                      'You must allow access to one window of your desktop',
                                    )
                                    console.error(error_6)
                                    reject(error_6)
                                  }
                                } else {
                                  mediaConstraints.video =
                                    screenConstraints.video
                                  resolve(mediaConstraints)
                                }
                              },
                            )
                          }
                        }
                        publisherProperties.videoSource = 'screen'
                      }
                    }
                  } else {
                    mediaConstraints.video['deviceId'] = {
                      exact: publisherProperties.videoSource,
                    }
                    resolve(mediaConstraints)
                  }
                } else {
                  resolve(mediaConstraints)
                }
              } else {
                resolve(mediaConstraints)
              }
            })
          }
          OpenVidu.prototype.startWs = function(onConnectSucces) {
            var config = {
              heartbeat: 5000,
              sendCloseMessage: false,
              ws: {
                uri: this.wsUri,
                useSockJS: false,
                onconnected: onConnectSucces,
                ondisconnect: this.disconnectCallback.bind(this),
                onreconnecting: this.reconnectingCallback.bind(this),
                onreconnected: this.reconnectedCallback.bind(this),
              },
              rpc: {
                requestTimeout: 10000,
                participantJoined: this.session.onParticipantJoined.bind(
                  this.session,
                ),
                participantPublished: this.session.onParticipantPublished.bind(
                  this.session,
                ),
                participantUnpublished: this.session.onParticipantUnpublished.bind(
                  this.session,
                ),
                participantLeft: this.session.onParticipantLeft.bind(
                  this.session,
                ),
                participantEvicted: this.session.onParticipantEvicted.bind(
                  this.session,
                ),
                recordingStarted: this.session.onRecordingStarted.bind(
                  this.session,
                ),
                recordingStopped: this.session.onRecordingStopped.bind(
                  this.session,
                ),
                sendMessage: this.session.onNewMessage.bind(this.session),
                streamPropertyChanged: this.session.onStreamPropertyChanged.bind(
                  this.session,
                ),
                filterEventDispatched: this.session.onFilterEventDispatched.bind(
                  this.session,
                ),
                iceCandidate: this.session.recvIceCandidate.bind(this.session),
                mediaError: this.session.onMediaError.bind(this.session),
              },
            }
            this.jsonRpcClient = new RpcBuilder.clients.JsonRpcClient(config)
          }
          OpenVidu.prototype.closeWs = function() {
            this.jsonRpcClient.close(4102, 'Connection closed by client')
          }
          OpenVidu.prototype.sendRequest = function(method, params, callback) {
            if (params && params instanceof Function) {
              callback = params
              params = {}
            }
            console.debug(
              'Sending request: {method:"' +
                method +
                '", params: ' +
                JSON.stringify(params) +
                '}',
            )
            this.jsonRpcClient.send(method, params, callback)
          }
          OpenVidu.prototype.getWsUri = function() {
            return this.wsUri
          }
          OpenVidu.prototype.getSecret = function() {
            return this.secret
          }
          OpenVidu.prototype.getRecorder = function() {
            return this.recorder
          }
          OpenVidu.prototype.disconnectCallback = function() {
            console.warn('Websocket connection lost')
            if (this.isRoomAvailable()) {
              this.session.onLostConnection('networkDisconnect')
            } else {
              alert('Connection error. Please reload page.')
            }
          }
          OpenVidu.prototype.reconnectingCallback = function() {
            console.warn('Websocket connection lost (reconnecting)')
            if (!this.isRoomAvailable()) {
              alert('Connection error. Please reload page.')
            }
          }
          OpenVidu.prototype.reconnectedCallback = function() {
            var _this = this
            console.warn('Websocket reconnected')
            if (this.isRoomAvailable()) {
              this.sendRequest(
                'connect',
                { sessionId: this.session.connection.rpcSessionId },
                function(error, response) {
                  if (error) {
                    console.error(error)
                    _this.session.onLostConnection('networkDisconnect')
                    _this.jsonRpcClient.close(4101, 'Reconnection fault')
                  } else {
                    _this.jsonRpcClient.resetPing()
                    _this.session.onRecoveredConnection()
                  }
                },
              )
            } else {
              alert('Connection error. Please reload page.')
            }
          }
          OpenVidu.prototype.isRoomAvailable = function() {
            if (
              this.session !== undefined &&
              this.session instanceof Session_1.Session
            ) {
              return true
            } else {
              console.warn('Session instance not found')
              return false
            }
          }
          return OpenVidu
        })()
        exports.OpenVidu = OpenVidu
      },
      {
        '../../package.json': 16,
        '../OpenViduInternal/Enums/OpenViduError': 28,
        '../OpenViduInternal/Enums/VideoInsertMode': 29,
        '../OpenViduInternal/Events/StreamPropertyChangedEvent': 39,
        '../OpenViduInternal/KurentoUtils/kurento-jsonrpc': 46,
        '../OpenViduInternal/ScreenSharing/Screen-Capturing': 51,
        '../OpenViduInternal/ScreenSharing/Screen-Capturing-Auto': 50,
        './LocalRecorder': 20,
        './Publisher': 22,
        './Session': 23,
        platform: 8,
        'wolfy87-eventemitter': 15,
      },
    ],
    22: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Session_1 = require('./Session')
        var Stream_1 = require('./Stream')
        var StreamManager_1 = require('./StreamManager')
        var StreamEvent_1 = require('../OpenViduInternal/Events/StreamEvent')
        var StreamPropertyChangedEvent_1 = require('../OpenViduInternal/Events/StreamPropertyChangedEvent')
        var VideoElementEvent_1 = require('../OpenViduInternal/Events/VideoElementEvent')
        var OpenViduError_1 = require('../OpenViduInternal/Enums/OpenViduError')
        var platform = require('platform')
        var Publisher = (function(_super) {
          __extends(Publisher, _super)
          function Publisher(targEl, properties, openvidu) {
            var _this =
              _super.call(
                this,
                new Stream_1.Stream(
                  openvidu.session
                    ? openvidu.session
                    : new Session_1.Session(openvidu),
                  { publisherProperties: properties, mediaConstraints: {} },
                ),
                targEl,
              ) || this
            _this.accessAllowed = false
            _this.isSubscribedToRemote = false
            _this.accessDenied = false
            _this.properties = properties
            _this.openvidu = openvidu
            _this.stream.ee.on('local-stream-destroyed', function(reason) {
              _this.stream.isLocalStreamPublished = false
              var streamEvent = new StreamEvent_1.StreamEvent(
                true,
                _this,
                'streamDestroyed',
                _this.stream,
                reason,
              )
              _this.emitEvent('streamDestroyed', [streamEvent])
              streamEvent.callDefaultBehavior()
            })
            return _this
          }
          Publisher.prototype.publishAudio = function(value) {
            var _this = this
            if (this.stream.audioActive !== value) {
              this.stream
                .getMediaStream()
                .getAudioTracks()
                .forEach(function(track) {
                  track.enabled = value
                })
              if (!!this.session && !!this.stream.streamId) {
                this.session.openvidu.sendRequest(
                  'streamPropertyChanged',
                  {
                    streamId: this.stream.streamId,
                    property: 'audioActive',
                    newValue: value,
                    reason: 'publishAudio',
                  },
                  function(error, response) {
                    if (error) {
                      console.error(
                        "Error sending 'streamPropertyChanged' event",
                        error,
                      )
                    } else {
                      _this.session.emitEvent('streamPropertyChanged', [
                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                          _this.session,
                          _this.stream,
                          'audioActive',
                          value,
                          !value,
                          'publishAudio',
                        ),
                      ])
                      _this.emitEvent('streamPropertyChanged', [
                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                          _this,
                          _this.stream,
                          'audioActive',
                          value,
                          !value,
                          'publishAudio',
                        ),
                      ])
                    }
                  },
                )
              }
              this.stream.audioActive = value
              console.info(
                "'Publisher' has " +
                  (value ? 'published' : 'unpublished') +
                  ' its audio stream',
              )
            }
          }
          Publisher.prototype.publishVideo = function(value) {
            var _this = this
            if (this.stream.videoActive !== value) {
              this.stream
                .getMediaStream()
                .getVideoTracks()
                .forEach(function(track) {
                  track.enabled = value
                })
              if (!!this.session && !!this.stream.streamId) {
                this.session.openvidu.sendRequest(
                  'streamPropertyChanged',
                  {
                    streamId: this.stream.streamId,
                    property: 'videoActive',
                    newValue: value,
                    reason: 'publishVideo',
                  },
                  function(error, response) {
                    if (error) {
                      console.error(
                        "Error sending 'streamPropertyChanged' event",
                        error,
                      )
                    } else {
                      _this.session.emitEvent('streamPropertyChanged', [
                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                          _this.session,
                          _this.stream,
                          'videoActive',
                          value,
                          !value,
                          'publishVideo',
                        ),
                      ])
                      _this.emitEvent('streamPropertyChanged', [
                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                          _this,
                          _this.stream,
                          'videoActive',
                          value,
                          !value,
                          'publishVideo',
                        ),
                      ])
                    }
                  },
                )
              }
              this.stream.videoActive = value
              console.info(
                "'Publisher' has " +
                  (value ? 'published' : 'unpublished') +
                  ' its video stream',
              )
            }
          }
          Publisher.prototype.subscribeToRemote = function(value) {
            value = value !== undefined ? value : true
            this.isSubscribedToRemote = value
            this.stream.subscribeToMyRemote(value)
          }
          Publisher.prototype.on = function(type, handler) {
            var _this = this
            _super.prototype.on.call(this, type, handler)
            if (type === 'streamCreated') {
              if (!!this.stream && this.stream.isLocalStreamPublished) {
                this.emitEvent('streamCreated', [
                  new StreamEvent_1.StreamEvent(
                    false,
                    this,
                    'streamCreated',
                    this.stream,
                    '',
                  ),
                ])
              } else {
                this.stream.ee.on('stream-created-by-publisher', function() {
                  _this.emitEvent('streamCreated', [
                    new StreamEvent_1.StreamEvent(
                      false,
                      _this,
                      'streamCreated',
                      _this.stream,
                      '',
                    ),
                  ])
                })
              }
            }
            if (type === 'remoteVideoPlaying') {
              if (
                this.stream.displayMyRemote() &&
                this.videos[0] &&
                this.videos[0].video &&
                this.videos[0].video.currentTime > 0 &&
                this.videos[0].video.paused === false &&
                this.videos[0].video.ended === false &&
                this.videos[0].video.readyState === 4
              ) {
                this.emitEvent('remoteVideoPlaying', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'remoteVideoPlaying',
                  ),
                ])
              }
            }
            if (type === 'accessAllowed') {
              if (this.accessAllowed) {
                this.emitEvent('accessAllowed', [])
              }
            }
            if (type === 'accessDenied') {
              if (this.accessDenied) {
                this.emitEvent('accessDenied', [])
              }
            }
            return this
          }
          Publisher.prototype.once = function(type, handler) {
            var _this = this
            _super.prototype.once.call(this, type, handler)
            if (type === 'streamCreated') {
              if (!!this.stream && this.stream.isLocalStreamPublished) {
                this.emitEvent('streamCreated', [
                  new StreamEvent_1.StreamEvent(
                    false,
                    this,
                    'streamCreated',
                    this.stream,
                    '',
                  ),
                ])
              } else {
                this.stream.ee.once('stream-created-by-publisher', function() {
                  _this.emitEvent('streamCreated', [
                    new StreamEvent_1.StreamEvent(
                      false,
                      _this,
                      'streamCreated',
                      _this.stream,
                      '',
                    ),
                  ])
                })
              }
            }
            if (type === 'remoteVideoPlaying') {
              if (
                this.stream.displayMyRemote() &&
                this.videos[0] &&
                this.videos[0].video &&
                this.videos[0].video.currentTime > 0 &&
                this.videos[0].video.paused === false &&
                this.videos[0].video.ended === false &&
                this.videos[0].video.readyState === 4
              ) {
                this.emitEvent('remoteVideoPlaying', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'remoteVideoPlaying',
                  ),
                ])
              }
            }
            if (type === 'accessAllowed') {
              if (this.accessAllowed) {
                this.emitEvent('accessAllowed', [])
              }
            }
            if (type === 'accessDenied') {
              if (this.accessDenied) {
                this.emitEvent('accessDenied', [])
              }
            }
            return this
          }
          Publisher.prototype.initialize = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              var constraints = {}
              var constraintsAux = {}
              var timeForDialogEvent = 1250
              var startTime
              var errorCallback = function(openViduError) {
                _this.accessDenied = true
                _this.accessAllowed = false
                reject(openViduError)
              }
              var successCallback = function(mediaStream) {
                _this.accessAllowed = true
                _this.accessDenied = false
                if (
                  typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.audioSource instanceof MediaStreamTrack
                ) {
                  mediaStream.removeTrack(mediaStream.getAudioTracks()[0])
                  mediaStream.addTrack(_this.properties.audioSource)
                }
                if (
                  typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.videoSource instanceof MediaStreamTrack
                ) {
                  mediaStream.removeTrack(mediaStream.getVideoTracks()[0])
                  mediaStream.addTrack(_this.properties.videoSource)
                }
                if (mediaStream.getAudioTracks()[0]) {
                  var enabled =
                    _this.stream.audioActive !== undefined &&
                    _this.stream.audioActive !== null
                      ? _this.stream.audioActive
                      : !!_this.stream.outboundStreamOpts.publisherProperties
                          .publishAudio
                  mediaStream.getAudioTracks()[0].enabled = enabled
                }
                if (mediaStream.getVideoTracks()[0]) {
                  var enabled =
                    _this.stream.videoActive !== undefined &&
                    _this.stream.videoActive !== null
                      ? _this.stream.videoActive
                      : !!_this.stream.outboundStreamOpts.publisherProperties
                          .publishVideo
                  mediaStream.getVideoTracks()[0].enabled = enabled
                }
                _this.videoReference = document.createElement('video')
                if (platform.name === 'Safari') {
                  _this.videoReference.setAttribute('playsinline', 'true')
                }
                _this.stream.setMediaStream(mediaStream)
                if (_this.firstVideoElement) {
                  _this.createVideoElement(
                    _this.firstVideoElement.targetElement,
                    _this.properties.insertMode,
                  )
                }
                _this.videoReference.srcObject = mediaStream
                if (!_this.stream.displayMyRemote()) {
                  _this.stream.updateMediaStreamInVideos()
                }
                delete _this.firstVideoElement
                if (_this.stream.isSendVideo()) {
                  if (!_this.stream.isSendScreen()) {
                    if (platform['isIonicIos'] || platform.name === 'Safari') {
                      _this.videoReference.style.display = 'none'
                      document.body.appendChild(_this.videoReference)
                      var videoDimensionsSet_1 = function() {
                        _this.stream.videoDimensions = {
                          width: _this.videoReference.videoWidth,
                          height: _this.videoReference.videoHeight,
                        }
                        _this.stream.isLocalStreamReadyToPublish = true
                        _this.stream.ee.emitEvent('stream-ready-to-publish', [])
                        document.body.removeChild(_this.videoReference)
                      }
                      var interval_1
                      _this.videoReference.addEventListener(
                        'loadedmetadata',
                        function() {
                          if (_this.videoReference.videoWidth === 0) {
                            interval_1 = setInterval(function() {
                              if (_this.videoReference.videoWidth !== 0) {
                                clearInterval(interval_1)
                                videoDimensionsSet_1()
                              }
                            }, 40)
                          } else {
                            videoDimensionsSet_1()
                          }
                        },
                      )
                    } else {
                      var _a = mediaStream.getVideoTracks()[0].getSettings(),
                        width = _a.width,
                        height = _a.height
                      if (
                        (platform.os.family === 'iOS' ||
                          platform.os.family === 'Android') &&
                        window.innerHeight > window.innerWidth
                      ) {
                        _this.stream.videoDimensions = {
                          width: height || 0,
                          height: width || 0,
                        }
                      } else {
                        _this.stream.videoDimensions = {
                          width: width || 0,
                          height: height || 0,
                        }
                      }
                      _this.stream.isLocalStreamReadyToPublish = true
                      _this.stream.ee.emitEvent('stream-ready-to-publish', [])
                    }
                  } else {
                    _this.videoReference.addEventListener(
                      'loadedmetadata',
                      function() {
                        _this.stream.videoDimensions = {
                          width: _this.videoReference.videoWidth,
                          height: _this.videoReference.videoHeight,
                        }
                        _this.screenShareResizeInterval = setInterval(
                          function() {
                            var firefoxSettings = mediaStream
                              .getVideoTracks()[0]
                              .getSettings()
                            var newWidth =
                              platform.name === 'Chrome' ||
                              platform.name === 'Opera'
                                ? _this.videoReference.videoWidth
                                : firefoxSettings.width
                            var newHeight =
                              platform.name === 'Chrome' ||
                              platform.name === 'Opera'
                                ? _this.videoReference.videoHeight
                                : firefoxSettings.height
                            if (
                              _this.stream.isLocalStreamPublished &&
                              (newWidth !==
                                _this.stream.videoDimensions.width ||
                                newHeight !==
                                  _this.stream.videoDimensions.height)
                            ) {
                              var oldValue_1 = {
                                width: _this.stream.videoDimensions.width,
                                height: _this.stream.videoDimensions.height,
                              }
                              _this.stream.videoDimensions = {
                                width: newWidth || 0,
                                height: newHeight || 0,
                              }
                              _this.session.openvidu.sendRequest(
                                'streamPropertyChanged',
                                {
                                  streamId: _this.stream.streamId,
                                  property: 'videoDimensions',
                                  newValue: JSON.stringify(
                                    _this.stream.videoDimensions,
                                  ),
                                  reason: 'screenResized',
                                },
                                function(error, response) {
                                  if (error) {
                                    console.error(
                                      "Error sending 'streamPropertyChanged' event",
                                      error,
                                    )
                                  } else {
                                    _this.session.emitEvent(
                                      'streamPropertyChanged',
                                      [
                                        new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                                          _this.session,
                                          _this.stream,
                                          'videoDimensions',
                                          _this.stream.videoDimensions,
                                          oldValue_1,
                                          'screenResized',
                                        ),
                                      ],
                                    )
                                    _this.emitEvent('streamPropertyChanged', [
                                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                                        _this,
                                        _this.stream,
                                        'videoDimensions',
                                        _this.stream.videoDimensions,
                                        oldValue_1,
                                        'screenResized',
                                      ),
                                    ])
                                  }
                                },
                              )
                            }
                          },
                          500,
                        )
                        _this.stream.isLocalStreamReadyToPublish = true
                        _this.stream.ee.emitEvent('stream-ready-to-publish', [])
                      },
                    )
                  }
                } else {
                  _this.stream.isLocalStreamReadyToPublish = true
                  _this.stream.ee.emitEvent('stream-ready-to-publish', [])
                }
                resolve()
              }
              var getMediaSuccess = function(
                mediaStream,
                definedAudioConstraint,
              ) {
                _this.clearPermissionDialogTimer(startTime, timeForDialogEvent)
                if (_this.stream.isSendScreen() && _this.stream.isSendAudio()) {
                  constraintsAux.audio = definedAudioConstraint
                  constraintsAux.video = false
                  startTime = Date.now()
                  _this.setPermissionDialogTimer(timeForDialogEvent)
                  navigator.mediaDevices
                    .getUserMedia(constraintsAux)
                    .then(function(audioOnlyStream) {
                      _this.clearPermissionDialogTimer(
                        startTime,
                        timeForDialogEvent,
                      )
                      mediaStream.addTrack(audioOnlyStream.getAudioTracks()[0])
                      successCallback(mediaStream)
                    })
                    .catch(function(error) {
                      _this.clearPermissionDialogTimer(
                        startTime,
                        timeForDialogEvent,
                      )
                      if (error.name === 'Error') {
                        error.name = error.constructor.name
                      }
                      var errorName, errorMessage
                      switch (error.name.toLowerCase()) {
                        case 'notfounderror':
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .INPUT_AUDIO_DEVICE_NOT_FOUND
                          errorMessage = error.toString()
                          errorCallback(
                            new OpenViduError_1.OpenViduError(
                              errorName,
                              errorMessage,
                            ),
                          )
                          break
                        case 'notallowederror':
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .DEVICE_ACCESS_DENIED
                          errorMessage = error.toString()
                          errorCallback(
                            new OpenViduError_1.OpenViduError(
                              errorName,
                              errorMessage,
                            ),
                          )
                          break
                        case 'overconstrainederror':
                          if (error.constraint.toLowerCase() === 'deviceid') {
                            errorName =
                              OpenViduError_1.OpenViduErrorName
                                .INPUT_AUDIO_DEVICE_NOT_FOUND
                            errorMessage =
                              "Audio input device with deviceId '" +
                              constraints.video.deviceId.exact +
                              "' not found"
                          } else {
                            errorName =
                              OpenViduError_1.OpenViduErrorName
                                .PUBLISHER_PROPERTIES_ERROR
                            errorMessage =
                              "Audio input device doesn't support the value passed for constraint '" +
                              error.constraint +
                              "'"
                          }
                          errorCallback(
                            new OpenViduError_1.OpenViduError(
                              errorName,
                              errorMessage,
                            ),
                          )
                          break
                      }
                    })
                } else {
                  successCallback(mediaStream)
                }
              }
              var getMediaError = function(error) {
                console.error(error)
                _this.clearPermissionDialogTimer(startTime, timeForDialogEvent)
                if (error.name === 'Error') {
                  error.name = error.constructor.name
                }
                var errorName, errorMessage
                switch (error.name.toLowerCase()) {
                  case 'notfounderror':
                    navigator.mediaDevices
                      .getUserMedia({
                        audio: false,
                        video: constraints.video,
                      })
                      .then(function(mediaStream) {
                        mediaStream.getVideoTracks().forEach(function(track) {
                          track.stop()
                        })
                        errorName =
                          OpenViduError_1.OpenViduErrorName
                            .INPUT_AUDIO_DEVICE_NOT_FOUND
                        errorMessage = error.toString()
                        errorCallback(
                          new OpenViduError_1.OpenViduError(
                            errorName,
                            errorMessage,
                          ),
                        )
                      })
                      .catch(function(e) {
                        errorName =
                          OpenViduError_1.OpenViduErrorName
                            .INPUT_VIDEO_DEVICE_NOT_FOUND
                        errorMessage = error.toString()
                        errorCallback(
                          new OpenViduError_1.OpenViduError(
                            errorName,
                            errorMessage,
                          ),
                        )
                      })
                    break
                  case 'notallowederror':
                    errorName = _this.stream.isSendScreen()
                      ? OpenViduError_1.OpenViduErrorName.SCREEN_CAPTURE_DENIED
                      : OpenViduError_1.OpenViduErrorName.DEVICE_ACCESS_DENIED
                    errorMessage = error.toString()
                    errorCallback(
                      new OpenViduError_1.OpenViduError(
                        errorName,
                        errorMessage,
                      ),
                    )
                    break
                  case 'overconstrainederror':
                    navigator.mediaDevices
                      .getUserMedia({
                        audio: false,
                        video: constraints.video,
                      })
                      .then(function(mediaStream) {
                        mediaStream.getVideoTracks().forEach(function(track) {
                          track.stop()
                        })
                        if (error.constraint.toLowerCase() === 'deviceid') {
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .INPUT_AUDIO_DEVICE_NOT_FOUND
                          errorMessage =
                            "Audio input device with deviceId '" +
                            constraints.audio.deviceId.exact +
                            "' not found"
                        } else {
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .PUBLISHER_PROPERTIES_ERROR
                          errorMessage =
                            "Audio input device doesn't support the value passed for constraint '" +
                            error.constraint +
                            "'"
                        }
                        errorCallback(
                          new OpenViduError_1.OpenViduError(
                            errorName,
                            errorMessage,
                          ),
                        )
                      })
                      .catch(function(e) {
                        if (error.constraint.toLowerCase() === 'deviceid') {
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .INPUT_VIDEO_DEVICE_NOT_FOUND
                          errorMessage =
                            "Video input device with deviceId '" +
                            constraints.video.deviceId.exact +
                            "' not found"
                        } else {
                          errorName =
                            OpenViduError_1.OpenViduErrorName
                              .PUBLISHER_PROPERTIES_ERROR
                          errorMessage =
                            "Video input device doesn't support the value passed for constraint '" +
                            error.constraint +
                            "'"
                        }
                        errorCallback(
                          new OpenViduError_1.OpenViduError(
                            errorName,
                            errorMessage,
                          ),
                        )
                      })
                    break
                  case 'aborterror':
                  case 'notreadableerror':
                    errorName =
                      OpenViduError_1.OpenViduErrorName.DEVICE_ALREADY_IN_USE
                    errorMessage = error.toString()
                    errorCallback(
                      new OpenViduError_1.OpenViduError(
                        errorName,
                        errorMessage,
                      ),
                    )
                    break
                  default:
                    errorName = OpenViduError_1.OpenViduErrorName.GENERIC_ERROR
                    errorMessage = error.toString()
                    errorCallback(
                      new OpenViduError_1.OpenViduError(
                        errorName,
                        errorMessage,
                      ),
                    )
                    break
                }
              }
              if (
                (typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.videoSource instanceof MediaStreamTrack &&
                  !_this.properties.audioSource) ||
                (typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.audioSource instanceof MediaStreamTrack &&
                  !_this.properties.videoSource) ||
                (typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.videoSource instanceof MediaStreamTrack &&
                  _this.properties.audioSource instanceof MediaStreamTrack)
              ) {
                var mediaStream = new MediaStream()
                if (
                  typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.videoSource instanceof MediaStreamTrack
                ) {
                  mediaStream.addTrack(_this.properties.videoSource)
                }
                if (
                  typeof MediaStreamTrack !== 'undefined' &&
                  _this.properties.audioSource instanceof MediaStreamTrack
                ) {
                  mediaStream.addTrack(_this.properties.audioSource)
                }
                successCallback(mediaStream)
                return
              }
              _this.openvidu
                .generateMediaConstraints(_this.properties)
                .then(function(myConstraints) {
                  constraints = myConstraints
                  var outboundStreamOptions = {
                    mediaConstraints: constraints,
                    publisherProperties: _this.properties,
                  }
                  _this.stream.setOutboundStreamOptions(outboundStreamOptions)
                  if (
                    _this.stream.isSendVideo() ||
                    _this.stream.isSendAudio()
                  ) {
                    var definedAudioConstraint_1 =
                      constraints.audio === undefined ? true : constraints.audio
                    constraintsAux.audio = _this.stream.isSendScreen()
                      ? false
                      : definedAudioConstraint_1
                    constraintsAux.video = constraints.video
                    startTime = Date.now()
                    _this.setPermissionDialogTimer(timeForDialogEvent)
                    if (
                      _this.stream.isSendScreen() &&
                      navigator.mediaDevices['getDisplayMedia'] &&
                      platform.name !== 'Electron'
                    ) {
                      navigator.mediaDevices['getDisplayMedia']({ video: true })
                        .then(function(mediaStream) {
                          getMediaSuccess(mediaStream, definedAudioConstraint_1)
                        })
                        .catch(function(error) {
                          getMediaError(error)
                        })
                    } else {
                      navigator.mediaDevices
                        .getUserMedia(constraintsAux)
                        .then(function(mediaStream) {
                          getMediaSuccess(mediaStream, definedAudioConstraint_1)
                        })
                        .catch(function(error) {
                          getMediaError(error)
                        })
                    }
                  } else {
                    reject(
                      new OpenViduError_1.OpenViduError(
                        OpenViduError_1.OpenViduErrorName.NO_INPUT_SOURCE_SET,
                        "Properties 'audioSource' and 'videoSource' cannot be set to false or null at the same time when calling 'OpenVidu.initPublisher'",
                      ),
                    )
                  }
                })
                .catch(function(error) {
                  errorCallback(error)
                })
            })
          }
          Publisher.prototype.reestablishStreamPlayingEvent = function() {
            if (this.ee.getListeners('streamPlaying').length > 0) {
              this.addPlayEventToFirstVideo()
            }
          }
          Publisher.prototype.setPermissionDialogTimer = function(waitTime) {
            var _this = this
            this.permissionDialogTimeout = setTimeout(function() {
              _this.emitEvent('accessDialogOpened', [])
            }, waitTime)
          }
          Publisher.prototype.clearPermissionDialogTimer = function(
            startTime,
            waitTime,
          ) {
            clearTimeout(this.permissionDialogTimeout)
            if (Date.now() - startTime > waitTime) {
              this.emitEvent('accessDialogClosed', [])
            }
          }
          return Publisher
        })(StreamManager_1.StreamManager)
        exports.Publisher = Publisher
      },
      {
        '../OpenViduInternal/Enums/OpenViduError': 28,
        '../OpenViduInternal/Events/StreamEvent': 37,
        '../OpenViduInternal/Events/StreamPropertyChangedEvent': 39,
        '../OpenViduInternal/Events/VideoElementEvent': 40,
        './Session': 23,
        './Stream': 24,
        './StreamManager': 25,
        platform: 8,
      },
    ],
    23: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var Connection_1 = require('./Connection')
        var Filter_1 = require('./Filter')
        var Subscriber_1 = require('./Subscriber')
        var ConnectionEvent_1 = require('../OpenViduInternal/Events/ConnectionEvent')
        var FilterEvent_1 = require('../OpenViduInternal/Events/FilterEvent')
        var RecordingEvent_1 = require('../OpenViduInternal/Events/RecordingEvent')
        var SessionDisconnectedEvent_1 = require('../OpenViduInternal/Events/SessionDisconnectedEvent')
        var SignalEvent_1 = require('../OpenViduInternal/Events/SignalEvent')
        var StreamEvent_1 = require('../OpenViduInternal/Events/StreamEvent')
        var StreamPropertyChangedEvent_1 = require('../OpenViduInternal/Events/StreamPropertyChangedEvent')
        var OpenViduError_1 = require('../OpenViduInternal/Enums/OpenViduError')
        var VideoInsertMode_1 = require('../OpenViduInternal/Enums/VideoInsertMode')
        var EventEmitter = require('wolfy87-eventemitter')
        var platform = require('platform')
        var Session = (function() {
          function Session(openvidu) {
            this.streamManagers = []
            this.remoteStreamsCreated = {}
            this.isFirstIonicIosSubscriber = true
            this.countDownForIonicIosSubscribersActive = true
            this.remoteConnections = {}
            this.speakingEventsEnabled = false
            this.ee = new EventEmitter()
            this.openvidu = openvidu
          }
          Session.prototype.connect = function(token, metadata) {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this.processToken(token)
              if (_this.openvidu.checkSystemRequirements()) {
                _this.options = {
                  sessionId: _this.sessionId,
                  participantId: token,
                  metadata: metadata
                    ? _this.stringClientMetadata(metadata)
                    : '',
                }
                _this
                  .connectAux(token)
                  .then(function() {
                    resolve()
                  })
                  .catch(function(error) {
                    reject(error)
                  })
              } else {
                reject(
                  new OpenViduError_1.OpenViduError(
                    OpenViduError_1.OpenViduErrorName.BROWSER_NOT_SUPPORTED,
                    'Browser ' +
                      platform.name +
                      ' (version ' +
                      platform.version +
                      ') for ' +
                      platform.os.family +
                      ' is not supported in OpenVidu',
                  ),
                )
              }
            })
          }
          Session.prototype.disconnect = function() {
            this.leave(false, 'disconnect')
          }
          Session.prototype.subscribe = function(
            stream,
            targetElement,
            param3,
            param4,
          ) {
            var properties = {}
            if (!!param3 && typeof param3 !== 'function') {
              properties = {
                insertMode:
                  typeof param3.insertMode !== 'undefined'
                    ? typeof param3.insertMode === 'string'
                      ? VideoInsertMode_1.VideoInsertMode[param3.insertMode]
                      : properties.insertMode
                    : VideoInsertMode_1.VideoInsertMode.APPEND,
                subscribeToAudio:
                  typeof param3.subscribeToAudio !== 'undefined'
                    ? param3.subscribeToAudio
                    : true,
                subscribeToVideo:
                  typeof param3.subscribeToVideo !== 'undefined'
                    ? param3.subscribeToVideo
                    : true,
              }
            } else {
              properties = {
                insertMode: VideoInsertMode_1.VideoInsertMode.APPEND,
                subscribeToAudio: true,
                subscribeToVideo: true,
              }
            }
            var completionHandler
            if (!!param3 && typeof param3 === 'function') {
              completionHandler = param3
            } else if (param4) {
              completionHandler = param4
            }
            console.info('Subscribing to ' + stream.connection.connectionId)
            stream
              .subscribe()
              .then(function() {
                console.info(
                  'Subscribed correctly to ' + stream.connection.connectionId,
                )
                if (completionHandler !== undefined) {
                  completionHandler(undefined)
                }
              })
              .catch(function(error) {
                if (completionHandler !== undefined) {
                  completionHandler(error)
                }
              })
            var subscriber = new Subscriber_1.Subscriber(
              stream,
              targetElement,
              properties,
            )
            if (subscriber.targetElement) {
              stream.streamManager.createVideoElement(
                subscriber.targetElement,
                properties.insertMode,
              )
            }
            return subscriber
          }
          Session.prototype.subscribeAsync = function(
            stream,
            targetElement,
            properties,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var subscriber
              var callback = function(error) {
                if (error) {
                  reject(error)
                } else {
                  resolve(subscriber)
                }
              }
              if (properties) {
                subscriber = _this.subscribe(
                  stream,
                  targetElement,
                  properties,
                  callback,
                )
              } else {
                subscriber = _this.subscribe(stream, targetElement, callback)
              }
            })
          }
          Session.prototype.unsubscribe = function(subscriber) {
            var connectionId = subscriber.stream.connection.connectionId
            console.info('Unsubscribing from ' + connectionId)
            this.openvidu.sendRequest(
              'unsubscribeFromVideo',
              { sender: subscriber.stream.connection.connectionId },
              function(error, response) {
                if (error) {
                  console.error(
                    'Error unsubscribing from ' + connectionId,
                    error,
                  )
                } else {
                  console.info('Unsubscribed correctly from ' + connectionId)
                }
                subscriber.stream.disposeWebRtcPeer()
                subscriber.stream.disposeMediaStream()
              },
            )
            subscriber.stream.streamManager.removeAllVideos()
          }
          Session.prototype.publish = function(publisher) {
            var _this = this
            return new Promise(function(resolve, reject) {
              publisher.session = _this
              publisher.stream.session = _this
              if (!publisher.stream.publishedOnce) {
                _this.connection.addStream(publisher.stream)
                publisher.stream
                  .publish()
                  .then(function() {
                    resolve()
                  })
                  .catch(function(error) {
                    reject(error)
                  })
              } else {
                publisher
                  .initialize()
                  .then(function() {
                    _this.connection.addStream(publisher.stream)
                    publisher.reestablishStreamPlayingEvent()
                    publisher.stream
                      .publish()
                      .then(function() {
                        resolve()
                      })
                      .catch(function(error) {
                        reject(error)
                      })
                  })
                  .catch(function(error) {
                    reject(error)
                  })
              }
            })
          }
          Session.prototype.unpublish = function(publisher) {
            var stream = publisher.stream
            if (!stream.connection) {
              console.error(
                'The associated Connection object of this Publisher is null',
                stream,
              )
              return
            } else if (stream.connection !== this.connection) {
              console.error(
                'The associated Connection object of this Publisher is not your local Connection.' +
                  "Only moderators can force unpublish on remote Streams via 'forceUnpublish' method",
                stream,
              )
              return
            } else {
              console.info(
                'Unpublishing local media (' +
                  stream.connection.connectionId +
                  ')',
              )
              this.openvidu.sendRequest('unpublishVideo', function(
                error,
                response,
              ) {
                if (error) {
                  console.error(error)
                } else {
                  console.info('Media unpublished correctly')
                }
              })
              stream.disposeWebRtcPeer()
              delete stream.connection.stream
              var streamEvent = new StreamEvent_1.StreamEvent(
                true,
                publisher,
                'streamDestroyed',
                publisher.stream,
                'unpublish',
              )
              publisher.emitEvent('streamDestroyed', [streamEvent])
              streamEvent.callDefaultBehavior()
            }
          }
          Session.prototype.forceDisconnect = function(connection) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info(
                'Forcing disconnect for connection ' + connection.connectionId,
              )
              _this.openvidu.sendRequest(
                'forceDisconnect',
                { connectionId: connection.connectionId },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error forcing disconnect for Connection ' +
                        connection.connectionId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to force a disconnection",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    console.info(
                      'Forcing disconnect correctly for Connection ' +
                        connection.connectionId,
                    )
                    resolve()
                  }
                },
              )
            })
          }
          Session.prototype.forceUnpublish = function(stream) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info('Forcing unpublish for stream ' + stream.streamId)
              _this.openvidu.sendRequest(
                'forceUnpublish',
                { streamId: stream.streamId },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error forcing unpublish for Stream ' + stream.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to force an unpublishing",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    console.info(
                      'Forcing unpublish correctly for Stream ' +
                        stream.streamId,
                    )
                    resolve()
                  }
                },
              )
            })
          }
          Session.prototype.signal = function(signal) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var signalMessage = {}
              if (signal.to && signal.to.length > 0) {
                var connectionIds_1 = []
                signal.to.forEach(function(connection) {
                  connectionIds_1.push(connection.connectionId)
                })
                signalMessage['to'] = connectionIds_1
              } else {
                signalMessage['to'] = []
              }
              signalMessage['data'] = signal.data ? signal.data : ''
              var typeAux = signal.type ? signal.type : 'signal'
              if (typeAux) {
                if (typeAux.substring(0, 7) !== 'signal:') {
                  typeAux = 'signal:' + typeAux
                }
              }
              signalMessage['type'] = typeAux
              _this.openvidu.sendRequest(
                'sendMessage',
                {
                  message: JSON.stringify(signalMessage),
                },
                function(error, response) {
                  if (error) {
                    reject(error)
                  } else {
                    resolve()
                  }
                },
              )
            })
          }
          Session.prototype.on = function(type, handler) {
            this.ee.on(type, function(event) {
              if (event) {
                console.info(
                  "Event '" + type + "' triggered by 'Session'",
                  event,
                )
              } else {
                console.info("Event '" + type + "' triggered by 'Session'")
              }
              handler(event)
            })
            if (
              type === 'publisherStartSpeaking' ||
              type === 'publisherStopSpeaking'
            ) {
              this.speakingEventsEnabled = true
              for (var connectionId in this.remoteConnections) {
                var str = this.remoteConnections[connectionId].stream
                if (!!str && str.hasAudio) {
                  str.enableSpeakingEvents()
                }
              }
            }
            return this
          }
          Session.prototype.once = function(type, handler) {
            this.ee.once(type, function(event) {
              if (event) {
                console.info(
                  "Event '" + type + "' triggered by 'Session'",
                  event,
                )
              } else {
                console.info("Event '" + type + "' triggered by 'Session'")
              }
              handler(event)
            })
            if (
              type === 'publisherStartSpeaking' ||
              type === 'publisherStopSpeaking'
            ) {
              this.speakingEventsEnabled = true
              for (var connectionId in this.remoteConnections) {
                var str = this.remoteConnections[connectionId].stream
                if (!!str && str.hasAudio) {
                  str.enableOnceSpeakingEvents()
                }
              }
            }
            return this
          }
          Session.prototype.off = function(type, handler) {
            if (!handler) {
              this.ee.removeAllListeners(type)
            } else {
              this.ee.off(type, handler)
            }
            if (
              type === 'publisherStartSpeaking' ||
              type === 'publisherStopSpeaking'
            ) {
              this.speakingEventsEnabled = false
              for (var connectionId in this.remoteConnections) {
                var str = this.remoteConnections[connectionId].stream
                if (str) {
                  str.disableSpeakingEvents()
                }
              }
            }
            return this
          }
          Session.prototype.onParticipantJoined = function(response) {
            var _this = this
            this.getConnection(response.id, '')
              .then(function(connection) {
                console.warn(
                  'Connection ' +
                    response.id +
                    ' already exists in connections list',
                )
              })
              .catch(function(openViduError) {
                var connection = new Connection_1.Connection(_this, response)
                _this.remoteConnections[response.id] = connection
                _this.ee.emitEvent('connectionCreated', [
                  new ConnectionEvent_1.ConnectionEvent(
                    false,
                    _this,
                    'connectionCreated',
                    connection,
                    '',
                  ),
                ])
              })
          }
          Session.prototype.onParticipantLeft = function(msg) {
            var _this = this
            this.getRemoteConnection(
              msg.connectionId,
              'Remote connection ' +
                msg.connectionId +
                " unknown when 'onParticipantLeft'. " +
                'Existing remote connections: ' +
                JSON.stringify(Object.keys(this.remoteConnections)),
            )
              .then(function(connection) {
                if (connection.stream) {
                  var stream = connection.stream
                  var streamEvent = new StreamEvent_1.StreamEvent(
                    true,
                    _this,
                    'streamDestroyed',
                    stream,
                    msg.reason,
                  )
                  _this.ee.emitEvent('streamDestroyed', [streamEvent])
                  streamEvent.callDefaultBehavior()
                  delete _this.remoteStreamsCreated[stream.streamId]
                  if (Object.keys(_this.remoteStreamsCreated).length === 0) {
                    _this.isFirstIonicIosSubscriber = true
                    _this.countDownForIonicIosSubscribersActive = true
                  }
                }
                delete _this.remoteConnections[connection.connectionId]
                _this.ee.emitEvent('connectionDestroyed', [
                  new ConnectionEvent_1.ConnectionEvent(
                    false,
                    _this,
                    'connectionDestroyed',
                    connection,
                    msg.reason,
                  ),
                ])
              })
              .catch(function(openViduError) {
                console.error(openViduError)
              })
          }
          Session.prototype.onParticipantPublished = function(response) {
            var _this = this
            var afterConnectionFound = function(connection) {
              _this.remoteConnections[connection.connectionId] = connection
              if (!_this.remoteStreamsCreated[connection.stream.streamId]) {
                _this.ee.emitEvent('streamCreated', [
                  new StreamEvent_1.StreamEvent(
                    false,
                    _this,
                    'streamCreated',
                    connection.stream,
                    '',
                  ),
                ])
              }
              _this.remoteStreamsCreated[connection.stream.streamId] = true
            }
            var connection
            this.getRemoteConnection(
              response.id,
              "Remote connection '" +
                response.id +
                "' unknown when 'onParticipantPublished'. " +
                'Existing remote connections: ' +
                JSON.stringify(Object.keys(this.remoteConnections)),
            )
              .then(function(con) {
                connection = con
                response.metadata = con.data
                connection.options = response
                connection.initRemoteStreams(response.streams)
                afterConnectionFound(connection)
              })
              .catch(function(openViduError) {
                connection = new Connection_1.Connection(_this, response)
                afterConnectionFound(connection)
              })
          }
          Session.prototype.onParticipantUnpublished = function(msg) {
            var _this = this
            if (msg.connectionId === this.connection.connectionId) {
              this.stopPublisherStream(msg.reason)
            } else {
              this.getRemoteConnection(
                msg.connectionId,
                "Remote connection '" +
                  msg.connectionId +
                  "' unknown when 'onParticipantUnpublished'. " +
                  'Existing remote connections: ' +
                  JSON.stringify(Object.keys(this.remoteConnections)),
              )
                .then(function(connection) {
                  var streamEvent = new StreamEvent_1.StreamEvent(
                    true,
                    _this,
                    'streamDestroyed',
                    connection.stream,
                    msg.reason,
                  )
                  _this.ee.emitEvent('streamDestroyed', [streamEvent])
                  streamEvent.callDefaultBehavior()
                  var streamId = connection.stream.streamId
                  delete _this.remoteStreamsCreated[streamId]
                  if (Object.keys(_this.remoteStreamsCreated).length === 0) {
                    _this.isFirstIonicIosSubscriber = true
                    _this.countDownForIonicIosSubscribersActive = true
                  }
                  connection.removeStream(streamId)
                })
                .catch(function(openViduError) {
                  console.error(openViduError)
                })
            }
          }
          Session.prototype.onParticipantEvicted = function(msg) {
            if (msg.connectionId === this.connection.connectionId) {
              if (!!this.sessionId && !this.connection.disposed) {
                this.leave(true, msg.reason)
              }
            }
          }
          Session.prototype.onNewMessage = function(msg) {
            var _this = this
            console.info('New signal: ' + JSON.stringify(msg))
            this.getConnection(
              msg.from,
              "Connection '" +
                msg.from +
                "' unknow when 'onNewMessage'. Existing remote connections: " +
                JSON.stringify(Object.keys(this.remoteConnections)) +
                '. Existing local connection: ' +
                this.connection.connectionId,
            )
              .then(function(connection) {
                _this.ee.emitEvent('signal', [
                  new SignalEvent_1.SignalEvent(
                    _this,
                    msg.type,
                    msg.data,
                    connection,
                  ),
                ])
                if (msg.type !== 'signal') {
                  _this.ee.emitEvent(msg.type, [
                    new SignalEvent_1.SignalEvent(
                      _this,
                      msg.type,
                      msg.data,
                      connection,
                    ),
                  ])
                }
              })
              .catch(function(openViduError) {
                console.error(openViduError)
              })
          }
          Session.prototype.onStreamPropertyChanged = function(msg) {
            var _this = this
            var callback = function(connection) {
              if (
                !!connection.stream &&
                connection.stream.streamId === msg.streamId
              ) {
                var stream = connection.stream
                var oldValue = void 0
                switch (msg.property) {
                  case 'audioActive':
                    oldValue = stream.audioActive
                    msg.newValue = msg.newValue === 'true'
                    stream.audioActive = msg.newValue
                    break
                  case 'videoActive':
                    oldValue = stream.videoActive
                    msg.newValue = msg.newValue === 'true'
                    stream.videoActive = msg.newValue
                    break
                  case 'videoDimensions':
                    oldValue = stream.videoDimensions
                    msg.newValue = JSON.parse(JSON.parse(msg.newValue))
                    stream.videoDimensions = msg.newValue
                    break
                  case 'filter':
                    oldValue = stream.filter
                    msg.newValue =
                      Object.keys(msg.newValue).length > 0
                        ? msg.newValue
                        : undefined
                    if (msg.newValue !== undefined) {
                      stream.filter = new Filter_1.Filter(
                        msg.newValue.type,
                        msg.newValue.options,
                      )
                      stream.filter.stream = stream
                      if (msg.newValue.lastExecMethod) {
                        stream.filter.lastExecMethod =
                          msg.newValue.lastExecMethod
                      }
                    } else {
                      delete stream.filter
                    }
                    msg.newValue = stream.filter
                    break
                }
                _this.ee.emitEvent('streamPropertyChanged', [
                  new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                    _this,
                    stream,
                    msg.property,
                    msg.newValue,
                    oldValue,
                    msg.reason,
                  ),
                ])
                if (stream.streamManager) {
                  stream.streamManager.emitEvent('streamPropertyChanged', [
                    new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                      stream.streamManager,
                      stream,
                      msg.property,
                      msg.newValue,
                      oldValue,
                      msg.reason,
                    ),
                  ])
                }
              } else {
                console.error(
                  "No stream with streamId '" +
                    msg.streamId +
                    "' found for connection '" +
                    msg.connectionId +
                    "' on 'streamPropertyChanged' event",
                )
              }
            }
            if (msg.connectionId === this.connection.connectionId) {
              callback(this.connection)
            } else {
              this.getRemoteConnection(
                msg.connectionId,
                'Remote connection ' +
                  msg.connectionId +
                  " unknown when 'onStreamPropertyChanged'. " +
                  'Existing remote connections: ' +
                  JSON.stringify(Object.keys(this.remoteConnections)),
              )
                .then(function(connection) {
                  callback(connection)
                })
                .catch(function(openViduError) {
                  console.error(openViduError)
                })
            }
          }
          Session.prototype.recvIceCandidate = function(msg) {
            var candidate = {
              candidate: msg.candidate,
              component: msg.component,
              foundation: msg.foundation,
              ip: msg.ip,
              port: msg.port,
              priority: msg.priority,
              protocol: msg.protocol,
              relatedAddress: msg.relatedAddress,
              relatedPort: msg.relatedPort,
              sdpMid: msg.sdpMid,
              sdpMLineIndex: msg.sdpMLineIndex,
              tcpType: msg.tcpType,
              usernameFragment: msg.usernameFragment,
              type: msg.type,
              toJSON: function() {
                return { candidate: msg.candidate }
              },
            }
            this.getConnection(
              msg.senderConnectionId,
              'Connection not found for connectionId ' +
                msg.senderConnectionId +
                ' owning endpoint ' +
                msg.endpointName +
                '. Ice candidate will be ignored: ' +
                candidate,
            )
              .then(function(connection) {
                var stream = connection.stream
                stream
                  .getWebRtcPeer()
                  .addIceCandidate(candidate)
                  .catch(function(error) {
                    console.error(
                      'Error adding candidate for ' +
                        stream.streamId +
                        ' stream of endpoint ' +
                        msg.endpointName +
                        ': ' +
                        error,
                    )
                  })
              })
              .catch(function(openViduError) {
                console.error(openViduError)
              })
          }
          Session.prototype.onSessionClosed = function(msg) {
            console.info('Session closed: ' + JSON.stringify(msg))
            var s = msg.sessionId
            if (s !== undefined) {
              this.ee.emitEvent('session-closed', [
                {
                  session: s,
                },
              ])
            } else {
              console.warn('Session undefined on session closed', msg)
            }
          }
          Session.prototype.onLostConnection = function(reason) {
            console.warn(
              'Lost connection in session ' +
                this.sessionId +
                ' waiting for reconnect',
            )
            if (!!this.sessionId && !this.connection.disposed) {
              this.leave(true, reason)
            }
          }
          Session.prototype.onRecoveredConnection = function() {
            console.warn('Recovered connection in Session ' + this.sessionId)
          }
          Session.prototype.onMediaError = function(params) {
            console.error('Media error: ' + JSON.stringify(params))
            var err = params.error
            if (err) {
              this.ee.emitEvent('error-media', [
                {
                  error: err,
                },
              ])
            } else {
              console.warn('Received undefined media error. Params:', params)
            }
          }
          Session.prototype.onRecordingStarted = function(response) {
            this.ee.emitEvent('recordingStarted', [
              new RecordingEvent_1.RecordingEvent(
                this,
                'recordingStarted',
                response.id,
                response.name,
              ),
            ])
          }
          Session.prototype.onRecordingStopped = function(response) {
            this.ee.emitEvent('recordingStopped', [
              new RecordingEvent_1.RecordingEvent(
                this,
                'recordingStopped',
                response.id,
                response.name,
                response.reason,
              ),
            ])
          }
          Session.prototype.onFilterEventDispatched = function(response) {
            var connectionId = response.connectionId
            var streamId = response.streamId
            this.getConnection(
              connectionId,
              'No connection found for connectionId ' + connectionId,
            ).then(function(connection) {
              console.info('Filter event dispatched')
              var stream = connection.stream
              stream.filter.handlers[response.eventType](
                new FilterEvent_1.FilterEvent(
                  stream.filter,
                  response.eventType,
                  response.data,
                ),
              )
            })
          }
          Session.prototype.emitEvent = function(type, eventArray) {
            this.ee.emitEvent(type, eventArray)
          }
          Session.prototype.leave = function(forced, reason) {
            var _this = this
            forced = !!forced
            console.info('Leaving Session (forced=' + forced + ')')
            if (this.connection) {
              if (!this.connection.disposed && !forced) {
                this.openvidu.sendRequest('leaveRoom', function(
                  error,
                  response,
                ) {
                  if (error) {
                    console.error(error)
                  }
                  _this.openvidu.closeWs()
                })
              } else {
                this.openvidu.closeWs()
              }
              this.stopPublisherStream(reason)
              if (!this.connection.disposed) {
                var sessionDisconnectEvent = new SessionDisconnectedEvent_1.SessionDisconnectedEvent(
                  this,
                  reason,
                )
                this.ee.emitEvent('sessionDisconnected', [
                  sessionDisconnectEvent,
                ])
                sessionDisconnectEvent.callDefaultBehavior()
              }
            } else {
              console.warn(
                'You were not connected to the session ' + this.sessionId,
              )
            }
          }
          Session.prototype.connectAux = function(token) {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this.openvidu.startWs(function(error) {
                if (error) {
                  reject(error)
                } else {
                  var joinParams = {
                    token: token ? token : '',
                    session: _this.sessionId,
                    platform: platform.description
                      ? platform.description
                      : 'unknown',
                    metadata: _this.options.metadata
                      ? _this.options.metadata
                      : '',
                    secret: _this.openvidu.getSecret(),
                    recorder: _this.openvidu.getRecorder(),
                  }
                  _this.openvidu.sendRequest('joinRoom', joinParams, function(
                    error,
                    response,
                  ) {
                    if (error) {
                      reject(error)
                    } else {
                      _this.capabilities = {
                        subscribe: true,
                        publish: _this.openvidu.role !== 'SUBSCRIBER',
                        forceUnpublish: _this.openvidu.role === 'MODERATOR',
                        forceDisconnect: _this.openvidu.role === 'MODERATOR',
                      }
                      _this.connection = new Connection_1.Connection(_this)
                      _this.connection.connectionId = response.id
                      _this.connection.creationTime = response.createdAt
                      _this.connection.data = response.metadata
                      _this.connection.rpcSessionId = response.sessionId
                      var events_1 = {
                        connections: new Array(),
                        streams: new Array(),
                      }
                      var existingParticipants = response.value
                      existingParticipants.forEach(function(participant) {
                        var connection = new Connection_1.Connection(
                          _this,
                          participant,
                        )
                        _this.remoteConnections[
                          connection.connectionId
                        ] = connection
                        events_1.connections.push(connection)
                        if (connection.stream) {
                          _this.remoteStreamsCreated[
                            connection.stream.streamId
                          ] = true
                          events_1.streams.push(connection.stream)
                        }
                      })
                      _this.ee.emitEvent('connectionCreated', [
                        new ConnectionEvent_1.ConnectionEvent(
                          false,
                          _this,
                          'connectionCreated',
                          _this.connection,
                          '',
                        ),
                      ])
                      events_1.connections.forEach(function(connection) {
                        _this.ee.emitEvent('connectionCreated', [
                          new ConnectionEvent_1.ConnectionEvent(
                            false,
                            _this,
                            'connectionCreated',
                            connection,
                            '',
                          ),
                        ])
                      })
                      events_1.streams.forEach(function(stream) {
                        _this.ee.emitEvent('streamCreated', [
                          new StreamEvent_1.StreamEvent(
                            false,
                            _this,
                            'streamCreated',
                            stream,
                            '',
                          ),
                        ])
                      })
                      resolve()
                    }
                  })
                }
              })
            })
          }
          Session.prototype.stopPublisherStream = function(reason) {
            if (this.connection.stream) {
              this.connection.stream.disposeWebRtcPeer()
              if (this.connection.stream.isLocalStreamPublished) {
                this.connection.stream.ee.emitEvent('local-stream-destroyed', [
                  reason,
                ])
              }
            }
          }
          Session.prototype.stringClientMetadata = function(metadata) {
            if (typeof metadata !== 'string') {
              return JSON.stringify(metadata)
            } else {
              return metadata
            }
          }
          Session.prototype.getConnection = function(
            connectionId,
            errorMessage,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var connection = _this.remoteConnections[connectionId]
              if (connection) {
                resolve(connection)
              } else {
                if (_this.connection.connectionId === connectionId) {
                  resolve(_this.connection)
                } else {
                  reject(
                    new OpenViduError_1.OpenViduError(
                      OpenViduError_1.OpenViduErrorName.GENERIC_ERROR,
                      errorMessage,
                    ),
                  )
                }
              }
            })
          }
          Session.prototype.getRemoteConnection = function(
            connectionId,
            errorMessage,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var connection = _this.remoteConnections[connectionId]
              if (connection) {
                resolve(connection)
              } else {
                reject(
                  new OpenViduError_1.OpenViduError(
                    OpenViduError_1.OpenViduErrorName.GENERIC_ERROR,
                    errorMessage,
                  ),
                )
              }
            })
          }
          Session.prototype.processToken = function(token) {
            var match = token.match(
              /^(wss?\:)\/\/(([^:\/?#]*)(?:\:([0-9]+))?)([\/]{0,1}[^?#]*)(\?[^#]*|)(#.*|)$/,
            )
            if (match) {
              var url = {
                protocol: match[1],
                // host: match[2],
                host: '192.168.13.9',
                hostname: match[3],
                port: match[4],
                pathname: match[5],
                search: match[6],
                hash: match[7],
              }
              var params = token.split('?')
              var queryParams = decodeURI(params[1])
                .split('&')
                .map(function(param) {
                  return param.split('=')
                })
                .reduce(function(values, _a) {
                  var key = _a[0],
                    value = _a[1]
                  values[key] = value
                  return values
                }, {})
              this.sessionId = queryParams['sessionId']
              var secret = queryParams['secret']
              var recorder = queryParams['recorder']
              var turnUsername = queryParams['turnUsername']
              var turnCredential = queryParams['turnCredential']
              var role = queryParams['role']
              var webrtcStatsInterval = queryParams['webrtcStatsInterval']
              var openviduServerVersion = queryParams['version']
              if (secret) {
                this.openvidu.secret = secret
              }
              if (recorder) {
                this.openvidu.recorder = true
              }
              if (!!turnUsername && !!turnCredential) {
                var stunUrl = 'stun:' + url.hostname + ':3478'
                var turnUrl1 = 'turn:' + url.hostname + ':3478'
                var turnUrl2 = turnUrl1 + '?transport=tcp'
                this.openvidu.iceServers = [
                  { urls: [stunUrl] },
                  {
                    urls: [turnUrl1, turnUrl2],
                    username: turnUsername,
                    credential: turnCredential,
                  },
                ]
                console.log(
                  'TURN temp credentials [' +
                    turnUsername +
                    ':' +
                    turnCredential +
                    ']',
                )
              }
              if (role) {
                this.openvidu.role = role
              }
              if (webrtcStatsInterval) {
                this.openvidu.webrtcStatsInterval = +webrtcStatsInterval
              }
              if (openviduServerVersion) {
                console.info(
                  'openvidu-server version: ' + openviduServerVersion,
                )
                if (openviduServerVersion !== this.openvidu.libraryVersion) {
                  console.error(
                    'OpenVidu Server (' +
                      openviduServerVersion +
                      ') and OpenVidu Browser (' +
                      this.openvidu.libraryVersion +
                      ') versions do NOT match. There may be incompatibilities',
                  )
                }
              }
              // this.openvidu.wsUri = 'wss://' + url.host + '/openvidu';
              // this.openvidu.httpUri = 'https://' + url.host;
              this.openvidu.wsUri = 'wss://' + url.host + ':4443/openvidu'
              this.openvidu.httpUri = 'https://' + url.host + ':4443'
            } else {
              console.error('Token "' + token + '" is not valid')
            }
          }
          return Session
        })()
        exports.Session = Session
      },
      {
        '../OpenViduInternal/Enums/OpenViduError': 28,
        '../OpenViduInternal/Enums/VideoInsertMode': 29,
        '../OpenViduInternal/Events/ConnectionEvent': 30,
        '../OpenViduInternal/Events/FilterEvent': 32,
        '../OpenViduInternal/Events/RecordingEvent': 34,
        '../OpenViduInternal/Events/SessionDisconnectedEvent': 35,
        '../OpenViduInternal/Events/SignalEvent': 36,
        '../OpenViduInternal/Events/StreamEvent': 37,
        '../OpenViduInternal/Events/StreamPropertyChangedEvent': 39,
        './Connection': 18,
        './Filter': 19,
        './Subscriber': 26,
        platform: 8,
        'wolfy87-eventemitter': 15,
      },
    ],
    24: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var Filter_1 = require('./Filter')
        var Subscriber_1 = require('./Subscriber')
        var WebRtcPeer_1 = require('../OpenViduInternal/WebRtcPeer/WebRtcPeer')
        var WebRtcStats_1 = require('../OpenViduInternal/WebRtcStats/WebRtcStats')
        var PublisherSpeakingEvent_1 = require('../OpenViduInternal/Events/PublisherSpeakingEvent')
        var StreamManagerEvent_1 = require('../OpenViduInternal/Events/StreamManagerEvent')
        var StreamPropertyChangedEvent_1 = require('../OpenViduInternal/Events/StreamPropertyChangedEvent')
        var OpenViduError_1 = require('../OpenViduInternal/Enums/OpenViduError')
        var EventEmitter = require('wolfy87-eventemitter')
        var hark = require('hark')
        var platform = require('platform')
        var Stream = (function() {
          function Stream(session, options) {
            var _this = this
            this.ee = new EventEmitter()
            this.isSubscribeToRemote = false
            this.isLocalStreamReadyToPublish = false
            this.isLocalStreamPublished = false
            this.publishedOnce = false
            this.publisherStartSpeakingEventEnabled = false
            this.publisherStopSpeakingEventEnabled = false
            this.volumeChangeEventEnabled = false
            this.session = session
            if (options.hasOwnProperty('id')) {
              this.inboundStreamOpts = options
              this.streamId = this.inboundStreamOpts.id
              this.creationTime = this.inboundStreamOpts.createdAt
              this.hasAudio = this.inboundStreamOpts.hasAudio
              this.hasVideo = this.inboundStreamOpts.hasVideo
              if (this.hasAudio) {
                this.audioActive = this.inboundStreamOpts.audioActive
              }
              if (this.hasVideo) {
                this.videoActive = this.inboundStreamOpts.videoActive
                this.typeOfVideo = !this.inboundStreamOpts.typeOfVideo
                  ? undefined
                  : this.inboundStreamOpts.typeOfVideo
                this.frameRate =
                  this.inboundStreamOpts.frameRate === -1
                    ? undefined
                    : this.inboundStreamOpts.frameRate
                this.videoDimensions = this.inboundStreamOpts.videoDimensions
              }
              if (
                !!this.inboundStreamOpts.filter &&
                Object.keys(this.inboundStreamOpts.filter).length > 0
              ) {
                if (
                  !!this.inboundStreamOpts.filter.lastExecMethod &&
                  Object.keys(this.inboundStreamOpts.filter.lastExecMethod)
                    .length === 0
                ) {
                  delete this.inboundStreamOpts.filter.lastExecMethod
                }
                this.filter = this.inboundStreamOpts.filter
              }
            } else {
              this.outboundStreamOpts = options
              this.hasAudio = this.isSendAudio()
              this.hasVideo = this.isSendVideo()
              if (this.hasAudio) {
                this.audioActive = !!this.outboundStreamOpts.publisherProperties
                  .publishAudio
              }
              if (this.hasVideo) {
                this.videoActive = !!this.outboundStreamOpts.publisherProperties
                  .publishVideo
                this.frameRate = this.outboundStreamOpts.publisherProperties.frameRate
                if (
                  typeof MediaStreamTrack !== 'undefined' &&
                  this.outboundStreamOpts.publisherProperties
                    .videoSource instanceof MediaStreamTrack
                ) {
                  this.typeOfVideo = 'CUSTOM'
                } else {
                  this.typeOfVideo = this.isSendScreen() ? 'SCREEN' : 'CAMERA'
                }
              }
              if (this.outboundStreamOpts.publisherProperties.filter) {
                this.filter = this.outboundStreamOpts.publisherProperties.filter
              }
            }
            this.ee.on('mediastream-updated', function() {
              _this.streamManager.updateMediaStream(_this.mediaStream)
              console.debug(
                'Video srcObject [' +
                  _this.mediaStream +
                  '] updated in stream [' +
                  _this.streamId +
                  ']',
              )
            })
          }
          Stream.prototype.on = function(type, handler) {
            var _this = this
            this.ee.on(type, function(event) {
              if (event) {
                console.info(
                  "Event '" +
                    type +
                    "' triggered by stream '" +
                    _this.streamId +
                    "'",
                  event,
                )
              } else {
                console.info(
                  "Event '" +
                    type +
                    "' triggered by stream '" +
                    _this.streamId +
                    "'",
                )
              }
              handler(event)
            })
            return this
          }
          Stream.prototype.once = function(type, handler) {
            var _this = this
            this.ee.once(type, function(event) {
              if (event) {
                console.info(
                  "Event '" +
                    type +
                    "' triggered once by stream '" +
                    _this.streamId +
                    "'",
                  event,
                )
              } else {
                console.info(
                  "Event '" +
                    type +
                    "' triggered once by stream '" +
                    _this.streamId +
                    "'",
                )
              }
              handler(event)
            })
            return this
          }
          Stream.prototype.off = function(type, handler) {
            if (!handler) {
              this.ee.removeAllListeners(type)
            } else {
              this.ee.off(type, handler)
            }
            return this
          }
          Stream.prototype.applyFilter = function(type, options) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info('Applying filter to stream ' + _this.streamId)
              options = options ? options : {}
              if (typeof options !== 'string') {
                options = JSON.stringify(options)
              }
              _this.session.openvidu.sendRequest(
                'applyFilter',
                { streamId: _this.streamId, type: type, options: options },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error applying filter for Stream ' + _this.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to apply a filter",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    console.info(
                      'Filter successfully applied on Stream ' + _this.streamId,
                    )
                    var oldValue = _this.filter
                    _this.filter = new Filter_1.Filter(type, options)
                    _this.filter.stream = _this
                    _this.session.emitEvent('streamPropertyChanged', [
                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                        _this.session,
                        _this,
                        'filter',
                        _this.filter,
                        oldValue,
                        'applyFilter',
                      ),
                    ])
                    _this.streamManager.emitEvent('streamPropertyChanged', [
                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                        _this.streamManager,
                        _this,
                        'filter',
                        _this.filter,
                        oldValue,
                        'applyFilter',
                      ),
                    ])
                    resolve(_this.filter)
                  }
                },
              )
            })
          }
          Stream.prototype.removeFilter = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.info('Removing filter of stream ' + _this.streamId)
              _this.session.openvidu.sendRequest(
                'removeFilter',
                { streamId: _this.streamId },
                function(error, response) {
                  if (error) {
                    console.error(
                      'Error removing filter for Stream ' + _this.streamId,
                      error,
                    )
                    if (error.code === 401) {
                      reject(
                        new OpenViduError_1.OpenViduError(
                          OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                          "You don't have permissions to remove a filter",
                        ),
                      )
                    } else {
                      reject(error)
                    }
                  } else {
                    console.info(
                      'Filter successfully removed from Stream ' +
                        _this.streamId,
                    )
                    var oldValue = _this.filter
                    delete _this.filter
                    _this.session.emitEvent('streamPropertyChanged', [
                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                        _this.session,
                        _this,
                        'filter',
                        _this.filter,
                        oldValue,
                        'applyFilter',
                      ),
                    ])
                    _this.streamManager.emitEvent('streamPropertyChanged', [
                      new StreamPropertyChangedEvent_1.StreamPropertyChangedEvent(
                        _this.streamManager,
                        _this,
                        'filter',
                        _this.filter,
                        oldValue,
                        'applyFilter',
                      ),
                    ])
                    resolve()
                  }
                },
              )
            })
          }
          Stream.prototype.getMediaStream = function() {
            return this.mediaStream
          }
          Stream.prototype.setMediaStream = function(mediaStream) {
            this.mediaStream = mediaStream
          }
          Stream.prototype.updateMediaStreamInVideos = function() {
            this.ee.emitEvent('mediastream-updated', [])
          }
          Stream.prototype.getWebRtcPeer = function() {
            return this.webRtcPeer
          }
          Stream.prototype.getRTCPeerConnection = function() {
            return this.webRtcPeer.pc
          }
          Stream.prototype.subscribeToMyRemote = function(value) {
            this.isSubscribeToRemote = value
          }
          Stream.prototype.setOutboundStreamOptions = function(
            outboundStreamOpts,
          ) {
            this.outboundStreamOpts = outboundStreamOpts
          }
          Stream.prototype.subscribe = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this
                .initWebRtcPeerReceive()
                .then(function() {
                  resolve()
                })
                .catch(function(error) {
                  reject(error)
                })
            })
          }
          Stream.prototype.publish = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              if (_this.isLocalStreamReadyToPublish) {
                _this
                  .initWebRtcPeerSend()
                  .then(function() {
                    resolve()
                  })
                  .catch(function(error) {
                    reject(error)
                  })
              } else {
                _this.ee.once('stream-ready-to-publish', function() {
                  _this
                    .publish()
                    .then(function() {
                      resolve()
                    })
                    .catch(function(error) {
                      reject(error)
                    })
                })
              }
            })
          }
          Stream.prototype.disposeWebRtcPeer = function() {
            if (this.webRtcPeer) {
              var isSenderAndCustomTrack =
                !!this.outboundStreamOpts &&
                typeof MediaStreamTrack !== 'undefined' &&
                this.outboundStreamOpts.publisherProperties
                  .videoSource instanceof MediaStreamTrack
              this.webRtcPeer.dispose(isSenderAndCustomTrack)
            }
            if (this.speechEvent) {
              if (this.speechEvent.stop) {
                this.speechEvent.stop()
              }
              delete this.speechEvent
            }
            this.stopWebRtcStats()
            console.info(
              (this.outboundStreamOpts ? 'Outbound ' : 'Inbound ') +
                "WebRTCPeer from 'Stream' with id [" +
                this.streamId +
                '] is now closed',
            )
          }
          Stream.prototype.disposeMediaStream = function() {
            if (this.mediaStream) {
              this.mediaStream.getAudioTracks().forEach(function(track) {
                track.stop()
              })
              this.mediaStream.getVideoTracks().forEach(function(track) {
                track.stop()
              })
              delete this.mediaStream
            }
            console.info(
              (this.outboundStreamOpts ? 'Local ' : 'Remote ') +
                "MediaStream from 'Stream' with id [" +
                this.streamId +
                '] is now disposed',
            )
          }
          Stream.prototype.displayMyRemote = function() {
            return this.isSubscribeToRemote
          }
          Stream.prototype.isSendAudio = function() {
            return (
              !!this.outboundStreamOpts &&
              this.outboundStreamOpts.publisherProperties.audioSource !==
                null &&
              this.outboundStreamOpts.publisherProperties.audioSource !== false
            )
          }
          Stream.prototype.isSendVideo = function() {
            return (
              !!this.outboundStreamOpts &&
              this.outboundStreamOpts.publisherProperties.videoSource !==
                null &&
              this.outboundStreamOpts.publisherProperties.videoSource !== false
            )
          }
          Stream.prototype.isSendScreen = function() {
            var screen =
              this.outboundStreamOpts.publisherProperties.videoSource ===
              'screen'
            if (platform.name === 'Electron') {
              screen =
                typeof this.outboundStreamOpts.publisherProperties
                  .videoSource === 'string' &&
                this.outboundStreamOpts.publisherProperties.videoSource.startsWith(
                  'screen:',
                )
            }
            return !!this.outboundStreamOpts && screen
          }
          Stream.prototype.setSpeechEventIfNotExists = function() {
            if (!this.speechEvent) {
              var harkOptions =
                this.session.openvidu.advancedConfiguration
                  .publisherSpeakingEventsOptions || {}
              harkOptions.interval =
                typeof harkOptions.interval === 'number'
                  ? harkOptions.interval
                  : 50
              harkOptions.threshold =
                typeof harkOptions.threshold === 'number'
                  ? harkOptions.threshold
                  : -50
              this.speechEvent = hark(this.mediaStream, harkOptions)
            }
          }
          Stream.prototype.enableSpeakingEvents = function() {
            var _this = this
            this.setSpeechEventIfNotExists()
            if (!this.publisherStartSpeakingEventEnabled) {
              this.publisherStartSpeakingEventEnabled = true
              this.speechEvent.on('speaking', function() {
                _this.session.emitEvent('publisherStartSpeaking', [
                  new PublisherSpeakingEvent_1.PublisherSpeakingEvent(
                    _this.session,
                    'publisherStartSpeaking',
                    _this.connection,
                    _this.streamId,
                  ),
                ])
              })
            }
            if (!this.publisherStopSpeakingEventEnabled) {
              this.publisherStopSpeakingEventEnabled = true
              this.speechEvent.on('stopped_speaking', function() {
                _this.session.emitEvent('publisherStopSpeaking', [
                  new PublisherSpeakingEvent_1.PublisherSpeakingEvent(
                    _this.session,
                    'publisherStopSpeaking',
                    _this.connection,
                    _this.streamId,
                  ),
                ])
              })
            }
          }
          Stream.prototype.enableOnceSpeakingEvents = function() {
            var _this = this
            this.setSpeechEventIfNotExists()
            if (!this.publisherStartSpeakingEventEnabled) {
              this.publisherStartSpeakingEventEnabled = true
              this.speechEvent.once('speaking', function() {
                _this.session.emitEvent('publisherStartSpeaking', [
                  new PublisherSpeakingEvent_1.PublisherSpeakingEvent(
                    _this.session,
                    'publisherStartSpeaking',
                    _this.connection,
                    _this.streamId,
                  ),
                ])
                _this.disableSpeakingEvents()
              })
            }
            if (!this.publisherStopSpeakingEventEnabled) {
              this.publisherStopSpeakingEventEnabled = true
              this.speechEvent.once('stopped_speaking', function() {
                _this.session.emitEvent('publisherStopSpeaking', [
                  new PublisherSpeakingEvent_1.PublisherSpeakingEvent(
                    _this.session,
                    'publisherStopSpeaking',
                    _this.connection,
                    _this.streamId,
                  ),
                ])
                _this.disableSpeakingEvents()
              })
            }
          }
          Stream.prototype.disableSpeakingEvents = function() {
            if (this.speechEvent) {
              if (this.volumeChangeEventEnabled) {
                this.speechEvent.off('speaking')
                this.speechEvent.off('stopped_speaking')
              } else {
                this.speechEvent.stop()
                delete this.speechEvent
              }
            }
            this.publisherStartSpeakingEventEnabled = false
            this.publisherStopSpeakingEventEnabled = false
          }
          Stream.prototype.enableVolumeChangeEvent = function() {
            var _this = this
            this.setSpeechEventIfNotExists()
            if (!this.volumeChangeEventEnabled) {
              this.volumeChangeEventEnabled = true
              this.speechEvent.on('volume_change', function(harkEvent) {
                var oldValue = _this.speechEvent.oldVolumeValue
                var value = { newValue: harkEvent, oldValue: oldValue }
                _this.speechEvent.oldVolumeValue = harkEvent
                _this.streamManager.emitEvent('streamAudioVolumeChange', [
                  new StreamManagerEvent_1.StreamManagerEvent(
                    _this.streamManager,
                    'streamAudioVolumeChange',
                    value,
                  ),
                ])
              })
            }
          }
          Stream.prototype.enableOnceVolumeChangeEvent = function() {
            var _this = this
            this.setSpeechEventIfNotExists()
            if (!this.volumeChangeEventEnabled) {
              this.volumeChangeEventEnabled = true
              this.speechEvent.once('volume_change', function(harkEvent) {
                var oldValue = _this.speechEvent.oldVolumeValue
                var value = { newValue: harkEvent, oldValue: oldValue }
                _this.speechEvent.oldVolumeValue = harkEvent
                _this.disableVolumeChangeEvent()
                _this.streamManager.emitEvent('streamAudioVolumeChange', [
                  new StreamManagerEvent_1.StreamManagerEvent(
                    _this.streamManager,
                    'streamAudioVolumeChange',
                    value,
                  ),
                ])
              })
            }
          }
          Stream.prototype.disableVolumeChangeEvent = function() {
            if (this.speechEvent) {
              if (this.session.speakingEventsEnabled) {
                this.speechEvent.off('volume_change')
              } else {
                this.speechEvent.stop()
                delete this.speechEvent
              }
            }
            this.volumeChangeEventEnabled = false
          }
          Stream.prototype.isLocal = function() {
            return !this.inboundStreamOpts && !!this.outboundStreamOpts
          }
          Stream.prototype.getSelectedIceCandidate = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this.webRtcStats
                .getSelectedIceCandidateInfo()
                .then(function(report) {
                  return resolve(report)
                })
                .catch(function(error) {
                  return reject(error)
                })
            })
          }
          Stream.prototype.getRemoteIceCandidateList = function() {
            return this.webRtcPeer.remoteCandidatesQueue
          }
          Stream.prototype.getLocalIceCandidateList = function() {
            return this.webRtcPeer.localCandidatesQueue
          }
          Stream.prototype.initWebRtcPeerSend = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              var userMediaConstraints = {
                audio: _this.isSendAudio(),
                video: _this.isSendVideo(),
              }
              var options = {
                mediaStream: _this.mediaStream,
                mediaConstraints: userMediaConstraints,
                onicecandidate: _this.connection.sendIceCandidate.bind(
                  _this.connection,
                ),
                iceServers: _this.getIceServersConf(),
                simulcast: false,
              }
              var successCallback = function(sdpOfferParam) {
                console.debug(
                  'Sending SDP offer to publish as ' + _this.streamId,
                  sdpOfferParam,
                )
                var typeOfVideo = ''
                if (_this.isSendVideo()) {
                  typeOfVideo =
                    typeof MediaStreamTrack !== 'undefined' &&
                    _this.outboundStreamOpts.publisherProperties
                      .videoSource instanceof MediaStreamTrack
                      ? 'CUSTOM'
                      : _this.isSendScreen()
                      ? 'SCREEN'
                      : 'CAMERA'
                }
                _this.session.openvidu.sendRequest(
                  'publishVideo',
                  {
                    sdpOffer: sdpOfferParam,
                    doLoopback: _this.displayMyRemote() || false,
                    hasAudio: _this.isSendAudio(),
                    hasVideo: _this.isSendVideo(),
                    audioActive: _this.audioActive,
                    videoActive: _this.videoActive,
                    typeOfVideo: typeOfVideo,
                    frameRate: _this.frameRate ? _this.frameRate : -1,
                    videoDimensions: JSON.stringify(_this.videoDimensions),
                    filter: _this.outboundStreamOpts.publisherProperties.filter,
                  },
                  function(error, response) {
                    if (error) {
                      if (error.code === 401) {
                        reject(
                          new OpenViduError_1.OpenViduError(
                            OpenViduError_1.OpenViduErrorName.OPENVIDU_PERMISSION_DENIED,
                            "You don't have permissions to publish",
                          ),
                        )
                      } else {
                        reject(
                          'Error on publishVideo: ' + JSON.stringify(error),
                        )
                      }
                    } else {
                      _this.webRtcPeer
                        .processAnswer(response.sdpAnswer, false)
                        .then(function() {
                          _this.streamId = response.id
                          _this.creationTime = response.createdAt
                          _this.isLocalStreamPublished = true
                          _this.publishedOnce = true
                          if (_this.displayMyRemote()) {
                            _this.remotePeerSuccessfullyEstablished()
                          }
                          _this.ee.emitEvent('stream-created-by-publisher', [])
                          _this.initWebRtcStats()
                          resolve()
                        })
                        .catch(function(error) {
                          reject(error)
                        })
                      console.info(
                        "'Publisher' successfully published to session",
                      )
                    }
                  },
                )
              }
              if (_this.displayMyRemote()) {
                _this.webRtcPeer = new WebRtcPeer_1.WebRtcPeerSendrecv(options)
              } else {
                _this.webRtcPeer = new WebRtcPeer_1.WebRtcPeerSendonly(options)
              }
              _this.webRtcPeer
                .generateOffer()
                .then(function(offer) {
                  successCallback(offer)
                })
                .catch(function(error) {
                  reject(
                    new Error(
                      '(publish) SDP offer error: ' + JSON.stringify(error),
                    ),
                  )
                })
            })
          }
          Stream.prototype.initWebRtcPeerReceive = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              var offerConstraints = {
                audio: _this.inboundStreamOpts.hasAudio,
                video: _this.inboundStreamOpts.hasVideo,
              }
              console.debug(
                "'Session.subscribe(Stream)' called. Constraints of generate SDP offer",
                offerConstraints,
              )
              var options = {
                onicecandidate: _this.connection.sendIceCandidate.bind(
                  _this.connection,
                ),
                mediaConstraints: offerConstraints,
                iceServers: _this.getIceServersConf(),
                simulcast: false,
              }
              var successCallback = function(sdpOfferParam) {
                console.debug(
                  'Sending SDP offer to subscribe to ' + _this.streamId,
                  sdpOfferParam,
                )
                _this.session.openvidu.sendRequest(
                  'receiveVideoFrom',
                  {
                    sender: _this.streamId,
                    sdpOffer: sdpOfferParam,
                  },
                  function(error, response) {
                    if (error) {
                      reject(
                        new Error(
                          'Error on recvVideoFrom: ' + JSON.stringify(error),
                        ),
                      )
                    } else {
                      if (_this.session.isFirstIonicIosSubscriber) {
                        _this.session.isFirstIonicIosSubscriber = false
                        setTimeout(function() {
                          _this.session.countDownForIonicIosSubscribersActive = false
                        }, 400)
                      }
                      var needsTimeoutOnProcessAnswer =
                        _this.session.countDownForIonicIosSubscribersActive
                      _this.webRtcPeer
                        .processAnswer(
                          response.sdpAnswer,
                          needsTimeoutOnProcessAnswer,
                        )
                        .then(function() {
                          _this.remotePeerSuccessfullyEstablished()
                          _this.initWebRtcStats()
                          resolve()
                        })
                        .catch(function(error) {
                          reject(error)
                        })
                    }
                  },
                )
              }
              _this.webRtcPeer = new WebRtcPeer_1.WebRtcPeerRecvonly(options)
              _this.webRtcPeer
                .generateOffer()
                .then(function(offer) {
                  successCallback(offer)
                })
                .catch(function(error) {
                  reject(
                    new Error(
                      '(subscribe) SDP offer error: ' + JSON.stringify(error),
                    ),
                  )
                })
            })
          }
          Stream.prototype.remotePeerSuccessfullyEstablished = function() {
            if (platform['isIonicIos']) {
              var pc1 = this.webRtcPeer.pc
              this.mediaStream = pc1.getRemoteStreams()[0]
            } else {
              this.mediaStream = new MediaStream()
              var receiver = void 0
              for (
                var _i = 0, _a = this.webRtcPeer.pc.getReceivers();
                _i < _a.length;
                _i++
              ) {
                receiver = _a[_i]
                if (receiver.track) {
                  this.mediaStream.addTrack(receiver.track)
                }
              }
            }
            console.debug('Peer remote stream', this.mediaStream)
            if (this.mediaStream) {
              if (this.streamManager instanceof Subscriber_1.Subscriber) {
                if (this.mediaStream.getAudioTracks()[0]) {
                  var enabled = !!this.streamManager.properties.subscribeToAudio
                  this.mediaStream.getAudioTracks()[0].enabled = enabled
                }
                if (this.mediaStream.getVideoTracks()[0]) {
                  var enabled = !!this.streamManager.properties.subscribeToVideo
                  this.mediaStream.getVideoTracks()[0].enabled = enabled
                }
              }
              this.updateMediaStreamInVideos()
              if (
                !this.displayMyRemote() &&
                !!this.mediaStream.getAudioTracks()[0] &&
                this.session.speakingEventsEnabled
              ) {
                this.enableSpeakingEvents()
              }
            }
          }
          Stream.prototype.initWebRtcStats = function() {
            this.webRtcStats = new WebRtcStats_1.WebRtcStats(this)
            this.webRtcStats.initWebRtcStats()
          }
          Stream.prototype.stopWebRtcStats = function() {
            if (!!this.webRtcStats && this.webRtcStats.isEnabled()) {
              this.webRtcStats.stopWebRtcStats()
            }
          }
          Stream.prototype.getIceServersConf = function() {
            var returnValue
            if (this.session.openvidu.advancedConfiguration.iceServers) {
              returnValue =
                this.session.openvidu.advancedConfiguration.iceServers ===
                'freeice'
                  ? undefined
                  : this.session.openvidu.advancedConfiguration.iceServers
            } else if (this.session.openvidu.iceServers) {
              returnValue = this.session.openvidu.iceServers
            } else {
              returnValue = undefined
            }
            return returnValue
          }
          Stream.prototype.gatherStatsForPeer = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              if (_this.isLocal()) {
                _this
                  .getRTCPeerConnection()
                  .getSenders()
                  .forEach(function(sender) {
                    return sender.getStats().then(function(response) {
                      response.forEach(function(report) {
                        if (_this.isReportWanted(report)) {
                          var finalReport = {}
                          finalReport['type'] = report.type
                          finalReport['timestamp'] = report.timestamp
                          finalReport['id'] = report.id
                          if (report.type === 'outbound-rtp') {
                            finalReport['ssrc'] = report.ssrc
                            finalReport['firCount'] = report.firCount
                            finalReport['pliCount'] = report.pliCount
                            finalReport['nackCount'] = report.nackCount
                            finalReport['qpSum'] = report.qpSum
                            if (report.kind) {
                              finalReport['mediaType'] = report.kind
                            } else if (report.mediaType) {
                              finalReport['mediaType'] = report.mediaType
                            } else {
                              finalReport['mediaType'] =
                                report.id.indexOf('VideoStream') !== -1
                                  ? 'video'
                                  : 'audio'
                            }
                            if (finalReport['mediaType'] === 'video') {
                              finalReport['framesEncoded'] =
                                report.framesEncoded
                            }
                            finalReport['packetsSent'] = report.packetsSent
                            finalReport['bytesSent'] = report.bytesSent
                          }
                          if (
                            report.type === 'candidate-pair' &&
                            report.totalRoundTripTime !== undefined
                          ) {
                            finalReport['availableOutgoingBitrate'] =
                              report.availableOutgoingBitrate
                            finalReport['rtt'] = report.currentRoundTripTime
                            finalReport['averageRtt'] =
                              report.totalRoundTripTime /
                              report.responsesReceived
                          }
                          if (
                            report.type === 'remote-inbound-rtp' ||
                            report.type === 'remote-outbound-rtp'
                          ) {
                          }
                          console.log(finalReport)
                        }
                      })
                    })
                  })
              } else {
                _this
                  .getRTCPeerConnection()
                  .getReceivers()
                  .forEach(function(receiver) {
                    return receiver.getStats().then(function(response) {
                      response.forEach(function(report) {
                        if (_this.isReportWanted(report)) {
                          var finalReport = {}
                          finalReport['type'] = report.type
                          finalReport['timestamp'] = report.timestamp
                          finalReport['id'] = report.id
                          if (report.type === 'inbound-rtp') {
                            finalReport['ssrc'] = report.ssrc
                            finalReport['firCount'] = report.firCount
                            finalReport['pliCount'] = report.pliCount
                            finalReport['nackCount'] = report.nackCount
                            finalReport['qpSum'] = report.qpSum
                            if (report.kind) {
                              finalReport['mediaType'] = report.kind
                            } else if (report.mediaType) {
                              finalReport['mediaType'] = report.mediaType
                            } else {
                              finalReport['mediaType'] =
                                report.id.indexOf('VideoStream') !== -1
                                  ? 'video'
                                  : 'audio'
                            }
                            if (finalReport['mediaType'] === 'video') {
                              finalReport['framesDecoded'] =
                                report.framesDecoded
                            }
                            finalReport['packetsReceived'] =
                              report.packetsReceived
                            finalReport['packetsLost'] = report.packetsLost
                            finalReport['jitter'] = report.jitter
                            finalReport['bytesReceived'] = report.bytesReceived
                          }
                          if (
                            report.type === 'candidate-pair' &&
                            report.totalRoundTripTime !== undefined
                          ) {
                            finalReport['availableIncomingBitrate'] =
                              report.availableIncomingBitrate
                            finalReport['rtt'] = report.currentRoundTripTime
                            finalReport['averageRtt'] =
                              report.totalRoundTripTime /
                              report.responsesReceived
                          }
                          if (
                            report.type === 'remote-inbound-rtp' ||
                            report.type === 'remote-outbound-rtp'
                          ) {
                          }
                          console.log(finalReport)
                        }
                      })
                    })
                  })
              }
            })
          }
          Stream.prototype.isReportWanted = function(report) {
            return (
              (report.type === 'inbound-rtp' && !this.isLocal()) ||
              (report.type === 'outbound-rtp' && this.isLocal()) ||
              (report.type === 'candidate-pair' &&
                report.nominated &&
                report.bytesSent > 0)
            )
          }
          return Stream
        })()
        exports.Stream = Stream
      },
      {
        '../OpenViduInternal/Enums/OpenViduError': 28,
        '../OpenViduInternal/Events/PublisherSpeakingEvent': 33,
        '../OpenViduInternal/Events/StreamManagerEvent': 38,
        '../OpenViduInternal/Events/StreamPropertyChangedEvent': 39,
        '../OpenViduInternal/WebRtcPeer/WebRtcPeer': 52,
        '../OpenViduInternal/WebRtcStats/WebRtcStats': 53,
        './Filter': 19,
        './Subscriber': 26,
        hark: 5,
        platform: 8,
        'wolfy87-eventemitter': 15,
      },
    ],
    25: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var StreamManagerEvent_1 = require('../OpenViduInternal/Events/StreamManagerEvent')
        var VideoElementEvent_1 = require('../OpenViduInternal/Events/VideoElementEvent')
        var VideoInsertMode_1 = require('../OpenViduInternal/Enums/VideoInsertMode')
        var EventEmitter = require('wolfy87-eventemitter')
        var platform = require('platform')
        var StreamManager = (function() {
          function StreamManager(stream, targetElement) {
            var _this = this
            this.videos = []
            this.lazyLaunchVideoElementCreatedEvent = false
            this.ee = new EventEmitter()
            this.stream = stream
            this.stream.streamManager = this
            this.remote = !this.stream.isLocal()
            if (targetElement) {
              var targEl = void 0
              if (typeof targetElement === 'string') {
                targEl = document.getElementById(targetElement)
              } else if (targetElement instanceof HTMLElement) {
                targEl = targetElement
              }
              if (targEl) {
                this.firstVideoElement = {
                  targetElement: targEl,
                  video: document.createElement('video'),
                  id: '',
                  canplayListenerAdded: false,
                }
                if (platform.name === 'Safari') {
                  this.firstVideoElement.video.setAttribute(
                    'playsinline',
                    'true',
                  )
                }
                this.targetElement = targEl
                this.element = targEl
              }
            }
            this.canPlayListener = function() {
              if (_this.stream.isLocal()) {
                if (!_this.stream.displayMyRemote()) {
                  console.info(
                    "Your local 'Stream' with id [" +
                      _this.stream.streamId +
                      '] video is now playing',
                  )
                  _this.ee.emitEvent('videoPlaying', [
                    new VideoElementEvent_1.VideoElementEvent(
                      _this.videos[0].video,
                      _this,
                      'videoPlaying',
                    ),
                  ])
                } else {
                  console.info(
                    "Your own remote 'Stream' with id [" +
                      _this.stream.streamId +
                      '] video is now playing',
                  )
                  _this.ee.emitEvent('remoteVideoPlaying', [
                    new VideoElementEvent_1.VideoElementEvent(
                      _this.videos[0].video,
                      _this,
                      'remoteVideoPlaying',
                    ),
                  ])
                }
              } else {
                console.info(
                  "Remote 'Stream' with id [" +
                    _this.stream.streamId +
                    '] video is now playing',
                )
                _this.ee.emitEvent('videoPlaying', [
                  new VideoElementEvent_1.VideoElementEvent(
                    _this.videos[0].video,
                    _this,
                    'videoPlaying',
                  ),
                ])
              }
              _this.ee.emitEvent('streamPlaying', [
                new StreamManagerEvent_1.StreamManagerEvent(
                  _this,
                  'streamPlaying',
                  undefined,
                ),
              ])
            }
          }
          StreamManager.prototype.on = function(type, handler) {
            var _this = this
            this.ee.on(type, function(event) {
              if (event) {
                console.info(
                  "Event '" +
                    type +
                    "' triggered by '" +
                    (_this.remote ? 'Subscriber' : 'Publisher') +
                    "'",
                  event,
                )
              } else {
                console.info(
                  "Event '" +
                    type +
                    "' triggered by '" +
                    (_this.remote ? 'Subscriber' : 'Publisher') +
                    "'",
                )
              }
              handler(event)
            })
            if (type === 'videoElementCreated') {
              if (!!this.stream && this.lazyLaunchVideoElementCreatedEvent) {
                this.ee.emitEvent('videoElementCreated', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'videoElementCreated',
                  ),
                ])
                this.lazyLaunchVideoElementCreatedEvent = false
              }
            }
            if (type === 'streamPlaying' || type === 'videoPlaying') {
              if (
                this.videos[0] &&
                this.videos[0].video &&
                this.videos[0].video.currentTime > 0 &&
                this.videos[0].video.paused === false &&
                this.videos[0].video.ended === false &&
                this.videos[0].video.readyState === 4
              ) {
                this.ee.emitEvent('streamPlaying', [
                  new StreamManagerEvent_1.StreamManagerEvent(
                    this,
                    'streamPlaying',
                    undefined,
                  ),
                ])
                this.ee.emitEvent('videoPlaying', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'videoPlaying',
                  ),
                ])
              }
            }
            if (type === 'streamAudioVolumeChange' && this.stream.hasAudio) {
              this.stream.enableVolumeChangeEvent()
            }
            return this
          }
          StreamManager.prototype.once = function(type, handler) {
            this.ee.once(type, function(event) {
              if (event) {
                console.info("Event '" + type + "' triggered once", event)
              } else {
                console.info("Event '" + type + "' triggered once")
              }
              handler(event)
            })
            if (type === 'videoElementCreated') {
              if (!!this.stream && this.lazyLaunchVideoElementCreatedEvent) {
                this.ee.emitEvent('videoElementCreated', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'videoElementCreated',
                  ),
                ])
              }
            }
            if (type === 'streamPlaying' || type === 'videoPlaying') {
              if (
                this.videos[0] &&
                this.videos[0].video &&
                this.videos[0].video.currentTime > 0 &&
                this.videos[0].video.paused === false &&
                this.videos[0].video.ended === false &&
                this.videos[0].video.readyState === 4
              ) {
                this.ee.emitEvent('streamPlaying', [
                  new StreamManagerEvent_1.StreamManagerEvent(
                    this,
                    'streamPlaying',
                    undefined,
                  ),
                ])
                this.ee.emitEvent('videoPlaying', [
                  new VideoElementEvent_1.VideoElementEvent(
                    this.videos[0].video,
                    this,
                    'videoPlaying',
                  ),
                ])
              }
            }
            if (type === 'streamAudioVolumeChange' && this.stream.hasAudio) {
              this.stream.enableOnceVolumeChangeEvent()
            }
            return this
          }
          StreamManager.prototype.off = function(type, handler) {
            if (!handler) {
              this.ee.removeAllListeners(type)
            } else {
              this.ee.off(type, handler)
            }
            if (type === 'streamAudioVolumeChange') {
              this.stream.disableVolumeChangeEvent()
            }
            return this
          }
          StreamManager.prototype.addVideoElement = function(video) {
            this.initializeVideoProperties(video)
            if (this.stream.isLocal() && this.stream.displayMyRemote()) {
              if (video.srcObject !== this.stream.getMediaStream()) {
                video.srcObject = this.stream.getMediaStream()
              }
            }
            for (var _i = 0, _a = this.videos; _i < _a.length; _i++) {
              var v = _a[_i]
              if (v.video === video) {
                return 0
              }
            }
            var returnNumber = 1
            for (
              var _b = 0, _c = this.stream.session.streamManagers;
              _b < _c.length;
              _b++
            ) {
              var streamManager = _c[_b]
              if (streamManager.disassociateVideo(video)) {
                returnNumber = -1
                break
              }
            }
            this.stream.session.streamManagers.forEach(function(streamManager) {
              streamManager.disassociateVideo(video)
            })
            this.pushNewStreamManagerVideo({
              video: video,
              id: video.id,
              canplayListenerAdded: false,
            })
            console.info('New video element associated to ', this)
            return returnNumber
          }
          StreamManager.prototype.createVideoElement = function(
            targetElement,
            insertMode,
          ) {
            var targEl
            if (typeof targetElement === 'string') {
              targEl = document.getElementById(targetElement)
              if (!targEl) {
                throw new Error(
                  "The provided 'targetElement' couldn't be resolved to any HTML element: " +
                    targetElement,
                )
              }
            } else if (targetElement instanceof HTMLElement) {
              targEl = targetElement
            } else {
              throw new Error(
                "The provided 'targetElement' couldn't be resolved to any HTML element: " +
                  targetElement,
              )
            }
            var video = document.createElement('video')
            this.initializeVideoProperties(video)
            var insMode = insertMode
              ? insertMode
              : VideoInsertMode_1.VideoInsertMode.APPEND
            switch (insMode) {
              case VideoInsertMode_1.VideoInsertMode.AFTER:
                targEl.parentNode.insertBefore(video, targEl.nextSibling)
                break
              case VideoInsertMode_1.VideoInsertMode.APPEND:
                targEl.appendChild(video)
                break
              case VideoInsertMode_1.VideoInsertMode.BEFORE:
                targEl.parentNode.insertBefore(video, targEl)
                break
              case VideoInsertMode_1.VideoInsertMode.PREPEND:
                targEl.insertBefore(video, targEl.childNodes[0])
                break
              case VideoInsertMode_1.VideoInsertMode.REPLACE:
                targEl.parentNode.replaceChild(video, targEl)
                break
              default:
                insMode = VideoInsertMode_1.VideoInsertMode.APPEND
                targEl.appendChild(video)
                break
            }
            var v = {
              targetElement: targEl,
              video: video,
              insertMode: insMode,
              id: video.id,
              canplayListenerAdded: false,
            }
            this.pushNewStreamManagerVideo(v)
            this.ee.emitEvent('videoElementCreated', [
              new VideoElementEvent_1.VideoElementEvent(
                v.video,
                this,
                'videoElementCreated',
              ),
            ])
            this.lazyLaunchVideoElementCreatedEvent = !!this.firstVideoElement
            return video
          }
          StreamManager.prototype.initializeVideoProperties = function(video) {
            if (!(this.stream.isLocal() && this.stream.displayMyRemote())) {
              if (video.srcObject !== this.stream.getMediaStream()) {
                video.srcObject = this.stream.getMediaStream()
              }
            }
            video.autoplay = true
            video.controls = false
            if (platform.name === 'Safari') {
              video.setAttribute('playsinline', 'true')
            }
            if (!video.id) {
              video.id =
                (this.remote ? 'remote-' : 'local-') +
                'video-' +
                this.stream.streamId
              if (!this.id && !!this.targetElement) {
                this.id = video.id
              }
            }
            if (!this.remote && !this.stream.displayMyRemote()) {
              video.muted = true
              if (
                video.style.transform === 'rotateY(180deg)' &&
                !this.stream.outboundStreamOpts.publisherProperties.mirror
              ) {
                this.removeMirrorVideo(video)
              } else if (
                this.stream.outboundStreamOpts.publisherProperties.mirror &&
                !this.stream.isSendScreen()
              ) {
                this.mirrorVideo(video)
              }
            }
          }
          StreamManager.prototype.removeAllVideos = function() {
            var _this = this
            for (
              var i = this.stream.session.streamManagers.length - 1;
              i >= 0;
              --i
            ) {
              if (this.stream.session.streamManagers[i] === this) {
                this.stream.session.streamManagers.splice(i, 1)
              }
            }
            this.videos.forEach(function(streamManagerVideo) {
              streamManagerVideo.video.removeEventListener(
                'canplay',
                _this.canPlayListener,
              )
              streamManagerVideo.canplayListenerAdded = false
              if (streamManagerVideo.targetElement) {
                streamManagerVideo.video.parentNode.removeChild(
                  streamManagerVideo.video,
                )
                _this.ee.emitEvent('videoElementDestroyed', [
                  new VideoElementEvent_1.VideoElementEvent(
                    streamManagerVideo.video,
                    _this,
                    'videoElementDestroyed',
                  ),
                ])
              }
              streamManagerVideo.video.srcObject = null
              _this.videos.filter(function(v) {
                return !v.targetElement
              })
            })
          }
          StreamManager.prototype.disassociateVideo = function(video) {
            var disassociated = false
            for (var i = 0; i < this.videos.length; i++) {
              if (this.videos[i].video === video) {
                this.videos[i].video.removeEventListener(
                  'canplay',
                  this.canPlayListener,
                )
                this.videos.splice(i, 1)
                disassociated = true
                console.info('Video element disassociated from ', this)
                break
              }
            }
            return disassociated
          }
          StreamManager.prototype.addPlayEventToFirstVideo = function() {
            if (
              !!this.videos[0] &&
              !!this.videos[0].video &&
              !this.videos[0].canplayListenerAdded
            ) {
              this.videos[0].video.addEventListener(
                'canplay',
                this.canPlayListener,
              )
              this.videos[0].canplayListenerAdded = true
            }
          }
          StreamManager.prototype.updateMediaStream = function(mediaStream) {
            this.videos.forEach(function(streamManagerVideo) {
              streamManagerVideo.video.srcObject = mediaStream
              if (platform['isIonicIos']) {
                var vParent = streamManagerVideo.video.parentElement
                var newVideo = streamManagerVideo.video
                vParent.replaceChild(newVideo, streamManagerVideo.video)
                streamManagerVideo.video = newVideo
              }
            })
          }
          StreamManager.prototype.emitEvent = function(type, eventArray) {
            this.ee.emitEvent(type, eventArray)
          }
          StreamManager.prototype.pushNewStreamManagerVideo = function(
            streamManagerVideo,
          ) {
            this.videos.push(streamManagerVideo)
            this.addPlayEventToFirstVideo()
            if (this.stream.session.streamManagers.indexOf(this) === -1) {
              this.stream.session.streamManagers.push(this)
            }
          }
          StreamManager.prototype.mirrorVideo = function(video) {
            if (!platform['isIonicIos']) {
              video.style.transform = 'rotateY(180deg)'
              video.style.webkitTransform = 'rotateY(180deg)'
            }
          }
          StreamManager.prototype.removeMirrorVideo = function(video) {
            video.style.transform = 'unset'
            video.style.webkitTransform = 'unset'
          }
          return StreamManager
        })()
        exports.StreamManager = StreamManager
      },
      {
        '../OpenViduInternal/Enums/VideoInsertMode': 29,
        '../OpenViduInternal/Events/StreamManagerEvent': 38,
        '../OpenViduInternal/Events/VideoElementEvent': 40,
        platform: 8,
        'wolfy87-eventemitter': 15,
      },
    ],
    26: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var StreamManager_1 = require('./StreamManager')
        var Subscriber = (function(_super) {
          __extends(Subscriber, _super)
          function Subscriber(stream, targEl, properties) {
            var _this = _super.call(this, stream, targEl) || this
            _this.element = _this.targetElement
            _this.stream = stream
            _this.properties = properties
            return _this
          }
          Subscriber.prototype.subscribeToAudio = function(value) {
            this.stream
              .getMediaStream()
              .getAudioTracks()
              .forEach(function(track) {
                track.enabled = value
              })
            console.info(
              "'Subscriber' has " +
                (value ? 'subscribed to' : 'unsubscribed from') +
                ' its audio stream',
            )
            return this
          }
          Subscriber.prototype.subscribeToVideo = function(value) {
            this.stream
              .getMediaStream()
              .getVideoTracks()
              .forEach(function(track) {
                track.enabled = value
              })
            console.info(
              "'Subscriber' has " +
                (value ? 'subscribed to' : 'unsubscribed from') +
                ' its video stream',
            )
            return this
          }
          return Subscriber
        })(StreamManager_1.StreamManager)
        exports.Subscriber = Subscriber
      },
      { './StreamManager': 25 },
    ],
    27: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var LocalRecorderState
        ;(function(LocalRecorderState) {
          LocalRecorderState['READY'] = 'READY'
          LocalRecorderState['RECORDING'] = 'RECORDING'
          LocalRecorderState['PAUSED'] = 'PAUSED'
          LocalRecorderState['FINISHED'] = 'FINISHED'
        })(
          (LocalRecorderState =
            exports.LocalRecorderState || (exports.LocalRecorderState = {})),
        )
      },
      {},
    ],
    28: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var OpenViduErrorName
        ;(function(OpenViduErrorName) {
          OpenViduErrorName['BROWSER_NOT_SUPPORTED'] = 'BROWSER_NOT_SUPPORTED'
          OpenViduErrorName['DEVICE_ACCESS_DENIED'] = 'DEVICE_ACCESS_DENIED'
          OpenViduErrorName['DEVICE_ALREADY_IN_USE'] = 'DEVICE_ALREADY_IN_USE'
          OpenViduErrorName['SCREEN_CAPTURE_DENIED'] = 'SCREEN_CAPTURE_DENIED'
          OpenViduErrorName['SCREEN_SHARING_NOT_SUPPORTED'] =
            'SCREEN_SHARING_NOT_SUPPORTED'
          OpenViduErrorName['SCREEN_EXTENSION_NOT_INSTALLED'] =
            'SCREEN_EXTENSION_NOT_INSTALLED'
          OpenViduErrorName['SCREEN_EXTENSION_DISABLED'] =
            'SCREEN_EXTENSION_DISABLED'
          OpenViduErrorName['INPUT_VIDEO_DEVICE_NOT_FOUND'] =
            'INPUT_VIDEO_DEVICE_NOT_FOUND'
          OpenViduErrorName['INPUT_AUDIO_DEVICE_NOT_FOUND'] =
            'INPUT_AUDIO_DEVICE_NOT_FOUND'
          OpenViduErrorName['NO_INPUT_SOURCE_SET'] = 'NO_INPUT_SOURCE_SET'
          OpenViduErrorName['PUBLISHER_PROPERTIES_ERROR'] =
            'PUBLISHER_PROPERTIES_ERROR'
          OpenViduErrorName['OPENVIDU_PERMISSION_DENIED'] =
            'OPENVIDU_PERMISSION_DENIED'
          OpenViduErrorName['OPENVIDU_NOT_CONNECTED'] = 'OPENVIDU_NOT_CONNECTED'
          OpenViduErrorName['GENERIC_ERROR'] = 'GENERIC_ERROR'
        })(
          (OpenViduErrorName =
            exports.OpenViduErrorName || (exports.OpenViduErrorName = {})),
        )
        var OpenViduError = (function() {
          function OpenViduError(name, message) {
            this.name = name
            this.message = message
          }
          return OpenViduError
        })()
        exports.OpenViduError = OpenViduError
      },
      {},
    ],
    29: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var VideoInsertMode
        ;(function(VideoInsertMode) {
          VideoInsertMode['AFTER'] = 'AFTER'
          VideoInsertMode['APPEND'] = 'APPEND'
          VideoInsertMode['BEFORE'] = 'BEFORE'
          VideoInsertMode['PREPEND'] = 'PREPEND'
          VideoInsertMode['REPLACE'] = 'REPLACE'
        })(
          (VideoInsertMode =
            exports.VideoInsertMode || (exports.VideoInsertMode = {})),
        )
      },
      {},
    ],
    30: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var ConnectionEvent = (function(_super) {
          __extends(ConnectionEvent, _super)
          function ConnectionEvent(
            cancelable,
            target,
            type,
            connection,
            reason,
          ) {
            var _this = _super.call(this, cancelable, target, type) || this
            _this.connection = connection
            _this.reason = reason
            return _this
          }
          ConnectionEvent.prototype.callDefaultBehavior = function() {}
          return ConnectionEvent
        })(Event_1.Event)
        exports.ConnectionEvent = ConnectionEvent
      },
      { './Event': 31 },
    ],
    31: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event = (function() {
          function Event(cancelable, target, type) {
            this.hasBeenPrevented = false
            this.cancelable = cancelable
            this.target = target
            this.type = type
          }
          Event.prototype.isDefaultPrevented = function() {
            return this.hasBeenPrevented
          }
          Event.prototype.preventDefault = function() {
            this.callDefaultBehavior = function() {}
            this.hasBeenPrevented = true
          }
          return Event
        })()
        exports.Event = Event
      },
      {},
    ],
    32: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var FilterEvent = (function(_super) {
          __extends(FilterEvent, _super)
          function FilterEvent(target, eventType, data) {
            var _this = _super.call(this, false, target, eventType) || this
            _this.data = data
            return _this
          }
          FilterEvent.prototype.callDefaultBehavior = function() {}
          return FilterEvent
        })(Event_1.Event)
        exports.FilterEvent = FilterEvent
      },
      { './Event': 31 },
    ],
    33: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var PublisherSpeakingEvent = (function(_super) {
          __extends(PublisherSpeakingEvent, _super)
          function PublisherSpeakingEvent(target, type, connection, streamId) {
            var _this = _super.call(this, false, target, type) || this
            _this.type = type
            _this.connection = connection
            _this.streamId = streamId
            return _this
          }
          PublisherSpeakingEvent.prototype.callDefaultBehavior = function() {}
          return PublisherSpeakingEvent
        })(Event_1.Event)
        exports.PublisherSpeakingEvent = PublisherSpeakingEvent
      },
      { './Event': 31 },
    ],
    34: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var RecordingEvent = (function(_super) {
          __extends(RecordingEvent, _super)
          function RecordingEvent(target, type, id, name, reason) {
            var _this = _super.call(this, false, target, type) || this
            _this.id = id
            if (name !== id) {
              _this.name = name
            }
            _this.reason = reason
            return _this
          }
          RecordingEvent.prototype.callDefaultBehavior = function() {}
          return RecordingEvent
        })(Event_1.Event)
        exports.RecordingEvent = RecordingEvent
      },
      { './Event': 31 },
    ],
    35: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var SessionDisconnectedEvent = (function(_super) {
          __extends(SessionDisconnectedEvent, _super)
          function SessionDisconnectedEvent(target, reason) {
            var _this =
              _super.call(this, true, target, 'sessionDisconnected') || this
            _this.reason = reason
            return _this
          }
          SessionDisconnectedEvent.prototype.callDefaultBehavior = function() {
            console.info(
              "Calling default behavior upon '" +
                this.type +
                "' event dispatched by 'Session'",
            )
            var session = this.target
            for (var connectionId in session.remoteConnections) {
              if (session.remoteConnections[connectionId].stream) {
                session.remoteConnections[
                  connectionId
                ].stream.disposeWebRtcPeer()
                session.remoteConnections[
                  connectionId
                ].stream.disposeMediaStream()
                if (
                  session.remoteConnections[connectionId].stream.streamManager
                ) {
                  session.remoteConnections[
                    connectionId
                  ].stream.streamManager.removeAllVideos()
                }
                delete session.remoteStreamsCreated[
                  session.remoteConnections[connectionId].stream.streamId
                ]
                session.remoteConnections[connectionId].dispose()
              }
              delete session.remoteConnections[connectionId]
            }
          }
          return SessionDisconnectedEvent
        })(Event_1.Event)
        exports.SessionDisconnectedEvent = SessionDisconnectedEvent
      },
      { './Event': 31 },
    ],
    36: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var SignalEvent = (function(_super) {
          __extends(SignalEvent, _super)
          function SignalEvent(target, type, data, from) {
            var _this = _super.call(this, false, target, type) || this
            _this.type = type
            _this.data = data
            _this.from = from
            return _this
          }
          SignalEvent.prototype.callDefaultBehavior = function() {}
          return SignalEvent
        })(Event_1.Event)
        exports.SignalEvent = SignalEvent
      },
      { './Event': 31 },
    ],
    37: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var Publisher_1 = require('../../OpenVidu/Publisher')
        var Session_1 = require('../../OpenVidu/Session')
        var StreamEvent = (function(_super) {
          __extends(StreamEvent, _super)
          function StreamEvent(cancelable, target, type, stream, reason) {
            var _this = _super.call(this, cancelable, target, type) || this
            _this.stream = stream
            _this.reason = reason
            return _this
          }
          StreamEvent.prototype.callDefaultBehavior = function() {
            if (this.type === 'streamDestroyed') {
              if (this.target instanceof Session_1.Session) {
                console.info(
                  "Calling default behavior upon '" +
                    this.type +
                    "' event dispatched by 'Session'",
                )
                this.stream.disposeWebRtcPeer()
              } else if (this.target instanceof Publisher_1.Publisher) {
                console.info(
                  "Calling default behavior upon '" +
                    this.type +
                    "' event dispatched by 'Publisher'",
                )
                clearInterval(this.target.screenShareResizeInterval)
                this.stream.isLocalStreamReadyToPublish = false
                var openviduPublishers = this.target.openvidu.publishers
                for (var i = 0; i < openviduPublishers.length; i++) {
                  if (openviduPublishers[i] === this.target) {
                    openviduPublishers.splice(i, 1)
                    break
                  }
                }
              }
              this.stream.disposeMediaStream()
              if (this.stream.streamManager)
                this.stream.streamManager.removeAllVideos()
              delete this.stream.session.remoteStreamsCreated[
                this.stream.streamId
              ]
              var remoteConnection = this.stream.session.remoteConnections[
                this.stream.connection.connectionId
              ]
              if (!!remoteConnection && !!remoteConnection.options) {
                var streamOptionsServer = remoteConnection.options.streams
                for (var i = streamOptionsServer.length - 1; i >= 0; --i) {
                  if (streamOptionsServer[i].id === this.stream.streamId) {
                    streamOptionsServer.splice(i, 1)
                  }
                }
              }
            }
          }
          return StreamEvent
        })(Event_1.Event)
        exports.StreamEvent = StreamEvent
      },
      {
        '../../OpenVidu/Publisher': 22,
        '../../OpenVidu/Session': 23,
        './Event': 31,
      },
    ],
    38: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var StreamManagerEvent = (function(_super) {
          __extends(StreamManagerEvent, _super)
          function StreamManagerEvent(target, type, value) {
            var _this = _super.call(this, false, target, type) || this
            _this.value = value
            return _this
          }
          StreamManagerEvent.prototype.callDefaultBehavior = function() {}
          return StreamManagerEvent
        })(Event_1.Event)
        exports.StreamManagerEvent = StreamManagerEvent
      },
      { './Event': 31 },
    ],
    39: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var StreamPropertyChangedEvent = (function(_super) {
          __extends(StreamPropertyChangedEvent, _super)
          function StreamPropertyChangedEvent(
            target,
            stream,
            changedProperty,
            newValue,
            oldValue,
            reason,
          ) {
            var _this =
              _super.call(this, false, target, 'streamPropertyChanged') || this
            _this.stream = stream
            _this.changedProperty = changedProperty
            _this.newValue = newValue
            _this.oldValue = oldValue
            _this.reason = reason
            return _this
          }
          StreamPropertyChangedEvent.prototype.callDefaultBehavior = function() {}
          return StreamPropertyChangedEvent
        })(Event_1.Event)
        exports.StreamPropertyChangedEvent = StreamPropertyChangedEvent
      },
      { './Event': 31 },
    ],
    40: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var Event_1 = require('./Event')
        var VideoElementEvent = (function(_super) {
          __extends(VideoElementEvent, _super)
          function VideoElementEvent(element, target, type) {
            var _this = _super.call(this, false, target, type) || this
            _this.element = element
            return _this
          }
          VideoElementEvent.prototype.callDefaultBehavior = function() {}
          return VideoElementEvent
        })(Event_1.Event)
        exports.VideoElementEvent = VideoElementEvent
      },
      { './Event': 31 },
    ],
    41: [
      function(require, module, exports) {
        function Mapper() {
          var sources = {}
          this.forEach = function(callback) {
            for (var key in sources) {
              var source = sources[key]
              for (var key2 in source) callback(source[key2])
            }
          }
          this.get = function(id, source) {
            var ids = sources[source]
            if (ids == undefined) return undefined
            return ids[id]
          }
          this.remove = function(id, source) {
            var ids = sources[source]
            if (ids == undefined) return
            delete ids[id]
            for (var i in ids) {
              return false
            }
            delete sources[source]
          }
          this.set = function(value, id, source) {
            if (value == undefined) return this.remove(id, source)
            var ids = sources[source]
            if (ids == undefined) sources[source] = ids = {}
            ids[id] = value
          }
        }
        Mapper.prototype.pop = function(id, source) {
          var value = this.get(id, source)
          if (value == undefined) return undefined
          this.remove(id, source)
          return value
        }
        module.exports = Mapper
      },
      {},
    ],
    42: [
      function(require, module, exports) {
        var JsonRpcClient = require('./jsonrpcclient')
        exports.JsonRpcClient = JsonRpcClient
      },
      { './jsonrpcclient': 43 },
    ],
    43: [
      function(require, module, exports) {
        var RpcBuilder = require('../')
        var WebSocketWithReconnection = require('./transports/webSocketWithReconnection')
        Date.now =
          Date.now ||
          function() {
            return +new Date()
          }
        var PING_INTERVAL = 5000
        var RECONNECTING = 'RECONNECTING'
        var CONNECTED = 'CONNECTED'
        var DISCONNECTED = 'DISCONNECTED'
        var Logger = console
        function JsonRpcClient(configuration) {
          var self = this
          var wsConfig = configuration.ws
          var notReconnectIfNumLessThan = -1
          var pingNextNum = 0
          var enabledPings = true
          var pingPongStarted = false
          var pingInterval
          var status = DISCONNECTED
          var onreconnecting = wsConfig.onreconnecting
          var onreconnected = wsConfig.onreconnected
          var onconnected = wsConfig.onconnected
          var onerror = wsConfig.onerror
          configuration.rpc.pull = function(params, request) {
            request.reply(null, 'push')
          }
          wsConfig.onreconnecting = function() {
            Logger.debug('--------- ONRECONNECTING -----------')
            if (status === RECONNECTING) {
              Logger.error(
                'Websocket already in RECONNECTING state when receiving a new ONRECONNECTING message. Ignoring it',
              )
              return
            }
            stopPing()
            status = RECONNECTING
            if (onreconnecting) {
              onreconnecting()
            }
          }
          wsConfig.onreconnected = function() {
            Logger.debug('--------- ONRECONNECTED -----------')
            if (status === CONNECTED) {
              Logger.error(
                'Websocket already in CONNECTED state when receiving a new ONRECONNECTED message. Ignoring it',
              )
              return
            }
            status = CONNECTED
            updateNotReconnectIfLessThan()
            if (onreconnected) {
              onreconnected()
            }
          }
          wsConfig.onconnected = function() {
            Logger.debug('--------- ONCONNECTED -----------')
            if (status === CONNECTED) {
              Logger.error(
                'Websocket already in CONNECTED state when receiving a new ONCONNECTED message. Ignoring it',
              )
              return
            }
            status = CONNECTED
            enabledPings = true
            usePing()
            if (onconnected) {
              onconnected()
            }
          }
          wsConfig.onerror = function(error) {
            Logger.debug('--------- ONERROR -----------')
            status = DISCONNECTED
            stopPing()
            if (onerror) {
              onerror(error)
            }
          }
          var ws = new WebSocketWithReconnection(wsConfig)
          Logger.debug('Connecting websocket to URI: ' + wsConfig.uri)
          var rpcBuilderOptions = {
            request_timeout: configuration.rpc.requestTimeout,
            ping_request_timeout: configuration.rpc.heartbeatRequestTimeout,
          }
          var rpc = new RpcBuilder(
            RpcBuilder.packers.JsonRPC,
            rpcBuilderOptions,
            ws,
            function(request) {
              Logger.debug('Received request: ' + JSON.stringify(request))
              try {
                var func = configuration.rpc[request.method]
                if (func === undefined) {
                  Logger.error(
                    'Method ' + request.method + ' not registered in client',
                  )
                } else {
                  func(request.params, request)
                }
              } catch (err) {
                Logger.error(
                  'Exception processing request: ' + JSON.stringify(request),
                )
                Logger.error(err)
              }
            },
          )
          this.send = function(method, params, callback) {
            if (method !== 'ping') {
              Logger.debug(
                'Request: method:' +
                  method +
                  ' params:' +
                  JSON.stringify(params),
              )
            }
            var requestTime = Date.now()
            rpc.encode(method, params, function(error, result) {
              if (error) {
                try {
                  Logger.error(
                    'ERROR:' +
                      error.message +
                      ' in Request: method:' +
                      method +
                      ' params:' +
                      JSON.stringify(params) +
                      ' request:' +
                      error.request,
                  )
                  if (error.data) {
                    Logger.error('ERROR DATA:' + JSON.stringify(error.data))
                  }
                } catch (e) {}
                error.requestTime = requestTime
              }
              if (callback) {
                if (result != undefined && result.value !== 'pong') {
                  Logger.debug('Response: ' + JSON.stringify(result))
                }
                callback(error, result)
              }
            })
          }
          function updateNotReconnectIfLessThan() {
            Logger.debug(
              'notReconnectIfNumLessThan = ' +
                pingNextNum +
                ' (old=' +
                notReconnectIfNumLessThan +
                ')',
            )
            notReconnectIfNumLessThan = pingNextNum
          }
          function sendPing() {
            if (enabledPings) {
              var params = null
              if (
                pingNextNum == 0 ||
                pingNextNum == notReconnectIfNumLessThan
              ) {
                params = {
                  interval: configuration.heartbeat || PING_INTERVAL,
                }
              }
              pingNextNum++
              self.send(
                'ping',
                params,
                (function(pingNum) {
                  return function(error, result) {
                    if (error) {
                      Logger.debug(
                        'Error in ping request #' +
                          pingNum +
                          ' (' +
                          error.message +
                          ')',
                      )
                      if (pingNum > notReconnectIfNumLessThan) {
                        enabledPings = false
                        updateNotReconnectIfLessThan()
                        Logger.debug(
                          'Server did not respond to ping message #' +
                            pingNum +
                            '. Reconnecting... ',
                        )
                        ws.reconnectWs()
                      }
                    }
                  }
                })(pingNextNum),
              )
            } else {
              Logger.debug('Trying to send ping, but ping is not enabled')
            }
          }
          function usePing() {
            if (!pingPongStarted) {
              Logger.debug('Starting ping (if configured)')
              pingPongStarted = true
              if (configuration.heartbeat != undefined) {
                pingInterval = setInterval(sendPing, configuration.heartbeat)
                sendPing()
              }
            }
          }
          function stopPing() {
            clearInterval(pingInterval)
            pingPongStarted = false
            enabledPings = false
            pingNextNum = -1
            rpc.cancel()
          }
          this.close = function(code, reason) {
            Logger.debug('Closing  with code: ' + code + ' because: ' + reason)
            if (pingInterval != undefined) {
              Logger.debug('Clearing ping interval')
              clearInterval(pingInterval)
            }
            pingPongStarted = false
            enabledPings = false
            if (configuration.sendCloseMessage) {
              Logger.debug('Sending close message')
              this.send('closeSession', null, function(error, result) {
                if (error) {
                  Logger.error(
                    'Error sending close message: ' + JSON.stringify(error),
                  )
                }
                ws.close(code, reason)
              })
            } else {
              ws.close(code, reason)
            }
          }
          this.forceClose = function(millis) {
            ws.forceClose(millis)
          }
          this.reconnect = function() {
            ws.reconnectWs()
          }
          this.resetPing = function() {
            enabledPings = true
            pingNextNum = 0
            usePing()
          }
        }
        module.exports = JsonRpcClient
      },
      { '../': 46, './transports/webSocketWithReconnection': 45 },
    ],
    44: [
      function(require, module, exports) {
        var WebSocketWithReconnection = require('./webSocketWithReconnection')
        exports.WebSocketWithReconnection = WebSocketWithReconnection
      },
      { './webSocketWithReconnection': 45 },
    ],
    45: [
      function(require, module, exports) {
        ;(function(global) {
          'use strict'
          var BrowserWebSocket = global.WebSocket || global.MozWebSocket
          var Logger = console
          var MAX_RETRIES = 2000
          var RETRY_TIME_MS = 3000
          var CONNECTING = 0
          var OPEN = 1
          var CLOSING = 2
          var CLOSED = 3
          function WebSocketWithReconnection(config) {
            var closing = false
            var registerMessageHandler
            var wsUri = config.uri
            var useSockJS = config.useSockJS
            var reconnecting = false
            var forcingDisconnection = false
            var ws
            if (useSockJS) {
              ws = new SockJS(wsUri)
            } else {
              ws = new WebSocket(wsUri)
            }
            ws.onopen = function() {
              logConnected(ws, wsUri)
              if (config.onconnected) {
                config.onconnected()
              }
            }
            ws.onerror = function(error) {
              Logger.error(
                'Could not connect to ' +
                  wsUri +
                  ' (invoking onerror if defined)',
                error,
              )
              if (config.onerror) {
                config.onerror(error)
              }
            }
            function logConnected(ws, wsUri) {
              try {
                Logger.debug('WebSocket connected to ' + wsUri)
              } catch (e) {
                Logger.error(e)
              }
            }
            var reconnectionOnClose = function() {
              if (ws.readyState === CLOSED) {
                if (closing) {
                  Logger.debug('Connection closed by user')
                } else {
                  Logger.debug(
                    'Connection closed unexpectecly. Reconnecting...',
                  )
                  reconnectToSameUri(MAX_RETRIES, 1)
                }
              } else {
                Logger.debug(
                  'Close callback from previous websocket. Ignoring it',
                )
              }
            }
            ws.onclose = reconnectionOnClose
            function reconnectToSameUri(maxRetries, numRetries) {
              Logger.debug(
                'reconnectToSameUri (attempt #' +
                  numRetries +
                  ', max=' +
                  maxRetries +
                  ')',
              )
              if (numRetries === 1) {
                if (reconnecting) {
                  Logger.warn(
                    'Trying to reconnectToNewUri when reconnecting... Ignoring this reconnection.',
                  )
                  return
                } else {
                  reconnecting = true
                }
                if (config.onreconnecting) {
                  config.onreconnecting()
                }
              }
              if (forcingDisconnection) {
                reconnectToNewUri(maxRetries, numRetries, wsUri)
              } else {
                if (config.newWsUriOnReconnection) {
                  config.newWsUriOnReconnection(function(error, newWsUri) {
                    if (error) {
                      Logger.debug(error)
                      setTimeout(function() {
                        reconnectToSameUri(maxRetries, numRetries + 1)
                      }, RETRY_TIME_MS)
                    } else {
                      reconnectToNewUri(maxRetries, numRetries, newWsUri)
                    }
                  })
                } else {
                  reconnectToNewUri(maxRetries, numRetries, wsUri)
                }
              }
            }
            function reconnectToNewUri(maxRetries, numRetries, reconnectWsUri) {
              Logger.debug('Reconnection attempt #' + numRetries)
              ws.close()
              wsUri = reconnectWsUri || wsUri
              var newWs
              if (useSockJS) {
                newWs = new SockJS(wsUri)
              } else {
                newWs = new WebSocket(wsUri)
              }
              newWs.onopen = function() {
                Logger.debug('Reconnected after ' + numRetries + ' attempts...')
                logConnected(newWs, wsUri)
                reconnecting = false
                registerMessageHandler()
                if (config.onreconnected()) {
                  config.onreconnected()
                }
                newWs.onclose = reconnectionOnClose
              }
              var onErrorOrClose = function(error) {
                Logger.warn('Reconnection error: ', error)
                if (numRetries === maxRetries) {
                  if (config.ondisconnect) {
                    config.ondisconnect()
                  }
                } else {
                  setTimeout(function() {
                    reconnectToSameUri(maxRetries, numRetries + 1)
                  }, RETRY_TIME_MS)
                }
              }
              newWs.onerror = onErrorOrClose
              ws = newWs
            }
            this.close = function() {
              closing = true
              ws.close()
            }
            this.forceClose = function(millis) {
              Logger.debug('Testing: Force WebSocket close')
              if (millis) {
                Logger.debug(
                  'Testing: Change wsUri for ' +
                    millis +
                    ' millis to simulate net failure',
                )
                var goodWsUri = wsUri
                wsUri = 'wss://21.234.12.34.4:443/'
                forcingDisconnection = true
                setTimeout(function() {
                  Logger.debug('Testing: Recover good wsUri ' + goodWsUri)
                  wsUri = goodWsUri
                  forcingDisconnection = false
                }, millis)
              }
              ws.close()
            }
            this.reconnectWs = function() {
              Logger.debug('reconnectWs')
              reconnectToSameUri(MAX_RETRIES, 1)
            }
            this.send = function(message) {
              ws.send(message)
            }
            this.addEventListener = function(type, callback) {
              registerMessageHandler = function() {
                ws.addEventListener(type, callback)
              }
              registerMessageHandler()
            }
          }
          module.exports = WebSocketWithReconnection
        }.call(
          this,
          typeof global !== 'undefined'
            ? global
            : typeof self !== 'undefined'
            ? self
            : typeof window !== 'undefined'
            ? window
            : {},
        ))
      },
      {},
    ],
    46: [
      function(require, module, exports) {
        var defineProperty_IE8 = false
        if (Object.defineProperty) {
          try {
            Object.defineProperty({}, 'x', {})
          } catch (e) {
            defineProperty_IE8 = true
          }
        }
        if (!Function.prototype.bind) {
          Function.prototype.bind = function(oThis) {
            if (typeof this !== 'function') {
              throw new TypeError(
                'Function.prototype.bind - what is trying to be bound is not callable',
              )
            }
            var aArgs = Array.prototype.slice.call(arguments, 1),
              fToBind = this,
              fNOP = function() {},
              fBound = function() {
                return fToBind.apply(
                  this instanceof fNOP && oThis ? this : oThis,
                  aArgs.concat(Array.prototype.slice.call(arguments)),
                )
              }
            fNOP.prototype = this.prototype
            fBound.prototype = new fNOP()
            return fBound
          }
        }
        var EventEmitter = require('events').EventEmitter
        var inherits = require('inherits')
        var packers = require('./packers')
        var Mapper = require('./Mapper')
        var BASE_TIMEOUT = 5000
        function unifyResponseMethods(responseMethods) {
          if (!responseMethods) return {}
          for (var key in responseMethods) {
            var value = responseMethods[key]
            if (typeof value == 'string')
              responseMethods[key] = {
                response: value,
              }
          }
          return responseMethods
        }
        function unifyTransport(transport) {
          if (!transport) return
          if (transport instanceof Function) return { send: transport }
          if (transport.send instanceof Function) return transport
          if (transport.postMessage instanceof Function) {
            transport.send = transport.postMessage
            return transport
          }
          if (transport.write instanceof Function) {
            transport.send = transport.write
            return transport
          }
          if (transport.onmessage !== undefined) return
          if (transport.pause instanceof Function) return
          throw new SyntaxError(
            'Transport is not a function nor a valid object',
          )
        }
        function RpcNotification(method, params) {
          if (defineProperty_IE8) {
            this.method = method
            this.params = params
          } else {
            Object.defineProperty(this, 'method', {
              value: method,
              enumerable: true,
            })
            Object.defineProperty(this, 'params', {
              value: params,
              enumerable: true,
            })
          }
        }
        function RpcBuilder(packer, options, transport, onRequest) {
          var self = this
          if (!packer) throw new SyntaxError('Packer is not defined')
          if (!packer.pack || !packer.unpack)
            throw new SyntaxError('Packer is invalid')
          var responseMethods = unifyResponseMethods(packer.responseMethods)
          if (options instanceof Function) {
            if (transport != undefined)
              throw new SyntaxError("There can't be parameters after onRequest")
            onRequest = options
            transport = undefined
            options = undefined
          }
          if (options && options.send instanceof Function) {
            if (transport && !(transport instanceof Function))
              throw new SyntaxError('Only a function can be after transport')
            onRequest = transport
            transport = options
            options = undefined
          }
          if (transport instanceof Function) {
            if (onRequest != undefined)
              throw new SyntaxError("There can't be parameters after onRequest")
            onRequest = transport
            transport = undefined
          }
          if (transport && transport.send instanceof Function)
            if (onRequest && !(onRequest instanceof Function))
              throw new SyntaxError('Only a function can be after transport')
          options = options || {}
          EventEmitter.call(this)
          if (onRequest) this.on('request', onRequest)
          if (defineProperty_IE8) this.peerID = options.peerID
          else Object.defineProperty(this, 'peerID', { value: options.peerID })
          var max_retries = options.max_retries || 0
          function transportMessage(event) {
            self.decode(event.data || event)
          }
          this.getTransport = function() {
            return transport
          }
          this.setTransport = function(value) {
            if (transport) {
              if (transport.removeEventListener)
                transport.removeEventListener('message', transportMessage)
              else if (transport.removeListener)
                transport.removeListener('data', transportMessage)
            }
            if (value) {
              if (value.addEventListener)
                value.addEventListener('message', transportMessage)
              else if (value.addListener)
                value.addListener('data', transportMessage)
            }
            transport = unifyTransport(value)
          }
          if (!defineProperty_IE8)
            Object.defineProperty(this, 'transport', {
              get: this.getTransport.bind(this),
              set: this.setTransport.bind(this),
            })
          this.setTransport(transport)
          var request_timeout = options.request_timeout || BASE_TIMEOUT
          var ping_request_timeout =
            options.ping_request_timeout || request_timeout
          var response_timeout = options.response_timeout || BASE_TIMEOUT
          var duplicates_timeout = options.duplicates_timeout || BASE_TIMEOUT
          var requestID = 0
          var requests = new Mapper()
          var responses = new Mapper()
          var processedResponses = new Mapper()
          var message2Key = {}
          function storeResponse(message, id, dest) {
            var response = {
              message: message,
              timeout: setTimeout(function() {
                responses.remove(id, dest)
              }, response_timeout),
            }
            responses.set(response, id, dest)
          }
          function storeProcessedResponse(ack, from) {
            var timeout = setTimeout(function() {
              processedResponses.remove(ack, from)
            }, duplicates_timeout)
            processedResponses.set(timeout, ack, from)
          }
          function RpcRequest(method, params, id, from, transport) {
            RpcNotification.call(this, method, params)
            this.getTransport = function() {
              return transport
            }
            this.setTransport = function(value) {
              transport = unifyTransport(value)
            }
            if (!defineProperty_IE8)
              Object.defineProperty(this, 'transport', {
                get: this.getTransport.bind(this),
                set: this.setTransport.bind(this),
              })
            var response = responses.get(id, from)
            if (!(transport || self.getTransport())) {
              if (defineProperty_IE8) this.duplicated = Boolean(response)
              else
                Object.defineProperty(this, 'duplicated', {
                  value: Boolean(response),
                })
            }
            var responseMethod = responseMethods[method]
            this.pack = packer.pack.bind(packer, this, id)
            this.reply = function(error, result, transport) {
              if (
                error instanceof Function ||
                (error && error.send instanceof Function)
              ) {
                if (result != undefined)
                  throw new SyntaxError(
                    "There can't be parameters after callback",
                  )
                transport = error
                result = null
                error = undefined
              } else if (
                result instanceof Function ||
                (result && result.send instanceof Function)
              ) {
                if (transport != undefined)
                  throw new SyntaxError(
                    "There can't be parameters after callback",
                  )
                transport = result
                result = null
              }
              transport = unifyTransport(transport)
              if (response) clearTimeout(response.timeout)
              if (from != undefined) {
                if (error) error.dest = from
                if (result) result.dest = from
              }
              var message
              if (error || result != undefined) {
                if (self.peerID != undefined) {
                  if (error) error.from = self.peerID
                  else result.from = self.peerID
                }
                if (responseMethod) {
                  if (responseMethod.error == undefined && error)
                    message = {
                      error: error,
                    }
                  else {
                    var method = error
                      ? responseMethod.error
                      : responseMethod.response
                    message = {
                      method: method,
                      params: error || result,
                    }
                  }
                } else
                  message = {
                    error: error,
                    result: result,
                  }
                message = packer.pack(message, id)
              } else if (response) message = response.message
              else message = packer.pack({ result: null }, id)
              storeResponse(message, id, from)
              transport =
                transport || this.getTransport() || self.getTransport()
              if (transport) return transport.send(message)
              return message
            }
          }
          inherits(RpcRequest, RpcNotification)
          function cancel(message) {
            var key = message2Key[message]
            if (!key) return
            delete message2Key[message]
            var request = requests.pop(key.id, key.dest)
            if (!request) return
            clearTimeout(request.timeout)
            storeProcessedResponse(key.id, key.dest)
          }
          this.cancel = function(message) {
            if (message) return cancel(message)
            for (var message in message2Key) cancel(message)
          }
          this.close = function() {
            var transport = this.getTransport()
            if (transport && transport.close)
              transport.close(4003, 'Cancel request')
            this.cancel()
            processedResponses.forEach(clearTimeout)
            responses.forEach(function(response) {
              clearTimeout(response.timeout)
            })
          }
          this.encode = function(method, params, dest, transport, callback) {
            if (params instanceof Function) {
              if (dest != undefined)
                throw new SyntaxError(
                  "There can't be parameters after callback",
                )
              callback = params
              transport = undefined
              dest = undefined
              params = undefined
            } else if (dest instanceof Function) {
              if (transport != undefined)
                throw new SyntaxError(
                  "There can't be parameters after callback",
                )
              callback = dest
              transport = undefined
              dest = undefined
            } else if (transport instanceof Function) {
              if (callback != undefined)
                throw new SyntaxError(
                  "There can't be parameters after callback",
                )
              callback = transport
              transport = undefined
            }
            if (self.peerID != undefined) {
              params = params || {}
              params.from = self.peerID
            }
            if (dest != undefined) {
              params = params || {}
              params.dest = dest
            }
            var message = {
              method: method,
              params: params,
            }
            if (callback) {
              var id = requestID++
              var retried = 0
              message = packer.pack(message, id)
              function dispatchCallback(error, result) {
                self.cancel(message)
                callback(error, result)
              }
              var request = {
                message: message,
                callback: dispatchCallback,
                responseMethods: responseMethods[method] || {},
              }
              var encode_transport = unifyTransport(transport)
              function sendRequest(transport) {
                var rt =
                  method === 'ping' ? ping_request_timeout : request_timeout
                request.timeout = setTimeout(
                  timeout,
                  rt * Math.pow(2, retried++),
                )
                message2Key[message] = { id: id, dest: dest }
                requests.set(request, id, dest)
                transport = transport || encode_transport || self.getTransport()
                if (transport) return transport.send(message)
                return message
              }
              function retry(transport) {
                transport = unifyTransport(transport)
                console.warn(retried + ' retry for request message:', message)
                var timeout = processedResponses.pop(id, dest)
                clearTimeout(timeout)
                return sendRequest(transport)
              }
              function timeout() {
                if (retried < max_retries) return retry(transport)
                var error = new Error('Request has timed out')
                error.request = message
                error.retry = retry
                dispatchCallback(error)
              }
              return sendRequest(transport)
            }
            message = packer.pack(message)
            transport = transport || this.getTransport()
            if (transport) return transport.send(message)
            return message
          }
          this.decode = function(message, transport) {
            if (!message) throw new TypeError('Message is not defined')
            try {
              message = packer.unpack(message)
            } catch (e) {
              return console.debug(e, message)
            }
            var id = message.id
            var ack = message.ack
            var method = message.method
            var params = message.params || {}
            var from = params.from
            var dest = params.dest
            if (self.peerID != undefined && from == self.peerID) return
            if (id == undefined && ack == undefined) {
              var notification = new RpcNotification(method, params)
              if (self.emit('request', notification)) return
              return notification
            }
            function processRequest() {
              transport = unifyTransport(transport) || self.getTransport()
              if (transport) {
                var response = responses.get(id, from)
                if (response) return transport.send(response.message)
              }
              var idAck = id != undefined ? id : ack
              var request = new RpcRequest(
                method,
                params,
                idAck,
                from,
                transport,
              )
              if (self.emit('request', request)) return
              return request
            }
            function processResponse(request, error, result) {
              request.callback(error, result)
            }
            function duplicatedResponse(timeout) {
              console.warn('Response already processed', message)
              clearTimeout(timeout)
              storeProcessedResponse(ack, from)
            }
            if (method) {
              if (dest == undefined || dest == self.peerID) {
                var request = requests.get(ack, from)
                if (request) {
                  var responseMethods = request.responseMethods
                  if (method == responseMethods.error)
                    return processResponse(request, params)
                  if (method == responseMethods.response)
                    return processResponse(request, null, params)
                  return processRequest()
                }
                var processed = processedResponses.get(ack, from)
                if (processed) return duplicatedResponse(processed)
              }
              return processRequest()
            }
            var error = message.error
            var result = message.result
            if (error && error.dest && error.dest != self.peerID) return
            if (result && result.dest && result.dest != self.peerID) return
            var request = requests.get(ack, from)
            if (!request) {
              var processed = processedResponses.get(ack, from)
              if (processed) return duplicatedResponse(processed)
              return console.warn(
                'No callback was defined for this message',
                message,
              )
            }
            processResponse(request, error, result)
          }
        }
        inherits(RpcBuilder, EventEmitter)
        RpcBuilder.RpcNotification = RpcNotification
        module.exports = RpcBuilder
        var clients = require('./clients')
        var transports = require('./clients/transports')
        RpcBuilder.clients = clients
        RpcBuilder.clients.transports = transports
        RpcBuilder.packers = packers
      },
      {
        './Mapper': 41,
        './clients': 42,
        './clients/transports': 44,
        './packers': 49,
        events: 1,
        inherits: 6,
      },
    ],
    47: [
      function(require, module, exports) {
        function pack(message, id) {
          var result = {
            jsonrpc: '2.0',
          }
          if (message.method) {
            result.method = message.method
            if (message.params) result.params = message.params
            if (id != undefined) result.id = id
          } else if (id != undefined) {
            if (message.error) {
              if (message.result !== undefined)
                throw new TypeError('Both result and error are defined')
              result.error = message.error
            } else if (message.result !== undefined)
              result.result = message.result
            else throw new TypeError('No result or error is defined')
            result.id = id
          }
          return JSON.stringify(result)
        }
        function unpack(message) {
          var result = message
          if (typeof message === 'string' || message instanceof String) {
            result = JSON.parse(message)
          }
          var version = result.jsonrpc
          if (version !== '2.0')
            throw new TypeError(
              "Invalid JsonRPC version '" + version + "': " + message,
            )
          if (result.method == undefined) {
            if (result.id == undefined)
              throw new TypeError('Invalid message: ' + message)
            var result_defined = result.result !== undefined
            var error_defined = result.error !== undefined
            if (result_defined && error_defined)
              throw new TypeError(
                'Both result and error are defined: ' + message,
              )
            if (!result_defined && !error_defined)
              throw new TypeError('No result or error is defined: ' + message)
            result.ack = result.id
            delete result.id
          }
          return result
        }
        exports.pack = pack
        exports.unpack = unpack
      },
      {},
    ],
    48: [
      function(require, module, exports) {
        function pack(message) {
          throw new TypeError('Not yet implemented')
        }
        function unpack(message) {
          throw new TypeError('Not yet implemented')
        }
        exports.pack = pack
        exports.unpack = unpack
      },
      {},
    ],
    49: [
      function(require, module, exports) {
        var JsonRPC = require('./JsonRPC')
        var XmlRPC = require('./XmlRPC')
        exports.JsonRPC = JsonRPC
        exports.XmlRPC = XmlRPC
      },
      { './JsonRPC': 47, './XmlRPC': 48 },
    ],
    50: [
      function(require, module, exports) {
        window.getScreenId = function(
          firefoxString,
          callback,
          custom_parameter,
        ) {
          if (
            navigator.userAgent.indexOf('Edge') !== -1 &&
            (!!navigator.msSaveOrOpenBlob || !!navigator.msSaveBlob)
          ) {
            callback({
              video: true,
            })
            return
          }
          if (navigator.mozGetUserMedia) {
            callback(null, 'firefox', {
              video: {
                mozMediaSource: firefoxString,
                mediaSource: firefoxString,
              },
            })
            return
          }
          window.addEventListener('message', onIFrameCallback)
          function onIFrameCallback(event) {
            if (!event.data) return
            if (event.data.chromeMediaSourceId) {
              if (event.data.chromeMediaSourceId === 'PermissionDeniedError') {
                callback('permission-denied')
              } else {
                callback(
                  null,
                  event.data.chromeMediaSourceId,
                  getScreenConstraints(
                    null,
                    event.data.chromeMediaSourceId,
                    event.data.canRequestAudioTrack,
                  ),
                )
              }
              window.removeEventListener('message', onIFrameCallback)
            }
            if (event.data.chromeExtensionStatus) {
              callback(
                event.data.chromeExtensionStatus,
                null,
                getScreenConstraints(event.data.chromeExtensionStatus),
              )
              window.removeEventListener('message', onIFrameCallback)
            }
          }
          if (!custom_parameter) {
            setTimeout(postGetSourceIdMessage, 100)
          } else {
            setTimeout(function() {
              postGetSourceIdMessage(custom_parameter)
            }, 100)
          }
        }
        function getScreenConstraints(error, sourceId, canRequestAudioTrack) {
          var screen_constraints = {
            audio: false,
            video: {
              mandatory: {
                chromeMediaSource: error ? 'screen' : 'desktop',
                maxWidth:
                  window.screen.width > 1920 ? window.screen.width : 1920,
                maxHeight:
                  window.screen.height > 1080 ? window.screen.height : 1080,
              },
              optional: [],
            },
          }
          if (canRequestAudioTrack) {
            screen_constraints.audio = {
              mandatory: {
                chromeMediaSource: error ? 'screen' : 'desktop',
              },
              optional: [],
            }
          }
          if (sourceId) {
            screen_constraints.video.mandatory.chromeMediaSourceId = sourceId
            if (
              screen_constraints.audio &&
              screen_constraints.audio.mandatory
            ) {
              screen_constraints.audio.mandatory.chromeMediaSourceId = sourceId
            }
          }
          return screen_constraints
        }
        function postGetSourceIdMessage(custom_parameter) {
          if (!iframe) {
            loadIFrame(function() {
              postGetSourceIdMessage(custom_parameter)
            })
            return
          }
          if (!iframe.isLoaded) {
            setTimeout(function() {
              postGetSourceIdMessage(custom_parameter)
            }, 100)
            return
          }
          if (!custom_parameter) {
            iframe.contentWindow.postMessage(
              {
                captureSourceId: true,
              },
              '*',
            )
          } else if (custom_parameter.forEach) {
            iframe.contentWindow.postMessage(
              {
                captureCustomSourceId: custom_parameter,
              },
              '*',
            )
          } else {
            iframe.contentWindow.postMessage(
              {
                captureSourceIdWithAudio: true,
              },
              '*',
            )
          }
        }
        var iframe
        window.getScreenConstraints = function(callback) {
          loadIFrame(function() {
            getScreenId(function(error, sourceId, screen_constraints) {
              if (!screen_constraints) {
                screen_constraints = {
                  video: true,
                }
              }
              callback(error, screen_constraints.video)
            })
          })
        }
        function loadIFrame(loadCallback) {
          if (iframe) {
            loadCallback()
            return
          }
          iframe = document.createElement('iframe')
          iframe.onload = function() {
            iframe.isLoaded = true
            loadCallback()
          }
          iframe.src =
            'https://openvidu.github.io/openvidu-screen-sharing-chrome-extension/'
          iframe.style.display = 'none'
          ;(document.body || document.documentElement).appendChild(iframe)
        }
        window.getChromeExtensionStatus = function(callback) {
          if (navigator.mozGetUserMedia) {
            callback('installed-enabled')
            return
          }
          window.addEventListener('message', onIFrameCallback)
          function onIFrameCallback(event) {
            if (!event.data) return
            if (event.data.chromeExtensionStatus) {
              callback(event.data.chromeExtensionStatus)
              window.removeEventListener('message', onIFrameCallback)
            }
          }
          setTimeout(postGetChromeExtensionStatusMessage, 100)
        }
        function postGetChromeExtensionStatusMessage() {
          if (!iframe) {
            loadIFrame(postGetChromeExtensionStatusMessage)
            return
          }
          if (!iframe.isLoaded) {
            setTimeout(postGetChromeExtensionStatusMessage, 100)
            return
          }
          iframe.contentWindow.postMessage(
            {
              getChromeExtensionStatus: true,
            },
            '*',
          )
        }
        exports.getScreenId = getScreenId
      },
      {},
    ],
    51: [
      function(require, module, exports) {
        var chromeMediaSource = 'screen'
        var sourceId
        var screenCallback
        var isFirefox = typeof window.InstallTrigger !== 'undefined'
        var isOpera =
          !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0
        var isChrome = !!window.chrome && !isOpera
        window.addEventListener('message', function(event) {
          if (event.origin != window.location.origin) {
            return
          }
          onMessageCallback(event.data)
        })
        function onMessageCallback(data) {
          if (data == 'PermissionDeniedError') {
            if (screenCallback) return screenCallback('PermissionDeniedError')
            else throw new Error('PermissionDeniedError')
          }
          if (data == 'rtcmulticonnection-extension-loaded') {
            chromeMediaSource = 'desktop'
          }
          if (data.sourceId && screenCallback) {
            screenCallback(
              (sourceId = data.sourceId),
              data.canRequestAudioTrack === true,
            )
          }
        }
        function isChromeExtensionAvailable(callback) {
          if (!callback) return
          if (chromeMediaSource == 'desktop') return callback(true)
          window.postMessage('are-you-there', '*')
          setTimeout(function() {
            if (chromeMediaSource == 'screen') {
              callback(false)
            } else callback(true)
          }, 2000)
        }
        function getSourceId(callback) {
          if (!callback) throw '"callback" parameter is mandatory.'
          if (sourceId) return callback(sourceId)
          screenCallback = callback
          window.postMessage('get-sourceId', '*')
        }
        function getCustomSourceId(arr, callback) {
          if (!arr || !arr.forEach)
            throw '"arr" parameter is mandatory and it must be an array.'
          if (!callback) throw '"callback" parameter is mandatory.'
          if (sourceId) return callback(sourceId)
          screenCallback = callback
          window.postMessage(
            {
              'get-custom-sourceId': arr,
            },
            '*',
          )
        }
        function getSourceIdWithAudio(callback) {
          if (!callback) throw '"callback" parameter is mandatory.'
          if (sourceId) return callback(sourceId)
          screenCallback = callback
          window.postMessage('audio-plus-tab', '*')
        }
        function getChromeExtensionStatus(extensionid, callback) {
          if (isFirefox) return callback('not-chrome')
          if (arguments.length != 2) {
            callback = extensionid
            extensionid = 'lfcgfepafnobdloecchnfaclibenjold'
          }
          var image = document.createElement('img')
          image.src = 'chrome-extension://' + extensionid + '/icon.png'
          image.onload = function() {
            chromeMediaSource = 'screen'
            window.postMessage('are-you-there', '*')
            setTimeout(function() {
              if (chromeMediaSource == 'screen') {
                callback('installed-disabled')
              } else callback('installed-enabled')
            }, 2000)
          }
          image.onerror = function() {
            callback('not-installed')
          }
        }
        function getScreenConstraintsWithAudio(callback) {
          getScreenConstraints(callback, true)
        }
        function getScreenConstraints(callback, captureSourceIdWithAudio) {
          sourceId = ''
          var firefoxScreenConstraints = {
            mozMediaSource: 'window',
            mediaSource: 'window',
          }
          if (isFirefox) return callback(null, firefoxScreenConstraints)
          var screen_constraints = {
            mandatory: {
              chromeMediaSource: chromeMediaSource,
              maxWidth: screen.width > 1920 ? screen.width : 1920,
              maxHeight: screen.height > 1080 ? screen.height : 1080,
            },
            optional: [],
          }
          if (chromeMediaSource == 'desktop' && !sourceId) {
            if (captureSourceIdWithAudio) {
              getSourceIdWithAudio(function(sourceId, canRequestAudioTrack) {
                screen_constraints.mandatory.chromeMediaSourceId = sourceId
                if (canRequestAudioTrack) {
                  screen_constraints.canRequestAudioTrack = true
                }
                callback(
                  sourceId == 'PermissionDeniedError' ? sourceId : null,
                  screen_constraints,
                )
              })
            } else {
              getSourceId(function(sourceId) {
                screen_constraints.mandatory.chromeMediaSourceId = sourceId
                callback(
                  sourceId == 'PermissionDeniedError' ? sourceId : null,
                  screen_constraints,
                )
              })
            }
            return
          }
          if (chromeMediaSource == 'desktop') {
            screen_constraints.mandatory.chromeMediaSourceId = sourceId
          }
          callback(null, screen_constraints)
        }
        exports.getScreenConstraints = getScreenConstraints
        exports.getScreenConstraintsWithAudio = getScreenConstraintsWithAudio
        exports.isChromeExtensionAvailable = isChromeExtensionAvailable
        exports.getChromeExtensionStatus = getChromeExtensionStatus
        exports.getSourceId = getSourceId
      },
      {},
    ],
    52: [
      function(require, module, exports) {
        'use strict'
        var __extends =
          (this && this.__extends) ||
          (function() {
            var extendStatics = function(d, b) {
              extendStatics =
                Object.setPrototypeOf ||
                ({ __proto__: [] } instanceof Array &&
                  function(d, b) {
                    d.__proto__ = b
                  }) ||
                function(d, b) {
                  for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]
                }
              return extendStatics(d, b)
            }
            return function(d, b) {
              extendStatics(d, b)
              function __() {
                this.constructor = d
              }
              d.prototype =
                b === null
                  ? Object.create(b)
                  : ((__.prototype = b.prototype), new __())
            }
          })()
        Object.defineProperty(exports, '__esModule', { value: true })
        var freeice = require('freeice')
        var uuid = require('uuid')
        var platform = require('platform')
        var WebRtcPeer = (function() {
          function WebRtcPeer(configuration) {
            var _this = this
            this.configuration = configuration
            this.remoteCandidatesQueue = []
            this.localCandidatesQueue = []
            this.iceCandidateList = []
            this.candidategatheringdone = false
            this.configuration.iceServers =
              !!this.configuration.iceServers &&
              this.configuration.iceServers.length > 0
                ? this.configuration.iceServers
                : freeice()
            this.pc = new RTCPeerConnection({
              iceServers: this.configuration.iceServers,
            })
            this.id = configuration.id ? configuration.id : uuid.v4()
            this.pc.onicecandidate = function(event) {
              if (event.candidate) {
                var candidate = event.candidate
                if (candidate) {
                  _this.localCandidatesQueue.push({
                    candidate: candidate.candidate,
                  })
                  _this.candidategatheringdone = false
                  _this.configuration.onicecandidate(event.candidate)
                } else if (!_this.candidategatheringdone) {
                  _this.candidategatheringdone = true
                }
              }
            }
            this.pc.onsignalingstatechange = function() {
              if (_this.pc.signalingState === 'stable') {
                while (_this.iceCandidateList.length > 0) {
                  _this.pc.addIceCandidate(_this.iceCandidateList.shift())
                }
              }
            }
            this.start()
          }
          WebRtcPeer.prototype.start = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              if (_this.pc.signalingState === 'closed') {
                reject(
                  'The peer connection object is in "closed" state. This is most likely due to an invocation of the dispose method before accepting in the dialogue',
                )
              }
              if (_this.configuration.mediaStream) {
                if (platform['isIonicIos']) {
                  var pc2 = _this.pc
                  pc2.addStream(_this.configuration.mediaStream)
                } else {
                  for (
                    var _i = 0,
                      _a = _this.configuration.mediaStream.getTracks();
                    _i < _a.length;
                    _i++
                  ) {
                    var track = _a[_i]
                    _this.pc.addTrack(track, _this.configuration.mediaStream)
                  }
                }
                resolve()
              }
            })
          }
          WebRtcPeer.prototype.dispose = function(
            videoSourceIsMediaStreamTrack,
          ) {
            console.debug('Disposing WebRtcPeer')
            try {
              if (this.pc) {
                if (this.pc.signalingState === 'closed') {
                  return
                }
                this.remoteCandidatesQueue = []
                this.localCandidatesQueue = []
                if (platform['isIonicIos']) {
                  var pc1 = this.pc
                  for (
                    var _i = 0, _a = pc1.getLocalStreams();
                    _i < _a.length;
                    _i++
                  ) {
                    var sender = _a[_i]
                    if (!videoSourceIsMediaStreamTrack) {
                      sender.stop()
                    }
                    pc1.removeStream(sender)
                  }
                  for (
                    var _b = 0, _c = pc1.getRemoteStreams();
                    _b < _c.length;
                    _b++
                  ) {
                    var receiver = _c[_b]
                    if (receiver.track) {
                      receiver.stop()
                    }
                  }
                } else {
                  for (
                    var _d = 0, _e = this.pc.getSenders();
                    _d < _e.length;
                    _d++
                  ) {
                    var sender = _e[_d]
                    if (!videoSourceIsMediaStreamTrack) {
                      if (sender.track) {
                        sender.track.stop()
                      }
                    }
                    this.pc.removeTrack(sender)
                  }
                  for (
                    var _f = 0, _g = this.pc.getReceivers();
                    _f < _g.length;
                    _f++
                  ) {
                    var receiver = _g[_f]
                    if (receiver.track) {
                      receiver.track.stop()
                    }
                  }
                }
                this.pc.close()
              }
            } catch (err) {
              console.warn('Exception disposing webrtc peer ' + err)
            }
          }
          WebRtcPeer.prototype.generateOffer = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              var offerAudio,
                offerVideo = true
              if (_this.configuration.mediaConstraints) {
                offerAudio =
                  typeof _this.configuration.mediaConstraints.audio ===
                  'boolean'
                    ? _this.configuration.mediaConstraints.audio
                    : true
                offerVideo =
                  typeof _this.configuration.mediaConstraints.video ===
                  'boolean'
                    ? _this.configuration.mediaConstraints.video
                    : true
              }
              var constraints = {
                offerToReceiveAudio:
                  _this.configuration.mode !== 'sendonly' && offerAudio,
                offerToReceiveVideo:
                  _this.configuration.mode !== 'sendonly' && offerVideo,
              }
              console.debug(
                'RTCPeerConnection constraints: ' + JSON.stringify(constraints),
              )
              if (
                platform.name === 'Safari' &&
                platform.ua.indexOf('Safari') !== -1
              ) {
                if (offerAudio) {
                  _this.pc.addTransceiver('audio', {
                    direction: _this.configuration.mode,
                  })
                }
                if (offerVideo) {
                  _this.pc.addTransceiver('video', {
                    direction: _this.configuration.mode,
                  })
                }
                _this.pc
                  .createOffer()
                  .then(function(offer) {
                    console.debug('Created SDP offer')
                    return _this.pc.setLocalDescription(offer)
                  })
                  .then(function() {
                    var localDescription = _this.pc.localDescription
                    if (localDescription) {
                      console.debug(
                        'Local description set',
                        localDescription.sdp,
                      )
                      resolve(localDescription.sdp)
                    } else {
                      reject('Local description is not defined')
                    }
                  })
                  .catch(function(error) {
                    return reject(error)
                  })
              } else {
                _this.pc
                  .createOffer(constraints)
                  .then(function(offer) {
                    console.debug('Created SDP offer')
                    return _this.pc.setLocalDescription(offer)
                  })
                  .then(function() {
                    var localDescription = _this.pc.localDescription
                    if (localDescription) {
                      console.debug(
                        'Local description set',
                        localDescription.sdp,
                      )
                      resolve(localDescription.sdp)
                    } else {
                      reject('Local description is not defined')
                    }
                  })
                  .catch(function(error) {
                    return reject(error)
                  })
              }
            })
          }
          WebRtcPeer.prototype.processAnswer = function(
            sdpAnswer,
            needsTimeoutOnProcessAnswer,
          ) {
            var _this = this
            return new Promise(function(resolve, reject) {
              var answer = {
                type: 'answer',
                sdp: sdpAnswer,
              }
              console.debug('SDP answer received, setting remote description')
              if (_this.pc.signalingState === 'closed') {
                reject('RTCPeerConnection is closed')
              }
              if (platform['isIonicIos']) {
                if (needsTimeoutOnProcessAnswer) {
                  setTimeout(function() {
                    console.info(
                      'setRemoteDescription run after timeout for Ionic iOS device',
                    )
                    _this.pc
                      .setRemoteDescription(new RTCSessionDescription(answer))
                      .then(function() {
                        return resolve()
                      })
                      .catch(function(error) {
                        return reject(error)
                      })
                  }, 250)
                } else {
                  _this.pc
                    .setRemoteDescription(new RTCSessionDescription(answer))
                    .then(function() {
                      return resolve()
                    })
                    .catch(function(error) {
                      return reject(error)
                    })
                }
              } else {
                _this.pc
                  .setRemoteDescription(answer)
                  .then(function() {
                    return resolve()
                  })
                  .catch(function(error) {
                    return reject(error)
                  })
              }
            })
          }
          WebRtcPeer.prototype.addIceCandidate = function(iceCandidate) {
            var _this = this
            return new Promise(function(resolve, reject) {
              console.debug('Remote ICE candidate received', iceCandidate)
              _this.remoteCandidatesQueue.push(iceCandidate)
              switch (_this.pc.signalingState) {
                case 'closed':
                  reject(new Error('PeerConnection object is closed'))
                  break
                case 'stable':
                  if (_this.pc.remoteDescription) {
                    _this.pc
                      .addIceCandidate(iceCandidate)
                      .then(function() {
                        return resolve()
                      })
                      .catch(function(error) {
                        return reject(error)
                      })
                  } else {
                    _this.iceCandidateList.push(iceCandidate)
                    resolve()
                  }
                  break
                default:
                  _this.iceCandidateList.push(iceCandidate)
                  resolve()
              }
            })
          }
          return WebRtcPeer
        })()
        exports.WebRtcPeer = WebRtcPeer
        var WebRtcPeerRecvonly = (function(_super) {
          __extends(WebRtcPeerRecvonly, _super)
          function WebRtcPeerRecvonly(configuration) {
            var _this = this
            configuration.mode = 'recvonly'
            _this = _super.call(this, configuration) || this
            return _this
          }
          return WebRtcPeerRecvonly
        })(WebRtcPeer)
        exports.WebRtcPeerRecvonly = WebRtcPeerRecvonly
        var WebRtcPeerSendonly = (function(_super) {
          __extends(WebRtcPeerSendonly, _super)
          function WebRtcPeerSendonly(configuration) {
            var _this = this
            configuration.mode = 'sendonly'
            _this = _super.call(this, configuration) || this
            return _this
          }
          return WebRtcPeerSendonly
        })(WebRtcPeer)
        exports.WebRtcPeerSendonly = WebRtcPeerSendonly
        var WebRtcPeerSendrecv = (function(_super) {
          __extends(WebRtcPeerSendrecv, _super)
          function WebRtcPeerSendrecv(configuration) {
            var _this = this
            configuration.mode = 'sendrecv'
            _this = _super.call(this, configuration) || this
            return _this
          }
          return WebRtcPeerSendrecv
        })(WebRtcPeer)
        exports.WebRtcPeerSendrecv = WebRtcPeerSendrecv
      },
      { freeice: 2, platform: 8, uuid: 9 },
    ],
    53: [
      function(require, module, exports) {
        'use strict'
        Object.defineProperty(exports, '__esModule', { value: true })
        var platform = require('platform')
        var WebRtcStats = (function() {
          function WebRtcStats(stream) {
            this.stream = stream
            this.webRtcStatsEnabled = false
            this.statsInterval = 1
            this.stats = {
              inbound: {
                audio: {
                  bytesReceived: 0,
                  packetsReceived: 0,
                  packetsLost: 0,
                },
                video: {
                  bytesReceived: 0,
                  packetsReceived: 0,
                  packetsLost: 0,
                  framesDecoded: 0,
                  nackCount: 0,
                },
              },
              outbound: {
                audio: {
                  bytesSent: 0,
                  packetsSent: 0,
                },
                video: {
                  bytesSent: 0,
                  packetsSent: 0,
                  framesEncoded: 0,
                  nackCount: 0,
                },
              },
            }
          }
          WebRtcStats.prototype.isEnabled = function() {
            return this.webRtcStatsEnabled
          }
          WebRtcStats.prototype.initWebRtcStats = function() {
            var _this = this
            var elastestInstrumentation = localStorage.getItem(
              'elastest-instrumentation',
            )
            if (elastestInstrumentation) {
              console.warn(
                'WebRtc stats enabled for stream ' +
                  this.stream.streamId +
                  ' of connection ' +
                  this.stream.connection.connectionId,
              )
              this.webRtcStatsEnabled = true
              var instrumentation_1 = JSON.parse(elastestInstrumentation)
              this.statsInterval = instrumentation_1.webrtc.interval
              console.warn(
                'localStorage item: ' + JSON.stringify(instrumentation_1),
              )
              this.webRtcStatsIntervalId = setInterval(function() {
                _this.sendStatsToHttpEndpoint(instrumentation_1)
              }, this.statsInterval * 1000)
              return
            }
            console.debug('WebRtc stats not enabled')
          }
          WebRtcStats.prototype.stopWebRtcStats = function() {
            if (this.webRtcStatsEnabled) {
              clearInterval(this.webRtcStatsIntervalId)
              console.warn(
                'WebRtc stats stopped for disposed stream ' +
                  this.stream.streamId +
                  ' of connection ' +
                  this.stream.connection.connectionId,
              )
            }
          }
          WebRtcStats.prototype.getSelectedIceCandidateInfo = function() {
            var _this = this
            return new Promise(function(resolve, reject) {
              _this.getStatsAgnostic(
                _this.stream.getRTCPeerConnection(),
                function(stats) {
                  if (
                    platform.name.indexOf('Chrome') !== -1 ||
                    platform.name.indexOf('Opera') !== -1
                  ) {
                    var localCandidateId = void 0,
                      remoteCandidateId = void 0,
                      googCandidatePair = void 0
                    var localCandidates = {}
                    var remoteCandidates = {}
                    for (var key in stats) {
                      var stat = stats[key]
                      if (stat.type === 'localcandidate') {
                        localCandidates[stat.id] = stat
                      } else if (stat.type === 'remotecandidate') {
                        remoteCandidates[stat.id] = stat
                      } else if (
                        stat.type === 'googCandidatePair' &&
                        stat.googActiveConnection === 'true'
                      ) {
                        googCandidatePair = stat
                        localCandidateId = stat.localCandidateId
                        remoteCandidateId = stat.remoteCandidateId
                      }
                    }
                    var finalLocalCandidate_1 =
                      localCandidates[localCandidateId]
                    if (finalLocalCandidate_1) {
                      var candList = _this.stream.getLocalIceCandidateList()
                      var cand = candList.filter(function(c) {
                        return (
                          !!c.candidate &&
                          c.candidate.indexOf(
                            finalLocalCandidate_1.ipAddress,
                          ) >= 0 &&
                          c.candidate.indexOf(
                            finalLocalCandidate_1.portNumber,
                          ) >= 0 &&
                          c.candidate.indexOf(finalLocalCandidate_1.priority) >=
                            0
                        )
                      })
                      finalLocalCandidate_1.raw = cand[0]
                        ? cand[0].candidate
                        : 'ERROR: Cannot find local candidate in list of sent ICE candidates'
                    } else {
                      finalLocalCandidate_1 =
                        'ERROR: No active local ICE candidate. Probably ICE-TCP is being used'
                    }
                    var finalRemoteCandidate_1 =
                      remoteCandidates[remoteCandidateId]
                    if (finalRemoteCandidate_1) {
                      var candList = _this.stream.getRemoteIceCandidateList()
                      var cand = candList.filter(function(c) {
                        return (
                          !!c.candidate &&
                          c.candidate.indexOf(
                            finalRemoteCandidate_1.ipAddress,
                          ) >= 0 &&
                          c.candidate.indexOf(
                            finalRemoteCandidate_1.portNumber,
                          ) >= 0 &&
                          c.candidate.indexOf(
                            finalRemoteCandidate_1.priority,
                          ) >= 0
                        )
                      })
                      finalRemoteCandidate_1.raw = cand[0]
                        ? cand[0].candidate
                        : 'ERROR: Cannot find remote candidate in list of received ICE candidates'
                    } else {
                      finalRemoteCandidate_1 =
                        'ERROR: No active remote ICE candidate. Probably ICE-TCP is being used'
                    }
                    resolve({
                      googCandidatePair: googCandidatePair,
                      localCandidate: finalLocalCandidate_1,
                      remoteCandidate: finalRemoteCandidate_1,
                    })
                  } else {
                    reject(
                      'Selected ICE candidate info only available for Chrome',
                    )
                  }
                },
                function(error) {
                  reject(error)
                },
              )
            })
          }
          WebRtcStats.prototype.sendStatsToHttpEndpoint = function(
            instrumentation,
          ) {
            var _this = this
            var sendPost = function(json) {
              var http = new XMLHttpRequest()
              var url = instrumentation.webrtc.httpEndpoint
              http.open('POST', url, true)
              http.setRequestHeader('Content-type', 'application/json')
              http.onreadystatechange = function() {
                if (http.readyState === 4 && http.status === 200) {
                  console.log(
                    'WebRtc stats successfully sent to ' +
                      url +
                      ' for stream ' +
                      _this.stream.streamId +
                      ' of connection ' +
                      _this.stream.connection.connectionId,
                  )
                }
              }
              http.send(json)
            }
            var f = function(stats) {
              if (platform.name.indexOf('Firefox') !== -1) {
                stats.forEach(function(stat) {
                  var json = {}
                  if (
                    stat.type === 'inbound-rtp' &&
                    stat.nackCount !== null &&
                    stat.isRemote === false &&
                    stat.id.startsWith('inbound') &&
                    stat.remoteId.startsWith('inbound')
                  ) {
                    var metricId =
                      'webrtc_inbound_' + stat.mediaType + '_' + stat.ssrc
                    var jit = stat.jitter * 1000
                    var metrics = {
                      bytesReceived:
                        (stat.bytesReceived -
                          _this.stats.inbound[stat.mediaType].bytesReceived) /
                        _this.statsInterval,
                      jitter: jit,
                      packetsReceived:
                        (stat.packetsReceived -
                          _this.stats.inbound[stat.mediaType].packetsReceived) /
                        _this.statsInterval,
                      packetsLost:
                        (stat.packetsLost -
                          _this.stats.inbound[stat.mediaType].packetsLost) /
                        _this.statsInterval,
                    }
                    var units = {
                      bytesReceived: 'bytes',
                      jitter: 'ms',
                      packetsReceived: 'packets',
                      packetsLost: 'packets',
                    }
                    if (stat.mediaType === 'video') {
                      metrics['framesDecoded'] =
                        (stat.framesDecoded -
                          _this.stats.inbound.video.framesDecoded) /
                        _this.statsInterval
                      metrics['nackCount'] =
                        (stat.nackCount - _this.stats.inbound.video.nackCount) /
                        _this.statsInterval
                      units['framesDecoded'] = 'frames'
                      units['nackCount'] = 'packets'
                      _this.stats.inbound.video.framesDecoded =
                        stat.framesDecoded
                      _this.stats.inbound.video.nackCount = stat.nackCount
                    }
                    _this.stats.inbound[stat.mediaType].bytesReceived =
                      stat.bytesReceived
                    _this.stats.inbound[stat.mediaType].packetsReceived =
                      stat.packetsReceived
                    _this.stats.inbound[stat.mediaType].packetsLost =
                      stat.packetsLost
                    json = {
                      '@timestamp': new Date(stat.timestamp).toISOString(),
                      exec: instrumentation.exec,
                      component: instrumentation.component,
                      stream: 'webRtc',
                      et_type: metricId,
                      stream_type: 'composed_metrics',
                      units: units,
                    }
                    json[metricId] = metrics
                    sendPost(JSON.stringify(json))
                  } else if (
                    stat.type === 'outbound-rtp' &&
                    stat.isRemote === false &&
                    stat.id.toLowerCase().includes('outbound')
                  ) {
                    var metricId =
                      'webrtc_outbound_' + stat.mediaType + '_' + stat.ssrc
                    var metrics = {
                      bytesSent:
                        (stat.bytesSent -
                          _this.stats.outbound[stat.mediaType].bytesSent) /
                        _this.statsInterval,
                      packetsSent:
                        (stat.packetsSent -
                          _this.stats.outbound[stat.mediaType].packetsSent) /
                        _this.statsInterval,
                    }
                    var units = {
                      bytesSent: 'bytes',
                      packetsSent: 'packets',
                    }
                    if (stat.mediaType === 'video') {
                      metrics['framesEncoded'] =
                        (stat.framesEncoded -
                          _this.stats.outbound.video.framesEncoded) /
                        _this.statsInterval
                      units['framesEncoded'] = 'frames'
                      _this.stats.outbound.video.framesEncoded =
                        stat.framesEncoded
                    }
                    _this.stats.outbound[stat.mediaType].bytesSent =
                      stat.bytesSent
                    _this.stats.outbound[stat.mediaType].packetsSent =
                      stat.packetsSent
                    json = {
                      '@timestamp': new Date(stat.timestamp).toISOString(),
                      exec: instrumentation.exec,
                      component: instrumentation.component,
                      stream: 'webRtc',
                      et_type: metricId,
                      stream_type: 'composed_metrics',
                      units: units,
                    }
                    json[metricId] = metrics
                    sendPost(JSON.stringify(json))
                  }
                })
              } else if (
                platform.name.indexOf('Chrome') !== -1 ||
                platform.name.indexOf('Opera') !== -1
              ) {
                for (
                  var _i = 0, _a = Object.keys(stats);
                  _i < _a.length;
                  _i++
                ) {
                  var key = _a[_i]
                  var stat = stats[key]
                  if (stat.type === 'ssrc') {
                    var json = {}
                    if (
                      'bytesReceived' in stat &&
                      ((stat.mediaType === 'audio' &&
                        'audioOutputLevel' in stat) ||
                        (stat.mediaType === 'video' && 'qpSum' in stat))
                    ) {
                      var metricId =
                        'webrtc_inbound_' + stat.mediaType + '_' + stat.ssrc
                      var metrics = {
                        bytesReceived:
                          (stat.bytesReceived -
                            _this.stats.inbound[stat.mediaType].bytesReceived) /
                          _this.statsInterval,
                        jitter: stat.googJitterBufferMs,
                        packetsReceived:
                          (stat.packetsReceived -
                            _this.stats.inbound[stat.mediaType]
                              .packetsReceived) /
                          _this.statsInterval,
                        packetsLost:
                          (stat.packetsLost -
                            _this.stats.inbound[stat.mediaType].packetsLost) /
                          _this.statsInterval,
                      }
                      var units = {
                        bytesReceived: 'bytes',
                        jitter: 'ms',
                        packetsReceived: 'packets',
                        packetsLost: 'packets',
                      }
                      if (stat.mediaType === 'video') {
                        metrics['framesDecoded'] =
                          (stat.framesDecoded -
                            _this.stats.inbound.video.framesDecoded) /
                          _this.statsInterval
                        metrics['nackCount'] =
                          (stat.googNacksSent -
                            _this.stats.inbound.video.nackCount) /
                          _this.statsInterval
                        units['framesDecoded'] = 'frames'
                        units['nackCount'] = 'packets'
                        _this.stats.inbound.video.framesDecoded =
                          stat.framesDecoded
                        _this.stats.inbound.video.nackCount = stat.googNacksSent
                      }
                      _this.stats.inbound[stat.mediaType].bytesReceived =
                        stat.bytesReceived
                      _this.stats.inbound[stat.mediaType].packetsReceived =
                        stat.packetsReceived
                      _this.stats.inbound[stat.mediaType].packetsLost =
                        stat.packetsLost
                      json = {
                        '@timestamp': new Date(stat.timestamp).toISOString(),
                        exec: instrumentation.exec,
                        component: instrumentation.component,
                        stream: 'webRtc',
                        et_type: metricId,
                        stream_type: 'composed_metrics',
                        units: units,
                      }
                      json[metricId] = metrics
                      sendPost(JSON.stringify(json))
                    } else if ('bytesSent' in stat) {
                      var metricId =
                        'webrtc_outbound_' + stat.mediaType + '_' + stat.ssrc
                      var metrics = {
                        bytesSent:
                          (stat.bytesSent -
                            _this.stats.outbound[stat.mediaType].bytesSent) /
                          _this.statsInterval,
                        packetsSent:
                          (stat.packetsSent -
                            _this.stats.outbound[stat.mediaType].packetsSent) /
                          _this.statsInterval,
                      }
                      var units = {
                        bytesSent: 'bytes',
                        packetsSent: 'packets',
                      }
                      if (stat.mediaType === 'video') {
                        metrics['framesEncoded'] =
                          (stat.framesEncoded -
                            _this.stats.outbound.video.framesEncoded) /
                          _this.statsInterval
                        units['framesEncoded'] = 'frames'
                        _this.stats.outbound.video.framesEncoded =
                          stat.framesEncoded
                      }
                      _this.stats.outbound[stat.mediaType].bytesSent =
                        stat.bytesSent
                      _this.stats.outbound[stat.mediaType].packetsSent =
                        stat.packetsSent
                      json = {
                        '@timestamp': new Date(stat.timestamp).toISOString(),
                        exec: instrumentation.exec,
                        component: instrumentation.component,
                        stream: 'webRtc',
                        et_type: metricId,
                        stream_type: 'composed_metrics',
                        units: units,
                      }
                      json[metricId] = metrics
                      sendPost(JSON.stringify(json))
                    }
                  }
                }
              }
            }
            this.getStatsAgnostic(
              this.stream.getRTCPeerConnection(),
              f,
              function(error) {
                console.log(error)
              },
            )
          }
          WebRtcStats.prototype.standardizeReport = function(response) {
            console.log(response)
            var standardReport = {}
            if (platform.name.indexOf('Firefox') !== -1) {
              Object.keys(response).forEach(function(key) {
                console.log(response[key])
              })
              return response
            }
            response.result().forEach(function(report) {
              var standardStats = {
                id: report.id,
                timestamp: report.timestamp,
                type: report.type,
              }
              report.names().forEach(function(name) {
                standardStats[name] = report.stat(name)
              })
              standardReport[standardStats.id] = standardStats
            })
            return standardReport
          }
          WebRtcStats.prototype.getStatsAgnostic = function(
            pc,
            successCb,
            failureCb,
          ) {
            var _this = this
            if (platform.name.indexOf('Firefox') !== -1) {
              return pc
                .getStats(null)
                .then(function(response) {
                  var report = _this.standardizeReport(response)
                  successCb(report)
                })
                .catch(failureCb)
            } else if (
              platform.name.indexOf('Chrome') !== -1 ||
              platform.name.indexOf('Opera') !== -1
            ) {
              return pc.getStats(
                function(response) {
                  var report = _this.standardizeReport(response)
                  successCb(report)
                },
                null,
                failureCb,
              )
            }
          }
          return WebRtcStats
        })()
        exports.WebRtcStats = WebRtcStats
      },
      { platform: 8 },
    ],
  },
  {},
  [17],
)
