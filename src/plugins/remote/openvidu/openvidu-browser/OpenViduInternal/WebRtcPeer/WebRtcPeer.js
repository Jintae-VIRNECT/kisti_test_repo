'use strict'
/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
        b === null ? Object.create(b) : ((__.prototype = b.prototype), new __())
    }
  })()
exports.__esModule = true
var freeice = require('freeice')
var uuid = require('uuid')
var platform = require('platform')
var WebRtcPeer = /** @class */ (function() {
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
          _this.localCandidatesQueue.push({ candidate: candidate.candidate })
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
  /**
   * This function creates the RTCPeerConnection object taking into account the
   * properties received in the constructor. It starts the SDP negotiation
   * process: generates the SDP offer and invokes the onsdpoffer callback. This
   * callback is expected to send the SDP offer, in order to obtain an SDP
   * answer from another peer.
   */
  WebRtcPeer.prototype.start = function() {
    var _this = this
    return new Promise(function(resolve, reject) {
      if (_this.pc.signalingState === 'closed') {
        reject(
          'The peer connection object is in "closed" state. This is most likely due to an invocation of the dispose method before accepting in the dialogue',
        )
      }
      if (_this.configuration.mediaStream) {
        for (
          var _i = 0, _a = _this.configuration.mediaStream.getTracks();
          _i < _a.length;
          _i++
        ) {
          var track = _a[_i]
          _this.pc.addTrack(track, _this.configuration.mediaStream)
        }
        resolve()
      }
    })
  }
  /**
   * This method frees the resources used by WebRtcPeer
   */
  WebRtcPeer.prototype.dispose = function() {
    console.debug('Disposing WebRtcPeer')
    if (this.pc) {
      if (this.pc.signalingState === 'closed') {
        return
      }
      this.pc.close()
      this.remoteCandidatesQueue = []
      this.localCandidatesQueue = []
    }
  }
  /**
   * Function that creates an offer, sets it as local description and returns the offer param
   * to send to OpenVidu Server (will be the remote description of other peer)
   */
  WebRtcPeer.prototype.generateOffer = function() {
    var _this = this
    return new Promise(function(resolve, reject) {
      var offerAudio,
        offerVideo = true
      // Constraints must have both blocks
      if (_this.configuration.mediaConstraints) {
        offerAudio =
          typeof _this.configuration.mediaConstraints.audio === 'boolean'
            ? _this.configuration.mediaConstraints.audio
            : true
        offerVideo =
          typeof _this.configuration.mediaConstraints.video === 'boolean'
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
      if (platform.name === 'Safari' && platform.ua.indexOf('Safari') !== -1) {
        // Safari (excluding Ionic), at least on iOS just seems to support unified plan, whereas in other browsers is not yet ready and considered experimental
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
              console.debug('Local description set', localDescription.sdp)
              resolve(localDescription.sdp)
            } else {
              reject('Local description is not defined')
            }
          })
          ['catch'](function(error) {
            return reject(error)
          })
      } else {
        // Rest of platforms
        _this.pc
          .createOffer(constraints)
          .then(function(offer) {
            console.debug('Created SDP offer')
            return _this.pc.setLocalDescription(offer)
          })
          .then(function() {
            var localDescription = _this.pc.localDescription
            if (localDescription) {
              console.debug('Local description set', localDescription.sdp)
              resolve(localDescription.sdp)
            } else {
              reject('Local description is not defined')
            }
          })
          ['catch'](function(error) {
            return reject(error)
          })
      }
    })
  }
  /**
   * Function invoked when a SDP answer is received. Final step in SDP negotiation, the peer
   * just needs to set the answer as its remote description
   */
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
        // Ionic iOS platform
        if (needsTimeoutOnProcessAnswer) {
          // 400 ms have not elapsed yet since first remote stream triggered Stream#initWebRtcPeerReceive
          setTimeout(function() {
            console.info(
              'setRemoteDescription run after timeout for Ionic iOS device',
            )
            _this.pc
              .setRemoteDescription(new RTCSessionDescription(answer))
              .then(function() {
                return resolve()
              })
              ['catch'](function(error) {
                return reject(error)
              })
          }, 250)
        } else {
          // 400 ms have elapsed
          _this.pc
            .setRemoteDescription(new RTCSessionDescription(answer))
            .then(function() {
              return resolve()
            })
            ['catch'](function(error) {
              return reject(error)
            })
        }
      } else {
        // Rest of platforms
        _this.pc
          .setRemoteDescription(answer)
          .then(function() {
            return resolve()
          })
          ['catch'](function(error) {
            return reject(error)
          })
      }
    })
  }
  /**
   * Callback function invoked when an ICE candidate is received
   */
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
              ['catch'](function(error) {
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
  WebRtcPeer.prototype.addIceConnectionStateChangeListener = function(otherId) {
    var _this = this
    this.pc.oniceconnectionstatechange = function() {
      var iceConnectionState = _this.pc.iceConnectionState
      switch (iceConnectionState) {
        case 'disconnected':
          // Possible network disconnection
          console.warn(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "disconnected". Possible network disconnection',
          )
          break
        case 'failed':
          console.error(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') to "failed"',
          )
          break
        case 'closed':
          console.log(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "closed"',
          )
          break
        case 'new':
          console.log(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "new"',
          )
          break
        case 'checking':
          console.log(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "checking"',
          )
          break
        case 'connected':
          console.log(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "connected"',
          )
          break
        case 'completed':
          console.log(
            'IceConnectionState of RTCPeerConnection ' +
              _this.id +
              ' (' +
              otherId +
              ') change to "completed"',
          )
          break
      }
    }
  }
  return WebRtcPeer
})()
exports.WebRtcPeer = WebRtcPeer
var WebRtcPeerRecvonly = /** @class */ (function(_super) {
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
var WebRtcPeerSendonly = /** @class */ (function(_super) {
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
var WebRtcPeerSendrecv = /** @class */ (function(_super) {
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
//# sourceMappingURL=WebRtcPeer.js.map
