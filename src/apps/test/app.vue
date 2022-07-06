<template>
  <div>
    세션 시그널 테스트 페이지
    <div>
      <label for="token">token</label>
      <input type="text" id="token" name="token" v-model="token" />
    </div>
    <p>
      토큰 예제
      wss://192.168.0.9:8000?sessionId=ses_YeLmBDkBwc&token=tok_TICsSe7E1kmpx18E&role=PUBLISHER&version=2.0
    </p>

    <button @click="connect">connect!</button>

    <p>보낼 시그널 메시지</p>
    <textarea></textarea>
  </div>
</template>

<script>
import { OpenVidu } from '@virnect/remote-webrtc'

const tokenBase = '&recorder=true&secret=remote'
const conOpts =
  '{"iceServers":[{"username":"remote","credential":"remote","url":"turn:192.168.0.9:3478","urls":"turn:192.168.0.9:3478"}],"role":"PUBLISHER","wsUri":"wss://192.168.0.9:8073/remote/websocket"}'
const metaData =
  '{"clientData":"4d127135f699616fb88e6bc4fa6d784a","roleType":"EXPERT","deviceType":"DESKTOP","device":0}'
export default {
  name: 'container',
  data() {
    return {
      OV: null,
      session: null,
      token: null,
    }
  },
  methods: {
    connect() {
      this.session = this.OV.initSession()
      this.session.connect(this.token, JSON.parse(conOpts), null)
    },
  },
  mounted() {
    this.OV = new OpenVidu()
  },
}
</script>

<style></style>
