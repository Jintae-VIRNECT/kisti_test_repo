import { Client } from '@stomp/stompjs'
import pushListener from './pushListener'

const config = {
  brokerURL: 'wss://192.168.6.3:8073/message',
  connectHeaders: {
    login: 'guest',
    passcode: 'guest',
  },
  debug: function(str) {
    console.log(str)
  },
  reconnectDelay: 5000,
  heartbeatIncoming: 0,
  heartbeatOutgoing: 0,
}

export const client = new Client(config)

client.onConnect = frame => {
  // Do something, all subscribes must be done is this callback
  // This is needed because this will be executed after a (re)connect
  console.log(frame)
  console.log(client)
}

client.onStompError = frame => {
  // Will be invoked in case of error encountered at Broker
  // Bad login/passcode typically will cause an error
  // Complaint brokers will set `message` header with a brief message. Body may contain details.
  // Compliant brokers will terminate the connection after any error
  console.log('Broker reported error: ' + frame.headers['message'])
  console.log('Additional details: ' + frame.body)
}

pushListener(client)

client.activate()

export default {
  install(Vue) {
    Vue.prototype.$push = client
  },
}
