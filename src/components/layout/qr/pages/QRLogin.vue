<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle">
      <el-col>
        <h2 class="title">{{ $t('qrLogin.title') }}</h2>
        <p v-html="$t('qrLogin.pageInfo')"></p>

        <div class="qr-login-body">
          <p>{{ $t('qrLogin.manual') }}</p>
          <div class="qr-image-box">
            <qrcode-stream @decode="onDecode" @init="onInit" />
          </div>
        </div>
        <div class="howto-qr-login">
          <p class="title">{{ $t('qrLoginCenter.howToLogin') }}</p>
          <div>
            <ol>
              <li
                v-for="(list, idx) of $t('qrLoginCenter.loginHowTo')"
                :key="idx"
              >
                <p v-if="!customInfo">
                  <span>{{ idx + 1 }}. </span>{{ list }}
                </p>
                <p v-else>
                  <span>{{ idx + 1 }}. </span>{{ nameSet(list) }}
                </p>
              </li>
            </ol>
            <p>{{ $t('qrLoginCenter.LoginInfo') }}</p>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { QrcodeStream } from 'vue-qrcode-reader'

export default {
  props: {
    userInfo: Object,
    env: String,
    customInfo: Object,
    subTitle: String,
  },
  components: {
    QrcodeStream,
  },
  data() {
    return {
      result: '',
      error: '',
    }
  },
  methods: {
    nameSet(txt) {
      if (this.env !== 'onpremise' || !/VIRNECT/.test(txt)) return txt
      else {
        let val = txt.replace(/VIRNECT/, this.customInfo.title)
        return val
      }
    },
    onDecode(result) {
      this.result = result
    },
    async onInit(promise) {
      try {
        await promise
      } catch (error) {
        if (error.name === 'NotAllowedError') {
          this.error = 'ERROR: you need to grant camera access permisson'
        } else if (error.name === 'NotFoundError') {
          this.error = 'ERROR: no camera on this device'
        } else if (error.name === 'NotSupportedError') {
          this.error = 'ERROR: secure context required (HTTPS, localhost)'
        } else if (error.name === 'NotReadableError') {
          this.error = 'ERROR: is the camera already in use?'
        } else if (error.name === 'OverconstrainedError') {
          this.error = 'ERROR: installed cameras are not suitable'
        } else if (error.name === 'StreamApiNotSupportedError') {
          this.error = 'ERROR: Stream API is not supported in this browser'
        }
      }
    },
  },
}
</script>

<style lang="scss" scoped>
p {
  font-weight: 500;
  font-family: $noto;
}
.container {
  text-align: center;
}
</style>
