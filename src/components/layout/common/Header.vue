<template>
  <header id="headerSection" class="header-section">
    <nav ref="nav" :class="classes">
      <div class="header-title-wrap">
        <div v-if="isMobile && customGnb">
          <button class="side-nav-button" @click="isSide = !isSide">
            <img
              src="@virnect/ui-assets/images/products/platform/header/ic-side-menu.svg"
              v-if="!isSide"
            />
            <img
              src="@virnect/ui-assets/images/products/platform/header/ic-side-close.svg"
              v-else
            />
          </button>
        </div>
        <button class="logo" @click="routeMain('h1')" v-if="isLogo">
          <h1>
            <img :src="logoSrc" />
          </h1>
        </button>
        <!-- subTitle -->
        <template v-if="!isMobile">
          <button v-if="subTitle" class="sub-title" @click="routeMain('h2')">
            <h2>{{ subTitle }}</h2>
          </button>
          <slot v-else name="subTitle"></slot>
        </template>
        <slot name="multiWorkspace"></slot>

        <h2 class="blind-heading" v-if="!subTitle">GNB</h2>
      </div>
      <GNB
        :type="type"
        v-if="customGnb && !isMobile"
        :customGnb="customGnb"
        :isMobile="isMobile"
      />
      <div class="mobile-nav" v-if="customGnb && isSide && isMobile">
        <GNB :type="type" :customGnb="customGnb" :isMobile="isMobile" />
      </div>
      <login-status
        :env="env"
        :urls="urls"
        :userInfo="userInfo"
        :showStatus="showStatus"
        :isMobile="isMobile"
        :localeArr="localeArr"
        :regex="regex"
        @logout="logout"
      />
    </nav>
  </header>
</template>

<script>
import LoginStatus from './partials/LoginStatus.vue'
import GNB from './partials/GNB.vue'
import urls from '../../urls'
import Cookies from 'js-cookie'

export default {
  components: {
    LoginStatus,
    GNB,
  },
  props: {
    env: {
      type: String,
      default() {
        return 'develop'
      },
    },
    logo: {
      default() {
        return {
          default: require('@virnect/ui-assets/images/products/platform/logo_default.svg'),
          white: require('@virnect/ui-assets/images/products/platform/logo_white.svg'),
        }
      },
    },
    isLogo: {
      default() {
        return {
          type: Boolean,
          default: true,
        }
      },
    },
    urls: {
      default() {
        return urls
      },
    },
    userInfo: Object,
    showStatus: {
      type: Object,
      default() {
        return {
          login: true,
          profile: false,
          language: true,
          portal: true,
        }
      },
    },
    type: {
      type: String,
      default: 'default',
      validator: value => {
        return ['default', 'promote'].indexOf(value) !== -1
      },
    },
    localeArr: {
      type: Object,
      default() {
        return {
          ko: '한국어',
          en: 'English',
        }
      },
    },
    subTitle: String,
    opacityBack: {
      type: Boolean,
      default: false,
    },
    customGnb: Array,
    regex: {
      type: RegExp,
      default() {
        return /remote|workstation|login/
      },
    },
  },
  data() {
    return {
      isMobile: false,
      isSide: false,
      linkBox: false,
      isScroll: false,
    }
  },
  watch: {
    $route() {
      this.isSide = false
    },
    isMobile() {
      this.$emit('isMobile', this.isMobile)
    },
  },
  computed: {
    logoSrc() {
      return !this.isScroll && this.opacityBack && !this.isSide
        ? this.logo.white
        : this.logo.default
    },
    classes() {
      return {
        [this.type]: this.type,
        visual: !this.isScroll && this.opacityBack && !this.isSide,
      }
    },
    profileImg() {
      return this.userInfo.profile == 'default'
        ? require('@virnect/ui-assets/images/products/platform/header/ic-user-profile.svg')
        : this.userInfo.profile
    },
    visualBoxHeight() {
      if (!this.isMobile) return 576
      else return 361
    },
  },
  methods: {
    observeHeader() {
      let offsetTop = window.pageYOffset
      if (offsetTop >= this.visualBoxHeight) {
        this.isScroll = true
      } else {
        this.isScroll = false
      }
    },
    routeMain(h) {
      if (this.subTitle && h === 'h1') {
        if (this.env === 'onpremise') {
          this.$router.push({
            path: '/',
          })
          return
        }
        window.open(this.urls.www)
      } else {
        if (this.$route.path === '/') return
        this.$router.push({
          path: '/',
        })
      }
    },
    async IS_MOBILE() {
      let bool = false
      ;(a => {
        if (
          /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(
            a,
          ) ||
          /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(
            a.substr(0, 4),
          )
        )
          bool = true
      })(navigator.userAgent || navigator.vendor || window.opera)

      if (
        navigator.userAgent.match(/Android/i) ||
        navigator.userAgent.match(/webOS/i) ||
        navigator.userAgent.match(/iPhone/i) ||
        // navigator.userAgent.match(/iPad/i) ||
        navigator.userAgent.match(/iPod/i) ||
        navigator.userAgent.match(/BlackBerry/i) ||
        navigator.userAgent.match(/Windows Phone/i)
      ) {
        bool = true
      }
      if (this.$el.clientWidth < 1000) {
        bool = true
      } else {
        bool = false
      }
      this.isMobile = bool
    },
    logout() {
      this.$emit('logout')
    },
    sideClose() {
      this.isSide = false
    },
    setLang() {
      const cookieOption = {
        domain:
          location.hostname.split('.').length === 3
            ? location.hostname.replace(/.*?\./, '')
            : location.hostname,
        sameSite: 'None',
      }
      let lang = Cookies.get('lang', cookieOption)

      if (this.$route.query.lang) {
        //query
        lang = this.$route.query.lang
      }
      if (Cookies.get('LPL_LANG')) {
        const lplLang = Cookies.get('LPL_LANG')
        //LGD Custom
        lang = lplLang === 1 ? 'en' : 'ko'
      }

      if (!lang) {
        //Browser lang
        if (navigator.language.substr(0, 2) !== 'ko') {
          lang = 'en'
        } else if (
          navigator.userLanguage &&
          navigator.userLanguage.substr(0, 2) !== 'ko'
        ) {
          lang = 'en'
        } else if (
          navigator.systemLanguage &&
          navigator.systemLanguage.substr(0, 2) !== 'ko'
        ) {
          lang = 'en'
        } else {
          lang = 'ko'
        }
        Cookies.set('lang', lang)
      }
      if (Cookies.get('lang', cookieOption) !== lang) {
        lang = lang === 'en' ? 'en' : 'ko'
        Cookies.set('lang', lang)
      }
      document.documentElement.lang = lang
      this.$i18n.locale = lang
    },
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.IS_MOBILE)
    window.removeEventListener('scroll', this.observeHeader)
  },
  mounted() {
    this.IS_MOBILE()
    this.setLang()
    window.addEventListener('resize', this.IS_MOBILE)
    window.addEventListener('scroll', this.observeHeader)
    document.addEventListener('click', () => {
      this.linkBox = false
    })
  },
}
</script>

<style lang="scss">
@import '~@virnect/ui-assets/css/header.css';
// @import 'node_modules/@virnect/ui-assets/css/header.css';
// @import './header.scss';
</style>
