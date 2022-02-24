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
        v-html="'* ' + $t('guest.guest_install_mobile_app_description')"
      ></p>
      <br />
      <p
        v-if="isOnpremise"
        class="guest-mobile__logo--text"
        v-html="
          '* ' + $t('guest.guest_install_mobile_app_download_description')
        "
      ></p>
    </section>
    <main class="guest-mobile__buttons">
      <button
        v-if="!isSafari"
        class="guest-mobile__buttons--runapp"
        @click="runAppOrOpenStore"
      >
        {{ $t('button.run_app') }}
      </button>

      <button
        v-if="isOnpremise"
        class="guest-mobile__buttons--download"
        @click="downloadApp"
      >
        {{ $t('button.download') }}
      </button>

      <button class="guest-mobile__buttons--linkweb" @click="accessWeb">
        {{ $t('button.connect_web') }}
      </button>
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
    async runAppOrOpenStore() {
      if (this.isOnpremise || this.isAppInstalled) {
        await this.runApp()
      } else {
        this.openPlayStore()
      }
    },
    async runApp() {
      if (this.isValid()) {
        const intentLink = await getIntentLink({
          workspaceId: this.$route.query.workspaceId,
          sessionId: this.$route.query.sessionId,
          packageName: this.packageName,
        })

        window.open(intentLink)
      } else {
        return false
      }
    },
    openPlayStore() {
      const isOpend = window.open(
        'https://play.google.com/store/apps/details?id=com.virnect.remote.mobile2',
      )
      if (!isOpend) {
        this.confirmDefault(this.$t('confirm.please_allow_popup'))
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
        this.logger(
          '[guest login] ',
          'navigator.getInstalledRelatedApps is undefined',
        )
        return false
      }

      const relatedApps = await navigator.getInstalledRelatedApps()

      this.debug('[guest login] installed app list :: ', relatedApps)
      this.logger('[guest login] package name ', this.packageName)

      const relatedApp = relatedApps.find(app => {
        return app.id === this.packageName
      })

      this.debug('[guest login] installed app info :: ', relatedApp)

      this.isAppInstalled = relatedApp ? true : false

      this.isAppInstalled
        ? this.logger('[guest login] ', 'app is installed')
        : this.logger('[guest login] ', 'app is not installed')
    },
  },

  async mounted() {
    await this.setAppInfo()
    await this.checkAppInstalled()
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';

.guest-mobile {
  // display: flex;
  // flex-direction: column;
  // align-items: center;
  //justify-content: center;
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
  //margin-bottom: 116.9994px;
  position: fixed;
  top: 12.6%;
  left: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 26.4rem;
  transform: translateX(-50%);
}

.guest-mobile__logo--img {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 8.8rem;
  height: 8.8rem;
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
  width: 11.1rem;
  height: 3.5rem;
  margin-bottom: 2.6rem;
}

.guest-mobile__logo--text {
  color: $new_color_text_sub;
  @include fontLevel(100);
  white-space: pre-line;
  text-align: center;
}

.guest-mobile__buttons {
  position: fixed;
  bottom: 7.8%;
  left: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transform: translateX(-50%);

  a,
  button {
    width: 22rem;
    height: 4.8rem;
    margin-top: 1.6rem;
    color: rgb(255, 255, 255);
    border-radius: 6px;
    box-shadow: 0px 6px 14px 0px rgba(0, 0, 0, 0.2);
    @include fontLevel(150);
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
  text-align: center;
  background: $new_color_bg_button_highlighted;
  border-radius: 6px;
}

.guest-mobile__buttons--download {
  background: $new_color_bg_button_sub3;
}

.guest-mobile__buttons--linkweb {
  background: $new_color_bg_button_sub;
  border-radius: 6px;
}
</style>
