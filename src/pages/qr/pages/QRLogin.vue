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
import { ref } from '@vue/composition-api'
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
  setup(props, { root }) {
    const result = ref('')
    const error = ref('')

    const nameSet = txt => {
      if (root.$env !== 'onpremise' || !/VIRNECT/.test(txt)) return txt
      else {
        let val = txt.replace(/VIRNECT/, props.customInfo.title)
        return val
      }
    }
    const onDecode = val => {
      result.value = val
    }
    const onInit = async promise => {
      try {
        await promise
      } catch (error) {
        if (error.name === 'NotAllowedError') {
          error.value = 'ERROR: you need to grant camera access permisson'
        } else if (error.name === 'NotFoundError') {
          error.value = 'ERROR: no camera on this device'
        } else if (error.name === 'NotSupportedError') {
          error.value = 'ERROR: secure context required (HTTPS, localhost)'
        } else if (error.name === 'NotReadableError') {
          error.value = 'ERROR: is the camera already in use?'
        } else if (error.name === 'OverconstrainedError') {
          error.value = 'ERROR: installed cameras are not suitable'
        } else if (error.name === 'StreamApiNotSupportedError') {
          error.value = 'ERROR: Stream API is not supported in this browser'
        }
      }
    }
    return {
      result,
      error,
      nameSet,
      onDecode,
      onInit,
    }
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
