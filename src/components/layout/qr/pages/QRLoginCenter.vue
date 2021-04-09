<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle">
      <el-col>
        <h2 class="title">{{ $t('qrLoginCenter.title') }}</h2>
        <p v-html="$t('qrLoginCenter.pageInfo')"></p>

        <div class="qr-login-body">
          <p>
            [{{ $t('qrLoginCenter.loginRole')
            }}<i v-if="isExpire"> {{ $t('qrLoginCenter.expire') }}</i
            >]
          </p>
          <div class="qr-image-box" :class="{ 'code-expire': isExpire }">
            <el-image
              v-if="qrImage"
              :src="qrImage"
              :preview-src-list="[qrImage]"
              alt="qrcode image"
              class="qrcode--image__inner"
            />
            <p v-if="isExpire">{{ $t('qrLoginCenter.renewalCode') }}</p>
          </div>
          <p>{{ $t('qrLoginCenter.effectiveTime') }}</p>
          <p class="qr-expire-count">{{ showTime }}</p>
          <el-button
            class="next-btn block-btn"
            @click="reset()"
            type="primary"
            >{{ $t('qrLoginCenter.resendCode') }}</el-button
          >
        </div>
        <div class="howto-qr-login">
          <p class="title">{{ $t('qrLoginCenter.howToLogin') }}</p>
          <div>
            <ol>
              <li
                v-for="(list, idx) of $t('qrLoginCenter.centerHowTo')"
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
import AuthService from 'service/auth-service'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import duration from 'dayjs/plugin/duration'
export default {
  props: {
    userInfo: Object,
    customInfo: Object,
    env: String,
    subTitle: String,
  },
  data() {
    return {
      runnerID: null,
      deadline: dayjs().add(3, 'minute').unix(),
      remainTime: 0,
      qrImage: null,
      isExpire: false,
    }
  },
  computed: {
    showTime() {
      let minute = parseInt(this.remainTime / 60)
      let second = parseInt(this.remainTime % 60)
      if (minute < 10) {
        minute = '0' + minute
      }
      if (second < 10) {
        second = '0' + second
      }
      return `${minute}:${second}`
    },
  },
  watch: {
    userInfo() {
      this.reset()
    },
  },
  methods: {
    nameSet(txt) {
      if (this.env !== 'onpremise' || !/VIRNECT/.test(txt)) return txt
      else {
        let val = txt.replace(/VIRNECT/, this.customInfo.title)
        return val
      }
    },
    async reset() {
      const params = {
        email: this.userInfo.email,
        userId: this.userInfo.uuid,
      }
      try {
        let otp = await AuthService.qrOtp({ params: params })
        if (otp.code == 200) {
          this.timeRunner()
          this.deadline = dayjs().add(3, 'minute').unix()
          this.remainTime = parseInt(this.deadline - dayjs().unix())
          this.qrImage = `data:image/png;base64,${otp.data.qrCode}`
        } else {
          throw otp.data
        }
      } catch (e) {
        location.replace(`${window.urls['console']}/?continue=${location.href}`)
      }
    },
    timeRunner() {
      clearInterval(this.runnerID)
      dayjs.extend(duration)
      dayjs.extend(utc)
      this.isExpire = false
      this.runnerID = setInterval(() => {
        const diff = this.deadline - dayjs().unix()
        this.remainTime = parseInt(dayjs.duration({ second: diff }).$ms / 1000)
        if (this.remainTime <= 0) {
          this.isExpire = true
          clearInterval(this.runnerID)
        }
      }, 1000)
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~element/image.css';

p {
  font-weight: 500;
  font-family: $noto;
}
.container {
  text-align: center;
  @media (max-width: $mobile) {
    padding: 96px 24px 24px;
  }
}
</style>
