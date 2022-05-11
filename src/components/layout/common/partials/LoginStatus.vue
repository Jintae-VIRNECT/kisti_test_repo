<template>
  <section class="login-status-section">
    <template v-if="!isMobile">
      <LoginBox v-if="showStatus.login" :urls="urls" :isMobile="isMobile" />

    </template>
    <button
      class="link-btn status-btn"
      @click.stop="linkControl"
      v-if="showStatus.portal"
    >
      <figure :class="{ active: linkBox }"></figure>
      <LinkBox :urls="urls" :env="env" :regex="regex" v-if="linkBox" />
    </button>
    <button
      class="status-btn thumbnail-btn"
      v-if="showStatus.profile"
      @click.stop="thumbControl"
    >
      <VirnectThumbnail :image="profileImg" :size="36" />
      <div class="hover-box" v-if="thumbBox" @click="hoverBoxClick">
        <template>
          <div class="thumbnail-head">
            <div class="img-wrap">
              <div class="profile-img">
                <VirnectThumbnail :image="profileImg" :size="isMobile ? 64 : 100" />
                <a
                  class="img-btn"
                  target="_blank"
                  :href="`${urls.account}/profile`"
                ></a>
              </div>
            </div>
            <div class="info-wrap">
              <p class="name-text">
                {{ userInfo.nickname == null ? 'Nickname' : userInfo.nickname }}
              </p>
              <p class="email-text">{{ userInfo.email }}</p>
              <a target="_blank" :href="urls.account" v-if="!isMobile">
                <template v-if="env !== 'onpremise'">VIRNECT</template>
                {{ $t('header.myAccount') }}
              </a>
              <button class="logout-button" @click="logout" v-else>
                {{ $t('header.logout') }}
              </button>
            </div>
          </div>
          <div class="thumbnail-body" v-if="!isMobile && env !== 'onpremise'">
            <button class="logout-button" @click="logout">
              {{ $t('header.logout') }}
            </button>
          </div>
        </template>
        <div class="thumbnail-foot" :class="{ onpremise: env === 'onpremise' }">
          <template v-if="!isMobile">
            <a
              target="_blank"
              :href="`${urls.www}/policy`"
              v-if="env !== 'onpremise'"
              >{{ $t('header.privacyPolicy') }}</a
            >
            <a
              target="_blank"
              :href="`${urls.www}/terms`"
              v-if="env !== 'onpremise'"
              >{{ $t('header.terms') }}</a
            >
            <button @click.stop="langBox = !langBox">
              <img
                src="@virnect/ui-assets/images/products/platform/header/ic-language.svg"
              />{{ locale == 'ko' ? '한국어' : 'English' }}
            </button>
          </template>
          <LangBox
            :env="env"
            :localeArr="localeArr"
            :showStatus="showStatus"
            :isMobile="isMobile"
            v-if="isMobile ? !langBox : langBox"
          />
          <button
            class="logout-button"
            @click="logout"
            v-if="!isMobile && env === 'onpremise'"
          >
            {{ $t('header.logout') }}
          </button>
        </div>
      </div>
    </button>
    <button
      class="status-btn thumbnail-btn"
      v-if="isMobile && showStatus.login"
      @click.stop="loginControl"
    >
      <VirnectThumbnail :size="36" />
      <div class="hover-box" v-if="loginBox" @click="hoverBoxClick">
        <template>
          <div class="thumbnail-head">
            <LoginBox :urls="urls" :isMobile="isMobile" />
          </div>
        </template>
        <div class="thumbnail-foot">
          <LangBox
            :env="env"
            :localeArr="localeArr"
            :showStatus="showStatus"
            :isMobile="isMobile"
            v-if="isMobile ? !langBox : langBox"
          />
        </div>
      </div>
    </button>

    <button
      class="language-btn status-btn"
      @click.stop="langControl"
      v-if="showStatus.language && !isMobile"
    >
      <figure :class="{ active: langBox }"></figure>
      <LangBox
        :env="env"
        :localeArr="localeArr"
        :showStatus="showStatus"
        v-if="langBox"
      />
    </button>
  </section>
</template>
<script>
import LangBox from './LangBox.vue'
import LinkBox from './LinkBox.vue'
import LoginBox from './LoginBox.vue'
export default {
  components: { LangBox, LoginBox, LinkBox },
  props: {
    userInfo: Object,
    urls: Object,
    env: String,
    showStatus: Object,
    localeArr: Object,
    isMobile: Boolean,
    regex: RegExp,
  },
  data() {
    return {
      currentUrl: '',
      langBox: false,
      linkBox: false,
      thumbBox: false,
      loginBox: false,
    }
  },
  computed: {
    profileImg() {
      if (this.userInfo && this.userInfo.profile) return this.userInfo.profile
      else return ''
    },
    locale() {
      return this.$i18n.locale
    },
  },
  methods: {
    langControl() {
      this.langBox = !this.langBox
      this.thumbBox = false
      this.linkBox = false
    },
    linkControl() {
      this.linkBox = !this.linkBox
      this.thumbBox = false
      this.langBox = false
      this.loginBox = false
    },
    thumbControl() {
      this.thumbBox = !this.thumbBox
      this.linkBox = false
      this.langBox = false
      this.loginBox = false
    },
    loginControl() {
      this.loginBox = !this.loginBox
      this.linkBox = false
    },
    hoverBoxClick(event) {
      event.stopPropagation()
    },
    logout() {
      this.$emit('logout')
    },
  },
  mounted() {
    this.currentUrl = location.href
    document.addEventListener('click', () => {
      this.thumbBox = false
      this.linkBox = false
      this.langBox = false
      this.loginBox = false
    })
  },
}
</script>
