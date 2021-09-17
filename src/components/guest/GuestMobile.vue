<template>
  <div class="guest-mobile">
    <section class="guest-mobile__logo">
      <div class="guest-mobile__logo--img">
        <img src="~assets/image/img_remote_logo.svg" alt="remote_logo" />
      </div>

      <img
        class="guest-mobile__logo--type"
        src="~assets/image/img_logo_type.svg"
        alt="Remote"
      />
      <p
        class="guest-mobile__logo--text"
        v-html="$t('guest.guest_install_mobile_app_description')"
      ></p>
    </section>
    <main class="guest-mobile__buttons">
      <!-- onpremise는 앱 실행버튼 항상 노출 -->
      <button
        class="guest-mobile__buttons--runapp"
        @click="runApp"
        v-if="isOnpremise || isAppInstalled"
      >
        {{ $t('button.run_app') }}
      </button>

      <button
        class="guest-mobile__buttons--playstore"
        @click="openPlayStore"
        v-else
      >
        <img
          src="~assets/image/img_google_app_store.svg"
          alt="app_store_logo"
        />
      </button>
      <!-- <button class="guest-mobile__buttons--runapp" @click="runApp">
        {{ $t('button.run_app') }}
      </button> -->

      <button class="guest-mobile__buttons--download" @click="downloadApp">
        {{ $t('button.download') }}
      </button>
      <!-- @TODO ykmo 2021-09-15 모바일 반응형 정식 릴리즈 전까지 숨김 처리 -->
      <!-- <button class="guest-mobile__buttons--linkweb" @click="accessWeb">
        {{ $t('button.connect_web') }}
      </button> -->
    </main>
  </div>
</template>

<script>
import confirmMixin from 'mixins/confirm'
import toastMixin from 'mixins/toast'
import { getLatestRemoteAosAppInfo, getIntentLink } from 'utils/appCheck'
export default {
  name: 'GuestMobile',
  mixins: [confirmMixin, toastMixin],
  data() {
    return {
      packageName: null,
      appUrl: null,
      isAppInstalled: false,
    }
  },
  methods: {
    openPlayStore() {
      const isOpend = window.open(
        'https://play.google.com/store/apps/details?id=com.virnect.remote.mobile2',
      )
      if (!isOpend) {
        this.confirmDefault(this.$t('confirm.please_allow_popup'))
      }
    },
    async runApp() {
      if (this.isValid()) {
        const intentLink = await getIntentLink({
          workspaceId: this.$route.query.workspaceId,
          sessionId: this.$route.query.sessionId,
          packageName: this.packageName,
        })
        console.log('intentLink::', intentLink)
        window.open(intentLink)
      } else {
        return false
      }
    },
    async downloadApp() {
      if (this.isValid()) {
        window.open(this.appUrl)
      } else {
        return false
      }
    },
    accessWeb() {
      this.$eventBus.$emit('updateServiceMode', 'web')
    },
    isValid() {
      if (this.packageName === null || this.appUrl === null) {
        this.confirmDefault(this.$t('confirm.network_error'))
        return false
      }
      return true
    },
    async setAppInfo() {
      const aosApp = await getLatestRemoteAosAppInfo()

      if (aosApp) {
        this.packageName = aosApp.packageName
        this.appUrl = aosApp.appUrl
      } else {
        this.confirmDefault(this.$t('confirm.network_error'))
      }
    },
    async checkAppInstalled() {
      const aosApp = await getLatestRemoteAosAppInfo()
      if (!aosApp) return false

      this.packageName = aosApp.packageName

      if (!navigator.getInstalledRelatedApps) {
        console.log('navigator.getInstalledRelatedApps is undefined')
        return false
      }

      const relatedApps = await navigator.getInstalledRelatedApps()
      console.log('installed app list :', relatedApps)
      const relatedApp = relatedApps.find(app => {
        console.log('installed app info ::', app)
        return app.url === this.packageName
      })

      this.isAppInstalled = relatedApp ? true : false
      console.log('isAppInstalled::',this.isAppInstalled)
    },
  },

  async mounted() {
    await this.setAppInfo()
    await this.checkAppInstalled()
  },
}
</script>

<style lang="scss">
.guest-mobile {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    -180deg,
    rgb(77, 86, 100) 0%,
    rgb(28, 32, 36) 100%
  );
  border-radius: 0px;
}
.guest-mobile__logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 116.9994px;
}

.guest-mobile__logo--img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100.0006px;
  height: 100.0006px;
  margin-bottom: 6px;
  background-image: linear-gradient(
      -225deg,
      rgb(255, 255, 255) 0%,
      rgb(188, 188, 188) 100%
    ),
    radial-gradient(circle at top left, #ffffff, #84bbe2);
  background-clip: content-box, border-box;
  background-origin: border-box;

  border: solid 0.8px transparent;

  border-radius: 27.2px;
  box-shadow: 0px 12px 15px 0px rgba(0, 0, 1, 0.4);
}

.guest-mobile__logo--type {
  width: 126px;
  height: 39.6004px;
  margin-bottom: 17.4006px;
}

.guest-mobile__logo--text {
  color: rgb(174, 182, 194);
  font-weight: 500;
  font-size: 14px;
  line-height: 20px;
  letter-spacing: 0px;
  white-space: pre-line;
  text-align: center;
}

.guest-mobile__buttons {
  display: flex;
  flex-direction: column;
  justify-content: center;

  a,
  button {
    width: 220.0002px;
    height: 48.0004px;

    margin-bottom: 16.0006px;
    border-radius: 6px;
    box-shadow: 0px 6px 14px 0px rgba(0, 0, 0, 0.2);
  }
}

.guest-mobile__buttons--playstore {
  padding-top: 4.9994px;
  background: rgb(255, 255, 255);
  border-radius: 6px;
  box-shadow: 0px 6px 14px 0px rgba(0, 0, 0, 0.2);
  > img {
    width: 108.9998px;
    height: 27.0004px;
  }
}

.guest-mobile__buttons--runapp {
  width: 220.0002px;
  height: 48.0004px;
  color: rgb(255, 255, 255);

  font-weight: 500;
  font-size: 14.9996px;
  letter-spacing: 0px;
  text-align: center;
  background: rgb(59, 146, 243);
  border-radius: 6px;
}

.guest-mobile__buttons--download {
  color: rgb(255, 255, 255);

  font-weight: 500;
  font-size: 14.9996px;
  letter-spacing: 0px;
  background: rgb(62, 69, 79);
}

.guest-mobile__buttons--linkweb {
  color: rgb(255, 255, 255);

  font-weight: 500;
  font-size: 14.9996px;
  letter-spacing: 0px;
  background: #616872;
  border-radius: 6px;
}
</style>
