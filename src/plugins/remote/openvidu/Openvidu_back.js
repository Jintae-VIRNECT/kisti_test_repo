import { OpenVidu } from 'openvidu-browser';
import $store from 'stores/service';

var OV;
var session;

var sessionName;	// Name of the video session the user will connect to
var token;			// Token retrieved from OpenVidu Server


/* OPENVIDU METHODS */
const joinSession = (name, nickName) => {
  return new Promise((resolve, reject) => {
    getToken(name, (token) => {

      // --- 1) Get an OpenVidu object ---
      OV = new OpenVidu();

      // --- 2) Init a session ---
      session = OV.initSession();
      // OV.getDevices().then(devices => {
      //   console.log(devices)
      //   // 'devices' array contains all available media devices
      // });

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
            var publisher = OV.initPublisher('video-view__main', {
              audioSource: undefined, // The source of audio. If undefined default microphone
              videoSource: undefined, //'https://www.w3schools.com/html/mov_bbb.mp4', // The source of video. If undefined default webcam
              publishAudio: true,  	// Whether you want to start publishing with your audio unmuted or not
              publishVideo: true,  	// Whether you want to start publishing with your video enabled or not
              resolution: '640x480',  // The resolution of your video
              frameRate: 30,			// The frame rate of your video
              insertMode: 'APPEND',	// How the video is inserted in the target element 'video-container'
              mirror: false       	// Whether to mirror your local video or not
            })

            // --- 7) Specify the actions when events take place in our publisher ---
            publisher.on('streamCreated', (event) => {
              updateSessionStream('main', publisher.stream.getMediaStream())
              console.log(publisher)

              $store.dispatch('setMainSession', {
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
}

const leaveSession = () => {

  // --- 9) Leave the session by calling 'disconnect' method over the Session object ---
  removeUser()

  $store.dispatch('clearSession')

	session.disconnect();
  session = null;  
}

const sendChat = (text) => {
  session.signal({
    data: text,
    to: session.connection,
    type: 'signal:chat'
  })
}

/* OTHER METHODS */
function addSessionEventListener() {
  session.on('streamCreated', sessionStreamCreated);
        
  // On every Stream destroyed...
  session.on('streamDestroyed', sessionStreamDestroyed);

  session.on('signal:chat', receiveChat)
}

function receiveChat(event) {
  let chat = {
    text: event.data.replace(/\</g, '&lt;'),
    name: JSON.parse(event.from.data.split('%/%')[0]).clientData,
    date: event.from.creationTime,
    chatId: event.from.rpcSessionId,
    type: false
  }
  if (session.connection.connectionId === event.from.connectionId) {
    // 본인
    chat.type = 'me'
  }
  $store.dispatch('addChat', chat)
}

function sessionStreamCreated(event) {
  console.log(event)

  // Subscribe to the Stream to receive it
  // HTML video will be appended to element with 'video-container' id

  addSession(event.stream)
  
  setTimeout(() => {
    let nodeId = event.stream.connection.connectionId
    var subscriber = session.subscribe(event.stream, 'video-view__'+nodeId)
    // var subscriber = session.subscribe(event.stream)
    console.log(subscriber)
    console.log(subscriber.stream.getMediaStream())
    subscriber.on('streamPlaying', (event) => {
      console.log(event)
      console.log(subscriber.stream.getMediaStream())
      updateSessionStream(nodeId, subscriber.stream.getMediaStream())
    })
  }, 1000)
}

function sessionStreamDestroyed (event) {
  // Delete the HTML element with the user's name and nickname
  const connectionId = event.stream.connection.connectionId
  $store.dispatch('removeSession', connectionId)
}

function addSession(stream) {
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
        serverData = JSON.parse(connection.data.split('%/%')[1]).serverData,
        nodeId = connection.connectionId
  
    streamObj = {
      stream: null,
      nickName: clientData,
      userName: serverData,
      nodeId: nodeId
    }
  }
  
  $store.dispatch('addSession', streamObj)
}

function updateSessionStream(nodeId, stream) {
  $store.dispatch('updateSessionStream', {
    nodeId: nodeId,
    stream: stream
  })
}

/* APPLICATION REST METHODS */

function getToken(name, callback) {
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

function removeUser() {
	httpPostRequest(
		'api-sessions/remove-user',
		{sessionName: sessionName, token: token},
		'User couldn\'t be removed from session', 
		(response) => {
			console.warn("You have been removed from session " + sessionName);
		}
	)
}

function httpPostRequest(url, body, errorMsg, callback) {
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

/* APPLICATION BROWSER METHODS */

module.exports = {
  join: joinSession,
  leave: leaveSession,
  chat: sendChat
}