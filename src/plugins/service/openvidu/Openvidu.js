import { OpenVidu } from 'openvidu-browser';
import openviduStore from './OpenviduStore'

// var OV;
var session;

var sessionName;	// Name of the video session the user will connect to
var token;			// Token retrieved from OpenVidu Server

export default {
  install(Vue, { Store }) {
    console.log(Store)
  
    if (!Store) {
      throw new Error('Can not find vuex store');
    } else {
      Store.registerModule('openvidu', openviduStore);
    }

    // --- 1) Get an OpenVidu object ---
    const OV = new OpenVidu()

    let publisher = null
    let recorder = null
    const subscribers = []

    const addSessionEventListener = () => {
      session.on('streamCreated', (event) => {
        // Subscribe to the Stream to receive it
        // HTML video will be appended to element with 'video-container' id
      
        let streamObj = addSession(event.stream)
        
        setTimeout(() => {
          let nodeId = event.stream.connection.connectionId
          var subscriber = session.subscribe(event.stream, 'video-view__' + nodeId, {
            insertMode: "PREPEND" //VideoInsertMode.PREPEND
          })
          // Store.dispatch('addChat', {
          //   text: streamObj.nickName + '님이 대화에 참여하셨습니다.',
          //   name: 'people',
          //   date: new Date,
          //   chatId: null,
          //   type: 'system'
          // })
          subscribers.push(subscriber)
          // var subscriber = session.subscribe(event.stream)
          console.log(subscriber)
          console.log(subscriber.stream.getMediaStream())
          subscriber.on('streamPlaying', (event) => {
            console.log(event)
            console.log(subscriber.stream.getMediaStream())
            updateSessionStream(nodeId, subscriber.stream.getMediaStream())
          })
        }, 1000)
      });
      
      // On every Stream destroyed...
      session.on('streamDestroyed', (event) => {
        // Delete the HTML element with the user's name and nickname
        const connectionId = event.stream.connection.connectionId
        Store.dispatch('removeSession', connectionId)
      });

      session.on('signal:audio', (event) => {
        let updateSession = {
          nodeId: event.from.connectionId,
          audio: event.data === 'true' ? true: false
        }

        Store.dispatch('updateSessionInfo', updateSession)
      })

      session.on('signal:video', (event) => {
        let updateSession = {
          nodeId: event.from.connectionId,
          video: event.data === 'true' ? true: false
        }

        Store.dispatch('updateSessionInfo', updateSession)
      })

      session.on('signal:chat', (event) => {
        console.log(event)
        let data = event.data
        let chat = {
          text: data.replace(/\</g, '&lt;'),
          name: JSON.parse(event.from.data.split('%/%')[0]).clientData,
          date: new Date(),
          chatId: event.from.rpcSessionId,
          type: false
        }
        if (session.connection.connectionId === event.from.connectionId) {
          // 본인
          chat.type = 'me'
        }
        Store.dispatch('addChat', chat)
      })

      session.on('signal:pointing', (event) => {
        console.log(event)
        let data = event.data
        let chat = {
          text: data.split('::')[1].replace(/\</g, '&lt;'),
          name: JSON.parse(event.from.data.split('%/%')[0]).clientData,
          date: data.split('::')[0],
          chatId: event.from.rpcSessionId,
          type: false
        }
        Store.dispatch('addChat', chat)
      })
    }

    const updateSessionStream = (nodeId, stream) => {
      Store.dispatch('updateSessionStream', {
        nodeId: nodeId,
        stream: stream
      })
    }

    const addSession = (stream) => {
      console.log('------------------connect session!!!!----------------------------')
      let streamObj
      if (stream.nodeId === 'main') {
        streamObj = {
          stream: null,
          // session: session,
          nickName: stream.nickName,
          userName: stream.userName,
          nodeId: 'main'
        }
      } else {
        let connection = stream.connection
        
        let clientData = JSON.parse(connection.data.split('%/%')[0]).clientData,
            // serverData = JSON.parse(connection.data.split('%/%')[1]).serverData,
            nodeId = connection.connectionId
      
        streamObj = {
          stream: null,
          nickName: clientData,
          // userName: serverData,
          nodeId: nodeId,
          audio: true,
          video: true
        }
      }
      
      Store.dispatch('addSession', streamObj)
      return streamObj
    }

    const getToken = (name, callback) => {
      sessionName = name // Video-call chosen by the user
    
      httpPostRequest(
        'api-sessions/get-token',
        {sessionName: sessionName},
        'Request of TOKEN gone WRONG:',
        (response) => {
          token = response[0]; // Get token from response
          console.warn('Request of TOKEN gone WELL (TOKEN:' + token + ')')
          callback(token); // Continue the join operation
        }
      );
    }

    const removeUser = () => {
      httpPostRequest(
        'api-sessions/remove-user',
        {sessionName: sessionName, token: token},
        'User couldn\'t be removed from session', 
        (response) => {
          console.warn("You have been removed from session " + sessionName);
        }
      )
    }
    
    const httpPostRequest = (url, body, errorMsg, callback) => {
      console.log(url, body)
      var http = new XMLHttpRequest();
      http.open('POST', 'https://192.168.0.105:5001/'+url, true);
      http.setRequestHeader('Content-type', 'application/json');
      http.addEventListener('readystatechange', processRequest, false);
      http.send(JSON.stringify(body));
    
      function processRequest() {
        if (http.readyState === 4) {
          if (http.status === 200) {
            try {
              callback(JSON.parse(http.responseText));
            } catch (e) {
              callback();
            }
          } else {
            console.warn(errorMsg);
            console.warn(http);
          }
        }
      }
    }
    
    const httpGetRequest = (url, errorMsg, callback) => {
      console.log(url)
      var http = new XMLHttpRequest();
      http.open('GET', 'https://192.168.0.105:5001/'+url, true);
      http.setRequestHeader('Content-type', 'application/json');
      http.addEventListener('readystatechange', processRequest, false);
      http.send();
    
      function processRequest() {
        if (http.readyState === 4) {
          if (http.status === 200) {
            try {
              callback(JSON.parse(http.responseText));
            } catch (e) {
              callback();
            }
          } else {
            console.warn(errorMsg);
            console.warn(http);
          }
        }
      }
    }
      
    window.onbeforeunload = () => {
      // Gracefully leave session
      console.log(session)
      if (session) {
        removeUser()
      }
    }

    Vue.prototype.$openvidu = {
      join: (name, nickName) => {
        return new Promise((resolve, reject) => {
        getToken(name, (token) => {

          // --- 2) Init a session ---
          session = OV.initSession();

          // --- 3) Specify the actions when events take place in the session ---
          // On every new Stream received...
          addSessionEventListener()

          // --- 4) Connect to the session passing the retrieved token and some more data from
          //        the client (in this case a JSON with the nickname chosen by the user) ---
          session.connect(token, { clientData: nickName })
            .then(() => {
              addSession({
                nickName: nickName,
                userName: name,
                nodeId: 'main'
              })
              setTimeout(() => {

                // --- 5) Set page layout for active call ---

                // Here we check somehow if the user has 'PUBLISHER' role before
                // trying to publish its stream. Even if someone modified the client's code and
                // published the stream, it wouldn't work if the token sent in Session.connect
                // method is not recognized as 'PUBLIHSER' role by OpenVidu Server

                // --- 6) Get your own camera stream ---
                publisher = OV.initPublisher('video-view__main', {
                  audioSource: undefined, // The source of audio. If undefined default microphone
                  videoSource: undefined, // The source of video. If undefined default webcam
                  publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
                  publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
                  resolution: '640x480',  // The resolution of your video
                  frameRate: 30,			// The frame rate of your video
                  insertMode: 'PREPEND',	// How the video is inserted in the target element 'video-container'
                  mirror: false       	// Whether to mirror your local video or not
                })

                // --- 7) Specify the actions when events take place in our publisher ---
                publisher.on('streamCreated', (event) => {
                  updateSessionStream('main', publisher.stream.getMediaStream())
                  console.log(publisher)

                  Store.dispatch('setMainSession', {
                    stream: publisher.stream.getMediaStream(),
                    // session: session,
                    // connection: publisher.connection,
                    nickName: nickName,
                    userName: name,
                    nodeId: 'main'
                  })
                })

                // --- 8) Publish your stream ---
                session.publish(publisher);
                resolve(session)
              }, 1000)
            })
            .catch(error => {
              console.warn('There was an error connecting to the session:', error.code, error.message);
              reject()
            })
          })
        })
      },
      leave: () => {
        // --- 9) Leave the session by calling 'disconnect' method over the Session object ---
        removeUser()

        Store.dispatch('clearSession')

        session.disconnect();
        session = null;  
      },
      sendChat: (text) => {
        if (text.trim().length === 0) return
        session.signal({
          data: text.trim(),
          to: session.connection,
          type: 'signal:chat'
        })
      },
      // sendAudio: (type) => {
      //   session.signal({
      //     enable: type,
      //     to: session.connection,
      //     type: 'signal:audio'
      //   })
      // },
      getDevices: () => {
        return new Promise((resolve, reject) => {
          OV.getDevices()
            .then(devices => {
              console.log(devices)
              let videos = []
              let audios = []
              devices.forEach(device => {
                if (device.kind === 'audioinput') {
                  audios.push(device)
                } else if (device.kind === 'videoinput') {
                  videos.push(device)
                }
              })
              resolve({
                videos,
                audios
              })
            })

        })
      },
      getState: () => {
        return {
          audio: publisher.stream.audioActive,
          video: publisher.stream.videoActive
        }
      },
      streamOnOff: (active) => {
        // console.log(publisher.stream.audioActive)
        publisher.publishVideo(active)
        session.signal({
          data: active ? "true" : "false",
          to: session.connection,
          type: 'signal:video'
        })
      },
      micOnOff: (active) => {
        publisher.publishAudio(active)
        session.signal({
          data: active ? "true" : "false",
          to: session.connection,
          type: 'signal:audio'
        })
      },
      audioOnOff: (id, statue) => {
          let idx = subscribers.findIndex(subscriber => subscriber.id.indexOf(id) > -1)
          if (idx < 0) {
            console.log('can not find user')
            return
          }
          let subscribe = subscribers[idx].subscribeToAudio(statue)
          // console.log(subscribe)
      },
      disconnect: (id) => {
        return new Promise((resolve, reject) => {

          let idx = subscribers.findIndex(subscriber => subscriber.id.indexOf(id) > -1)
          // token(session) 생성 시 OpenViduRole.MODERATOR 으로 생성 해야함
          if (idx < 0) {
            reject('can not find user')
            return
          }
          
          session.forceDisconnect({ connectionId: subscribers[idx].stream.connection.connectionId })
            .then(() => {
              resolve()
            })
            .catch(err => {
              reject(err)
            })
        })
      },
      record: () => {
        return new Promise((resolve, reject) => {
          if (recorder !== null && recorder.state === 'READY') {
            reject('you can not start record')
            return
          }
          
          recorder = OV.initLocalRecorder(session.connection.stream)
          recorder.record()
            .then(() => {
              resolve()
            })
            .catch(err => {
              reject(err)
            })
        })
      },
      stop: () => {
        return new Promise((resolve, reject) => {
          if (recorder === null ||
            (recorder.state !== 'RECORDING' && recorder.state !== 'PAUSED')) {
            reject('you can not stop record')
            return
          }
          
          recorder.stop()
            .then(() => {
              recorder.download()
              recorder = null
              resolve()
            })
            .catch(err => {
              reject(err)
            })
        })
      },
      active: () => {
        return new Promise((resolve, reject) => {
          httpGetRequest(
            'api-sessions/active',
            'User couldn\'t be removed from session', 
            (response) => {
              console.warn("You have been removed from session " + sessionName);
              console.log(response)
              resolve(response)
            }
          )
        })
      }
    }
  }
}