<template>
  <div class="push">
    <h1 class="push-title">Remote Push Test</h1>
    <section class="push-section">
      <h2>푸시알림 가이드</h2>
      <div class="push-value">
        <span
          ><a href="https://web-push-codelab.glitch.me/"
            >https://web-push-codelab.glitch.me/</a
          >에 접속 후 Public Key를 input에 입력한다.</span
        ><br />
        <span
          >입력 후 알림 받기로 권한을 수락 후 하단에 조회되는 Subscription
          정보를 위 데모페이지의 'Subscription to Send To'에 복사하고 텍스트를
          전송한다.</span
        >
      </div>
    </section>

    <section class="push-section">
      <h2 class="push-subtitle">Subscribe Test</h2>
      <div class="push-control">
        <input
          class="push-serverkey"
          type="text"
          v-model="applicationServerPublicKey"
        />
        <button
          class="push-button"
          @click="isSubscribed ? unSubscribe() : subscribe()"
        >
          {{ isSubscribed ? '알림 받지 않기' : '알림 받기' }}
        </button>
      </div>
      <div class="push-value">
        <span>{{ viewSubscription }}</span>
      </div>
    </section>
  </div>
</template>
<script>
// 전송 테스트는 여기서: https://web-push-codelab.glitch.me/
const serviceWorker = '/static/js/sw.js'

export default {
  data() {
    return {
      applicationServerPublicKey:
        'BEnG_QrdlPEYnbzrBMoCcnaICel46eACTUnJMlYDcwhaS_MyEBwq5zsdXKt8gRZDstgeLxdjsyZViJzo9Fbt0_I',
      swRegistration: null,
      isSubscribed: false,
      viewSubscription: '',
    }
  },
  methods: {
    init() {
      window.addEventListener('load', () => {
        if ('serviceWorker' in navigator && 'PushManager' in window) {
          console.log('Service Worker and Push is supported')

          navigator.serviceWorker
            .register(serviceWorker)
            .then(swReg => {
              console.log('Service Worker is registered', swReg)

              this.swRegistration = swReg
              this.initialiseUI()
            })
            .catch(error => {
              console.error('Service Worker Error', error)
            })
        } else {
          console.warn('Push messaging is not supported')
          // pushButton.textContent = 'Push Not Supported';
        }
      })
    },
    initialiseUI() {
      this.swRegistration.pushManager.getSubscription().then(subscription => {
        this.isSubscribed = !(subscription === null)

        if (this.isSubscribed) {
          this.viewSubscription = JSON.stringify(subscription)
          console.log('User IS subscribed.')
        } else {
          console.log('User is NOT subscribed.')
        }
      })
    },
    subscribe() {
      const applicationServerKey = this.urlB64ToUint8Array(
        this.applicationServerPublicKey,
      )
      this.swRegistration.pushManager
        .subscribe({
          userVisibleOnly: true,
          applicationServerKey: applicationServerKey,
        })
        .then(subscription => {
          console.log('User is subscribed:', subscription)
          this.isSubscribed = true
          this.viewSubscription = JSON.stringify(subscription)
        })
        .catch(err => {
          console.log('Failed to subscribe the user: ', err)
        })
    },
    unSubscribe() {
      this.swRegistration.pushManager
        .getSubscription()
        .then(subscription => {
          if (subscription) {
            return subscription.unsubscribe()
          }
        })
        .catch(error => {
          console.log('Error unsubscribing', error)
        })
        .then(() => {
          console.log('User is unsubscribed.')
          this.isSubscribed = false
          this.viewSubscription = ''
        })
    },
    urlB64ToUint8Array(base64String) {
      const padding = '='.repeat((4 - (base64String.length % 4)) % 4)
      const base64 = (base64String + padding)
        .replace(/\-/g, '+')
        .replace(/_/g, '/')

      const rawData = window.atob(base64)
      const outputArray = new Uint8Array(rawData.length)

      for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i)
      }
      return outputArray
    },
  },
  mounted() {
    this.init()
  },
}
</script>

<style lang="scss" scoped>
.push-title {
  margin: 20px;
}
.push-section {
  margin: 20px;
  background-color: #dedede;
  border-radius: 10px;
}
.push-control {
  display: inline;
}
.push-serverkey {
  min-width: 600px;
  padding: 8px 15px;
  color: #fff;
  background-color: #787878;
  border: solid 1px #fff;
  border-radius: 4px;
}
.push-button {
  margin-top: 20px;
  padding: 8px 15px;
  color: #fff;
  background-color: #267bff;
  border-radius: 4px;
}

.push-value {
  margin-top: 20px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  & > span {
    word-break: break-word;
  }
}
</style>
